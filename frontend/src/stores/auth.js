import { defineStore } from "pinia";
import { ref, computed } from "vue";
import http from "@/api/http";

export const useAuthStore = defineStore("auth", () => {
  const token = ref(localStorage.getItem("token") || "");
  const user = ref(null);
  const loading = ref(false);

  const isAuthenticated = computed(() => !!token.value);

  async function login(credentials) {
    loading.value = true;
    try {
      const { data } = await http.post("/api/v1/auth/login", credentials);
      if (data.success) {
        token.value = data.data.token;
        localStorage.setItem("token", data.data.token);
        await fetchUserInfo();
        return { success: true };
      }
      return { success: false, message: data.message };
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
      const { data } = await http.get("/api/v1/auth/userinfo");
      if (data.success) {
        user.value = data.data;
      }
    } catch (error) {
      console.error("获取用户信息失败:", error);
    }
  }

  function logout() {
    token.value = "";
    user.value = null;
    localStorage.removeItem("token");
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