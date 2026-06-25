<script setup>
const props = defineProps({
  sceneData: {
    type: Object,
    required: true
  },
  activeLayers: {
    type: Array,
    default: () => ['carbonEmission', 'carbonSink', 'devices', 'monitoringPoints']
  },
  isMobile: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['device-click', 'point-click'])

// 设备类型图标
const deviceTypeIcons = {
  WEATHER: '🌤️',
  SOIL: '🌱',
  GAS: '💨',
  FLUX: '📡',
  ENERGY: '⚡',
  DUST: '🌫️'
}

// 设备状态颜色
const statusColors = {
  online: '#67C23A',
  offline: '#F56C6C',
  warning: '#E6A23C'
}

// 设备点击
function onDeviceClick(device) {
  emit('device-click', device)
}

// 监测点点击
function onMonitoringPointClick(point) {
  emit('point-click', point)
}
</script>

<template>
  <div class="scene-container" :class="{ 'mobile': isMobile }">
    <!-- 地形基底 -->
    <div class="terrain">
      <!-- 热力图层 -->
      <div v-if="activeLayers.includes('carbonEmission')" class="heatmap-layer">
        <div v-for="(point, index) in sceneData.monitoringPoints" :key="'heat-' + index"
          class="heatmap-point"
          :style="{
            left: `${30 + index * 25}%`,
            top: `${30 + index * 15}%`,
            background: `radial-gradient(circle, ${point.emission > 200 ? 'rgba(245, 108, 108, 0.6)' : 'rgba(230, 162, 60, 0.4)'} 0%, transparent 70%)`
          }">
        </div>
      </div>

      <!-- 碳汇分布点 -->
      <div v-if="activeLayers.includes('carbonSink')" class="sink-points">
        <div v-for="(point, index) in sceneData.monitoringPoints" :key="'sink-' + index"
          class="sink-point"
          :style="{
            left: `${25 + index * 20}%`,
            top: `${40 + index * 10}%`
          }"
          @click="onMonitoringPointClick(point)">
          <div class="sink-marker" :title="point.name">
            <span class="sink-icon">🌳</span>
            <span class="sink-value">{{ point.sink }} tCO2e</span>
          </div>
        </div>
      </div>

      <!-- IoT设备标记 -->
      <div v-if="activeLayers.includes('devices')" class="device-markers">
        <div v-for="device in sceneData.devices" :key="device.id"
          class="device-marker"
          :class="{ 'offline': device.status === 'offline' }"
          :style="{
            left: `${device.x}%`,
            top: `${device.y}%`
          }"
          @click="onDeviceClick(device)">
          <div class="device-icon-wrapper">
            <span class="device-icon">{{ deviceTypeIcons[device.type] }}</span>
            <span class="device-status" :style="{ backgroundColor: statusColors[device.status] }"></span>
          </div>
          <div class="device-tooltip">
            <div class="tooltip-header">{{ device.name }}</div>
            <div class="tooltip-content">
              <div v-for="(value, key) in device.data" :key="key" class="tooltip-item">
                <span class="item-key">{{ key }}:</span>
                <span class="item-value">{{ value }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 监测点数据卡片 -->
      <div v-if="activeLayers.includes('monitoringPoints')" class="monitoring-cards">
        <div v-for="(point, index) in sceneData.monitoringPoints" :key="'card-' + index"
          class="monitoring-card"
          :style="{
            left: `${20 + index * 25}%`,
            top: `${20 + index * 20}%`
          }"
          @click="onMonitoringPointClick(point)">
          <div class="card-header">{{ point.name }}</div>
          <div class="card-content">
            <div class="card-item">
              <span class="card-label">排放</span>
              <span class="card-value emission">{{ point.emission }}</span>
            </div>
            <div class="card-item">
              <span class="card-label">碳汇</span>
              <span class="card-value sink">{{ point.sink }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.scene-container {
  width: 100%;
  height: 100%;
  perspective: 1000px;
  perspective-origin: 50% 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.scene-container.mobile {
  perspective: none;
}

.terrain {
  width: 80%;
  height: 80%;
  position: relative;
  transform: rotateX(60deg) rotateZ(-30deg);
  transform-style: preserve-3d;
  background: linear-gradient(45deg, #2d5016 0%, #4a7c23 50%, #6b8e3a 100%);
  border-radius: 20px;
  box-shadow: 
    0 20px 60px rgba(0, 0, 0, 0.5),
    inset 0 0 100px rgba(0, 0, 0, 0.2);
}

.scene-container.mobile .terrain {
  transform: none;
  width: 95%;
  height: 90%;
}

.heatmap-layer {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.heatmap-point {
  position: absolute;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  filter: blur(20px);
  animation: pulse 3s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.7; }
  50% { transform: scale(1.2); opacity: 1; }
}

.sink-points {
  position: absolute;
  width: 100%;
  height: 100%;
}

.sink-point {
  position: absolute;
  cursor: pointer;
  transform: translate(-50%, -50%);
}

.sink-marker {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px;
  background: rgba(103, 194, 58, 0.3);
  border-radius: 8px;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(103, 194, 58, 0.5);
  transition: all 0.3s ease;
}

.sink-marker:hover {
  transform: scale(1.1);
  background: rgba(103, 194, 58, 0.5);
}

.sink-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.sink-value {
  font-size: 12px;
  color: #67C23A;
  font-weight: 600;
}

.device-markers {
  position: absolute;
  width: 100%;
  height: 100%;
}

.device-marker {
  position: absolute;
  cursor: pointer;
  transform: translate(-50%, -50%);
  z-index: 10;
}

.device-marker.offline {
  opacity: 0.6;
}

.device-icon-wrapper {
  position: relative;
  width: 40px;
  height: 40px;
  background: rgba(64, 158, 255, 0.3);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(5px);
  border: 2px solid rgba(64, 158, 255, 0.6);
  transition: all 0.3s ease;
}

.device-icon-wrapper:hover {
  transform: scale(1.2);
  background: rgba(64, 158, 255, 0.5);
}

.device-icon {
  font-size: 20px;
}

.device-status {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid #1a1a2e;
  animation: blink 2s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.device-tooltip {
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 8px;
  padding: 12px;
  min-width: 150px;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  z-index: 100;
}

.device-marker:hover .device-tooltip {
  opacity: 1;
  visibility: visible;
  transform: translateX(-50%) translateY(-10px);
}

.tooltip-header {
  font-weight: 600;
  margin-bottom: 8px;
  color: #409EFF;
}

.tooltip-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
  font-size: 12px;
}

.item-key {
  color: #909399;
}

.item-value {
  color: #fff;
  font-weight: 500;
}

.monitoring-cards {
  position: absolute;
  width: 100%;
  height: 100%;
}

.monitoring-card {
  position: absolute;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 16px;
  min-width: 140px;
  cursor: pointer;
  transform: translate(-50%, -50%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.monitoring-card:hover {
  transform: translate(-50%, -50%) scale(1.05);
  border-color: rgba(64, 158, 255, 0.5);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.card-header {
  font-weight: 600;
  margin-bottom: 12px;
  color: #E6A23C;
  font-size: 14px;
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.card-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-label {
  font-size: 12px;
  color: #909399;
}

.card-value {
  font-weight: 600;
  font-size: 14px;
}

.card-value.emission {
  color: #F56C6C;
}

.card-value.sink {
  color: #67C23A;
}
</style>