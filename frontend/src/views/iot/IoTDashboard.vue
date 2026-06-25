<script setup>
import { ref, onMounted, onUnmounted, reactive, computed, nextTick } from "vue";
import * as echarts from "echarts";
import "echarts-gl";
import { ElMessage } from "element-plus";
import {
  Monitor,
  Cpu,
  Refresh,
  VideoPlay,
  VideoPause,
  Warning,
  Connection,
  Histogram,
  DataLine,
  TrendCharts,
} from "@element-plus/icons-vue";
import {
  startDemo,
  stopDemo,
  getDemoStatus,
  getDeviceLatestAll,
  getSensorHistory,
  getActiveAlarms,
} from "@/api/iot";

// Demo 状态
const demoRunning = ref(false);
const demoDataCount = ref(0);
const demoDuration = ref(0);
const wsConnected = ref(false);
const demoLoading = ref(false);

// 设备数据
const devices = reactive([
  { code: "DEMO_WEATHER_001", name: "气象站", type: "weather", icon: "Cloudy", online: true, lastUpdate: "", data: {} },
  { code: "DEMO_SOIL_001", name: "土壤传感器", type: "soil", icon: "Histogram", online: true, lastUpdate: "", data: {} },
  { code: "DEMO_GAS_001", name: "气体分析仪", type: "gas", icon: "DataLine", online: true, lastUpdate: "", data: {} },
]);

// 告警数据
const alarms = ref([]);
const activeAlarmCount = ref(0);

// 图表配置
const timeRange = ref("1h");
const selectedMetrics = ref(["temperature", "humidity", "co2", "soilMoisture"]);
const selectedDevice = ref("DEMO_WEATHER_001");
const chart3DRef = ref(null);
const chartTimeRef = ref(null);
const chartCorrelationRef = ref(null);

let chart3D = null;
let chartTime = null;
let chartCorrelation = null;
let demoStatusTimer = null;
let deviceDataTimer = null;
let chartDataTimer = null;

// 指标选项
const metricOptions = [
  { value: "temperature", label: "温度" },
  { value: "humidity", label: "湿度" },
  { value: "co2", label: "CO₂浓度" },
  { value: "soilMoisture", label: "土壤湿度" },
  { value: "windSpeed", label: "风速" },
  { value: "precipitation", label: "降水量" },
  { value: "soilTemperature", label: "土壤温度" },
  { value: "lightIntensity", label: "光照强度" },
  { value: "ch4", label: "CH₄浓度" },
  { value: "n2o", label: "N₂O浓度" },
];

// 时间范围选项
const timeRangeOptions = [
  { value: "1h", label: "1小时" },
  { value: "6h", label: "6小时" },
  { value: "24h", label: "24小时" },
  { value: "7d", label: "7天" },
];

// 指标中文映射
const metricLabels = {
  temperature: "温度 (°C)",
  humidity: "湿度 (%)",
  co2: "CO₂ (ppm)",
  soilMoisture: "土壤湿度 (%)",
  windSpeed: "风速 (m/s)",
  precipitation: "降水量 (mm)",
  lightIntensity: "光照 (lux)",
  soilTemperature: "土壤温度 (°C)",
  soilPH: "土壤pH",
  soilConductivity: "电导率 (mS/cm)",
  ch4: "CH₄ (ppm)",
  n2o: "N₂O (ppm)",
};

// 指标颜色
const metricColors = {
  temperature: "#F56C6C",
  humidity: "#409EFF",
  co2: "#67C23A",
  soilMoisture: "#E6A23C",
  windSpeed: "#8B5CF6",
  precipitation: "#00BCD4",
  soilTemperature: "#FF7043",
  lightIntensity: "#FFD54F",
  soilConductivity: "#A1887F",
  ch4: "#7E57C2",
  n2o: "#26A69A",
};

