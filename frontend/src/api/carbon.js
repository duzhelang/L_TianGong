import http from "./http";

export function calculateInventory(inventoryData) {
  return http.post("/api/v1/carbon/inventory", inventoryData);
}

export function getInventoryHistory(projectId) {
  return http.get("/api/v1/carbon/inventory/history", { params: { projectId } });
}

export function getInventoryDetail(inventoryId) {
  return http.get(`/api/v1/carbon/inventory/${inventoryId}`);
}

export function calculateEmissions(emissionData) {
  return http.post("/api/v1/carbon/emissions", emissionData);
}

export function getEmissionFactors(category) {
  return http.get("/api/v1/carbon/factors", { params: { category } });
}

export function generateReport(reportData) {
  return http.post("/api/v1/carbon/reports", reportData);
}

export function getReportList(projectId) {
  return http.get("/api/v1/carbon/reports", { params: { projectId } });
}

export function getReportDetail(reportId) {
  return http.get(`/api/v1/carbon/reports/${reportId}`);
}

export function downloadReport(reportId, format) {
  return http.get(`/api/v1/carbon/reports/${reportId}/download`, {
    params: { format },
    responseType: "blob",
  });
}

export function getDashboardStats() {
  return http.get("/api/v1/carbon/dashboard/stats");
}

export function getEmissionTrend(params) {
  return http.get("/api/v1/carbon/dashboard/trend", { params });
}

export function getEmissionBreakdown(params) {
  return http.get("/api/v1/carbon/dashboard/breakdown", { params });
}

export function getRecentProjects(limit) {
  return http.get("/api/v1/carbon/projects/recent", { params: { limit } });
}

export function getNotifications(params) {
  return http.get("/api/v1/carbon/notifications", { params });
}

export function markNotificationRead(notificationId) {
  return http.put(`/api/v1/carbon/notifications/${notificationId}/read`);
}

export function getMethodologyList() {
  return http.get("/api/v1/carbon/methodologies");
}

export function getMethodologyDetail(methodologyId) {
  return http.get(`/api/v1/carbon/methodologies/${methodologyId}`);
}

export function updateMethodology(methodologyId, data) {
  return http.put(`/api/v1/carbon/methodologies/${methodologyId}`, data);
}

export function getUncertaintyAnalysis(inventoryId) {
  return http.get(`/api/v1/carbon/inventory/${inventoryId}/uncertainty`);
}

export function getReductionSuggestions(inventoryId) {
  return http.get(`/api/v1/carbon/inventory/${inventoryId}/suggestions`);
}

export function submitInventory(inventoryData) {
  return http.post("/api/v1/carbon/inventory/submit", inventoryData);
}

export function getInventoryList(params) {
  return http.get("/api/v1/carbon/inventory/list", { params });
}

export function deleteInventory(inventoryId) {
  return http.delete(`/api/v1/carbon/inventory/${inventoryId}`);
}

export function exportInventory(inventoryId, format) {
  return http.get(`/api/v1/carbon/inventory/${inventoryId}/export`, {
    params: { format },
    responseType: "blob",
  });
}