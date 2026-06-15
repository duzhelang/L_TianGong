import { defineStore } from "pinia";
import { ref } from "vue";
import {
  getNDVIData,
  getNPPData,
  getLandUseData,
  getBiomassEstimation,
} from "@/api/remote-sensing";

export const useRemoteSensingStore = defineStore("remote-sensing", () => {
  const ndviData = ref(null);
  const nppData = ref(null);
  const landUseData = ref(null);
  const biomassData = ref(null);
  const loading = ref(false);
  const error = ref(null);

  async function fetchNDVIData(timeRange, region) {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await getNDVIData({ ...timeRange, region });
      if (data.success) {
        ndviData.value = data.data;
      }
    } catch (err) {
      error.value = err?.response?.data?.message || "获取NDVI数据失败";
    } finally {
      loading.value = false;
    }
  }

  async function fetchNPPData(timeRange, region) {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await getNPPData({ ...timeRange, region });
      if (data.success) {
        nppData.value = data.data;
      }
    } catch (err) {
      error.value = err?.response?.data?.message || "获取NPP数据失败";
    } finally {
      loading.value = false;
    }
  }

  async function fetchLandUseData(region) {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await getLandUseData({ region });
      if (data.success) {
        landUseData.value = data.data;
      }
    } catch (err) {
      error.value = err?.response?.data?.message || "获取土地利用数据失败";
    } finally {
      loading.value = false;
    }
  }

  async function fetchBiomassData(region) {
    loading.value = true;
    error.value = null;
    try {
      const { data } = await getBiomassEstimation({ region });
      if (data.success) {
        biomassData.value = data.data;
      }
    } catch (err) {
      error.value = err?.response?.data?.message || "获取生物量数据失败";
    } finally {
      loading.value = false;
    }
  }

  return {
    ndviData,
    nppData,
    landUseData,
    biomassData,
    loading,
    error,
    fetchNDVIData,
    fetchNPPData,
    fetchLandUseData,
    fetchBiomassData,
  };
});
