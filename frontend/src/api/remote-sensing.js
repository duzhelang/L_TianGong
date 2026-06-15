import http from "./http";

export function getNDVIData(params) {
  return http.get("/api/v1/remote-sensing/ndvi", { params });
}

export function getNPPData(params) {
  return http.get("/api/v1/remote-sensing/npp", { params });
}

export function getLandUseData(params) {
  return http.get("/api/v1/remote-sensing/land-use", { params });
}

export function getBiomassEstimation(params) {
  return http.get("/api/v1/remote-sensing/biomass", { params });
}

export function getEnvironmentParameters(params) {
  return http.get("/api/v1/environment/parameters", { params });
}

export function getCorrelationAnalysis(params) {
  return http.get("/api/v1/environment/correlation", { params });
}

export function getCarbonSinkDistribution(params) {
  return http.get("/api/v1/carbon/sink-distribution", { params });
}

export function getCarbonSinkTimeSeries(params) {
  return http.get("/api/v1/carbon/sink-time-series", { params });
}

export function getPotentialAreas(params) {
  return http.get("/api/v1/carbon/potential-areas", { params });
}

export function getEnvironmentWarnings(params) {
  return http.get("/api/v1/environment/warnings", { params });
}

export function exportRemoteSensingData(params) {
  return http.get("/api/v1/remote-sensing/export", {
    params,
    responseType: "blob",
  });
}

export function exportEnvironmentData(params) {
  return http.get("/api/v1/environment/export", {
    params,
    responseType: "blob",
  });
}

export function exportCarbonSinkData(params) {
  return http.get("/api/v1/carbon/sink-export", {
    params,
    responseType: "blob",
  });
}