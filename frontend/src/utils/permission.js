import { usePermissionStore } from "@/stores/permission";

// 菜单权限映射
const menuPermissions = {
  "/": ["dashboard"],
  "/carbon/inventory": ["carbon:inventory"],
  "/carbon/project": ["carbon:project"],
  "/carbon/engine": ["carbon:engine"],
  "/mrv/report": ["mrv:report"],
  "/remote-sensing": ["remote-sensing:view"],
  "/environment": ["environment:view"],
  "/iot": ["iot:view"],
  "/iot/devices": ["iot:device"],
  "/carbon/sink-map": ["carbon:sink"],
  "/carbon/trading": ["carbon:trading"],
  "/ai/assistant": ["ai:assistant"],
  "/digital-twin": ["digital-twin:view"],
  "/system/users": ["system:user"],
  "/system/roles": ["system:role"],
  "/system/permissions": ["system:permission"],
  "/system/audit-log": ["system:audit"],
};

/**
 * 检查是否有指定权限
 * @param {string} permissionCode - 权限编码
 * @returns {boolean}
 */
export function checkPermission(permissionCode) {
  const permissionStore = usePermissionStore();
  return permissionStore.hasPermission(permissionCode);
}

/**
 * 检查是否有指定角色
 * @param {string} roleCode - 角色编码
 * @returns {boolean}
 */
export function checkRole(roleCode) {
  const permissionStore = usePermissionStore();
  return permissionStore.hasRole(roleCode);
}

/**
 * 获取菜单权限配置
 * @returns {object} 菜单权限映射
 */
export function getMenuPermissions() {
  return menuPermissions;
}

/**
 * 检查用户是否有访问指定菜单的权限
 * @param {string} path - 菜单路径
 * @returns {boolean}
 */
export function hasMenuPermission(path) {
  const requiredPermissions = menuPermissions[path];
  if (!requiredPermissions) {
    return true;
  }
  return requiredPermissions.some((permission) => checkPermission(permission));
}