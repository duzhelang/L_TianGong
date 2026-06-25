<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'

import {
  Cloudy,
  TrendCharts,
  DataAnalysis,
  Cpu,
  Warning
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const props = defineProps({
  sceneData: {
    type: Object,
    required: true
  },
  showPanel: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['toggle-panel'])

// 历史趋势图
const trendChartRef = ref(null)
let trendChart = null

// 告警级别颜色
const alertLevelColors = {
  error: '#F56C6C',
  warning: '#E6A23C',
  info: '#909399'
}

// 初始化趋势图
function initTrendChart() {
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
    updateChart()
  }
}

// 更新图表数据
function updateChart() {
  if (!trendChart) return
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['碳排放', '碳汇'],
      bottom: 0,
      textStyle: {
        color: '#fff'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      axisLabel: {
        color: '#909399'
      },
      axisLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.2)'
        }
      }
    },
    yAxis: {
      type: 'value',
      name: 'tCO2e',
      nameTextStyle: {
        color: '#909399'
      },
      axisLabel: {
        color: '#909399'
      },
      axisLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.2)'
        }
      },
      splitLine: {
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.1)'
        }
      }
    },
    series: [
      {
        name: '碳排放',
        type: 'line',
        smooth: true,
        data: [120, 132, 101, 134, 90, 230, 210],
        itemStyle: {
          color: '#F56C6C'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(245, 108, 108, 0.3)' },
              { offset: 1, color: 'rgba(245, 108, 108, 0.05)' }
            ]
          }
        }
      },
      {
        name: '碳汇',
        type: 'line',
        smooth: true,
        data: [80, 92, 71, 84, 60, 120, 110],
        itemStyle: {
          color: '#67C23A'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
              { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
            ]
          }
        }
      }
    ]
  }
  trendChart.setOption(option)
}

// 监听场景数据变化
watch(() => props.sceneData, () => {
  updateChart()
}, { deep: true })

// 窗口大小变化处理
function handleResize() {
  if (trendChart) {
    trendChart.resize()
  }
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
  initTrendChart()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (trendChart) {
    trendChart.dispose()
    trendChart = null
  }
})
</script>

