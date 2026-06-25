-- EcoCarbon-MRV 数据库索引优化脚本
-- 提升查询性能

-- ============================================
-- 1. 用户表索引
-- ============================================

CREATE INDEX idx_user_username ON sys_user(username);
CREATE INDEX idx_user_role ON sys_user(role);
CREATE INDEX idx_user_status ON sys_user(status);

-- ============================================
-- 2. 碳项目表索引
-- ============================================

CREATE INDEX idx_project_scene_type ON carbon_project(scene_type);
CREATE INDEX idx_project_status ON carbon_project(status);
CREATE INDEX idx_project_created_by ON carbon_project(created_by);
CREATE INDEX idx_project_area_name ON carbon_project(area_name);

-- ============================================
-- 3. 排放因子表索引
-- ============================================

CREATE INDEX idx_factor_code ON emission_factor(factor_code);
CREATE INDEX idx_factor_category ON emission_factor(category);
CREATE INDEX idx_factor_gas_type ON emission_factor(gas_type);
CREATE INDEX idx_factor_region ON emission_factor(region);

-- ============================================
-- 4. 活动数据表索引
-- ============================================

CREATE INDEX idx_activity_project_id ON activity_data(project_id);
CREATE INDEX idx_activity_data_type ON activity_data(data_type);
CREATE INDEX idx_activity_period ON activity_data(period_start, period_end);
CREATE INDEX idx_activity_status ON activity_data(status);

-- ============================================
-- 5. 碳排放表索引
-- ============================================

CREATE INDEX idx_emission_project_id ON carbon_emission(project_id);
CREATE INDEX idx_emission_scope ON carbon_emission(scope);
CREATE INDEX idx_emission_gas_type ON carbon_emission(gas_type);
CREATE INDEX idx_emission_period ON carbon_emission(period_start, period_end);
CREATE INDEX idx_emission_status ON carbon_emission(status);

-- 复合索引：项目+范围+时间
CREATE INDEX idx_emission_project_scope_period ON carbon_emission(project_id, scope, period_start);

-- ============================================
-- 6. 方法学配置表索引
-- ============================================

CREATE INDEX idx_methodology_code ON methodology_config(methodology_code);
CREATE INDEX idx_methodology_scene_type ON methodology_config(scene_type);
CREATE INDEX idx_methodology_status ON methodology_config(status);

-- ============================================
-- 7. IoT设备表索引
-- ============================================

CREATE INDEX idx_device_code ON iot_device(device_code);
CREATE INDEX idx_device_type ON iot_device(device_type);
CREATE INDEX idx_device_project_id ON iot_device(project_id);
CREATE INDEX idx_device_status ON iot_device(status);

-- ============================================
-- 8. 告警规则表索引
-- ============================================

CREATE INDEX idx_alarm_rule_device_type ON iot_alarm_rule(device_type);
CREATE INDEX idx_alarm_rule_status ON iot_alarm_rule(status);

-- ============================================
-- 9. 告警记录表索引
-- ============================================

CREATE INDEX idx_alarm_record_device_code ON iot_alarm_record(device_code);
CREATE INDEX idx_alarm_record_rule_id ON iot_alarm_record(rule_id);
CREATE INDEX idx_alarm_record_status ON iot_alarm_record(status);
CREATE INDEX idx_alarm_record_trigger_time ON iot_alarm_record(trigger_time);

-- ============================================
-- 10. 碳资产表索引
-- ============================================

CREATE INDEX idx_asset_code ON carbon_asset(asset_code);
CREATE INDEX idx_asset_type ON carbon_asset(asset_type);
CREATE INDEX idx_asset_project_id ON carbon_asset(project_id);
CREATE INDEX idx_asset_owner_id ON carbon_asset(owner_id);
CREATE INDEX idx_asset_status ON carbon_asset(status);

-- ============================================
-- 11. 碳交易表索引
-- ============================================

CREATE INDEX idx_transaction_order_code ON carbon_transaction(order_code);
CREATE INDEX idx_transaction_asset_id ON carbon_transaction(asset_id);
CREATE INDEX idx_transaction_buyer_id ON carbon_transaction(buyer_id);
CREATE INDEX idx_transaction_seller_id ON carbon_transaction(seller_id);
CREATE INDEX idx_transaction_status ON carbon_transaction(status);
CREATE INDEX idx_transaction_order_type ON carbon_transaction(order_type);

-- ============================================
-- 12. 碳价表索引
-- ============================================

CREATE INDEX idx_price_exchange ON carbon_price(exchange);
CREATE INDEX idx_price_product_code ON carbon_price(product_code);
CREATE INDEX idx_price_date ON carbon_price(price_date);

-- 复合索引：交易所+产品+日期
CREATE INDEX idx_price_exchange_product_date ON carbon_price(exchange, product_code, price_date);

-- ============================================
-- 13. MRV报告表索引
-- ============================================

CREATE INDEX idx_report_project_id ON mrv_report(project_id);
CREATE INDEX idx_report_type ON mrv_report(report_type);
CREATE INDEX idx_report_status ON mrv_report(status);
CREATE INDEX idx_report_created_by ON mrv_report(created_by);

-- ============================================
-- 14. 审计日志表索引
-- ============================================

CREATE INDEX idx_audit_user_id ON sys_audit_log(user_id);
CREATE INDEX idx_audit_operation ON sys_audit_log(operation);
CREATE INDEX idx_audit_created_at ON sys_audit_log(created_at);

-- 复合索引：用户+时间
CREATE INDEX idx_audit_user_time ON sys_audit_log(user_id, created_at);

-- ============================================
-- 15. 遥感数据表索引
-- ============================================

CREATE INDEX idx_remote_sensing_area_id ON remote_sensing_data(area_id);
CREATE INDEX idx_remote_sensing_data_date ON remote_sensing_data(data_date);
CREATE INDEX idx_remote_sensing_index_type ON remote_sensing_data(index_type);
