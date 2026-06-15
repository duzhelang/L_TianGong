import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "@/stores/auth";

const routes = [
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/auth/Login.vue"),
    meta: { requiresAuth: false },
  },
  {
    path: "/",
    component: () => import("@/components/layout/AppLayout.vue"),
    meta: { requiresAuth: true },
    children: [
      {
        path: "",
        name: "Dashboard",
        component: () => import("@/views/dashboard/Dashboard.vue"),
        meta: { title: "仪表盘" },
      },
      {
        path: "carbon/inventory",
        name: "CarbonInventory",
        component: () => import("@/views/carbon/CarbonInventory.vue"),
        meta: { title: "碳盘查" },
      },
      {
        path: "carbon/project",
        name: "CarbonProject",
        component: () => import("@/views/carbon/CarbonProject.vue"),
        meta: { title: "碳项目管理" },
      },
      {
        path: "carbon/engine",
        name: "CarbonEngine",
        component: () => import("@/views/carbon/CarbonEngine.vue"),
        meta: { title: "碳核算引擎" },
      },
      {
        path: "mrv/report",
        name: "MRVReport",
        component: () => import("@/views/mrv/MRVReport.vue"),
        meta: { title: "MRV报告" },
      },
      {
        path: "remote-sensing",
        name: "RemoteSensing",
        component: () => import("@/views/remote-sensing/RemoteSensing.vue"),
        meta: { title: "遥感监测" },
      },
      {
        path: "remote-sensing/ndvi",
        name: "RemoteSensingNDVI",
        component: () => import("@/views/remote-sensing/RemoteSensing.vue"),
        meta: { title: "NDVI监测" },
      },
      {
        path: "remote-sensing/npp",
        name: "RemoteSensingNPP",
        component: () => import("@/views/remote-sensing/RemoteSensing.vue"),
        meta: { title: "NPP反演" },
      },
      {
        path: "environment",
        name: "EnvironmentMonitor",
        component: () => import("@/views/environment/EnvironmentMonitor.vue"),
        meta: { title: "环境监测" },
      },
      {
        path: "carbon/sink-map",
        name: "CarbonSinkMap",
        component: () => import("@/views/carbon/CarbonSinkMap.vue"),
        meta: { title: "碳汇一张图" },
      },
      {
        path: "short-drama",
        name: "ShortDrama",
        component: () => import("@/views/short-drama/ShortDrama.vue"),
        meta: { title: "短剧中心" },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth);

  if (requiresAuth && !authStore.isAuthenticated) {
    next({ name: "Login" });
  } else if (to.name === "Login" && authStore.isAuthenticated) {
    next({ name: "Dashboard" });
  } else {
    next();
  }
});

export default router;