<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useCarbonStore } from '@/stores/carbon';
import * as echarts from 'echarts';

const carbonStore = useCarbonStore();
const loading = ref(false);
const currentTime = ref('');
let timeInterval = null;

const totalEmission = ref(12450);
const totalSink = ref(8200);
const netEmission = ref(4250);
const projectCount = ref(15);

const trendChartRef = ref(null);
const structureChartRef = ref(null);
const mapChartRef = ref(null);
let trendChart = null;
let structureChart = null;
let mapChart = null;

const recentProjects = ref([
  { name: '水稻种植碳减排项目', area: '江苏省', status: 'active', progress: 75 },
  { name: '小麦种植碳汇项目', area: '山东省', status: 'active', progress: 45 },
  { name: '玉米种植碳盘查', area: '河北省', status: 'completed', progress: 100 },
  { name: '有机农场碳中和项目', area: '浙江省', status: 'draft', progress: 20 },
]);

const realtimeData = ref([
  { time: '14:30:15', device: '气象站001', metric: '温度', value: '28.5°C' },
  { time: '14:30:12', device: '土壤站002', metric: '湿度', value: '65%' },
  { time: '14:30:08', device: '气体分析仪003', metric: 'CO2', value: '420ppm' },
  { time: '14:30:05', device: '气象站001', metric: '风速', value: '3.2m/s' },
  { time: '14:30:01', device: '土壤站002', metric: '温度', value: '22.1°C' },
]);

function updateTime() {
  const now = new Date();
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
}

function initTrendChart() {
  if (!trendChartRef.value) return;
  trendChart = echarts.init(trendChartRef.value);
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['2024年', '2023年'], textStyle: { color: '#fff' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
      axisLabel: { color: '#fff' }
    },
    yAxis: { type: 'value', axisLabel: { color: '#fff' } },
    series: [
      {
        name: '2024年',
        type: 'line',
        smooth: true,
        data: [1200, 1350, 1100, 1400, 950, 1800, 1650, 1500, 1700, 1900, 2100, 1850],
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '2023年',
        type: 'line',
        smooth: true,
        data: [1100, 1250, 1050, 1300, 900, 1700, 1550, 1400, 1600, 1800, 2000, 1750],
        itemStyle: { color: '#67C23A' }
      }
    ]
  };
  trendChart.setOption(option);
}

function initStructureChart() {
  if (!structureChartRef.value) return;
  structureChart = echarts.init(structureChartRef.value);
  const option = {
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', left: 'left', textStyle: { color: '#fff' } },
    series: [
      {
        name: '排放结构',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 35, name: '化肥施用' },
          { value: 25, name: '稻田甲烷' },
          { value: 20, name: '农机作业' },
          { value: 12, name: '灌溉用电' },
          { value: 8, name: '其他' }
        ],
        emphasis: {
          itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' }
        }
      }
    ]
  };
  structureChart.setOption(option);
}

function initMapChart() {
  if (!mapChartRef.value) return;
  mapChart = echarts.init(mapChartRef.value);
  const option = {
    tooltip: { trigger: 'item' },
    geo: {
      map: 'china',
      roam: true,
      label: { show: true, color: '#fff' },
      itemStyle: { areaColor: '#1a3a5c', borderColor: '#409EFF' }
    },
    series: [
      {
        name: '碳汇项目',
        type: 'scatter',
        coordinateSystem: 'geo',
        data: [
          { name: '江苏省', value: [119.48, 32.97, 1200] },
          { name: '山东省', value: [117.02, 36.67, 800] },
          { name: '河北省', value: [115.48, 38.03, 600] },
          { name: '浙江省', value: [120.15, 30.26, 900] }
        ],
        symbolSize: function(val) { return val[2] / 100; },
        itemStyle: { color: '#67C23A' }
      }
    ]
  };
  mapChart.setOption(option);
}

function handleResize() {
  trendChart?.resize();
  structureChart?.resize();
  mapChart?.resize();
}

onMounted(() => {
  updateTime();
  timeInterval = setInterval(updateTime, 1000);
  initTrendChart();
  initStructureChart();
  initMapChart();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  if (timeInterval) clearInterval(timeInterval);
  window.removeEventListener('resize', handleResize);
  trendChart?.dispose();
  structureChart?.dispose();
  mapChart?.dispose();
});
</script>

