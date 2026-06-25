<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  FullScreen,
  Refresh,
  View,
  Hide
} from '@element-plus/icons-vue'
import SceneViewer from '@/components/digital-twin/SceneViewer.vue'
import DataPanel from '@/components/digital-twin/DataPanel.vue'
import Timeline from '@/components/digital-twin/Timeline.vue'

// 状态管理
const loading = ref(false)
const activeScene = ref('farm')
const activeLayers = ref(['carbonEmission', 'carbonSink', 'devices', 'monitoringPoints'])
const currentTime = ref(new Date())
const timeRange = ref([new Date(Date.now() - 7 * 24 * 60 * 60 * 1000), new Date()])
const isPlaying = ref(false)
const playbackSpeed = ref(1)
const isFullscreen = ref(false)
const showRightPanel = ref(true)
const showTimeline = ref(true)
const isMobile = ref(false)

// 场景数据
const scenes = {
  farm: {
    name: '示范农场',
    icon: '🌾',
    description: '农业碳排放监测'
  },
  forest: {
    name: '林地场景',
    icon: '🌲',
    description: '森林碳汇监测'
  },
  industrial: {
    name: '工业场景',
    icon: '🏭',
    description: '工业碳排放监测'
  }
}

// Mock数据
const mockSceneData = {
  farm: {
    name: '示范农场',
    area: 500,
    carbonEmission: 1250.5,
    carbonSink: 890.2,
    netEmission: 360.3,
    devices: [
      { id: 1, name: '气象站A', type: 'WEATHER', status: 'online', x: 20, y: 30, data: { temp: 25.6, humidity: 65, co2: 412 } },
      { id: 2, name: '土壤监测B', type: 'SOIL', status: 'online', x: 45, y: 55, data: { moisture: 28, ph: 6.5, n: 45 } },
      { id: 3, name: '气体分析仪C', type: 'GAS', status: 'offline', x: 70, y: 40, data: { ch4: 1.8, n2o: 0.3 } }
    ],
    monitoringPoints: [
      { id: 1, name: '水稻田A区', emission: 320.5, sink: 180.2 },
      { id: 2, name: '蔬菜大棚B区', emission: 150.3, sink: 95.8 },
      { id: 3, name: '果园C区', emission: 85.2, sink: 220.5 }
    ],
    alerts: [
      { id: 1, level: 'warning', message: '水稻田A区甲烷浓度偏高', time: '10:30' },
      { id: 2, level: 'info', message: '气象站A数据更新', time: '10:25' },
      { id: 3, level: 'error', message: '气体分析仪C离线', time: '09:45' }
    ],
    realtimeData: {
      temperature: { value: 25.6, unit: '°C', trend: 'up' },
      humidity: { value: 65, unit: '%', trend: 'stable' },
      co2: { value: 412, unit: 'ppm', trend: 'up' },
      windSpeed: { value: 3.2, unit: 'm/s', trend: 'down' }
    }
  },
  forest: {
    name: '林地场景',
    area: 1200,
    carbonEmission: 320.8,
    carbonSink: 2850.6,
    netEmission: -2529.8,
    devices: [
      { id: 4, name: '通量塔A', type: 'FLUX', status: 'online', x: 35, y: 25, data: { nee: -2.5, gpp: 8.2 } },
      { id: 5, name: '土壤呼吸监测B', type: 'SOIL', status: 'online', x: 60, y: 65, data: { respiration: 3.8, moisture: 32 } }
    ],
    monitoringPoints: [
      { id: 4, name: '针叶林区', emission: 120.5, sink: 1250.8 },
      { id: 5, name: '阔叶林区', emission: 95.2, sink: 980.5 },
      { id: 6, name: '混交林区', emission: 105.1, sink: 619.3 }
    ],
    alerts: [
      { id: 4, level: 'info', message: '通量塔A数据正常', time: '10:20' },
      { id: 5, level: 'warning', message: '土壤呼吸监测B电池电量低', time: '09:30' }
    ],
    realtimeData: {
      temperature: { value: 22.3, unit: '°C', trend: 'stable' },
      humidity: { value: 78, unit: '%', trend: 'up' },
      co2: { value: 385, unit: 'ppm', trend: 'down' },
      windSpeed: { value: 2.1, unit: 'm/s', trend: 'stable' }
    }
  },
  industrial: {
    name: '工业场景',
    area: 80,
    carbonEmission: 8500.2,
    carbonSink: 120.5,
    netEmission: 8379.7,
    devices: [
      { id: 6, name: '烟气分析仪A', type: 'GAS', status: 'online', x: 25, y: 45, data: { co2: 12500, so2: 45, nox: 120 } },
      { id: 7, name: '能耗监测B', type: 'ENERGY', status: 'online', x: 55, y: 30, data: { power: 850, steam: 12.5 } },
      { id: 8, name: '粉尘监测C', type: 'DUST', status: 'online', x: 80, y: 60, data: { pm25: 35, pm10: 68 } }
    ],
    monitoringPoints: [
      { id: 7, name: '1号生产线', emission: 3200.5, sink: 45.2 },
      { id: 8, name: '2号生产线', emission: 2850.3, sink: 35.8 },
      { id: 9, name: '锅炉房', emission: 2449.4, sink: 39.5 }
    ],
    alerts: [
      { id: 6, level: 'error', message: '1号生产线排放超标', time: '10:15' },
      { id: 7, level: 'warning', message: '锅炉房温度异常', time: '10:10' },
      { id: 8, level: 'info', message: '粉尘监测C数据更新', time: '10:05' }
    ],
    realtimeData: {
      temperature: { value: 45.2, unit: '°C', trend: 'up' },
      humidity: { value: 42, unit: '%', trend: 'down' },
      co2: { value: 12500, unit: 'ppm', trend: 'up' },
      windSpeed: { value: 1.8, unit: 'm/s', trend: 'stable' }
    }
  }
}

