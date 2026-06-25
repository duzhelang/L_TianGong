<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick, watch } from "vue";
import * as echarts from "echarts";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  Monitor,
  Cpu,
  Refresh,
  Plus,
  Edit,
  Delete,
  View,
  Setting,
  Search,
  Warning,
  Connection,
  Check,
  CircleCheck,
  Location,
  DataLine,
  Histogram,
  Cloudy,
} from "@element-plus/icons-vue";

// ========== 权限控制 ==========
// 使用 v-permission 指令控制按钮显示，无需硬编码权限对象

// ========== 设备总览 Tab ==========
const overviewStats = reactive({
  total: 24,
  online: 18,
  offline: 4,
  alarm: 2,
});

// 设备类型分布数据
const deviceTypeData = [
  { name: "气象站", value: 8 },
  { name: "土壤传感器", value: 6 },
  { name: "水质监测", value: 5 },
  { name: "气体分析仪", value: 5 },
];

// 设备状态分布数据
const deviceStatusData = [
  { name: "在线", value: 18 },
  { name: "离线", value: 4 },
  { name: "告警", value: 2 },
];

// 最近告警数据
const recentAlarms = ref([
  { id: 1, deviceCode: "WEATHER_001", deviceName: "气象站-01", metric: "温度", actualValue: 38.5, threshold: 35, severity: "WARNING", time: "2024-01-15 14:30:00" },
  { id: 2, deviceCode: "SOIL_003", deviceName: "土壤传感器-03", metric: "土壤湿度", actualValue: 15, threshold: 20, severity: "ERROR", time: "2024-01-15 13:45:00" },
  { id: 3, deviceCode: "GAS_002", deviceName: "气体分析仪-02", metric: "CO2浓度", actualValue: 520, threshold: 500, severity: "INFO", time: "2024-01-15 12:20:00" },
]);

const deviceTypeChartRef = ref(null);
const deviceStatusChartRef = ref(null);
const deviceTypeChart = ref(null);
const deviceStatusChart = ref(null);

// 初始化设备类型分布饼图
function initDeviceTypeChart() {
  if (!deviceTypeChartRef.value) return;
  deviceTypeChart.value = echarts.init(deviceTypeChartRef.value);
  const option = {
    tooltip: { trigger: "item", formatter: "{a} <br/>{b}: {c} ({d}%)" },
    legend: { orient: "vertical", right: 10, top: "center", textStyle: { color: "#B0B0B0" } },
    color: ["#409EFF", "#67C23A", "#E6A23C", "#F56C6C"],
    series: [{
      name: "设备类型",
      type: "pie",
      radius: ["40%", "70%"],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: "#1a1a2e", borderWidth: 2 },
      label: { show: false, position: "center" },
      emphasis: {
        label: { show: true, fontSize: 16, fontWeight: "bold", color: "#E0E0E0" },
      },
      labelLine: { show: false },
      data: deviceTypeData,
    }],
  };
  deviceTypeChart.value.setOption(option);
}

// 初始化设备状态分布饼图
function initDeviceStatusChart() {
  if (!deviceStatusChartRef.value) return;
  deviceStatusChart.value = echarts.init(deviceStatusChartRef.value);
  const option = {
    tooltip: { trigger: "item", formatter: "{a} <br/>{b}: {c} ({d}%)" },
    legend: { orient: "vertical", right: 10, top: "center", textStyle: { color: "#B0B0B0" } },
    color: ["#67C23A", "#909399", "#F56C6C"],
    series: [{
      name: "设备状态",
      type: "pie",
      radius: ["40%", "70%"],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: "#1a1a2e", borderWidth: 2 },
      label: { show: false, position: "center" },
      emphasis: {
        label: { show: true, fontSize: 16, fontWeight: "bold", color: "#E0E0E0" },
      },
      labelLine: { show: false },
      data: deviceStatusData,
    }],
  };
  deviceStatusChart.value.setOption(option);
}

// ========== 设备列表 Tab ==========
const deviceSearchForm = reactive({
  keyword: "",
  deviceType: "",
  status: "",
});

const deviceTypeOptions = [
  { value: "WEATHER", label: "气象站" },
  { value: "SOIL", label: "土壤传感器" },
  { value: "WATER", label: "水质监测" },
  { value: "GAS", label: "气体分析仪" },
];

const deviceStatusOptions = [
  { value: "ONLINE", label: "在线" },
  { value: "OFFLINE", label: "离线" },
];

const deviceList = ref([
  { id: 1, deviceCode: "WEATHER_001", deviceName: "气象站-01", deviceType: "WEATHER", location: "A区温室", longitude: 116.404, latitude: 39.915, status: "ONLINE", lastOnlineAt: "2024-01-15 14:30:00", productKey: "pk_weather_001" },
  { id: 2, deviceCode: "WEATHER_002", deviceName: "气象站-02", deviceType: "WEATHER", location: "B区大棚", longitude: 116.414, latitude: 39.925, status: "ONLINE", lastOnlineAt: "2024-01-15 14:25:00", productKey: "pk_weather_002" },
  { id: 3, deviceCode: "SOIL_001", deviceName: "土壤传感器-01", deviceType: "SOIL", location: "A区温室", longitude: 116.405, latitude: 39.916, status: "ONLINE", lastOnlineAt: "2024-01-15 14:28:00", productKey: "pk_soil_001" },
  { id: 4, deviceCode: "SOIL_003", deviceName: "土壤传感器-03", deviceType: "SOIL", location: "C区农田", longitude: 116.424, latitude: 39.935, status: "OFFLINE", lastOnlineAt: "2024-01-14 18:00:00", productKey: "pk_soil_003" },
  { id: 5, deviceCode: "WATER_001", deviceName: "水质监测-01", deviceType: "WATER", location: "灌溉水源", longitude: 116.394, latitude: 39.905, status: "ONLINE", lastOnlineAt: "2024-01-15 14:20:00", productKey: "pk_water_001" },
  { id: 6, deviceCode: "GAS_001", deviceName: "气体分析仪-01", deviceType: "GAS", location: "A区温室", longitude: 116.406, latitude: 39.917, status: "ONLINE", lastOnlineAt: "2024-01-15 14:32:00", productKey: "pk_gas_001" },
  { id: 7, deviceCode: "GAS_002", deviceName: "气体分析仪-02", deviceType: "GAS", location: "B区大棚", longitude: 116.415, latitude: 39.926, status: "OFFLINE", lastOnlineAt: "2024-01-13 10:00:00", productKey: "pk_gas_002" },
]);

