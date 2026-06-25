import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { useAuthStore } from "./auth";

// 角色-权限映射
const rolePermissions = {
  ADMIN: [
    "dashboard",
    "carbon:inventory",
    "carbon:project",
    "carbon:engine",
    "mrv:report",
    "remote-sensing:view",
    "environment:view",
    "iot:view",
    "iot:device",
    "carbon:sink",
    "carbon:trading",
    "ai:assistant",
    "digital-twin:view",
    "system:user",
    "system:role",
    "system:permission",
    "system:audit",
  ],
  USER: [
    "dashboard",
    "carbon:inventory",
    "carbon:project",
    "carbon:engine",
    "mrv:report",
    "remote-sensing:view",
    "environment:view",
    "iot:view",
    "iot:device",
    "carbon:sink",
    "carbon:trading",
    "ai:assistant",
    "digital-twin:view",
  ],
};

export const usePermissionStore = defineStore("permission", () => {
  const permissions = ref([]);
  const roles = ref([]);

  // 设置权限列表
  function setPermissions(permissionList) {
    permissions.value = permissionList;
  }

  // 设置角色列表
  function setRoles(roleList) {
    roles.value = roleList;
  }

  // 检查是否有指定权限
  function hasPermission(permissionCode) {
    return permissions.value.includes(permissionCode);
  }

  // 检查是否有指定角色
  function hasRole(roleCode) {
    return roles.value.includes(roleCode);
  }

  // 根据用户角色生成权限列表
  function generatePermissions() {
    const authStore = useAuthStore();
    const userRole = authStore.user?.role;

    if (!userRole) {
      permissions.value = [];
      roles.value = [];
      return;
    }

    // 设置角色
    roles.value = [userRole];

    // 根据角色设置权限
    if (userRole === "ADMIN") {
      permissions.value = rolePermissions.ADMIN;
    } else if (userRole === "USER") {
      permissions.value = rolePermissions.USER;
    } else {
      permissions.value = [];
    }
  }

  // 重置权限
  function resetPermissions() {
    permissions.value = [];
    roles.value = [];
  }

  return {
    permissions,
    roles,
    setPermissions,
    setRoles,
    hasPermission,
    hasRole,
    generatePermissions,
    resetPermissions,
  };
});