<template>
  <div class="data-screen">
    <div class="screen-header">
      <h1>碳排放监测数据大屏</h1>
      <div class="header-time">{{ currentTime }}</div>
    </div>

    <div class="screen-content">
      <div class="left-panel">
        <div class="stats-cards">
          <div class="stat-card">
            <div class="stat-icon emission">🏭</div>
            <div class="stat-info">
              <div class="stat-value">{{ totalEmission.toLocaleString() }}</div>
              <div class="stat-label">碳排放总量 (tCO2e)</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon sink">🌳</div>
            <div class="stat-info">
              <div class="stat-value">{{ totalSink.toLocaleString() }}</div>
              <div class="stat-label">碳汇总量 (tCO2e)</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon net">📊</div>
            <div class="stat-info">
              <div class="stat-value">{{ netEmission.toLocaleString() }}</div>
              <div class="stat-label">净排放量 (tCO2e)</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon project">📁</div>
            <div class="stat-info">
              <div class="stat-value">{{ projectCount }}</div>
              <div class="stat-label">项目数量</div>
            </div>
          </div>
        </div>

        <div class="chart-container">
          <div class="chart-title">碳排放趋势</div>
          <div ref="trendChartRef" class="chart"></div>
        </div>
      </div>

      <div class="center-panel">
        <div class="map-container">
          <div class="chart-title">碳汇分布地图</div>
          <div ref="mapChartRef" class="chart map-chart"></div>
        </div>
      </div>

      <div class="right-panel">
        <div class="chart-container">
          <div class="chart-title">排放结构分析</div>
          <div ref="structureChartRef" class="chart"></div>
        </div>

        <div class="realtime-container">
          <div class="chart-title">实时数据</div>
          <div class="realtime-list">
            <div v-for="(item, index) in realtimeData" :key="index" class="realtime-item">
              <div class="realtime-time">{{ item.time }}</div>
              <div class="realtime-device">{{ item.device }}</div>
              <div class="realtime-metric">{{ item.metric }}</div>
              <div class="realtime-value">{{ item.value }}</div>
            </div>
          </div>
        </div>

        <div class="projects-container">
          <div class="chart-title">最近项目</div>
          <div class="project-list">
            <div v-for="(project, index) in recentProjects" :key="index" class="project-item">
              <div class="project-info">
                <div class="project-name">{{ project.name }}</div>
                <div class="project-area">{{ project.area }}</div>
              </div>
              <div class="project-progress">
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: project.progress + '%' }"></div>
                </div>
                <div class="progress-text">{{ project.progress }}%</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.data-screen {
  background: linear-gradient(135deg, #0c1929 0%, #1a2a3a 100%);
  min-height: 100vh;
  color: #fff;
  padding: 20px;
}

.screen-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  background: rgba(64, 158, 255, 0.1);
  border: 1px solid rgba(64, 158, 255, 0.3);
  border-radius: 8px;
  margin-bottom: 20px;
}

.screen-header h1 {
  font-size: 28px;
  font-weight: 600;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.header-time {
  font-size: 18px;
  color: #909399;
}

.screen-content {
  display: grid;
  grid-template-columns: 1fr 1.5fr 1fr;
  gap: 20px;
  height: calc(100vh - 140px);
}

.left-panel, .right-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.center-panel {
  display: flex;
  flex-direction: column;
}

.stats-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.stat-card {
  background: rgba(64, 158, 255, 0.1);
  border: 1px solid rgba(64, 158, 255, 0.3);
  border-radius: 8px;
  padding: 15px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.emission { background: rgba(245, 108, 108, 0.2); }
.stat-icon.sink { background: rgba(103, 194, 58, 0.2); }
.stat-icon.net { background: rgba(64, 158, 255, 0.2); }
.stat-icon.project { background: rgba(230, 162, 60, 0.2); }

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #409EFF;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.chart-container, .map-container, .realtime-container, .projects-container {
  background: rgba(64, 158, 255, 0.1);
  border: 1px solid rgba(64, 158, 255, 0.3);
  border-radius: 8px;
  padding: 15px;
  flex: 1;
}

.chart-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 10px;
  color: #409EFF;
}

.chart {
  width: 100%;
  height: calc(100% - 30px);
}

.map-chart {
  height: 100%;
}

.realtime-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 200px;
  overflow-y: auto;
}

.realtime-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: rgba(64, 158, 255, 0.05);
  border-radius: 4px;
  font-size: 12px;
}

.realtime-time { color: #909399; width: 60px; }
.realtime-device { color: #E6A23C; flex: 1; }
.realtime-metric { color: #67C23A; width: 50px; }
.realtime-value { color: #409EFF; font-weight: 500; }

.project-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.project-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: rgba(64, 158, 255, 0.05);
  border-radius: 4px;
}

.project-name {
  font-size: 14px;
  font-weight: 500;
}

.project-area {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.project-progress {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-bar {
  width: 100px;
  height: 6px;
  background: rgba(64, 158, 255, 0.2);
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  border-radius: 3px;
}

.progress-text {
  font-size: 12px;
  color: #409EFF;
  width: 35px;
}
</style>
