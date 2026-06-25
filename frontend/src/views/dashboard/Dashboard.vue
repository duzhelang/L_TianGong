<script setup>
import { ref, reactive, onMounted, markRaw } from "vue";
import { useCarbonStore } from "@/stores/carbon";
import { useRouter } from "vue-router";
import CarbonChart from "@/components/common/CarbonChart.vue";
import EmissionBreakdown from "@/components/common/EmissionBreakdown.vue";
import {
  Cloudy,
  Folder,
  TrendCharts,
  CircleCheck,
  Bell,
  ArrowRight,
  Document,
  DataLine,
  Cpu,
  Warning,
} from "@element-plus/icons-vue";

const carbonStore = useCarbonStore();
const router = useRouter();

const loading = ref(false);
const dataUpdateTime = ref(formatDateTime(new Date()));

function formatDateTime(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}`;
}

const stats = ref([
  {
    title: "碳排放总量",
    value: 12450,
    unit: "tCO2e",
    icon: markRaw(Cloudy),
    color: "#409EFF",
    trend: -5.2,
    description: "较去年同期",
  },
  {
    title: "范围一排放",
    value: 8200,
    unit: "tCO2e",
    icon: markRaw(TrendCharts),
    color: "#E6A23C",
    trend: -3.8,
    description: "直接排放",
  },
  {
    title: "范围二排放",
    value: 3100,
    unit: "tCO2e",
    icon: markRaw(TrendCharts),
    color: "#F56C6C",
    trend: -8.1,
    description: "间接排放",
  },
  {
    title: "范围三排放",
    value: 1150,
    unit: "tCO2e",
    icon: markRaw(TrendCharts),
    color: "#67C23A",
    trend: -12.5,
    description: "其他间接排放",
  },
]);

const recentProjects = ref([
  {
    id: 1,
    name: "水稻种植碳减排项目",
    area: "江苏省",
    status: "active",
    progress: 75,
    lastUpdate: "2小时前",
  },
  {
    id: 2,
    name: "小麦种植碳汇项目",
    area: "山东省",
    status: "active",
    progress: 45,
    lastUpdate: "4小时前",
  },
  {
    id: 3,
    name: "玉米种植碳盘查",
    area: "河北省",
    status: "completed",
    progress: 100,
    lastUpdate: "1天前",
  },
  {
    id: 4,
    name: "有机农场碳中和项目",
    area: "浙江省",
    status: "draft",
    progress: 20,
    lastUpdate: "3天前",
  },
]);

const notifications = ref([
  {
    id: 1,
    type: "warning",
    title: "碳排放超标预警",
    message: "水稻种植项目本月碳排放较上月增加15%",
    time: "10分钟前",
    read: false,
  },
  {
    id: 2,
    type: "success",
    title: "MRV报告生成完成",
    message: "小麦种植项目2024年度MRV报告已生成",
    time: "1小时前",
    read: false,
  },
  {
    id: 3,
    type: "info",
    title: "系统更新",
    message: "碳核算引擎已更新至最新版本",
    time: "2小时前",
    read: true,
  },
  {
    id: 4,
    type: "warning",
    title: "数据质量提醒",
    message: "3个项目缺少12月能耗数据",
    time: "1天前",
    read: true,
  },
]);

const emissionTrendData = ref({
  xAxis: ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],
  series: [
    {
      name: "2024年",
      data: [1200, 1350, 1100, 1400, 950, 1800, 1650, 1500, 1700, 1900, 2100, 1850],
      color: "#409EFF",
    },
    {
      name: "2023年",
      data: [1100, 1250, 1050, 1300, 900, 1700, 1550, 1400, 1600, 1800, 2000, 1750],
      color: "#C0C4CC",
    },
  ],
});

const emissionBreakdownData = ref([
  { name: "化肥施用", value: 35 },
  { name: "稻田甲烷", value: 25 },
  { name: "农机作业", value: 20 },
  { name: "灌溉用电", value: 12 },
  { name: "其他", value: 8 },
]);

function getStatusType(status) {
  const map = { active: "success", completed: "info", draft: "warning" };
  return map[status] || "info";
}

function getStatusLabel(status) {
  const map = { active: "进行中", completed: "已完成", draft: "草稿" };
  return map[status] || status;
}

function getNotificationIcon(type) {
  const map = {
    warning: Bell,
    success: CircleCheck,
    info: Document,
    danger: Warning,
  };
  return markRaw(map[type] || Bell);
}

function getNotificationColor(type) {
  const map = {
    warning: "#E6A23C",
    success: "#67C23A",
    info: "#409EFF",
    danger: "#F56C6C",
  };
  return map[type] || "#909399";
}

function navigateTo(path) {
  router.push(path);
}

function markAsRead(id) {
  const notification = notifications.value.find((n) => n.id === id);
  if (notification) {
    notification.read = true;
  }
}

onMounted(async () => {
  loading.value = true;
  try {
    await carbonStore.fetchProjects();
  } catch (error) {
    console.error("获取数据失败", error);
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <div>
        <h2>仪表盘</h2>
        <p>碳排放概览与数据统计</p>
      </div>
      <div class="header-time">
        数据更新时间: {{ dataUpdateTime }}
      </div>
    </div>

    <div class="stats-grid">
      <div
        v-for="stat in stats"
        :key="stat.title"
        class="stat-card"
      >
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
            <el-icon v-if="stat.trend > 0"><ArrowRight /></el-icon>
            <el-icon v-else><ArrowRight /></el-icon>
            {{ Math.abs(stat.trend) }}% {{ stat.description }}
          </div>
        </div>
      </div>
    </div>

    <div class="dashboard-content">
      <div class="chart-section">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>碳排放趋势</span>
              <el-radio-group size="small">
                <el-radio-button value="monthly">月度</el-radio-button>
                <el-radio-button value="quarterly">季度</el-radio-button>
                <el-radio-button value="yearly">年度</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <CarbonChart
            type="line"
            :data="emissionTrendData"
            height="350px"
          />
        </el-card>
      </div>

      <div class="breakdown-section">
        <el-card class="breakdown-card">
          <template #header>
            <div class="card-header">
              <span>排放结构</span>
              <el-button type="primary" link @click="navigateTo('/carbon/inventory')">
                查看详情
              </el-button>
            </div>
          </template>
          <EmissionBreakdown :data="emissionBreakdownData" />
        </el-card>
      </div>
    </div>

    <div class="dashboard-bottom">
      <div class="projects-section">
        <el-card class="projects-card">
          <template #header>
            <div class="card-header">
              <span>最近项目</span>
              <el-button type="primary" link @click="navigateTo('/carbon/project')">
                查看全部
              </el-button>
            </div>
          </template>

          <div class="project-list">
            <div
              v-for="project in recentProjects"
              :key="project.id"
              class="project-item"
              @click="navigateTo('/carbon/project')"
            >
              <div class="project-info">
                <div class="project-name">{{ project.name }}</div>
                <div class="project-meta">
                  <span class="project-area">{{ project.area }}</span>
                  <span class="project-time">{{ project.lastUpdate }}</span>
                </div>
              </div>
              <div class="project-status">
                <el-tag :type="getStatusType(project.status)" size="small">
                  {{ getStatusLabel(project.status) }}
                </el-tag>
                <el-progress
                  :percentage="project.progress"
                  :show-text="false"
                  :stroke-width="4"
                  style="width: 100px;"
                />
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <div class="notifications-section">
        <el-card class="notifications-card">
          <template #header>
            <div class="card-header">
              <span>系统通知</span>
              <el-badge :value="notifications.filter((n) => !n.read).length" :hidden="notifications.filter((n) => !n.read).length === 0">
                <el-button type="primary" link>
                  全部已读
                </el-button>
              </el-badge>
            </div>
          </template>

          <div class="notification-list">
            <div
              v-for="notification in notifications"
              :key="notification.id"
              class="notification-item"
              :class="{ unread: !notification.read }"
              @click="markAsRead(notification.id)"
            >
              <div
                class="notification-icon"
                :style="{ color: getNotificationColor(notification.type) }"
              >
                <el-icon :size="16"><component :is="getNotificationIcon(notification.type)" /></el-icon>
              </div>
              <div class="notification-content">
                <div class="notification-title">{{ notification.title }}</div>
                <div class="notification-message">{{ notification.message }}</div>
                <div class="notification-time">{{ notification.time }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <div class="quick-actions">
      <el-card class="actions-card">
        <template #header>
          <span>快捷操作</span>
        </template>
        <div class="actions-grid">
          <div class="action-item" @click="navigateTo('/carbon/inventory')">
            <el-icon :size="32" color="#409EFF"><DataLine /></el-icon>
            <span>碳盘查</span>
          </div>
          <div class="action-item" @click="navigateTo('/carbon/project')">
            <el-icon :size="32" color="#67C23A"><Folder /></el-icon>
            <span>项目管理</span>
          </div>
          <div class="action-item" @click="navigateTo('/carbon/engine')">
            <el-icon :size="32" color="#E6A23C"><Cpu /></el-icon>
            <span>核算引擎</span>
          </div>
          <div class="action-item" @click="navigateTo('/mrv/report')">
            <el-icon :size="32" color="#F56C6C"><Document /></el-icon>
            <span>生成报告</span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 20px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.dashboard-header h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}

.dashboard-header p {
  color: #909399;
  font-size: 14px;
}

.header-time {
  color: #909399;
  font-size: 13px;
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
  color: #f56c6c;
}

.stat-trend.negative {
  color: #67c23a;
}

.dashboard-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  height: 100%;
}

.breakdown-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dashboard-bottom {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.projects-card,
.notifications-card {
  height: 100%;
}

.project-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.project-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.project-item:hover {
  background: #ecf5ff;
}

.project-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.project-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.project-status {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.notification-item:hover {
  background: #f5f7fa;
}

.notification-item.unread {
  background: #ecf5ff;
}

.notification-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  flex-shrink: 0;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.notification-message {
  font-size: 13px;
  color: #606266;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}

.quick-actions {
  margin-bottom: 20px;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-item:hover {
  background: #ecf5ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.action-item span {
  font-size: 14px;
  color: #303133;
}
</style>