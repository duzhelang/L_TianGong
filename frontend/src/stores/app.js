import { defineStore } from "pinia";
import { ref, computed } from "vue";

export const useAppStore = defineStore("app", () => {
  const sidebarCollapsed = ref(false);
  const currentRoute = ref(null);
  const notifications = ref([]);
  const loading = ref(false);

  const unreadCount = computed(() =>
    notifications.value.filter((n) => !n.read).length
  );

  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value;
  }

  function setCurrentRoute(route) {
    currentRoute.value = route;
  }

  function addNotification(notification) {
    notifications.value.unshift({
      id: Date.now(),
      read: false,
      timestamp: new Date().toISOString(),
      ...notification,
    });
  }

  function markNotificationAsRead(id) {
    const notification = notifications.value.find((n) => n.id === id);
    if (notification) {
      notification.read = true;
    }
  }

  function clearNotifications() {
    notifications.value = [];
  }

  function setLoading(value) {
    loading.value = value;
  }

  return {
    sidebarCollapsed,
    currentRoute,
    notifications,
    loading,
    unreadCount,
    toggleSidebar,
    setCurrentRoute,
    addNotification,
    markNotificationAsRead,
    clearNotifications,
    setLoading,
  };
});