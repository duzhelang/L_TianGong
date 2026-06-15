<script setup>
import { useAppStore } from "@/stores/app";
import { useAuthStore } from "@/stores/auth";
import { useRouter } from "vue-router";
import { ElMessageBox } from "element-plus";
import { Fold, Expand, Bell, User, SwitchButton } from "@element-plus/icons-vue";

const appStore = useAppStore();
const authStore = useAuthStore();
const router = useRouter();

function toggleSidebar() {
  appStore.toggleSidebar();
}

async function handleLogout() {
  try {
    await ElMessageBox.confirm("确定要退出登录吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
    authStore.logout();
    router.push("/login");
  } catch {
    // 取消退出
  }
}
</script>

<template>
  <header class="app-header">
    <div class="header-left">
      <el-icon class="collapse-btn" @click="toggleSidebar">
        <Fold v-if="!appStore.sidebarCollapsed" />
        <Expand v-else />
      </el-icon>
      <div class="breadcrumb">
        <span class="title">EcoCarbon-MRV</span>
      </div>
    </div>

    <div class="header-right">
      <el-badge :value="appStore.unreadCount" :hidden="appStore.unreadCount === 0" class="notification-badge">
        <el-icon class="header-icon"><Bell /></el-icon>
      </el-badge>

      <el-dropdown trigger="click">
        <div class="user-info">
          <el-avatar :size="32" :icon="User" />
          <span class="username">{{ authStore.user?.username || "用户" }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item :icon="User">个人中心</el-dropdown-item>
            <el-dropdown-item :icon="SwitchButton" divided @click="handleLogout">
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<style scoped>
.app-header {
  height: 60px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
  transition: color 0.3s;
}

.collapse-btn:hover {
  color: #409eff;
}

.breadcrumb .title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-icon {
  font-size: 20px;
  color: #606266;
  cursor: pointer;
  transition: color 0.3s;
}

.header-icon:hover {
  color: #409eff;
}

.notification-badge {
  line-height: 1;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: #606266;
}
</style>