<template>
  <div v-show="showPanel" class="right-panel" :class="{ 'collapsed': !showPanel }">
    <div class="panel-content">
      <!-- 场景概览卡片 -->
      <div class="scene-overview">
        <h3 class="panel-title">{{ sceneData.name }}概览</h3>
        <div class="overview-grid">
          <div class="overview-item">
            <div class="item-icon emission">
              <el-icon><Cloudy /></el-icon>
            </div>
            <div class="item-info">
              <div class="item-label">总碳排放</div>
              <div class="item-value">{{ sceneData.carbonEmission }} <small>tCO2e</small></div>
            </div>
          </div>
          <div class="overview-item">
            <div class="item-icon sink">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="item-info">
              <div class="item-label">总碳汇</div>
              <div class="item-value">{{ sceneData.carbonSink }} <small>tCO2e</small></div>
            </div>
          </div>
          <div class="overview-item">
            <div class="item-icon net" :class="{ 'positive': sceneData.netEmission > 0, 'negative': sceneData.netEmission < 0 }">
              <el-icon><DataAnalysis /></el-icon>
            </div>
            <div class="item-info">
              <div class="item-label">净排放</div>
              <div class="item-value" :class="{ 'positive': sceneData.netEmission > 0, 'negative': sceneData.netEmission < 0 }">
                {{ sceneData.netEmission }} <small>tCO2e</small>
              </div>
            </div>
          </div>
          <div class="overview-item">
            <div class="item-icon device">
              <el-icon><Cpu /></el-icon>
            </div>
            <div class="item-info">
              <div class="item-label">设备在线率</div>
              <div class="item-value">
                {{ Math.round(sceneData.devices.filter(d => d.status === 'online').length / sceneData.devices.length * 100) }}%
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 实时数据 -->
      <div class="realtime-data">
        <h3 class="panel-title">实时数据</h3>
        <div class="data-grid">
          <div v-for="(data, key) in sceneData.realtimeData" :key="key" class="data-item">
            <div class="data-label">{{ key === 'temperature' ? '温度' : key === 'humidity' ? '湿度' : key === 'co2' ? 'CO₂浓度' : '风速' }}</div>
            <div class="data-value">
              {{ data.value }} <small>{{ data.unit }}</small>
              <span class="trend" :class="data.trend">
                {{ data.trend === 'up' ? '↑' : data.trend === 'down' ? '↓' : '→' }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 告警信息 -->
      <div class="alerts-section">
        <h3 class="panel-title">告警信息</h3>
        <div class="alerts-list">
          <div v-for="alert in sceneData.alerts" :key="alert.id" class="alert-item" :class="alert.level">
            <div class="alert-icon" :style="{ color: alertLevelColors[alert.level] }">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="alert-content">
              <div class="alert-message">{{ alert.message }}</div>
              <div class="alert-time">{{ alert.time }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 历史趋势图 -->
      <div class="trend-chart">
        <h3 class="panel-title">历史趋势</h3>
        <div ref="trendChartRef" class="chart-container"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.right-panel {
  width: 360px;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(10px);
  border-left: 1px solid rgba(255, 255, 255, 0.1);
  overflow-y: auto;
  transition: all 0.3s ease;
}

.right-panel.collapsed {
  width: 0;
  padding: 0;
  border: none;
}

.panel-content {
  padding: 20px;
}

.panel-title {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #E6A23C;
  border-bottom: 1px solid rgba(230, 162, 60, 0.3);
  padding-bottom: 8px;
}

.scene-overview {
  margin-bottom: 24px;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.overview-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.item-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.item-icon.emission {
  background: rgba(245, 108, 108, 0.2);
  color: #F56C6C;
}

.item-icon.sink {
  background: rgba(103, 194, 58, 0.2);
  color: #67C23A;
}

.item-icon.net {
  background: rgba(64, 158, 255, 0.2);
  color: #409EFF;
}

.item-icon.net.positive {
  background: rgba(245, 108, 108, 0.2);
  color: #F56C6C;
}

.item-icon.net.negative {
  background: rgba(103, 194, 58, 0.2);
  color: #67C23A;
}

.item-icon.device {
  background: rgba(230, 162, 60, 0.2);
  color: #E6A23C;
}

.item-info {
  flex: 1;
}

.item-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.item-value {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.item-value.positive {
  color: #F56C6C;
}

.item-value.negative {
  color: #67C23A;
}

.item-value small {
  font-size: 12px;
  color: #909399;
  font-weight: normal;
}

.realtime-data {
  margin-bottom: 24px;
}

.data-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.data-item {
  padding: 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.data-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.data-value {
  font-size: 20px;
  font-weight: 600;
  color: #fff;
}

.data-value small {
  font-size: 12px;
  color: #909399;
  font-weight: normal;
}

.trend {
  margin-left: 8px;
  font-size: 14px;
}

.trend.up {
  color: #F56C6C;
}

.trend.down {
  color: #67C23A;
}

.trend.stable {
  color: #E6A23C;
}

.alerts-section {
  margin-bottom: 24px;
}

.alerts-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.alert-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.alert-item.error {
  border-left: 3px solid #F56C6C;
}

.alert-item.warning {
  border-left: 3px solid #E6A23C;
}

.alert-item.info {
  border-left: 3px solid #909399;
}

.alert-icon {
  font-size: 16px;
  margin-top: 2px;
}

.alert-content {
  flex: 1;
}

.alert-message {
  font-size: 14px;
  margin-bottom: 4px;
  color: #fff;
}

.alert-time {
  font-size: 12px;
  color: #909399;
}

.trend-chart {
  margin-bottom: 24px;
}

.chart-container {
  width: 100%;
  height: 200px;
}
</style>