const filteredDeviceList = computed(() => {
  return deviceList.value.filter((device) => {
    const matchKeyword = !deviceSearchForm.keyword || 
      device.deviceCode.toLowerCase().includes(deviceSearchForm.keyword.toLowerCase()) ||
      device.deviceName.toLowerCase().includes(deviceSearchForm.keyword.toLowerCase());
    const matchType = !deviceSearchForm.deviceType || device.deviceType === deviceSearchForm.deviceType;
    const matchStatus = !deviceSearchForm.status || device.status === deviceSearchForm.status;
    return matchKeyword && matchType && matchStatus;
  });
});

const devicePagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
});

watch(filteredDeviceList, (val) => {
  devicePagination.total = val.length;
}, { immediate: true });

const paginatedDeviceList = computed(() => {
  const start = (devicePagination.currentPage - 1) * devicePagination.pageSize;
  const end = start + devicePagination.pageSize;
  return filteredDeviceList.value.slice(start, end);
});

// 新增/编辑设备对话框
const deviceDialogVisible = ref(false);
const deviceDialogTitle = ref("新增设备");
const deviceForm = reactive({
  id: null,
  deviceCode: "",
  deviceName: "",
  deviceType: "WEATHER",
  productKey: "",
  location: "",
  longitude: null,
  latitude: null,
});

function resetDeviceForm() {
  deviceForm.id = null;
  deviceForm.deviceCode = "";
  deviceForm.deviceName = "";
  deviceForm.deviceType = "WEATHER";
  deviceForm.productKey = "";
  deviceForm.location = "";
  deviceForm.longitude = null;
  deviceForm.latitude = null;
}

function handleAddDevice() {
  resetDeviceForm();
  deviceDialogTitle.value = "新增设备";
  deviceDialogVisible.value = true;
}

function handleEditDevice(row) {
  deviceDialogTitle.value = "编辑设备";
  Object.assign(deviceForm, {
    id: row.id,
    deviceCode: row.deviceCode,
    deviceName: row.deviceName,
    deviceType: row.deviceType,
    productKey: row.productKey,
    location: row.location,
    longitude: row.longitude,
    latitude: row.latitude,
  });
  deviceDialogVisible.value = true;
}

function handleSaveDevice() {
  if (!deviceForm.deviceCode || !deviceForm.deviceName) {
    ElMessage.warning("请填写设备编码和名称");
    return;
  }
  if (deviceForm.id) {
    const index = deviceList.value.findIndex((d) => d.id === deviceForm.id);
    if (index !== -1) {
      Object.assign(deviceList.value[index], { ...deviceForm });
    }
    ElMessage.success("设备更新成功");
  } else {
    const newDevice = {
      ...deviceForm,
      id: Date.now(),
      status: "OFFLINE",
      lastOnlineAt: "-",
    };
    deviceList.value.push(newDevice);
    ElMessage.success("设备添加成功");
  }
  deviceDialogVisible.value = false;
}

function handleDeleteDevice(row) {
  ElMessageBox.confirm(`确定要删除设备 "${row.deviceName}" 吗？`, "删除确认", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    const index = deviceList.value.findIndex((d) => d.id === row.id);
    if (index !== -1) {
      deviceList.value.splice(index, 1);
    }
    ElMessage.success("设备删除成功");
  }).catch(() => {});
}

// 设备详情抽屉
const deviceDetailVisible = ref(false);
const currentDevice = ref(null);
const deviceRealtimeData = ref({
  temperature: 25.3,
  humidity: 62,
  co2: 420,
  soilMoisture: 35,
});

function handleViewDevice(row) {
  currentDevice.value = row;
  deviceDetailVisible.value = true;
}

function getDeviceTypeLabel(type) {
  const map = { WEATHER: "气象站", SOIL: "土壤传感器", WATER: "水质监测", GAS: "气体分析仪" };
  return map[type] || type;
}

function getDeviceTypeTagType(type) {
  const map = { WEATHER: "primary", SOIL: "success", WATER: "warning", GAS: "danger" };
  return map[type] || "info";
}

function getStatusTagType(status) {
  return status === "ONLINE" ? "success" : "danger";
}

function getStatusLabel(status) {
  return status === "ONLINE" ? "在线" : "离线";
}

// ========== 告警管理 Tab ==========
const alarmStats = reactive({
  active: 5,
  acknowledged: 3,
  resolved: 12,
});

// 告警规则列表
const alarmRules = ref([
  { id: 1, ruleName: "高温告警", deviceType: "WEATHER", deviceCode: "-", metric: "temperature", condition: "GT", thresholdValue: 35, thresholdValue2: null, severity: "WARNING", enabled: true },
  { id: 2, ruleName: "低温告警", deviceType: "WEATHER", deviceCode: "-", metric: "temperature", condition: "LT", thresholdValue: 5, thresholdValue2: null, severity: "WARNING", enabled: true },
  { id: 3, ruleName: "土壤湿度低", deviceType: "SOIL", deviceCode: "-", metric: "soilMoisture", condition: "LT", thresholdValue: 20, thresholdValue2: null, severity: "ERROR", enabled: true },
  { id: 4, ruleName: "CO2浓度高", deviceType: "GAS", deviceCode: "-", metric: "co2", condition: "GT", thresholdValue: 500, thresholdValue2: null, severity: "INFO", enabled: true },
  { id: 5, ruleName: "温度区间异常", deviceType: "WEATHER", deviceCode: "WEATHER_001", metric: "temperature", condition: "BETWEEN", thresholdValue: 10, thresholdValue2: 30, severity: "WARNING", enabled: false },
]);

