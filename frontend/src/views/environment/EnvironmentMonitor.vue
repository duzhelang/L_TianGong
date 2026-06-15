<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { useCarbonStore } from "@/stores/carbon";
import {
  Sunny,
  Cloudy,
  Umbrella,
  Warning,
  TrendCharts,
  DataLine,
  Bell,
  Refresh,
  Download,
  Setting,
} from "@element-plus/icons-vue";

const carbonStore = useCarbonStore();

const loading = ref(false);
const activeTab = ref("parameters");

const environmentParams = ref([
  { name: "温度", value: 25.6, unit: "°C", icon: Sunny, color: "#F56C6C", trend: 2.1, status: "normal" },
  { name: "湿度", value: 68.2, unit: "%", icon: Umbrella, color: "#409EFF", trend: -1.5, status: "normal" },
  { name: "土壤温度", value: 18.3, unit: "°C", icon: Cloudy, color: "#E6A23C", trend: 0.8, status: "normal" },
  { name: "土壤湿度", value: 42.5, unit: "%", icon: Umbrella, color: "#67C23A", trend: 3.2, status: "warning" },
  { name: "光照强度", value: 45200, unit: "lux", icon: Sunny, color: "#909399", trend: -5.3, status: "normal" },
  { name: "CO2浓度", value: 412, unit: "ppm", icon: Cloudy, color: "#303133", trend: 1.2, status: "normal" },
]);

const correlationData = ref({
  xAxis: ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],
  series: [
    { name: "温度", data: [5, 8, 12, 18, 24, 28, 32, 31, 26, 18, 12, 6], color: "#F56C6C" },
    { name: "碳吸收", data: [120, 150, 280, 450, 680, 820, 750, 700, 580, 420, 280, 150], color: "#67C23A" },
  ],
});

const warnings = ref([
  { id: 1, level: "high", title: "土壤湿度过低", message: "监测点A3土壤湿度降至35%以下，可能影响作物生长", time: "10分钟前", location: "华北平原A区" },
  { id: 2, level: "medium", title: "温度异常升高", message: "监测点B7温度超过35°C，持续2小时", time: "30分钟前", location: "长江流域B区" },
  { id: 3, level: "low", title: "CO2浓度波动", message: "监测点C2 CO2浓度日波动超过50ppm", time: "1小时前", location: "华南地区C区" },
  { id: 4, level: "high", title: "土壤盐碱化预警", message: "监测点D5土壤电导率持续升高", time: "2小时前", location: "东北地区D区" },
]);

const couplingChartOption = ref({
  tooltip: { trigger: "axis" },
  legend: { data: ["温度", "碳吸收", "土壤湿度"] },
  grid: { left: "3%", right: "4%", bottom: "3%", containLabel: true },
  xAxis: { type: "category", data: ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"] },
  yAxis: [
    { type: "value", name: "温度/°C", position: "left" },
    { type: "value", name: "碳吸收/gC/m²", position: "right" },
  ],
  series: [
    { name: "温度", type: "line", data: [5, 8, 12, 18, 24, 28, 32, 31, 26, 18, 12, 6], yAxisIndex: 0, itemStyle: { color: "#F56C6C" } },
    { name: "碳吸收", type: "bar", data: [120, 150, 280, 450, 680, 820, 750, 700, 580, 420, 280, 150], yAxisIndex: 1, itemStyle: { color: "#67C23A" } },
    { name: "土壤湿度", type: "line", data: [45, 42, 48, 52, 55, 58, 62, 60, 55, 50, 48, 45], yAxisIndex: 0, itemStyle: { color: "#409EFF" }, lineStyle: { type: "dashed" } },
  ],
});

function getWarningLevel(level) {
  const map = { high: "danger", medium: "warning", low: "info" };
  return map[level] || "info";
}

function getWarningLabel(level) {
  const map = { high: "紧急", medium: "警告", low: "提示" };
  return map[level] || "提示";
}

function getStatusType(status) {
  const map = { normal: "success", warning: "warning", danger: "danger" };
  return map[status] || "info";
}

function getStatusLabel(status) {
  const map = { normal: "正常", warning: "警告", danger: "危险" };
  return map[status] || "未知";
}

function handleRefresh() {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
  }, 1000);
}

function handleExport() {
  console.log("导出环境数据");
}

onMounted(() => {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
  }, 500);
});
</script>

