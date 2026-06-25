<script setup>
import { ref, onMounted } from "vue";

const props = defineProps({
  data: {
    type: Array,
    default: () => [],
  },
  center: {
    type: Array,
    default: () => [104.065735, 30.659462],
  },
  zoom: {
    type: Number,
    default: 5,
  },
});

const mapContainer = ref(null);
const isLoaded = ref(false);
const selectedPoint = ref(null);

const mapPoints = ref([
  { id: 1, name: "江苏省农场", position: [118.767413, 32.041544], emissions: 1250 },
  { id: 2, name: "山东省农场", position: [117.000923, 36.675807], emissions: 980 },
  { id: 3, name: "河北省农场", position: [114.502461, 38.045474], emissions: 1100 },
  { id: 4, name: "浙江省农场", position: [120.153576, 30.287459], emissions: 850 },
  { id: 5, name: "河南省农场", position: [113.665412, 34.757975], emissions: 1050 },
]);

function handlePointClick(point) {
  selectedPoint.value = point;
}

function handleClose() {
  selectedPoint.value = null;
}

function getEmissionLevel(emissions) {
  if (emissions > 1200) return "high";
  if (emissions > 1000) return "medium";
  return "low";
}

function getEmissionColor(emissions) {
  const level = getEmissionLevel(emissions);
  const colors = {
    high: "#F56C6C",
    medium: "#E6A23C",
    low: "#67C23A",
  };
  return colors[level];
}

onMounted(() => {
  setTimeout(() => {
    isLoaded.value = true;
  }, 500);
});
</script>

<template>
  <div class="carbon-map">
    <el-alert
      title="演示数据"
      description="当前显示为模拟监测点，实际部署后将接入GPS定位的实地监测设备"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 12px;"
    />
    <div class="map-header">
      <h4>碳分布地图</h4>
      <div class="map-legend">
        <div class="legend-item">
          <span class="legend-dot high"></span>
          <span>高排放 (>1200 tCO2e)</span>
        </div>
        <div class="legend-item">
          <span class="legend-dot medium"></span>
          <span>中排放 (1000-1200 tCO2e)</span>
        </div>
        <div class="legend-item">
          <span class="legend-dot low"></span>
          <span>低排放 (<1000 tCO2e)</span>
        </div>
      </div>
    </div>

    <div class="map-container" ref="mapContainer">
      <div v-if="!isLoaded" class="map-loading">
        <el-icon class="is-loading" :size="32"><Loading /></el-icon>
        <span>地图加载中...</span>
      </div>

      <div v-else class="map-placeholder">
        <div class="map-grid">
          <div
            v-for="point in mapPoints"
            :key="point.id"
            class="map-point"
            :class="{ selected: selectedPoint?.id === point.id }"
            :style="{
              left: `${((point.position[0] - 73) / (135 - 73)) * 100}%`,
              top: `${((53 - point.position[1]) / (53 - 18)) * 100}%`,
            }"
            @click="handlePointClick(point)"
          >
            <div
              class="point-marker"
              :style="{ backgroundColor: getEmissionColor(point.emissions) }"
            >
              <div class="point-pulse"></div>
            </div>
            <div class="point-label">{{ point.name }}</div>
          </div>
        </div>

        <div v-if="selectedPoint" class="map-info-window">
          <div class="info-header">
            <h5>{{ selectedPoint.name }}</h5>
            <el-button type="primary" link @click="handleClose">
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
          <div class="info-content">
            <div class="info-item">
              <span class="label">碳排放量:</span>
              <span class="value">{{ selectedPoint.emissions }} tCO2e</span>
            </div>
            <div class="info-item">
              <span class="label">排放等级:</span>
              <el-tag
                :type="getEmissionLevel(selectedPoint.emissions) === 'high' ? 'danger' : getEmissionLevel(selectedPoint.emissions) === 'medium' ? 'warning' : 'success'"
                size="small"
              >
                {{ getEmissionLevel(selectedPoint.emissions) === 'high' ? '高' : getEmissionLevel(selectedPoint.emissions) === 'medium' ? '中' : '低' }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="map-stats">
      <div class="stat-item">
        <span class="stat-label">监测点总数</span>
        <span class="stat-value">{{ mapPoints.length }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">总排放量</span>
        <span class="stat-value">{{ mapPoints.reduce((sum, p) => sum + p.emissions, 0) }} tCO2e</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">高排放点</span>
        <span class="stat-value danger">{{ mapPoints.filter((p) => p.emissions > 1200).length }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.carbon-map {
  width: 100%;
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.map-header {
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.map-header h4 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.map-legend {
  display: flex;
  gap: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #606266;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.legend-dot.high {
  background: #F56C6C;
}

.legend-dot.medium {
  background: #E6A23C;
}

.legend-dot.low {
  background: #67C23A;
}

.map-container {
  position: relative;
  height: 400px;
  background: linear-gradient(135deg, #e8f4f8 0%, #d4e6f1 100%);
  overflow: hidden;
}

.map-loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #909399;
}

.map-placeholder {
  position: relative;
  width: 100%;
  height: 100%;
}

.map-grid {
  position: relative;
  width: 100%;
  height: 100%;
}

.map-point {
  position: absolute;
  transform: translate(-50%, -50%);
  cursor: pointer;
  z-index: 10;
}

.point-marker {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  position: relative;
  border: 2px solid white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.point-pulse {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: inherit;
  opacity: 0.3;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    transform: translate(-50%, -50%) scale(0.8);
    opacity: 0.3;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.2);
    opacity: 0.1;
  }
  100% {
    transform: translate(-50%, -50%) scale(0.8);
    opacity: 0.3;
  }
}

.point-label {
  position: absolute;
  top: -28px;
  left: 50%;
  transform: translateX(-50%);
  white-space: nowrap;
  font-size: 12px;
  color: #303133;
  background: white;
  padding: 2px 8px;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  opacity: 0;
  transition: opacity 0.2s;
}

.map-point:hover .point-label {
  opacity: 1;
}

.map-point.selected .point-label {
  opacity: 1;
  background: #409EFF;
  color: white;
}

.map-info-window {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  padding: 16px;
  min-width: 200px;
  z-index: 100;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.info-header h5 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-item .label {
  font-size: 13px;
  color: #909399;
}

.info-item .value {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.map-stats {
  display: flex;
  justify-content: space-around;
  padding: 16px;
  border-top: 1px solid #ebeef5;
  background: #fafafa;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.stat-value.danger {
  color: #F56C6C;
}
</style>