// 告警记录列表
const alarmRecords = ref([
  { id: 1, ruleId: 1, ruleName: "高温告警", deviceCode: "WEATHER_001", metric: "temperature", actualValue: 38.5, thresholdValue: 35, severity: "WARNING", status: "ACTIVE", acknowledgedBy: null, acknowledgedAt: null, resolvedAt: null, createdAt: "2024-01-15 14:30:00" },
  { id: 2, ruleId: 3, ruleName: "土壤湿度低", deviceCode: "SOIL_003", metric: "soilMoisture", actualValue: 15, thresholdValue: 20, severity: "ERROR", status: "ACTIVE", acknowledgedBy: null, acknowledgedAt: null, resolvedAt: null, createdAt: "2024-01-15 13:45:00" },
  { id: 3, ruleId: 4, ruleName: "CO2浓度高", deviceCode: "GAS_002", metric: "co2", actualValue: 520, thresholdValue: 500, severity: "INFO", status: "ACKNOWLEDGED", acknowledgedBy: "admin", acknowledgedAt: "2024-01-15 12:30:00", resolvedAt: null, createdAt: "2024-01-15 12:20:00" },
  { id: 4, ruleId: 2, ruleName: "低温告警", deviceCode: "WEATHER_002", metric: "temperature", actualValue: 3.2, thresholdValue: 5, severity: "WARNING", status: "RESOLVED", acknowledgedBy: "admin", acknowledgedAt: "2024-01-14 20:00:00", resolvedAt: "2024-01-15 08:00:00", createdAt: "2024-01-14 18:00:00" },
]);

const alarmRecordPagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
});

watch(alarmRecords, (val) => {
  alarmRecordPagination.total = val.length;
}, { immediate: true });

const paginatedAlarmRecords = computed(() => {
  const start = (alarmRecordPagination.currentPage - 1) * alarmRecordPagination.pageSize;
  const end = start + alarmRecordPagination.pageSize;
  return alarmRecords.value.slice(start, end);
});

// 新增告警规则对话框
const alarmRuleDialogVisible = ref(false);
const alarmRuleDialogTitle = ref("新增告警规则");
const alarmRuleForm = reactive({
  id: null,
  ruleName: "",
  deviceType: "",
  deviceCode: "",
  metric: "",
  condition: "GT",
  thresholdValue: null,
  thresholdValue2: null,
  severity: "WARNING",
  enabled: true,
});

const conditionOptions = [
  { value: "GT", label: "大于" },
  { value: "LT", label: "小于" },
  { value: "EQ", label: "等于" },
  { value: "BETWEEN", label: "介于" },
];

const severityOptions = [
  { value: "INFO", label: "信息" },
  { value: "WARNING", label: "警告" },
  { value: "ERROR", label: "错误" },
];

const metricOptions = [
  { value: "temperature", label: "温度" },
  { value: "humidity", label: "湿度" },
  { value: "co2", label: "CO2浓度" },
  { value: "soilMoisture", label: "土壤湿度" },
  { value: "windSpeed", label: "风速" },
  { value: "precipitation", label: "降水量" },
];

function resetAlarmRuleForm() {
  alarmRuleForm.id = null;
  alarmRuleForm.ruleName = "";
  alarmRuleForm.deviceType = "";
  alarmRuleForm.deviceCode = "";
  alarmRuleForm.metric = "";
  alarmRuleForm.condition = "GT";
  alarmRuleForm.thresholdValue = null;
  alarmRuleForm.thresholdValue2 = null;
  alarmRuleForm.severity = "WARNING";
  alarmRuleForm.enabled = true;
}

function handleAddAlarmRule() {
  resetAlarmRuleForm();
  alarmRuleDialogTitle.value = "新增告警规则";
  alarmRuleDialogVisible.value = true;
}

function handleEditAlarmRule(row) {
  alarmRuleDialogTitle.value = "编辑告警规则";
  Object.assign(alarmRuleForm, { ...row });
  alarmRuleDialogVisible.value = true;
}

function handleSaveAlarmRule() {
  if (!alarmRuleForm.ruleName || !alarmRuleForm.metric) {
    ElMessage.warning("请填写规则名称和监控指标");
    return;
  }
  if (alarmRuleForm.id) {
    const index = alarmRules.value.findIndex((r) => r.id === alarmRuleForm.id);
    if (index !== -1) {
      Object.assign(alarmRules.value[index], { ...alarmRuleForm });
    }
    ElMessage.success("告警规则更新成功");
  } else {
    const newRule = { ...alarmRuleForm, id: Date.now() };
    alarmRules.value.push(newRule);
    ElMessage.success("告警规则添加成功");
  }
  alarmRuleDialogVisible.value = false;
}

function handleDeleteAlarmRule(row) {
  ElMessageBox.confirm(`确定要删除告警规则 "${row.ruleName}" 吗？`, "删除确认", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    const index = alarmRules.value.findIndex((r) => r.id === row.id);
    if (index !== -1) {
      alarmRules.value.splice(index, 1);
    }
    ElMessage.success("告警规则删除成功");
  }).catch(() => {});
}

function handleAcknowledgeAlarm(row) {
  ElMessageBox.confirm("确定要确认此告警吗？", "确认告警", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "info",
  }).then(() => {
    row.status = "ACKNOWLEDGED";
    row.acknowledgedBy = "admin";
    row.acknowledgedAt = new Date().toLocaleString("zh-CN");
    alarmStats.active--;
    alarmStats.acknowledged++;
    ElMessage.success("告警已确认");
  }).catch(() => {});
}