// 初始化 3D 图表
function initChart3D() {
  if (!chart3DRef.value) return;

  chart3D = echarts.init(chart3DRef.value);

  // 生成模拟数据
  const hours = Array.from({ length: 24 }, (_, i) => `${i}:00`);
  const metrics = ["temperature", "humidity", "co2", "soilMoisture"];
  const data = [];

  for (let i = 0; i < hours.length; i++) {
    for (let j = 0; j < metrics.length; j++) {
      const value = Math.random() * 100;
      data.push([i, j, value]);
    }
  }

  const option = {
    tooltip: {
      show: true,
      formatter: (params) => {
        const [x, y, z] = params.value;
        return `时间: ${hours[x]}<br/>指标: ${metricLabels[metrics[y]]}<br/>数值: ${z.toFixed(1)}`;
      },
    },
    visualMap: {
      max: 100,
      inRange: {
        color: ["#313695", "#4575b4", "#74add1", "#abd9e9", "#fee090", "#fdae61", "#f46d43", "#d73027"],
      },
    },
    xAxis3D: {
      type: "category",
      data: hours,
      name: "时间",
    },
    yAxis3D: {
      type: "category",
      data: metrics.map((m) => metricLabels[m]),
      name: "指标",
    },
    zAxis3D: {
      type: "value",
      name: "数值",
    },
    grid3D: {
      boxWidth: 200,
      boxHeight: 80,
      boxDepth: 80,
      viewControl: {
        projection: "perspective",
        autoRotate: false,
        alpha: 20,
        beta: 40,
        distance: 200,
      },
      light: {
        main: {
          intensity: 1.2,
          shadow: true,
        },
        ambient: {
          intensity: 0.3,
        },
      },
    },
    series: [
      {
        type: "bar3D",
        data: data,
        shading: "color",
        label: {
          show: false,
        },
        itemStyle: {
          opacity: 0.8,
        },
        emphasis: {
          itemStyle: {
            opacity: 1,
          },
          label: {
            show: true,
          },
        },
      },
    ],
  };

  chart3D.setOption(option);
}

// 初始化时序图表
function initChartTime() {
  if (!chartTimeRef.value) return;

  chartTime = echarts.init(chartTimeRef.value);
  updateTimeChart();
}

// 更新时序图表
function updateTimeChart() {
  if (!chartTime) return;

  // 生成模拟数据
  const now = new Date();
  const xData = [];
  const seriesData = {};

  selectedMetrics.value.forEach((metric) => {
    seriesData[metric] = [];
  });

  // 根据时间范围生成数据点
  let points = 60;
  let interval = 1;
  if (timeRange.value === "6h") { points = 72; interval = 5; }
  if (timeRange.value === "24h") { points = 96; interval = 15; }
  if (timeRange.value === "7d") { points = 168; interval = 60; }

  for (let i = points; i >= 0; i--) {
    const time = new Date(now.getTime() - i * interval * 60000);
    xData.push(time.toLocaleTimeString("zh-CN", { hour: "2-digit", minute: "2-digit" }));

    selectedMetrics.value.forEach((metric) => {
      const baseValue = metric === "temperature" ? 25 : metric === "humidity" ? 60 : metric === "co2" ? 400 : 30;
      seriesData[metric].push(baseValue + (Math.random() - 0.5) * 20);
    });
  }

  const series = selectedMetrics.value.map((metric) => ({
    name: metricLabels[metric],
    type: "line",
    smooth: true,
    symbol: "none",
    lineStyle: {
      width: 2,
    },
    areaStyle: {
      opacity: 0.1,
    },
    data: seriesData[metric],
    itemStyle: {
      color: metricColors[metric],
    },
  }));

  const option = {
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "cross",
      },
    },
    legend: {
      data: selectedMetrics.value.map((m) => metricLabels[m]),
      top: 0,
      textStyle: {
        color: "#E0E0E0",
      },
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "15%",
      top: "15%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: xData,
      axisLabel: {
        color: "#B0B0B0",
      },
      axisLine: {
        lineStyle: {
          color: "#404040",
        },
      },
    },
    yAxis: {
      type: "value",
      axisLabel: {
        color: "#B0B0B0",
      },
      axisLine: {
        lineStyle: {
          color: "#404040",
        },
      },
      splitLine: {
        lineStyle: {
          color: "#303030",
        },
      },
    },
    dataZoom: [
      {
        type: "slider",
        start: 0,
        end: 100,
        bottom: 0,
        height: 20,
        borderColor: "#404040",
        fillerColor: "rgba(64, 158, 255, 0.2)",
        handleStyle: {
          color: "#409EFF",
        },
        textStyle: {
          color: "#B0B0B0",
        },
      },
    ],
    series: series,
  };

  chartTime.setOption(option, true);
}

