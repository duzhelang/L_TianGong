<script setup>
import { useAppStore } from "@/stores/app";
import { useRoute, useRouter } from "vue-router";
import { computed, ref } from "vue";
import {
  Odometer,
  DataLine,
  FolderOpened,
  Cpu,
  MapLocation,
  Document,
  Setting,
  Bell,
  TrendCharts,
  Picture,
  Cloudy,
  VideoPlay,
} from "@element-plus/icons-vue";

const appStore = useAppStore();
const route = useRoute();
const router = useRouter();

const menuItems = [
  {
    path: "/",
    icon: Odometer,
    title: "仪表盘",
  },
  {
    path: "/carbon/inventory",
    icon: DataLine,
    title: "碳盘查",
  },
  {
    path: "/carbon/project",
    icon: FolderOpened,
    title: "碳项目管理",
  },
  {
    path: "/carbon/engine",
    icon: Cpu,
    title: "碳核算引擎",
  },
  {
    path: "/remote-sensing",
    icon: MapLocation,
    title: "遥感监测",
    children: [
      {
        path: "/remote-sensing",
        icon: DataLine,
        title: "NDVI监测",
      },
      {
        path: "/remote-sensing/npp",
        icon: TrendCharts,
        title: "NPP反演",
      },
      {
        path: "/remote-sensing",
        icon: Picture,
        title: "土地利用",
      },
    ],
  },
  {
    path: "/environment",
    icon: Cloudy,
    title: "环境监测",
    children: [
      {
        path: "/environment",
        icon: DataLine,
        title: "环境参数",
      },
      {
        path: "/environment",
        icon: TrendCharts,
        title: "碳过程关联",
      },
    ],
  },
  {
    path: "/carbon/sink-map",
    icon: MapLocation,
    title: "碳汇一张图",
  },
  {
    path: "/methodology",
    icon: Document,
    title: "方法学配置",
    disabled: true,
  },
  {
    path: "/mrv/report",
    icon: Document,
    title: "MRV报告",
  },
  {
    path: "/short-drama",
    icon: VideoPlay,
    title: "短剧中心",
  },
  {
    path: "/settings",
    icon: Setting,
    title: "系统设置",
    disabled: true,
  },
];

const activeMenu = computed(() => route.path);

function handleMenuClick(path) {
  router.push(path);
}
</script>

<template>
  <aside class="app-sidebar" :class="{ collapsed: appStore.sidebarCollapsed }">
    <div class="sidebar-header">
      <div class="logo">
        <span v-if="!appStore.sidebarCollapsed" class="logo-text">EC</span>
        <span v-else class="logo-icon">E</span>
      </div>
    </div>

    <el-menu
      :default-active="activeMenu"
      :collapse="appStore.sidebarCollapsed"
      class="sidebar-menu"
      @select="handleMenuClick"
    >
      <template v-for="item in menuItems" :key="item.path">
        <el-menu-item
          v-if="!item.children"
          :index="item.path"
          :disabled="item.disabled"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.title }}</template>
          <el-badge
            v-if="item.badge"
            :value="item.badge"
            :max="99"
            class="menu-badge"
          />
        </el-menu-item>

        <el-sub-menu v-else :index="item.path">
          <template #title>
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </template>
          <el-menu-item
            v-for="child in item.children"
            :key="child.path"
            :index="child.path"
            :disabled="child.disabled"
          >
            <el-icon><component :is="child.icon" /></el-icon>
            <template #title>{{ child.title }}</template>
          </el-menu-item>
        </el-sub-menu>
      </template>
    </el-menu>

    <div class="sidebar-footer" v-if="!appStore.sidebarCollapsed">
      <div class="version-info">
        <span class="version">v1.0.0</span>
        <span class="copyright">EcoCarbon-MRV</span>
      </div>
    </div>
  </aside>
</template>

<style scoped>
.app-sidebar {
  width: 240px;
  height: 100vh;
  background: #304156;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
  transition: width 0.3s ease;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.app-sidebar.collapsed {
  width: 64px;
}

.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #263445;
  flex-shrink: 0;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  color: #409eff;
  letter-spacing: 2px;
}

.logo-icon {
  font-size: 24px;
  font-weight: 700;
  color: #409eff;
}

.sidebar-menu {
  border-right: none;
  background: transparent;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 240px;
}

:deep(.el-menu-item) {
  color: #bfcbd9;
  position: relative;
}

:deep(.el-menu-item:hover) {
  background: #263445;
}

:deep(.el-menu-item.is-active) {
  color: #409eff;
  background: #263445;
}

:deep(.el-menu-item.is-disabled) {
  opacity: 0.5;
}

:deep(.el-sub-menu__title) {
  color: #bfcbd9;
}

:deep(.el-sub-menu__title:hover) {
  background: #263445;
}

:deep(.el-sub-menu .el-menu-item) {
  min-width: auto;
}

.menu-badge {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  flex-shrink: 0;
}

.version-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.4);
}

.version-info .version {
  font-weight: 500;
}

.version-info .copyright {
  font-size: 10px;
}
</style>