function handleResolveAlarm(row) {
  ElMessageBox.confirm("确定要解决此告警吗？", "解决告警", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "success",
  }).then(() => {
    row.status = "RESOLVED";
    row.resolvedAt = new Date().toLocaleString("zh-CN");
    if (row.acknowledgedBy) {
      alarmStats.acknowledged--;
    } else {
      alarmStats.active--;
    }
    alarmStats.resolved++;
    ElMessage.success("告警已解决");
  }).catch(() => {});
}

function getSeverityTagType(severity) {
  const map = { INFO: "info", WARNING: "warning", ERROR: "danger" };
  return map[severity] || "info";
}

function getAlarmStatusTagType(status) {
  const map = { ACTIVE: "danger", ACKNOWLEDGED: "warning", RESOLVED: "success" };
  return map[status] || "info";
}

function getAlarmStatusLabel(status) {
  const map = { ACTIVE: "活跃", ACKNOWLEDGED: "已确认", RESOLVED: "已解决" };
  return map[status] || status;
}

function getConditionLabel(condition) {
  const map = { GT: "大于", LT: "小于", EQ: "等于", BETWEEN: "介于" };
  return map[condition] || condition;
}

// ========== 设备地图 Tab ==========
const mapContainerRef = ref(null);
const mapDevices = computed(() => {
  return deviceList.value.map((device) => ({
    ...device,
    x: ((device.longitude - 116.38) / 0.06) * 100,
    y: ((device.latitude - 39.89) / 0.06) * 100,
  }));
});

function getDeviceTypeIcon(type) {
  const map = { WEATHER: "🌤", SOIL: "🌱", WATER: "💧", GAS: "🔬" };
  return map[type] || "📡";
}

// ========== 生命周期 ==========
function handleResize() {
  if (deviceTypeChart.value) deviceTypeChart.value.resize();
  if (deviceStatusChart.value) deviceStatusChart.value.resize();
}

onMounted(() => {
  nextTick(() => {
    initDeviceTypeChart();
    initDeviceStatusChart();
  });
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  if (deviceTypeChart.value) {
    deviceTypeChart.value.dispose();
    deviceTypeChart.value = null;
  }
  if (deviceStatusChart.value) {
    deviceStatusChart.value.dispose();
    deviceStatusChart.value = null;
  }
});
</script>

