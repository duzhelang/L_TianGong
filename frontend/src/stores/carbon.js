import { defineStore } from "pinia";
import { ref, computed } from "vue";
import http from "@/api/http";
import {
  getNDVIData,
  getNPPData,
  getLandUseData,
  getBiomassEstimation,
  getEnvironmentParameters,
  getCorrelationAnalysis,
  getCarbonSinkDistribution,
  getCarbonSinkTimeSeries,
  getPotentialAreas,
  getEnvironmentWarnings,
} from "@/api/remote-sensing";

export const useCarbonStore = defineStore("carbon", () => {
  const projects = ref([]);
  const currentProject = ref(null);
  const inventoryData = ref(null);
  const loading = ref(false);
  const error = ref(null);

  const ndviData = ref(null);
  const nppData = ref(null);
  const landUseData = ref(null);
  const biomassData = ref(null);
  const environmentParams = ref(null);
  const correlationData = ref(null);
  const carbonSinkDistribution = ref(null);
  const carbonSinkTimeSeries = ref(null);
  const potentialAreas = ref([]);
  const environmentWarnings = ref([]);

  const totalEmissions = computed(() => {
    if (!inventoryData.value) return 0;
    return inventoryData.value.totalEmissions || 0;
  });

  const emissionByCategory = computed(() => {
    if (!inventoryData.value) return [];
    return inventoryData.value.emissionByCategory || [];
  });

  async function fetchProjects() {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await http.get("/api/v1/projects");
      if (data.success) {
        projects.value = data.data || [];
      }
    } catch (err) {
      error.value = err?.response?.data?.message || "获取项目列表失败";
    } finally {
      loading.value = false;
    }
  }

  async function createProject(projectData) {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await http.post("/api/v1/projects", projectData);
      if (data.success) {
        await fetchProjects();
        return { success: true, message: data.message };
      }
      return { success: false, message: data.message };
    } catch (err) {
      const message = err?.response?.data?.message || "创建项目失败";
      error.value = message;
      return { success: false, message };
    } finally {
      loading.value = false;
    }
  }

  async function fetchProjectDetail(projectId) {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await http.get(`/api/v1/projects/${projectId}`);
      if (data.success) {
        currentProject.value = data.data;
      }
    } catch (err) {
      error.value = err?.response?.data?.message || "获取项目详情失败";
    } finally {
      loading.value = false;
    }
  }

  async function calculateInventory(inventoryForm) {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await http.post("/api/v1/carbon/inventory", inventoryForm);
      if (data.success) {
        inventoryData.value = data.data;
        return { success: true, data: data.data };
      }
      return { success: false, message: data.message };
    } catch (err) {
      const message = err?.response?.data?.message || "碳盘查计算失败";
      error.value = message;
      return { success: false, message };
    } finally {
      loading.value = false;
    }
  }

  async function fetchNDVIData(params) {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await getNDVIData(params);
      if (data.success) {
        ndviData.value = data.data;
      }
    } catch (err) {
      error.value = err?.response?.data?.message || "获取NDVI数据失败";
    } finally {
      loading.value = false;
    }
  }

  async function fetchNPPData(params) {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await getNPPData(params);
      if (data.success) {
        nppData.value = data.data;
      }
    } catch (err) {
      error.value = err?.response?.data?.message || "获取NPP数据失败";
    } finally {
      loading.value = false;
    }
  }

  async function fetchLandUseData(params) {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await getLandUseData(params);
      if (data.success) {
        landUseData.value = data.data;
      }
    } catch (err) {
      error.value = err?.response?.data?.message || "获取土地利用数据失败";
    } finally {
      loading.value = false;
    }
  }

  async function fetchBiomassData(params) {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await getBiomassEstimation(params);
      if (data.success) {
        biomassData.value = data.data;
      }
    } catch (err) {
      error.value = err?.response?.data?.message || "获取生物量数据失败";
    } finally {
      loading.value = false;
    }
  }

  async function fetchEnvironmentParams(params) {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await getEnvironmentParameters(params);
      if (data.success) {
        environmentParams.value = data.data;
      }
    } catch (err) {
      error.value = err?.response?.data?.message || "获取环境参数失败";
    } finally {
      loading.value = false;
    }
  }

  async function fetchCorrelationData(params) {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await getCorrelationAnalysis(params);
      if (data.success) {
        correlationData.value = data.data;
      }
    } catch (err) {
      error.value = err?.response?.data?.message || "获取关联分析数据失败";
    } finally {
      loading.value = false;
    }
  }

  async function fetchCarbonSinkDistribution(params) {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await getCarbonSinkDistribution(params);
      if (data.success) {
        carbonSinkDistribution.value = data.data;
      }
    } catch (err) {
      error.value = err?.response?.data?.message || "获取碳汇分布数据失败";
    } finally {
      loading.value = false;
    }
  }

  async function fetchCarbonSinkTimeSeries(params) {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await getCarbonSinkTimeSeries(params);
      if (data.success) {
        carbonSinkTimeSeries.value = data.data;
      }
    } catch (err) {
      error.value = err?.response?.data?.message || "获取碳汇时间序列数据失败";
    } finally {
      loading.value = false;
    }
  }

  async function fetchPotentialAreas(params) {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await getPotentialAreas(params);
      if (data.success) {
        potentialAreas.value = data.data || [];
      }
    } catch (err) {
      error.value = err?.response?.data?.message || "获取潜力区数据失败";
    } finally {
      loading.value = false;
    }
  }

  async function fetchEnvironmentWarnings(params) {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await getEnvironmentWarnings(params);
      if (data.success) {
        environmentWarnings.value = data.data || [];
      }
    } catch (err) {
      error.value = err?.response?.data?.message || "获取环境预警数据失败";
    } finally {
      loading.value = false;
    }
  }

  function clearError() {
    error.value = null;
  }

  return {
    projects,
    currentProject,
    inventoryData,
    loading,
    error,
    ndviData,
    nppData,
    landUseData,
    biomassData,
    environmentParams,
    correlationData,
    carbonSinkDistribution,
    carbonSinkTimeSeries,
    potentialAreas,
    environmentWarnings,
    totalEmissions,
    emissionByCategory,
    fetchProjects,
    createProject,
    fetchProjectDetail,
    calculateInventory,
    fetchNDVIData,
    fetchNPPData,
    fetchLandUseData,
    fetchBiomassData,
    fetchEnvironmentParams,
    fetchCorrelationData,
    fetchCarbonSinkDistribution,
    fetchCarbonSinkTimeSeries,
    fetchPotentialAreas,
    fetchEnvironmentWarnings,
    clearError,
  };
});