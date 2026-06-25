import { defineStore } from "pinia";
import { ref, computed } from "vue";
import http from "@/api/http";
import { usePermissionStore } from "./permission";

export const useAuthStore = defineStore("auth", () => {
  const token = ref(localStorage.getItem("token") || "");
  const user = ref(null);
  const loading = ref(false);

  const isAuthenticated = computed(() => !!token.value);

  async function login(credentials) {
    loading.value = true;
    try {
      localStorage.removeItem("token");
      localStorage.removeItem("refreshToken");
      token.value = "";
      
      const response = await http.post("/api/v1/auth/login", credentials);
      const result = response.data;
      if (result.success) {
        token.value = result.data.accessToken;
        localStorage.setItem("token", result.data.accessToken);
        localStorage.setItem("refreshToken", result.data.refreshToken);
        user.value = {
          username: result.data.username,
          role: result.data.role,
        };
        
        // 生成权限
        const permissionStore = usePermissionStore();
        permissionStore.generatePermissions();
        
        return { success: true };
      }
      return { success: false, message: result.message };
    } catch (error) {
      return {
        success: false,
        message: error?.response?.data?.message || "登录失败",
      };
    } finally {
      loading.value = false;
    }
  }

  async function fetchUserInfo() {
    try {
      const response = await http.get("/api/v1/auth/userinfo");
      const result = response.data;
      if (result.success) {
        user.value = result.data;
        
        // 生成权限
        const permissionStore = usePermissionStore();
        permissionStore.generatePermissions();
      }
    } catch (error) {
      console.error("获取用户信息失败:", error);
    }
  }

  function logout() {
    token.value = "";
    user.value = null;
    localStorage.removeItem("token");
    localStorage.removeItem("refreshToken");
    
    // 重置权限
    const permissionStore = usePermissionStore();
    permissionStore.resetPermissions();
  }

  return {
    token,
    user,
    loading,
    isAuthenticated,
    login,
    fetchUserInfo,
    logout,
  };
});