<template>
  <div class="environment-monitor" v-loading="loading">
    <div class="page-header">
      <div class="header-left">
        <h2>环境监测</h2>
        <p>多参数环境监测与碳过程关联分析</p>
      </div>
      <div class="header-actions">
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
        <el-button :icon="Download" @click="handleExport">导出</el-button>
        <el-button :icon="Setting">设置</el-button>
      </div>
    </div>

    <div class="params-grid">
      <div v-for="param in environmentParams" :key="param.name" class="param-card">
        <div class="param-icon" :style="{ backgroundColor: param.color + '20', color: param.color }">
          <el-icon :size="28"><component :is="param.icon" /></el-icon>
        </div>
        <div class="param-info">
          <div class="param-name">{{ param.name }}</div>
          <div class="param-value">
            {{ param.value.toLocaleString() }}
            <span class="param-unit">{{ param.unit }}</span>
          </div>
          <div class="param-trend" :class="{ positive: param.trend > 0, negative: param.trend < 0 }">
            <el-icon v-if="param.trend > 0"><TrendCharts /></el-icon>
            <el-icon v-else><TrendCharts /></el-icon>
            {{ Math.abs(param.trend) }}%
          </div>
        </div>
        <el-tag :type="getStatusType(param.status)" size="small" class="param-status">
          {{ getStatusLabel(param.status) }}
        </el-tag>
      </div>
    </div>

    <div class="main-content">
      <div class="chart-section">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>环境-碳耦合关系</span>
              <el-radio-group v-model="activeTab" size="small">
                <el-radio-button value="parameters">参数关联</el-radio-button>
                <el-radio-button value="correlation">相关性分析</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <div class="chart-placeholder">
              <el-icon :size="48" color="#C0C4CC"><DataLine /></el-icon>
              <p>环境-碳耦合关系图</p>
              <p class="chart-hint">展示温度、湿度与碳吸收的耦合关系</p>
            </div>
          </div>
        </el-card>
      </div>

      <div class="warnings-section">
        <el-card class="warnings-card">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon><Bell /></el-icon>
                <span>异常预警</span>
              </div>
              <el-badge :value="warnings.length" :max="99">
                <el-button type="primary" link size="small">全部预警</el-button>
              </el-badge>
            </div>
          </template>

          <div class="warnings-list">
            <div v-for="warning in warnings" :key="warning.id" class="warning-item">
              <div class="warning-header">
                <el-tag :type="getWarningLevel(warning.level)" size="small">
                  {{ getWarningLabel(warning.level) }}
                </el-tag>
                <span class="warning-time">{{ warning.time }}</span>
              </div>
              <div class="warning-title">{{ warning.title }}</div>
              <div class="warning-message">{{ warning.message }}</div>
              <div class="warning-location">
                <el-icon><MapLocation /></el-icon>
                {{ warning.location }}
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <div class="analysis-section">
      <el-card class="analysis-card">
        <template #header>
          <div class="card-header">
            <span>碳过程关联分析</span>
            <el-button type="primary" link size="small">查看详情</el-button>
          </div>
        </template>
        <div class="analysis-grid">
          <div class="analysis-item">
            <div class="analysis-title">温度-碳吸收相关性</div>
            <div class="analysis-value">0.85</div>
            <div class="analysis-desc">强正相关</div>
            <el-progress :percentage="85" :show-text="false" :stroke-width="8" status="success" />
          </div>
          <div class="analysis-item">
            <div class="analysis-title">湿度-碳吸收相关性</div>
            <div class="analysis-value">0.62</div>
            <div class="analysis-desc">中等正相关</div>
            <el-progress :percentage="62" :show-text="false" :stroke-width="8" status="warning" />
          </div>
          <div class="analysis-item">
            <div class="analysis-title">土壤温度-碳吸收相关性</div>
            <div class="analysis-value">0.78</div>
            <div class="analysis-desc">强正相关</div>
            <el-progress :percentage="78" :show-text="false" :stroke-width="8" status="success" />
          </div>
          <div class="analysis-item">
            <div class="analysis-title">CO2浓度-碳吸收相关性</div>
            <div class="analysis-value">0.45</div>
            <div class="analysis-desc">弱正相关</div>
            <el-progress :percentage="45" :show-text="false" :stroke-width="8" status="exception" />
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.environment-monitor {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}

.page-header p {
  color: #909399;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.params-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.param-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: relative;
}

.param-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.param-info {
  flex: 1;
}

.param-name {
  font-size: 14px;
  color: #909399;
  margin-bottom: 4px;
}

.param-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.param-unit {
  font-size: 14px;
  color: #909399;
  font-weight: normal;
}

.param-trend {
  font-size: 12px;
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.param-trend.positive {
  color: #f56c6c;
}

.param-trend.negative {
  color: #67c23a;
}

.param-status {
  position: absolute;
  top: 12px;
  right: 12px;
}

.main-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card,
.warnings-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header .header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-container {
  height: 350px;
  background: #f5f7fa;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-placeholder {
  text-align: center;
  color: #909399;
}

.chart-placeholder p {
  margin-top: 12px;
  font-size: 16px;
}

.chart-placeholder .chart-hint {
  font-size: 14px;
  margin-top: 8px;
  color: #C0C4CC;
}

.warnings-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-height: 350px;
  overflow-y: auto;
}

.warning-item {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  border-left: 4px solid #E6A23C;
}

.warning-item:has(.el-tag--danger) {
  border-left-color: #F56C6C;
}

.warning-item:has(.el-tag--info) {
  border-left-color: #909399;
}

.warning-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.warning-time {
  font-size: 12px;
  color: #C0C4CC;
}

.warning-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.warning-message {
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
  line-height: 1.5;
}

.warning-location {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.analysis-section {
  margin-bottom: 24px;
}

.analysis-card {
  height: 100%;
}

.analysis-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.analysis-item {
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  text-align: center;
}

.analysis-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
}

.analysis-value {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.analysis-desc {
  font-size: 12px;
  color: #909399;
  margin-bottom: 12px;
}

@media (max-width: 1200px) {
  .params-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .main-content {
    grid-template-columns: 1fr;
  }

  .analysis-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .params-grid {
    grid-template-columns: 1fr;
  }

  .analysis-grid {
    grid-template-columns: 1fr;
  }
}
</style>