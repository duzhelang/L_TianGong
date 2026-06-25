<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { useRemoteSensingStore } from "@/stores/remote-sensing";
import NDVIChart from "@/components/remote-sensing/NDVIChart.vue";
import NPPHeatmap from "@/components/remote-sensing/NPPHeatmap.vue";
import LandUsePie from "@/components/remote-sensing/LandUsePie.vue";
import BiomassCard from "@/components/remote-sensing/BiomassCard.vue";
import {
  MapLocation,
  Calendar,
  Search,
  Refresh,
  Download,
  DataLine,
  TrendCharts,
  Picture,
  Box,
} from "@element-plus/icons-vue";

const store = useRemoteSensingStore();

const loading = ref(false);
const activeTab = ref("ndvi");

const timeRange = reactive({
  start: "2024-01-01",
  end: "2024-12-31",
});

const selectedRegion = ref("all");

const regionOptions = [
  { label: "全部区域", value: "all" },
  { label: "华北平原", value: "huabei" },
  { label: "东北地区", value: "dongbei" },
  { label: "长江流域", value: "changjiang" },
  { label: "华南地区", value: "huanan" },
];

const mapPlaceholder = ref(true);

const ndviData = computed(() => store.ndviData);
const nppData = computed(() => store.nppData);
const landUseData = computed(() => store.landUseData);
const biomassData = computed(() => store.biomassData);

const stats = ref([
  { title: "平均NDVI", value: 0.72, unit: "", icon: DataLine, color: "#67C23A" },
  { title: "年均NPP", value: 856, unit: "gC/m²", icon: TrendCharts, color: "#409EFF" },
  { title: "植被覆盖度", value: 68.5, unit: "%", icon: Picture, color: "#E6A23C" },
  { title: "总碳储量", value: 1250, unit: "万吨", icon: Box, color: "#F56C6C" },
]);

async function fetchData() {
  loading.value = true;
  try {
    await Promise.all([
      store.fetchNDVIData(timeRange, selectedRegion.value),
      store.fetchNPPData(timeRange, selectedRegion.value),
      store.fetchLandUseData(selectedRegion.value),
      store.fetchBiomassData(selectedRegion.value),
    ]);
  } catch (error) {
    console.error("获取遥感数据失败", error);
  } finally {
    loading.value = false;
  }
}

function handleTimeChange() {
  fetchData();
}

function handleRegionChange() {
  fetchData();
}

function handleRefresh() {
  fetchData();
}

function handleExport() {
  console.log("导出数据");
}

onMounted(() => {
  fetchData();
});
</script>

<template>
  <div class="remote-sensing" v-loading="loading">
    <div class="page-header">
      <div class="header-left">
        <h2>遥感监测</h2>
        <p>多源遥感数据融合与碳汇反演分析</p>
      </div>
      <div class="header-actions">
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
        <el-button :icon="Download" @click="handleExport">导出</el-button>
      </div>
    </div>

    <div class="filter-bar">
      <div class="filter-item">
        <span class="filter-label">时间范围</span>
        <el-date-picker
          v-model="timeRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="handleTimeChange"
        />
      </div>
      <div class="filter-item">
        <span class="filter-label">监测区域</span>
        <el-select
          v-model="selectedRegion"
          placeholder="选择区域"
          @change="handleRegionChange"
        >
          <el-option
            v-for="item in regionOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </div>
    </div>

    <div class="stats-grid">
      <div v-for="stat in stats" :key="stat.title" class="stat-card">
        <div
          class="stat-icon"
          :style="{ backgroundColor: stat.color + '20', color: stat.color }"
        >
          <el-icon :size="24"><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">
            {{ typeof stat.value === 'number' ? stat.value.toLocaleString() : stat.value }}
            <span class="stat-unit">{{ stat.unit }}</span>
          </div>
          <div class="stat-title">{{ stat.title }}</div>
        </div>
      </div>
    </div>

    <div class="map-section">
      <el-card class="map-card">
        <template #header>
          <div class="card-header">
            <div class="header-left">
              <el-icon><MapLocation /></el-icon>
              <span>遥感监测地图</span>
            </div>
            <div class="map-controls">
              <el-radio-group v-model="activeTab" size="small">
                <el-radio-button value="ndvi">NDVI</el-radio-button>
                <el-radio-button value="npp">NPP</el-radio-button>
                <el-radio-button value="landuse">土地利用</el-radio-button>
              </el-radio-group>
            </div>
          </div>
        </template>
        <div class="map-container">
          <div class="map-placeholder">
            <el-icon :size="64" color="#C0C4CC"><MapLocation /></el-icon>
            <p>遥感监测地图区域</p>
            <p class="map-hint">待接入卫星遥感数据源，支持NDVI、NPP、土地利用等多图层叠加显示</p>
            <el-tag type="warning" effect="plain" style="margin-top: 12px;">实地部署后接入</el-tag>
          </div>
        </div>
      </el-card>
    </div>

    <div class="charts-grid">
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>NDVI时序变化</span>
            <el-tag type="success" size="small">归一化植被指数</el-tag>
          </div>
        </template>
        <NDVIChart :data="ndviData" height="300px" />
      </el-card>

      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>NPP空间分布</span>
            <el-tag type="warning" size="small">净初级生产力</el-tag>
          </div>
        </template>
        <NPPHeatmap :data="nppData" height="300px" />
      </el-card>
    </div>

    <div class="bottom-grid">
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>土地利用分类</span>
            <el-tag type="info" size="small">分类统计</el-tag>
          </div>
        </template>
        <LandUsePie :data="landUseData" height="300px" />
      </el-card>

      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>生物量估算</span>
            <el-tag type="danger" size="small">碳储量换算</el-tag>
          </div>
        </template>
        <BiomassCard :data="biomassData" />
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.remote-sensing {
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
  gap: 24px;
  margin-bottom: 24px;
  padding: 16px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
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

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.bottom-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  height: 100%;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts-grid,
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