// 初始化关联分析图表
function initChartCorrelation() {
  if (!chartCorrelationRef.value) return;

  chartCorrelation = echarts.init(chartCorrelationRef.value);

  // 生成模拟相关性数据
  const factors = ["温度", "湿度", "CO2", "土壤湿度"];
  const carbonMetrics = ["碳排放", "碳吸收", "净排放"];

  const data = [];
  for (let i = 0; i < factors.length; i++) {
    for (let j = 0; j < carbonMetrics.length; j++) {
      data.push([i, j, (Math.random() * 2 - 1).toFixed(2)]);
    }
  }

  const option = {
    tooltip: {
      position: "top",
      formatter: (params) => {
        const [x, y, value] = params.value;
        return `${factors[x]} 与 ${carbonMetrics[y]}<br/>相关系数: ${value}`;
      },
    },
    grid: {
      left: "3%",
      right: "10%",
      bottom: "10%",
      top: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: factors,
      axisLabel: {
        color: "#B0B0B0",
      },
      axisLine: {
        lineStyle: {
          color: "#404040",
        },
      },
    },
    yAxis: {
      type: "category",
      data: carbonMetrics,
      axisLabel: {
        color: "#B0B0B0",
      },
      axisLine: {
        lineStyle: {
          color: "#404040",
        },
      },
    },
    visualMap: {
      min: -1,
      max: 1,
      calculable: true,
      orient: "horizontal",
      left: "center",
      bottom: "0%",
      inRange: {
        color: ["#313695", "#4575b4", "#74add1", "#abd9e9", "#fee090", "#fdae61", "#f46d43", "#d73027"],
      },
      textStyle: {
        color: "#B0B0B0",
      },
    },
    series: [
      {
        name: "相关性",
        type: "heatmap",
        data: data,
        label: {
          show: true,
          color: "#fff",
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
  };

  chartCorrelation.setOption(option);
}

// 启动 Demo
async function handleStartDemo() {
  demoLoading.value = true;
  try {
    await startDemo();
    demoRunning.value = true;
    ElMessage.success("模拟数据已启动");
    startDemoStatusPolling();
  } catch (error) {
    ElMessage.error("启动失败: " + (error.message || "未知错误"));
  } finally {
    demoLoading.value = false;
  }
}

// 停止 Demo
async function handleStopDemo() {
  demoLoading.value = true;
  try {
    await stopDemo();
    demoRunning.value = false;
    ElMessage.success("模拟数据已停止");
    stopDemoStatusPolling();
  } catch (error) {
    ElMessage.error("停止失败: " + (error.message || "未知错误"));
  } finally {
    demoLoading.value = false;
  }
}

// 获取 Demo 状态
async function fetchDemoStatus() {
  try {
    const res = await getDemoStatus();
    if (res.data) {
      demoRunning.value = res.data.running || false;
      demoDataCount.value = res.data.dataCount || 0;
      demoDuration.value = res.data.duration || 0;
    }
  } catch (error) {
    console.error("获取Demo状态失败:", error);
  }
}

// 启动 Demo 状态轮询
function startDemoStatusPolling() {
  if (demoStatusTimer) clearInterval(demoStatusTimer);
  demoStatusTimer = setInterval(fetchDemoStatus, 3000);
}

// 停止 Demo 状态轮询
function stopDemoStatusPolling() {
  if (demoStatusTimer) {
    clearInterval(demoStatusTimer);
    demoStatusTimer = null;
  }
}

// 获取设备数据
async function fetchDeviceData() {
  for (const device of devices) {
    try {
      const res = await getDeviceLatestAll(device.code);
      if (res.data) {
        device.data = res.data;
        device.online = true;
        device.lastUpdate = new Date().toLocaleTimeString("zh-CN");
      }
    } catch (error) {
      device.online = false;
      console.error(`获取设备 ${device.code} 数据失败:`, error);
    }
  }
}

// 获取告警数据
async function fetchAlarms() {
  try {
    const res = await getActiveAlarms();
    alarms.value = res.data || [];
    activeAlarmCount.value = alarms.value.length;
  } catch (error) {
    console.error("获取告警数据失败:", error);
  }
}

// 刷新所有数据
function refreshAll() {
  fetchDeviceData();
  fetchAlarms();
  updateTimeChart();
  ElMessage.success("数据已刷新");
}

// 格式化持续时间
function formatDuration(seconds) {
  if (!seconds) return "0秒";
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const secs = seconds % 60;
  if (hours > 0) return `${hours}小时${minutes}分`;
  if (minutes > 0) return `${minutes}分${secs}秒`;
  return `${secs}秒`;
}

// 获取设备图标
function getDeviceIcon(type) {
  switch (type) {
    case "weather": return Monitor;
    case "soil": return Histogram;
    case "gas": return DataLine;
    default: return Cpu;
  }
}

// 获取告警级别颜色
function getAlarmLevelColor(level) {
  switch (level) {
    case "CRITICAL": return "#F56C6C";
    case "WARNING": return "#E6A23C";
    case "INFO": return "#409EFF";
    default: return "#909399";
  }
}

// 获取告警状态颜色
function getAlarmStatusColor(status) {
  switch (status) {
    case "ACTIVE": return "#F56C6C";
    case "ACKNOWLEDGED": return "#E6A23C";
    case "RESOLVED": return "#67C23A";
    default: return "#909399";
  }
}

onMounted(() => {
  nextTick(() => {
    initChart3D();
    initChartTime();
    initChartCorrelation();
  });

  // 初始获取数据
  fetchDemoStatus();
  fetchDeviceData();
  fetchAlarms();

  // 启动轮询
  demoStatusTimer = setInterval(fetchDemoStatus, 3000);
  deviceDataTimer = setInterval(fetchDeviceData, 3000);
  chartDataTimer = setInterval(() => {
    updateTimeChart();
    fetchAlarms();
  }, 10000);

  // 监听窗口大小变化
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  // 清除所有定时器
  if (demoStatusTimer) clearInterval(demoStatusTimer);
  if (deviceDataTimer) clearInterval(deviceDataTimer);
  if (chartDataTimer) clearInterval(chartDataTimer);

  // 移除事件监听
  window.removeEventListener("resize", handleResize);

  // 销毁图表
  if (chart3D) {
    chart3D.dispose();
    chart3D = null;
  }
  if (chartTime) {
    chartTime.dispose();
    chartTime = null;
  }
  if (chartCorrelation) {
    chartCorrelation.dispose();
    chartCorrelation = null;
  }
});

function handleResize() {
  if (chart3D) chart3D.resize();
  if (chartTime) chartTime.resize();
  if (chartCorrelation) chartCorrelation.resize();
}

watch([timeRange, selectedMetrics], () => {
  updateTimeChart();
});
</script>

<template>
  <div class="iot-dashboard">
    <!-- 页面标题 -->
    <div class="dashboard-header">
      <h1 class="page-title">
        <el-icon><Monitor /></el-icon>
        IoT 实时监测中心
      </h1>
      <div class="header-actions">
        <el-button :icon="Refresh" @click="refreshAll">刷新数据</el-button>
      </div>
    </div>

    <!-- Demo 控制面板 -->
    <div class="demo-panel">
      <div class="demo-controls">
        <el-button
          v-if="!demoRunning"
          type="success"
          :icon="VideoPlay"
          :loading="demoLoading"
          @click="handleStartDemo"
        >
          启动模拟数据
        </el-button>
        <el-button
          v-else
          type="danger"
          :icon="VideoPause"
          :loading="demoLoading"
          @click="handleStopDemo"
        >
          停止模拟
        </el-button>

        <el-tag :type="demoRunning ? 'success' : 'info'" effect="dark" class="status-tag">
          {{ demoRunning ? '模拟运行中' : '模拟已停止' }}
        </el-tag>

        <div class="demo-stats">
          <span class="stat-item">
            <span class="stat-label">已生成数据:</span>
            <span class="stat-value">{{ demoDataCount }} 条</span>
          </span>
          <span class="stat-item">
            <span class="stat-label">运行时长:</span>
            <span class="stat-value">{{ formatDuration(demoDuration) }}</span>
          </span>
        </div>

        <div class="ws-status">
          <span class="status-dot" :class="{ connected: wsConnected }"></span>
          <span class="status-text">{{ wsConnected ? 'WebSocket 已连接' : 'WebSocket 未连接' }}</span>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="dashboard-content">
      <!-- 左侧: 设备卡片 -->
      <div class="left-panel">
        <div class="panel-title">
          <el-icon><Cpu /></el-icon>
          设备状态
        </div>
        <div class="device-cards">
          <el-card
            v-for="device in devices"
            :key="device.code"
            class="device-card"
            :class="{ offline: !device.online }"
          >
            <div class="device-header">
              <div class="device-info">
                <el-icon class="device-icon" :size="24">
                  <component :is="getDeviceIcon(device.type)" />
                </el-icon>
                <div>
                  <div class="device-name">{{ device.name }}</div>
                  <div class="device-code">{{ device.code }}</div>
                </div>
              </div>
              <div class="device-status">
                <span class="status-dot" :class="{ online: device.online }"></span>
                <span class="status-text">{{ device.online ? '在线' : '离线' }}</span>
              </div>
            </div>
            <div class="device-data" v-if="device.online">
              <div
                v-for="(entry, key) in device.data"
                :key="key"
                class="data-item"
              >
                <span class="data-label">{{ metricLabels[key] || key }}:</span>
                <span class="data-value">{{ typeof entry === 'object' && entry !== null ? Number(entry.value).toFixed(1) : entry }}{{ typeof entry === 'object' && entry !== null ? entry.unit : '' }}</span>
              </div>
            </div>
            <div class="device-update-time" v-if="device.lastUpdate">
              最后更新: {{ device.lastUpdate }}
            </div>
          </el-card>
        </div>
      </div>

      <!-- 中间区域 -->
      <div class="center-panel">
        <!-- 3D 图表 -->
        <div class="chart-section">
          <div class="panel-title">
            <el-icon><DataLine /></el-icon>
            多维传感器数据 3D 视图
          </div>
          <div ref="chart3DRef" class="chart-3d"></div>
        </div>

        <!-- 时序图表 -->
        <div class="chart-section">
          <div class="panel-title">
            <el-icon><TrendCharts /></el-icon>
            交互式时序图表
          </div>
          <div class="chart-toolbar">
            <el-radio-group v-model="timeRange" size="small">
              <el-radio-button
                v-for="option in timeRangeOptions"
                :key="option.value"
                :value="option.value"
              >
                {{ option.label }}
              </el-radio-button>
            </el-radio-group>
            <el-select
              v-model="selectedMetrics"
              multiple
              collapse-tags
              collapse-tags-tooltip
              placeholder="选择指标"
              size="small"
              class="metric-select"
            >
              <el-option
                v-for="option in metricOptions"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
            <el-select
              v-model="selectedDevice"
              placeholder="选择设备"
              size="small"
              class="device-select"
            >
              <el-option
                v-for="device in devices"
                :key="device.code"
                :label="device.name"
                :value="device.code"
              />
            </el-select>
          </div>
          <div ref="chartTimeRef" class="chart-time"></div>
        </div>
      </div>

      <!-- 右侧区域 -->
      <div class="right-panel">
        <!-- 告警面板 -->
        <div class="alarm-section">
          <div class="panel-title">
            <el-icon><Warning /></el-icon>
            活跃告警
            <el-badge :value="activeAlarmCount" :max="99" class="alarm-badge" />
          </div>
          <div class="alarm-list">
            <div
              v-for="alarm in alarms"
              :key="alarm.id"
              class="alarm-item"
            >
              <div class="alarm-header">
                <el-tag
                  :color="getAlarmLevelColor(alarm.level)"
                  effect="dark"
                  size="small"
                >
                  {{ alarm.level }}
                </el-tag>
                <el-tag
                  :color="getAlarmStatusColor(alarm.status)"
                  effect="plain"
                  size="small"
                >
                  {{ alarm.status }}
                </el-tag>
              </div>
              <div class="alarm-content">
                <div class="alarm-device">{{ alarm.deviceCode }}</div>
                <div class="alarm-metric">{{ alarm.metric }}: {{ alarm.actualValue }} (阈值: {{ alarm.threshold }})</div>
                <div class="alarm-time">{{ new Date(alarm.createdAt).toLocaleString("zh-CN") }}</div>
              </div>
            </div>
            <el-empty v-if="alarms.length === 0" description="暂无活跃告警" :image-size="60" />
          </div>
        </div>

        <!-- 碳排放关联分析 -->
        <div class="correlation-section">
          <div class="panel-title">
            <el-icon><TrendCharts /></el-icon>
            碳排放关联分析
          </div>
          <div ref="chartCorrelationRef" class="chart-correlation"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.iot-dashboard {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  padding: 20px;
  color: #E0E0E0;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 12px;
  color: #409EFF;
  margin: 0;
}

.demo-panel {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 16px 24px;
  margin-bottom: 20px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.demo-controls {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.status-tag {
  font-size: 14px;
}

.demo-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stat-label {
  color: #B0B0B0;
  font-size: 14px;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #409EFF;
}

.ws-status {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: auto;
}

.status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #F56C6C;
  box-shadow: 0 0 8px #F56C6C;
}

.status-dot.connected,
.status-dot.online {
  background: #67C23A;
  box-shadow: 0 0 8px #67C23A;
}

.status-text {
  font-size: 14px;
  color: #B0B0B0;
}

.dashboard-content {
  display: grid;
  grid-template-columns: 300px 1fr 350px;
  gap: 20px;
  min-height: calc(100vh - 200px);
}

.left-panel,
.center-panel,
.right-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.panel-title {
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #409EFF;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(64, 158, 255, 0.3);
}

.device-cards {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.device-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.device-card.offline {
  opacity: 0.6;
}

.device-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.device-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.device-icon {
  color: #409EFF;
}

.device-name {
  font-size: 16px;
  font-weight: 600;
  color: #E0E0E0;
}

.device-code {
  font-size: 12px;
  color: #B0B0B0;
}

.device-status {
  display: flex;
  align-items: center;
  gap: 6px;
}

.device-data {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
  margin-bottom: 12px;
}

.data-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 6px;
}

.data-label {
  font-size: 12px;
  color: #B0B0B0;
}

.data-value {
  font-size: 16px;
  font-weight: 600;
  color: #409EFF;
}

.device-update-time {
  font-size: 12px;
  color: #909399;
  text-align: right;
}

.chart-section {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.chart-3d {
  width: 100%;
  height: 400px;
}

.chart-time {
  width: 100%;
  height: 350px;
}

.chart-toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.metric-select,
.device-select {
  width: 200px;
}

.alarm-section {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  flex: 1;
}

.alarm-badge {
  margin-left: 8px;
}

.alarm-list {
  max-height: 300px;
  overflow-y: auto;
}

.alarm-item {
  padding: 12px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 8px;
  margin-bottom: 8px;
}

.alarm-header {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.alarm-device {
  font-size: 14px;
  font-weight: 600;
  color: #E0E0E0;
}

.alarm-metric {
  font-size: 13px;
  color: #B0B0B0;
  margin: 4px 0;
}

.alarm-time {
  font-size: 12px;
  color: #909399;
}

.correlation-section {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.chart-correlation {
  width: 100%;
  height: 250px;
}

/* Element Plus 暗色主题覆盖 */
:deep(.el-card) {
  background: transparent;
  border: none;
  color: #E0E0E0;
}

:deep(.el-card__body) {
  padding: 16px;
}

:deep(.el-radio-group) {
  --el-radio-button-checked-bg-color: #409EFF;
}

:deep(.el-select) {
  --el-select-input-focus-border-color: #409EFF;
}

:deep(.el-empty__description p) {
  color: #B0B0B0;
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .dashboard-content {
    grid-template-columns: 280px 1fr 320px;
  }
}

@media (max-width: 1200px) {
  .dashboard-content {
    grid-template-columns: 1fr;
  }
  
  .left-panel {
    order: 2;
  }
  
  .center-panel {
    order: 1;
  }
  
  .right-panel {
    order: 3;
  }
  
  .device-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  }
}
</style>