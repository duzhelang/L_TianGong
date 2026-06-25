<script setup>
import { ref, reactive, onMounted, computed, watch } from "vue";
import { useCarbonStore } from "@/stores/carbon";
import {
  MapLocation,
  Calendar,
  TrendCharts,
  DataLine,
  Box,
  Refresh,
  Download,
  Setting,
  ArrowRight,
  Warning,
} from "@element-plus/icons-vue";

const carbonStore = useCarbonStore();

const loading = ref(false);
const activeLayer = ref("distribution");
const selectedYear = ref(2024);
const yearRange = ref([2020, 2024]);

const yearOptions = [
  { label: "2020年", value: 2020 },
  { label: "2021年", value: 2021 },
  { label: "2022年", value: 2022 },
  { label: "2023年", value: 2023 },
  { label: "2024年", value: 2024 },
];

const carbonStats = ref([
  { title: "总碳储量", value: 1250, unit: "万吨", icon: Box, color: "#67C23A", trend: 3.2 },
  { title: "年碳汇量", value: 85, unit: "万吨/年", icon: TrendCharts, color: "#409EFF", trend: 5.8 },
  { title: "碳汇密度", value: 2.8, unit: "tC/ha", icon: DataLine, color: "#E6A23C", trend: 1.5 },
  { title: "潜力区面积", value: 45000, unit: "公顷", icon: MapLocation, color: "#F56C6C", trend: -2.1 },
]);

const distributionData = ref({
  regions: [
    { name: "东北地区", value: 320, percentage: 25.6 },
    { name: "华北平原", value: 280, percentage: 22.4 },
    { name: "长江流域", value: 250, percentage: 20.0 },
    { name: "华南地区", value: 200, percentage: 16.0 },
    { name: "西南地区", value: 150, percentage: 12.0 },
    { name: "西北地区", value: 50, percentage: 4.0 },
  ],
  timeSeries: {
    xAxis: ["2020", "2021", "2022", "2023", "2024"],
    series: [
      { name: "碳储量", data: [1100, 1150, 1190, 1220, 1250], color: "#67C23A" },
      { name: "碳汇量", data: [72, 76, 80, 83, 85], color: "#409EFF" },
    ],
  },
});

const potentialAreas = ref([
  { id: 1, name: "东北黑土地保护区", area: 12000, potential: 45, priority: "high", status: "待开发" },
  { id: 2, name: "华北盐碱地改良区", area: 8500, potential: 32, priority: "medium", status: "规划中" },
  { id: 3, name: "长江湿地修复区", area: 6500, potential: 28, priority: "high", status: "进行中" },
  { id: 4, name: "华南红树林保护区", area: 4500, potential: 22, priority: "low", status: "已完成" },
  { id: 5, name: "西南森林碳汇区", area: 15000, potential: 58, priority: "high", status: "待开发" },
]);

const carbonAccounting = ref([
  { category: "土壤有机碳", value: 680, percentage: 54.4, color: "#67C23A" },
  { category: "植被碳储量", value: 320, percentage: 25.6, color: "#409EFF" },
  { category: "湿地碳储量", value: 150, percentage: 12.0, color: "#E6A23C" },
  { category: "其他碳库", value: 100, percentage: 8.0, color: "#909399" },
]);

const yearComparison = computed(() => {
  const years = yearOptions.filter(y => y.value >= yearRange.value[0] && y.value <= yearRange.value[1]);
  return years.map(y => ({
    year: y.value,
    carbon: 1000 + (y.value - 2020) * 60,
    sink: 70 + (y.value - 2020) * 4,
  }));
});

function getPriorityType(priority) {
  const map = { high: "danger", medium: "warning", low: "info" };
  return map[priority] || "info";
}

function getPriorityLabel(priority) {
  const map = { high: "高优先级", medium: "中优先级", low: "低优先级" };
  return map[priority] || "未知";
}

function getStatusType(status) {
  const map = { "待开发": "warning", "规划中": "info", "进行中": "primary", "已完成": "success" };
  return map[status] || "info";
}

function handleYearChange() {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
  }, 500);
}

function handleRefresh() {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
  }, 1000);
}

function handleExport() {
  console.log("导出碳汇数据");
}

onMounted(() => {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
  }, 500);
});
</script>

