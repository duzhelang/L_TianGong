import http from "./http";

// 获取碳资产列表
export function getAssets(params) {
  return http.get("/api/v1/trading/assets", { params });
}

// 获取资产详情
export function getAssetById(id) {
  return http.get(`/api/v1/trading/assets/${id}`);
}

// 创建碳资产
export function createAsset(data) {
  return http.post("/api/v1/trading/assets", data);
}

// 更新碳资产
export function updateAsset(id, data) {
  return http.put(`/api/v1/trading/assets/${id}`, data);
}

// 删除碳资产
export function deleteAsset(id) {
  return http.delete(`/api/v1/trading/assets/${id}`);
}

// 获取交易记录
export function getTransactions(params) {
  return http.get("/api/v1/trading/transactions", { params });
}

// 创建交易订单
export function createTransaction(data) {
  return http.post("/api/v1/trading/transactions", data);
}

// 获取交易详情
export function getTransactionById(id) {
  return http.get(`/api/v1/trading/transactions/${id}`);
}

// 取消交易
export function cancelTransaction(id) {
  return http.put(`/api/v1/trading/transactions/${id}/cancel`);
}

// 获取碳价行情
export function getCarbonPrices(params) {
  return http.get("/api/v1/trading/prices", { params });
}

// 获取碳价趋势
export function getCarbonPriceTrend(params) {
  return http.get("/api/v1/trading/prices/trend", { params });
}

// 获取交易统计
export function getTradingStats() {
  return http.get("/api/v1/trading/stats");
}

// 获取交易所列表
export function getExchanges() {
  return http.get("/api/v1/trading/exchanges");
}
