import http from './http'

// 传感器数据
export const getSensorLatest = (deviceCode, metric) => http.get('/api/v1/sensor/latest', { params: { deviceCode, metric } })
export const getSensorHistory = (params) => http.get('/api/v1/sensor/history', { params })
export const getSensorStatistics = (params) => http.get('/api/v1/sensor/statistics', { params })
export const getDeviceLatestAll = (deviceCode) => http.get(`/api/v1/sensor/device/${deviceCode}/latest`)
export const postSensorData = (data) => http.post('/api/v1/sensor/data', data)

// Demo 控制
export const startDemo = () => http.post('/api/v1/sensor/demo/start')
export const stopDemo = () => http.post('/api/v1/sensor/demo/stop')
export const getDemoStatus = () => http.get('/api/v1/sensor/demo/status')

// 设备管理
export const getDevices = (page, size) => http.get('/api/v1/iot/devices', { params: { page, size } })
export const getOnlineDevices = () => http.get('/api/v1/iot/devices/online')
export const getDeviceAlarms = (deviceCode) => http.get(`/api/v1/iot/alarms/records/device/${deviceCode}`)
export const getActiveAlarms = () => http.get('/api/v1/iot/alarms/records/active')
export const getActiveAlarmCount = () => http.get('/api/v1/iot/alarms/count/active')