<template>
  <div class="carbon-sink-map" v-loading="loading">
    <el-alert
      title="演示模式"
      description="当前显示为模拟数据，实际部署后将接入遥感监测与实地采样数据"
      type="warning"
      :closable="false"
      show-icon
      style="margin-bottom: 16px;"
    />
    <div class="page-header">
      <div class="header-left">
        <h2>碳汇一张图</h2>
        <p>土壤碳汇分布、储量变化与潜力区预测</p>
      </div>
      <div class="header-actions">
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
        <el-button :icon="Download" @click="handleExport">导出</el-button>
        <el-button :icon="Setting">设置</el-button>
      </div>
    </div>

    <div class="filter-bar">
      <div class="filter-item">
        <span class="filter-label">监测年份</span>
        <el-select v-model="selectedYear" placeholder="选择年份" @change="handleYearChange">
          <el-option v-for="item in yearOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </div>
      <div class="filter-item">
        <span class="filter-label">对比范围</span>
        <el-slider v-model="yearRange" range :min="2020" :max="2024" :marks="{2020: '2020', 2024: '2024'}" style="width: 300px;" />
      </div>
    </div>

    <div class="stats-grid">
      <div v-for="stat in carbonStats" :key="stat.title" class="stat-card">
        <div class="stat-icon" :style="{ backgroundColor: stat.color + '20', color: stat.color }">
          <el-icon :size="24"><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">
            {{ stat.value.toLocaleString() }}
            <span class="stat-unit">{{ stat.unit }}</span>
          </div>
          <div class="stat-title">{{ stat.title }}</div>
          <div class="stat-trend" :class="{ positive: stat.trend > 0, negative: stat.trend < 0 }">
            <el-icon><ArrowRight /></el-icon>
            {{ Math.abs(stat.trend) }}% 较上年
          </div>
        </div>
      </div>
    </div>

    <div class="map-section">
      <el-card class="map-card">
        <template #header>
          <div class="card-header">
            <div class="header-left">
              <el-icon><MapLocation /></el-icon>
              <span>碳汇分布热力图</span>
            </div>
            <div class="map-controls">
              <el-radio-group v-model="activeLayer" size="small">
                <el-radio-button value="distribution">碳汇分布</el-radio-button>
                <el-radio-button value="density">碳汇密度</el-radio-button>
                <el-radio-button value="potential">潜力区</el-radio-button>
              </el-radio-group>
            </div>
          </div>
        </template>
        <div class="map-container">
          <div class="map-placeholder">
            <el-icon :size="64" color="#C0C4CC"><MapLocation /></el-icon>
            <p>碳汇分布热力图</p>
            <p class="map-hint">展示全国土壤碳汇空间分布，支持多图层叠加</p>
          </div>
        </div>
      </el-card>
    </div>

    <div class="content-grid">
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>碳储量时间变化</span>
            <el-tag type="success" size="small">时间序列</el-tag>
          </div>
        </template>
        <div class="chart-container">
          <div class="chart-placeholder">
            <el-icon :size="48" color="#C0C4CC"><TrendCharts /></el-icon>
            <p>碳储量时间变化图</p>
            <p class="chart-hint">展示碳储量随时间的变化趋势</p>
          </div>
        </div>
      </el-card>

      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>区域碳汇分布</span>
            <el-tag type="info" size="small">分区统计</el-tag>
          </div>
        </template>
        <div class="chart-container">
          <div class="chart-placeholder">
            <el-icon :size="48" color="#C0C4CC"><DataLine /></el-icon>
            <p>区域碳汇分布图</p>
            <p class="chart-hint">展示各区域碳汇量对比</p>
          </div>
        </div>
      </el-card>
    </div>

    <div class="bottom-grid">
      <el-card class="potential-card">
        <template #header>
          <div class="card-header">
            <div class="header-left">
              <el-icon><Warning /></el-icon>
              <span>潜力区预测</span>
            </div>
            <el-button type="primary" link size="small">查看详情</el-button>
          </div>
        </template>

        <el-table :data="potentialAreas" style="width: 100%">
          <el-table-column prop="name" label="区域名称" min-width="150" />
          <el-table-column prop="area" label="面积(公顷)" width="120" align="center">
            <template #default="{ row }">
              {{ row.area.toLocaleString() }}
            </template>
          </el-table-column>
          <el-table-column prop="potential" label="碳汇潜力" width="120" align="center">
            <template #default="{ row }">
              <el-progress :percentage="row.potential" :show-text="false" :stroke-width="8" style="width: 80px;" />
              <span style="margin-left: 8px;">{{ row.potential }}%</span>
            </template>
          </el-table-column>
          <el-table-column prop="priority" label="优先级" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getPriorityType(row.priority)" size="small">
                {{ getPriorityLabel(row.priority) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card class="accounting-card">
        <template #header>
          <div class="card-header">
            <span>碳汇计量结果</span>
            <el-tag type="warning" size="small">分类汇总</el-tag>
          </div>
        </template>

        <div class="accounting-summary">
          <div class="summary-total">
            <div class="total-label">总碳储量</div>
            <div class="total-value">1250 <span class="total-unit">万吨</span></div>
          </div>
          <div class="summary-breakdown">
            <div v-for="item in carbonAccounting" :key="item.category" class="breakdown-item">
              <div class="breakdown-header">
                <span class="breakdown-name">{{ item.category }}</span>
                <span class="breakdown-value">{{ item.value }} 万吨</span>
              </div>
              <el-progress :percentage="item.percentage" :show-text="false" :stroke-width="8" :color="item.color" />
              <div class="breakdown-percentage">{{ item.percentage }}%</div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.carbon-sink-map {
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

.filter-bar {
  display: flex;
  gap: 32px;
  margin-bottom: 24px;
  padding: 16px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  align-items: center;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.stat-unit {
  font-size: 14px;
  color: #909399;
  font-weight: normal;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.stat-trend {
  font-size: 12px;
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-trend.positive {
  color: #67c23a;
}

.stat-trend.negative {
  color: #f56c6c;
}

.map-section {
  margin-bottom: 24px;
}

.map-card {
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

.map-container {
  height: 400px;
  background: #f5f7fa;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.map-placeholder {
  text-align: center;
  color: #909399;
}

.map-placeholder p {
  margin-top: 16px;
  font-size: 16px;
}

.map-placeholder .map-hint {
  font-size: 14px;
  margin-top: 8px;
  color: #C0C4CC;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  height: 100%;
}

.chart-container {
  height: 300px;
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

.bottom-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.potential-card,
.accounting-card {
  height: 100%;
}

.accounting-summary {
  padding: 20px;
}

.summary-total {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #67C23A 0%, #409EFF 100%);
  border-radius: 12px;
  margin-bottom: 24px;
}

.total-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 8px;
}

.total-value {
  font-size: 36px;
  font-weight: 700;
  color: white;
}

.total-unit {
  font-size: 16px;
  font-weight: normal;
}

.summary-breakdown {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.breakdown-item {
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.breakdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.breakdown-name {
  font-size: 14px;
  color: #606266;
}

.breakdown-value {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.breakdown-percentage {
  text-align: right;
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .content-grid,
  .bottom-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
    gap: 12px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>