// 当前场景数据
const currentSceneData = computed(() => {
  return mockSceneData[activeScene.value]
})

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

// 告警级别颜色
const alertLevelColors = {
  error: '#F56C6C',
  warning: '#E6A23C',
  info: '#909399'
}

// 时间格式化
function formatTime(date) {
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

function formatDate(date) {
  return date.toLocaleDateString('zh-CN')
}

// 全屏切换
function toggleFullscreen() {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    document.exitFullscreen()
    isFullscreen.value = false
  }
}

// 场景切换
function switchScene(scene) {
  activeScene.value = scene
  ElMessage.success(`已切换到${scenes[scene].name}`)
}

// 图层切换
function toggleLayer(layer) {
  const index = activeLayers.value.indexOf(layer)
  if (index > -1) {
    activeLayers.value.splice(index, 1)
  } else {
    activeLayers.value.push(layer)
  }
}

// 播放控制
function togglePlayback() {
  isPlaying.value = !isPlaying.value
  if (isPlaying.value) {
    ElMessage.success('开始播放历史数据')
  } else {
    ElMessage.info('暂停播放')
  }
}

// 速度控制
function changeSpeed(speed) {
  playbackSpeed.value = speed
  ElMessage.info(`播放速度调整为 ${speed}x`)
}

// 设备点击
function onDeviceClick(device) {
  ElMessageBox.alert(
    `<div>
      <p><strong>设备名称：</strong>${device.name}</p>
      <p><strong>设备类型：</strong>${device.type}</p>
      <p><strong>状态：</strong>${device.status === 'online' ? '在线' : '离线'}</p>
      <p><strong>数据：</strong></p>
      ${Object.entries(device.data).map(([key, value]) => `<p>${key}: ${value}</p>`).join('')}
    </div>`,
    '设备详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '确定'
    }
  )
}

// 监测点点击
function onMonitoringPointClick(point) {
  ElMessageBox.alert(
    `<div>
      <p><strong>监测点：</strong>${point.name}</p>
      <p><strong>碳排放：</strong>${point.emission} tCO2e</p>
      <p><strong>碳汇：</strong>${point.sink} tCO2e</p>
      <p><strong>净排放：</strong>${(point.emission - point.sink).toFixed(1)} tCO2e</p>
    </div>`,
    '监测点详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '确定'
    }
  )
}

// 刷新数据
function refreshData() {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    ElMessage.success('数据已刷新')
  }, 1000)
}

// 响应式检测
function checkMobile() {
  isMobile.value = window.innerWidth < 768
  if (isMobile.value) {
    showRightPanel.value = false
  }
}

// 窗口大小变化处理
function handleResize() {
  checkMobile()
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<template>
  <div class="digital-twin-container" v-loading="loading">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <h2 class="toolbar-title">数字孪生可视化</h2>
        <div class="scene-selector">
          <el-radio-group v-model="activeScene" @change="switchScene" size="small">
            <el-radio-button v-for="(scene, key) in scenes" :key="key" :value="key">
              <span class="scene-icon">{{ scene.icon }}</span>
              {{ scene.name }}
            </el-radio-button>
          </el-radio-group>
        </div>
      </div>
      <div class="toolbar-center">
        <div class="layer-controls">
          <el-checkbox-group v-model="activeLayers" size="small">
            <el-checkbox value="carbonEmission" label="碳排放层" />
            <el-checkbox value="carbonSink" label="碳汇层" />
            <el-checkbox value="devices" label="设备层" />
            <el-checkbox value="monitoringPoints" label="监测点层" />
          </el-checkbox-group>
        </div>
      </div>
      <div class="toolbar-right">
        <el-button :icon="Refresh" circle @click="refreshData" />
        <el-button :icon="FullScreen" circle @click="toggleFullscreen" />
        <el-button :icon="showRightPanel ? Hide : View" circle @click="showRightPanel = !showRightPanel" />
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 可视化区域 -->
      <div class="visualization-area">
        <SceneViewer 
          :scene-data="currentSceneData"
          :active-layers="activeLayers"
          :is-mobile="isMobile"
          @device-click="onDeviceClick"
          @point-click="onMonitoringPointClick"
        />
      </div>

      <!-- 右侧面板 -->
      <DataPanel 
        :scene-data="currentSceneData"
        :show-panel="showRightPanel"
        @toggle-panel="showRightPanel = !showRightPanel"
      />
    </div>

    <!-- 底部时间轴 -->
    <Timeline 
      v-if="showTimeline"
      :current-time="currentTime"
      :time-range="timeRange"
      :is-playing="isPlaying"
      :playback-speed="playbackSpeed"
      @update:currentTime="currentTime = $event"
      @toggle-playback="togglePlayback"
      @change-speed="changeSpeed"
    />
  </div>
</template>

<style scoped>
.digital-twin-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  color: #fff;
  overflow: hidden;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.toolbar-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.scene-selector .el-radio-button__inner {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.scene-icon {
  margin-right: 6px;
}

.toolbar-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.layer-controls .el-checkbox__label {
  color: #fff;
}

.toolbar-right {
  display: flex;
  gap: 8px;
}

.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.visualization-area {
  flex: 1;
  position: relative;
  overflow: hidden;
}






</style>