<template>
  <div class="device-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">
        <el-icon><Cpu /></el-icon>
        IoT 设备管理
      </h1>
      <el-button :icon="Refresh" @click="ElMessage.success('数据已刷新')">刷新数据</el-button>
    </div>

    <!-- 主要标签页 -->
    <el-tabs type="border-card" class="main-tabs">
      <!-- 标签页1: 设备总览 -->
      <el-tab-pane label="设备总览">
        <template #label>
          <span class="tab-label">
            <el-icon><Monitor /></el-icon>
            设备总览
          </span>
        </template>

        <!-- 统计卡片 -->
        <div class="stat-cards">
          <el-card class="stat-card stat-total" shadow="hover">
            <div class="stat-card-content">
              <div class="stat-info">
                <div class="stat-label">设备总数</div>
                <div class="stat-value">{{ overviewStats.total }}</div>
              </div>
              <el-icon class="stat-icon" :size="48"><Cpu /></el-icon>
            </div>
          </el-card>
          <el-card class="stat-card stat-online" shadow="hover">
            <div class="stat-card-content">
              <div class="stat-info">
                <div class="stat-label">在线设备</div>
                <div class="stat-value">{{ overviewStats.online }}</div>
              </div>
              <el-icon class="stat-icon" :size="48"><Connection /></el-icon>
            </div>
          </el-card>
          <el-card class="stat-card stat-offline" shadow="hover">
            <div class="stat-card-content">
              <div class="stat-info">
                <div class="stat-label">离线设备</div>
                <div class="stat-value">{{ overviewStats.offline }}</div>
              </div>
              <el-icon class="stat-icon" :size="48"><Monitor /></el-icon>
            </div>
          </el-card>
          <el-card class="stat-card stat-alarm" shadow="hover">
            <div class="stat-card-content">
              <div class="stat-info">
                <div class="stat-label">告警设备</div>
                <div class="stat-value">{{ overviewStats.alarm }}</div>
              </div>
              <el-icon class="stat-icon" :size="48"><Warning /></el-icon>
            </div>
          </el-card>
        </div>

        <!-- 图表区域 -->
        <div class="chart-row">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>设备类型分布</span>
              </div>
            </template>
            <div ref="deviceTypeChartRef" class="chart-container"></div>
          </el-card>
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>设备状态分布</span>
              </div>
            </template>
            <div ref="deviceStatusChartRef" class="chart-container"></div>
          </el-card>
        </div>

        <!-- 最近告警列表 -->
        <el-card class="alarm-list-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近告警</span>
              <el-button type="primary" link>查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentAlarms" stripe style="width: 100%">
            <el-table-column prop="deviceCode" label="设备编码" width="160" />
            <el-table-column prop="deviceName" label="设备名称" width="160" />
            <el-table-column prop="metric" label="告警指标" width="120" />
            <el-table-column prop="actualValue" label="实际值" width="100">
              <template #default="{ row }">
                <span :class="{ 'value-danger': row.severity === 'ERROR', 'value-warning': row.severity === 'WARNING' }">
                  {{ row.actualValue }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="threshold" label="阈值" width="100" />
            <el-table-column prop="severity" label="严重程度" width="120">
              <template #default="{ row }">
                <el-tag :type="getSeverityTagType(row.severity)" effect="dark">
                  {{ row.severity }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="time" label="告警时间" min-width="180" />
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 标签页2: 设备列表 -->
      <el-tab-pane label="设备列表">
        <template #label>
          <span class="tab-label">
            <el-icon><Histogram /></el-icon>
            设备列表
          </span>
        </template>

        <!-- 搜索筛选栏 -->
        <el-card class="filter-card" shadow="hover">
          <el-form :inline="true" :model="deviceSearchForm" class="filter-form">
            <el-form-item label="关键词">
              <el-input
                v-model="deviceSearchForm.keyword"
                placeholder="设备编码/名称"
                clearable
                :prefix-icon="Search"
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="设备类型">
              <el-select v-model="deviceSearchForm.deviceType" placeholder="全部类型" clearable style="width: 150px">
                <el-option v-for="item in deviceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="deviceSearchForm.status" placeholder="全部状态" clearable style="width: 120px">
                <el-option v-for="item in deviceStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="Search">搜索</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 操作栏 -->
        <div class="action-bar">
          <el-button
            v-permission="'iot:device:create'"
            type="primary"
            :icon="Plus"
            @click="handleAddDevice"
          >
            新增设备
          </el-button>
        </div>

        <!-- 设备表格 -->
        <el-card class="table-card" shadow="hover">
          <el-table :data="paginatedDeviceList" stripe style="width: 100%" row-key="id">
            <el-table-column prop="deviceCode" label="设备编码" width="160" sortable />
            <el-table-column prop="deviceName" label="设备名称" width="160" />
            <el-table-column prop="deviceType" label="设备类型" width="120">
              <template #default="{ row }">
                <el-tag :type="getDeviceTypeTagType(row.deviceType)">
                  {{ getDeviceTypeLabel(row.deviceType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="location" label="位置" width="150" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusTagType(row.status)" effect="dark">
                  {{ getStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="lastOnlineAt" label="最后在线时间" min-width="180" sortable />
            <el-table-column label="操作" width="260" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link :icon="View" @click="handleViewDevice(row)">查看</el-button>
                <el-button
                  v-permission="'iot:device:edit'"
                  type="warning"
                  link
                  :icon="Edit"
                  @click="handleEditDevice(row)"
                >
                  编辑
                </el-button>
                <el-button
                  v-permission="'iot:device:delete'"
                  type="danger"
                  link
                  :icon="Delete"
                  @click="handleDeleteDevice(row)"
                >
                  删除
                </el-button>
                <el-button type="info" link :icon="Setting">配置</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="devicePagination.currentPage"
              v-model:page-size="devicePagination.pageSize"
              :page-sizes="[10, 20, 50]"
              :total="devicePagination.total"
              layout="total, sizes, prev, pager, next, jumper"
            />
          </div>
        </el-card>

        <!-- 新增/编辑设备对话框 -->
        <el-dialog
          v-model="deviceDialogVisible"
          :title="deviceDialogTitle"
          width="600px"
          destroy-on-close
        >
          <el-form :model="deviceForm" label-width="100px">
            <el-form-item label="设备编码" required>
              <el-input v-model="deviceForm.deviceCode" placeholder="请输入设备编码" />
            </el-form-item>
            <el-form-item label="设备名称" required>
              <el-input v-model="deviceForm.deviceName" placeholder="请输入设备名称" />
            </el-form-item>
            <el-form-item label="设备类型">
              <el-select v-model="deviceForm.deviceType" style="width: 100%">
                <el-option v-for="item in deviceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="Product Key">
              <el-input v-model="deviceForm.productKey" placeholder="请输入Product Key" />
            </el-form-item>
            <el-form-item label="安装位置">
              <el-input v-model="deviceForm.location" placeholder="请输入安装位置" />
            </el-form-item>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="经度">
                  <el-input-number v-model="deviceForm.longitude" :precision="6" :step="0.001" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="纬度">
                  <el-input-number v-model="deviceForm.latitude" :precision="6" :step="0.001" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <template #footer>
            <el-button @click="deviceDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSaveDevice">确定</el-button>
          </template>
        </el-dialog>

        <!-- 设备详情抽屉 -->
        <el-drawer
          v-model="deviceDetailVisible"
          title="设备详情"
          size="500px"
          direction="rtl"
        >
          <template v-if="currentDevice">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="设备编码">{{ currentDevice.deviceCode }}</el-descriptions-item>
              <el-descriptions-item label="设备名称">{{ currentDevice.deviceName }}</el-descriptions-item>
              <el-descriptions-item label="设备类型">
                <el-tag :type="getDeviceTypeTagType(currentDevice.deviceType)">
                  {{ getDeviceTypeLabel(currentDevice.deviceType) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="安装位置">{{ currentDevice.location }}</el-descriptions-item>
              <el-descriptions-item label="经度">{{ currentDevice.longitude }}</el-descriptions-item>
              <el-descriptions-item label="纬度">{{ currentDevice.latitude }}</el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="getStatusTagType(currentDevice.status)" effect="dark">
                  {{ getStatusLabel(currentDevice.status) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="Product Key">{{ currentDevice.productKey }}</el-descriptions-item>
              <el-descriptions-item label="最后在线时间">{{ currentDevice.lastOnlineAt }}</el-descriptions-item>
            </el-descriptions>

            <el-divider content-position="left">实时数据</el-divider>
            <div class="realtime-data-grid">
              <div class="realtime-data-item" v-for="(value, key) in deviceRealtimeData" :key="key">
                <div class="data-label">{{ key === 'temperature' ? '温度' : key === 'humidity' ? '湿度' : key === 'co2' ? 'CO2' : '土壤湿度' }}</div>
                <div class="data-value">{{ value }}{{ key === 'temperature' ? '°C' : key === 'humidity' || key === 'soilMoisture' ? '%' : 'ppm' }}</div>
              </div>
            </div>

            <el-divider content-position="left">历史数据趋势</el-divider>
            <div class="history-chart-placeholder">
              <el-empty description="暂无历史数据" :image-size="80" />
            </div>
          </template>
        </el-drawer>
      </el-tab-pane>

      <!-- 标签页3: 告警管理 -->
      <el-tab-pane label="告警管理">
        <template #label>
          <span class="tab-label">
            <el-icon><Warning /></el-icon>
            告警管理
          </span>
        </template>

        <!-- 告警统计卡片 -->
        <div class="stat-cards">
          <el-card class="stat-card stat-alarm-active" shadow="hover">
            <div class="stat-card-content">
              <div class="stat-info">
                <div class="stat-label">活跃告警</div>
                <div class="stat-value">{{ alarmStats.active }}</div>
              </div>
              <el-icon class="stat-icon" :size="48"><Warning /></el-icon>
            </div>
          </el-card>
          <el-card class="stat-card stat-alarm-ack" shadow="hover">
            <div class="stat-card-content">
              <div class="stat-info">
                <div class="stat-label">已确认</div>
                <div class="stat-value">{{ alarmStats.acknowledged }}</div>
              </div>
              <el-icon class="stat-icon" :size="48"><Check /></el-icon>
            </div>
          </el-card>
          <el-card class="stat-card stat-alarm-resolved" shadow="hover">
            <div class="stat-card-content">
              <div class="stat-info">
                <div class="stat-label">已解决</div>
                <div class="stat-value">{{ alarmStats.resolved }}</div>
              </div>
              <el-icon class="stat-icon" :size="48"><CircleCheck /></el-icon>
            </div>
          </el-card>
        </div>

        <!-- 告警规则列表 -->
        <el-card class="table-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>告警规则</span>
              <el-button
                v-permission="'iot:alarm:create'"
                type="primary"
                :icon="Plus"
                size="small"
                @click="handleAddAlarmRule"
              >
                新增规则
              </el-button>
            </div>
          </template>
          <el-table :data="alarmRules" stripe style="width: 100%" row-key="id">
            <el-table-column prop="ruleName" label="规则名称" width="150" />
            <el-table-column prop="deviceType" label="设备类型" width="120">
              <template #default="{ row }">
                <el-tag v-if="row.deviceType" :type="getDeviceTypeTagType(row.deviceType)">
                  {{ getDeviceTypeLabel(row.deviceType) }}
                </el-tag>
                <span v-else>全部</span>
              </template>
            </el-table-column>
            <el-table-column prop="deviceCode" label="指定设备" width="150">
              <template #default="{ row }">
                {{ row.deviceCode === '-' ? '全部设备' : row.deviceCode }}
              </template>
            </el-table-column>
            <el-table-column prop="metric" label="监控指标" width="120">
              <template #default="{ row }">
                {{ metricOptions.find(m => m.value === row.metric)?.label || row.metric }}
              </template>
            </el-table-column>
            <el-table-column prop="condition" label="条件" width="100">
              <template #default="{ row }">
                {{ getConditionLabel(row.condition) }}
              </template>
            </el-table-column>
            <el-table-column prop="thresholdValue" label="阈值" width="100">
              <template #default="{ row }">
                <span v-if="row.condition === 'BETWEEN'">{{ row.thresholdValue }} ~ {{ row.thresholdValue2 }}</span>
                <span v-else>{{ row.thresholdValue }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="severity" label="严重程度" width="100">
              <template #default="{ row }">
                <el-tag :type="getSeverityTagType(row.severity)" effect="dark">
                  {{ row.severity }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="enabled" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.enabled ? 'success' : 'info'">
                  {{ row.enabled ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-permission="'iot:alarm:edit'"
                  type="warning"
                  link
                  :icon="Edit"
                  @click="handleEditAlarmRule(row)"
                >
                  编辑
                </el-button>
                <el-button
                  v-permission="'iot:alarm:delete'"
                  type="danger"
                  link
                  :icon="Delete"
                  @click="handleDeleteAlarmRule(row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 告警记录表格 -->
        <el-card class="table-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>告警记录</span>
            </div>
          </template>
          <el-table :data="paginatedAlarmRecords" stripe style="width: 100%" row-key="id">
            <el-table-column prop="ruleName" label="告警规则" width="150" />
            <el-table-column prop="deviceCode" label="设备编码" width="160" />
            <el-table-column prop="metric" label="监控指标" width="120">
              <template #default="{ row }">
                {{ metricOptions.find(m => m.value === row.metric)?.label || row.metric }}
              </template>
            </el-table-column>
            <el-table-column prop="actualValue" label="实际值" width="100">
              <template #default="{ row }">
                <span :class="{ 'value-danger': row.severity === 'ERROR', 'value-warning': row.severity === 'WARNING' }">
                  {{ row.actualValue }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="thresholdValue" label="阈值" width="100" />
            <el-table-column prop="severity" label="严重程度" width="100">
              <template #default="{ row }">
                <el-tag :type="getSeverityTagType(row.severity)" effect="dark">
                  {{ row.severity }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getAlarmStatusTagType(row.status)">
                  {{ getAlarmStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="告警时间" min-width="180" sortable />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-if="row.status === 'ACTIVE'"
                  type="warning"
                  link
                  :icon="Check"
                  @click="handleAcknowledgeAlarm(row)"
                >
                  确认
                </el-button>
                <el-button
                  v-if="row.status !== 'RESOLVED'"
                  type="success"
                  link
                  :icon="CircleCheck"
                  @click="handleResolveAlarm(row)"
                >
                  解决
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="alarmRecordPagination.currentPage"
              v-model:page-size="alarmRecordPagination.pageSize"
              :page-sizes="[10, 20, 50]"
              :total="alarmRecordPagination.total"
              layout="total, sizes, prev, pager, next, jumper"
            />
          </div>
        </el-card>

        <!-- 新增/编辑告警规则对话框 -->
        <el-dialog
          v-model="alarmRuleDialogVisible"
          :title="alarmRuleDialogTitle"
          width="600px"
          destroy-on-close
        >
          <el-form :model="alarmRuleForm" label-width="100px">
            <el-form-item label="规则名称" required>
              <el-input v-model="alarmRuleForm.ruleName" placeholder="请输入规则名称" />
            </el-form-item>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="设备类型">
                  <el-select v-model="alarmRuleForm.deviceType" placeholder="全部类型" clearable style="width: 100%">
                    <el-option v-for="item in deviceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="指定设备">
                  <el-input v-model="alarmRuleForm.deviceCode" placeholder="留空表示全部设备" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="监控指标" required>
              <el-select v-model="alarmRuleForm.metric" placeholder="请选择监控指标" style="width: 100%">
                <el-option v-for="item in metricOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="触发条件">
                  <el-select v-model="alarmRuleForm.condition" style="width: 100%">
                    <el-option v-for="item in conditionOptions" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="阈值" required>
                  <el-input-number v-model="alarmRuleForm.thresholdValue" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="alarmRuleForm.condition === 'BETWEEN'">
                <el-form-item label="阈值上限">
                  <el-input-number v-model="alarmRuleForm.thresholdValue2" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="严重程度">
                  <el-select v-model="alarmRuleForm.severity" style="width: 100%">
                    <el-option v-for="item in severityOptions" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="启用状态">
                  <el-switch v-model="alarmRuleForm.enabled" active-text="启用" inactive-text="禁用" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <template #footer>
            <el-button @click="alarmRuleDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSaveAlarmRule">确定</el-button>
          </template>
        </el-dialog>
      </el-tab-pane>

      <!-- 标签页4: 设备地图 -->
      <el-tab-pane label="设备地图">
        <template #label>
          <span class="tab-label">
            <el-icon><Location /></el-icon>
            设备地图
          </span>
        </template>

        <el-card class="map-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>设备位置分布</span>
              <div class="map-legend">
                <span class="legend-item">
                  <span class="legend-dot online"></span>
                  在线
                </span>
                <span class="legend-item">
                  <span class="legend-dot offline"></span>
                  离线
                </span>
                <span class="legend-item">
                  <span class="legend-dot alarm"></span>
                  告警
                </span>
              </div>
            </div>
          </template>
          <div ref="mapContainerRef" class="map-container">
            <!-- 地图背景网格 -->
            <div class="map-grid">
              <div v-for="i in 10" :key="'h'+i" class="grid-line horizontal" :style="{ top: (i * 10) + '%' }"></div>
              <div v-for="i in 10" :key="'v'+i" class="grid-line vertical" :style="{ left: (i * 10) + '%' }"></div>
            </div>
            
            <!-- 区域标注 -->
            <div class="map-area area-a" style="left: 20%; top: 30%;">
              <div class="area-label">A区温室</div>
            </div>
            <div class="map-area area-b" style="left: 55%; top: 25%;">
              <div class="area-label">B区大棚</div>
            </div>
            <div class="map-area area-c" style="left: 70%; top: 60%;">
              <div class="area-label">C区农田</div>
            </div>
            <div class="map-area area-water" style="left: 10%; top: 70%;">
              <div class="area-label">灌溉水源</div>
            </div>

            <!-- 设备标记点 -->
            <div
              v-for="device in mapDevices"
              :key="device.id"
              class="device-marker"
              :class="{
                online: device.status === 'ONLINE',
                offline: device.status === 'OFFLINE',
              }"
              :style="{
                left: Math.min(Math.max(device.x, 5), 95) + '%',
                top: Math.min(Math.max(device.y, 5), 95) + '%',
              }"
              :title="device.deviceName + ' - ' + getStatusLabel(device.status)"
            >
              <div class="marker-icon">{{ getDeviceTypeIcon(device.deviceType) }}</div>
              <div class="marker-pulse"></div>
              <div class="marker-label">{{ device.deviceName }}</div>
            </div>
          </div>
        </el-card>

        <!-- 设备列表（按位置排序） -->
        <el-card class="table-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>设备位置信息</span>
            </div>
          </template>
          <el-table :data="deviceList" stripe style="width: 100%">
            <el-table-column prop="deviceCode" label="设备编码" width="160" />
            <el-table-column prop="deviceName" label="设备名称" width="160" />
            <el-table-column prop="deviceType" label="类型" width="120">
              <template #default="{ row }">
                <span>{{ getDeviceTypeIcon(row.deviceType) }} {{ getDeviceTypeLabel(row.deviceType) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="location" label="位置" width="150" />
            <el-table-column prop="longitude" label="经度" width="120" />
            <el-table-column prop="latitude" label="纬度" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusTagType(row.status)" effect="dark">
                  {{ getStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped>
.device-management {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  padding: 20px;
  color: #E0E0E0;
}

.page-header {
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

/* 标签页样式 */
.main-tabs {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 统计卡片 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.stat-card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: 14px;
  color: #B0B0B0;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
}

.stat-icon {
  opacity: 0.6;
}

.stat-total .stat-value { color: #409EFF; }
.stat-total .stat-icon { color: #409EFF; }
.stat-online .stat-value { color: #67C23A; }
.stat-online .stat-icon { color: #67C23A; }
.stat-offline .stat-value { color: #909399; }
.stat-offline .stat-icon { color: #909399; }
.stat-alarm .stat-value { color: #F56C6C; }
.stat-alarm .stat-icon { color: #F56C6C; }
.stat-alarm-active .stat-value { color: #F56C6C; }
.stat-alarm-active .stat-icon { color: #F56C6C; }
.stat-alarm-ack .stat-value { color: #E6A23C; }
.stat-alarm-ack .stat-icon { color: #E6A23C; }
.stat-alarm-resolved .stat-value { color: #67C23A; }
.stat-alarm-resolved .stat-icon { color: #67C23A; }

/* 图表区域 */
.chart-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
}

.chart-container {
  height: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 筛选区域 */
.filter-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

/* 操作栏 */
.action-bar {
  margin-bottom: 20px;
}

/* 表格卡片 */
.table-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  margin-bottom: 20px;
}

.alarm-list-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

/* 数值颜色 */
.value-danger {
  color: #F56C6C;
  font-weight: 600;
}

.value-warning {
  color: #E6A23C;
  font-weight: 600;
}

/* 实时数据网格 */
.realtime-data-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin: 16px 0;
}

.realtime-data-item {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  padding: 16px;
  text-align: center;
}

.realtime-data-item .data-label {
  font-size: 12px;
  color: #B0B0B0;
  margin-bottom: 8px;
}

.realtime-data-item .data-value {
  font-size: 24px;
  font-weight: 600;
  color: #409EFF;
}

.history-chart-placeholder {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 地图区域 */
.map-card {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  margin-bottom: 20px;
}

.map-container {
  position: relative;
  height: 500px;
  background: linear-gradient(135deg, #0a1628 0%, #1a2a4a 100%);
  border-radius: 8px;
  overflow: hidden;
}

.map-grid {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.grid-line {
  position: absolute;
  background: rgba(64, 158, 255, 0.1);
}

.grid-line.horizontal {
  left: 0;
  right: 0;
  height: 1px;
}

.grid-line.vertical {
  top: 0;
  bottom: 0;
  width: 1px;
}

/* 区域标注 */
.map-area {
  position: absolute;
  padding: 12px 20px;
  border-radius: 8px;
  border: 1px dashed rgba(64, 158, 255, 0.3);
  background: rgba(64, 158, 255, 0.05);
}

.area-a {
  width: 180px;
  height: 120px;
}

.area-b {
  width: 160px;
  height: 100px;
}

.area-c {
  width: 140px;
  height: 100px;
}

.area-water {
  width: 120px;
  height: 80px;
}

.area-label {
  font-size: 14px;
  color: #409EFF;
  font-weight: 500;
}

/* 设备标记 */
.device-marker {
  position: absolute;
  transform: translate(-50%, -50%);
  cursor: pointer;
  z-index: 10;
}

.marker-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(26, 26, 46, 0.9);
  border: 2px solid #67C23A;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  position: relative;
  z-index: 2;
}

.device-marker.offline .marker-icon {
  border-color: #909399;
  opacity: 0.6;
}

.marker-pulse {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(103, 194, 58, 0.3);
  animation: pulse 2s infinite;
}

.device-marker.offline .marker-pulse {
  display: none;
}

@keyframes pulse {
  0% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 1;
  }
  100% {
    transform: translate(-50%, -50%) scale(2.5);
    opacity: 0;
  }
}

.marker-label {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  margin-top: 8px;
  white-space: nowrap;
  font-size: 12px;
  color: #E0E0E0;
  background: rgba(0, 0, 0, 0.7);
  padding: 4px 8px;
  border-radius: 4px;
}

.device-marker:hover .marker-label {
  background: rgba(64, 158, 255, 0.8);
}

/* 图例 */
.map-legend {
  display: flex;
  gap: 20px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #B0B0B0;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.legend-dot.online {
  background: #67C23A;
  box-shadow: 0 0 8px #67C23A;
}

.legend-dot.offline {
  background: #909399;
}

.legend-dot.alarm {
  background: #F56C6C;
  box-shadow: 0 0 8px #F56C6C;
}

/* Element Plus 深色主题覆盖 */
:deep(.el-tabs__header) {
  background: rgba(255, 255, 255, 0.03);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

:deep(.el-tabs__item) {
  color: #B0B0B0;
}

:deep(.el-tabs__item.is-active) {
  color: #409EFF;
  background: rgba(64, 158, 255, 0.1);
}

:deep(.el-tabs__content) {
  padding: 20px;
}

:deep(.el-card) {
  background: transparent;
  border: none;
  color: #E0E0E0;
}

:deep(.el-card__header) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding: 16px 20px;
}

:deep(.el-table) {
  background: transparent;
  color: #E0E0E0;
}

:deep(.el-table tr) {
  background: transparent;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
  background: rgba(255, 255, 255, 0.03);
}

:deep(.el-table th.el-table__cell) {
  background: rgba(255, 255, 255, 0.05);
  color: #B0B0B0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

:deep(.el-table td.el-table__cell) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

:deep(.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell) {
  background: rgba(64, 158, 255, 0.1);
}

:deep(.el-pagination) {
  --el-pagination-bg-color: transparent;
  --el-pagination-text-color: #B0B0B0;
  --el-pagination-button-bg-color: rgba(255, 255, 255, 0.05);
}

:deep(.el-dialog) {
  background: #1a1a2e;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

:deep(.el-dialog__title) {
  color: #E0E0E0;
}

:deep(.el-drawer) {
  background: #1a1a2e;
}

:deep(.el-drawer__header) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  color: #E0E0E0;
}

:deep(.el-descriptions) {
  --el-descriptions-item-bordered-label-background: rgba(255, 255, 255, 0.05);
}

:deep(.el-descriptions__label) {
  color: #B0B0B0;
}

:deep(.el-descriptions__content) {
  color: #E0E0E0;
}

:deep(.el-empty__description p) {
  color: #B0B0B0;
}

:deep(.el-form-item__label) {
  color: #B0B0B0;
}

:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05);
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.1) inset;
}

:deep(.el-input__inner) {
  color: #E0E0E0;
}

:deep(.el-select .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05);
}

:deep(.el-divider__text) {
  background: #1a1a2e;
  color: #409EFF;
}

:deep(.el-divider) {
  border-color: rgba(255, 255, 255, 0.1);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .chart-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: 1fr;
  }
  
  .map-container {
    height: 350px;
  }
}
</style>
