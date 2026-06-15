<script setup>
import { useAppStore } from "@/stores/app";
import AppHeader from "./AppHeader.vue";
import AppSidebar from "./AppSidebar.vue";

const appStore = useAppStore();
</script>

<template>
  <div class="app-layout">
    <AppSidebar />
    <div class="main-container" :class="{ collapsed: appStore.sidebarCollapsed }">
      <AppHeader />
      <main class="main-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.app-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
}

.main-container {
  flex: 1;
  margin-left: 240px;
  transition: margin-left 0.3s ease;
}

.main-container.collapsed {
  margin-left: 64px;
}

.main-content {
  padding: 20px;
  min-height: calc(100vh - 60px);
}
</style>