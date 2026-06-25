import { checkPermission } from "@/utils/permission";

/**
 * v-permission 指令
 * 用法：v-permission="'user:create'" 或 v-permission="['user:create', 'user:edit']"
 * 无权限时隐藏元素（display: none）
 */
export const permissionDirective = {
  mounted(el, binding) {
    const { value } = binding;
    if (!value) return;

    let hasPermission = false;

    if (Array.isArray(value)) {
      hasPermission = value.some((permission) => checkPermission(permission));
    } else {
      hasPermission = checkPermission(value);
    }

    if (!hasPermission) {
      el.style.display = "none";
    }
  },
  updated(el, binding) {
    const { value } = binding;
    if (!value) return;

    let hasPermission = false;

    if (Array.isArray(value)) {
      hasPermission = value.some((permission) => checkPermission(permission));
    } else {
      hasPermission = checkPermission(value);
    }

    if (!hasPermission) {
      el.style.display = "none";
    } else {
      el.style.display = "";
    }
  },
};

/**
 * v-permission-disabled 指令
 * 用法：v-permission-disabled="'user:create'"
 * 无权限时禁用元素
 */
export const permissionDisabledDirective = {
  mounted(el, binding) {
    const { value } = binding;
    if (!value) return;

    let hasPermission = false;

    if (Array.isArray(value)) {
      hasPermission = value.some((permission) => checkPermission(permission));
    } else {
      hasPermission = checkPermission(value);
    }

    if (!hasPermission) {
      el.disabled = true;
      el.classList.add("is-disabled");
    }
  },
  updated(el, binding) {
    const { value } = binding;
    if (!value) return;

    let hasPermission = false;

    if (Array.isArray(value)) {
      hasPermission = value.some((permission) => checkPermission(permission));
    } else {
      hasPermission = checkPermission(value);
    }

    if (!hasPermission) {
      el.disabled = true;
      el.classList.add("is-disabled");
    } else {
      el.disabled = false;
      el.classList.remove("is-disabled");
    }
  },
};