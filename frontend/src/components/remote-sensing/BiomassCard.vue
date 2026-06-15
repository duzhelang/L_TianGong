<script setup>
import { ref, computed } from "vue";
import {
  Box,
  TrendCharts,
  DataLine,
  ArrowRight,
} from "@element-plus/icons-vue";

const props = defineProps({
  data: {
    type: Object,
    default: () => ({}),
  },
});

const defaultData = {
  totalBiomass: 12500,
  carbonContent: 0.45,
  carbonStorage: 5625,
  carbonDensity: 2.8,
  annualSequestration: 850,
  trend: 3.2,
  regions: [
    { name: "东北地区", biomass: 3200, carbon: 1440 },
    { name: "华北平原", biomass: 2800, carbon: 1260 },
    { name: "长江流域", biomass: 2500, carbon: 1125 },
    { name: "华南地区", biomass: 2000, carbon: 900 },
    { name: "西南地区", biomass: 1500, carbon: 675 },
    { name: "西北地区", biomass: 500, carbon: 225 },
  ],
};

const biomassData = computed(() => {
  return props.data?.totalBiomass ? props.data : defaultData;
});

const stats = computed(() => [
  {
    title: "总生物量",
    value: biomassData.value.totalBiomass,
    unit: "万吨",
    icon: Box,
    color: "#67C23A",
  },
  {
    title: "碳含量系数",
    value: biomassData.value.carbonContent,
    unit: "",
    icon: DataLine,
    color: "#409EFF",
  },
  {
    title: "碳储量",
    value: biomassData.value.carbonStorage,
    unit: "万吨",
    icon: TrendCharts,
    color: "#E6A23C",
  },
  {
    title: "年固碳量",
    value: biomassData.value.annualSequestration,
    unit: "万吨/年",
    icon: ArrowRight,
    color: "#F56C6C",
  },
]);
</script>

<template>
  <div class="biomass-card">
    <div class="stats-grid">
      <div v-for="stat in stats" :key="stat.title" class="stat-item">
        <div class="stat-icon" :style="{ backgroundColor: stat.color + '20', color: stat.color }">
          <el-icon :size="20"><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">
            {{ typeof stat.value === 'number' ? stat.value.toLocaleString() : stat.value }}
            <span class="stat-unit">{{ stat.unit }}</span>
          </div>
          <div class="stat-title">{{ stat.title }}</div>
        </div>
      </div>
    </div>

    <div class="trend-section">
      <div class="trend-header">
        <span class="trend-label">固碳趋势</span>
        <span class="trend-value" :class="{ positive: biomassData.trend > 0 }">
          <el-icon><ArrowRight /></el-icon>
          {{ Math.abs(biomassData.trend) }}%
        </span>
      </div>
      <el-progress
        :percentage="75"
        :show-text="false"
        :stroke-width="8"
        status="success"
      />
    </div>

    <div class="region-list">
      <div class="region-header">
        <span>区域分布</span>
        <span class="region-total">总计: {{ biomassData.totalBiomass.toLocaleString() }} 万吨</span>
      </div>
      <div v-for="region in biomassData.regions" :key="region.name" class="region-item">
        <div class="region-info">
          <span class="region-name">{{ region.name }}</span>
          <span class="region-value">{{ region.biomass.toLocaleString() }} 万吨</span>
        </div>
        <el-progress
          :percentage="(region.biomass / biomassData.totalBiomass) * 100"
          :show-text="false"
          :stroke-width="6"
          :color="region.biomass > 2000 ? '#67C23A' : '#409EFF'"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.biomass-card {
  padding: 16px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.stat-unit {
  font-size: 12px;
  color: #909399;
  font-weight: normal;
}

.stat-title {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.trend-section {
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.trend-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.trend-label {
  font-size: 14px;
  color: #606266;
}

.trend-value {
  font-size: 14px;
  font-weight: 500;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.trend-value.positive {
  color: #67c23a;
}

.region-list {
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.region-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
  color: #606266;
}

.region-total {
  font-size: 12px;
  color: #909399;
}

.region-item {
  margin-bottom: 12px;
}

.region-item:last-child {
  margin-bottom: 0;
}

.region-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.region-name {
  font-size: 13px;
  color: #606266;
}

.region-value {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
}
</style>