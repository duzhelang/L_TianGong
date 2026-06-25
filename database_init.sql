-- EcoCarbon-MRV 统一数据库初始化脚本
-- 基于 Entity 类生成，与 JPA 实体保持一致

CREATE DATABASE IF NOT EXISTS ecocarbon_mrv DEFAULT CHARACTER SET utf8mb4;
USE ecocarbon_mrv;

-- ==================== 系统管理表 ====================

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    role VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(32) NOT NULL UNIQUE,
    role_code VARCHAR(32) NOT NULL UNIQUE,
    description VARCHAR(128),
    status INT NOT NULL DEFAULT 1,
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    permission_name VARCHAR(64) NOT NULL,
    permission_code VARCHAR(64) NOT NULL UNIQUE,
    resource_type VARCHAR(32),
    resource_path VARCHAR(128),
    description VARCHAR(128),
    created_at DATETIME
);

CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at DATETIME
);

CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_at DATETIME
);

CREATE TABLE IF NOT EXISTS sys_audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    username VARCHAR(64),
    operation VARCHAR(32),
    method VARCHAR(128),
    params TEXT,
    ip VARCHAR(64),
    duration BIGINT,
    status INT,
    error_msg TEXT,
    created_at DATETIME
);

-- ==================== 碳项目管理表 ====================

CREATE TABLE IF NOT EXISTS carbon_project (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    scene_type VARCHAR(64),
    area_name VARCHAR(128),
    area_ha DOUBLE,
    description VARCHAR(512),
    status VARCHAR(32) DEFAULT 'ACTIVE',
    methodology VARCHAR(64),
    baseline_year INT,
    target_reduction DOUBLE,
    start_date DATE,
    end_date DATE,
    contact_person VARCHAR(64),
    contact_phone VARCHAR(32),
    created_by BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ==================== 排放因子与计算表 ====================

CREATE TABLE IF NOT EXISTS emission_factor (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    factor_code VARCHAR(64) NOT NULL UNIQUE,
    factor_name VARCHAR(128) NOT NULL,
    category VARCHAR(64) NOT NULL,
    subcategory VARCHAR(64),
    gas_type VARCHAR(32) NOT NULL,
    factor_value DOUBLE NOT NULL,
    unit VARCHAR(64) NOT NULL,
    source VARCHAR(256),
    region VARCHAR(64),
    year INT,
    status VARCHAR(32) DEFAULT 'ACTIVE',
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS activity_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    data_type VARCHAR(64) NOT NULL,
    activity_category VARCHAR(64) NOT NULL,
    activity_subcategory VARCHAR(64),
    value DOUBLE NOT NULL,
    unit VARCHAR(64) NOT NULL,
    period_start DATE,
    period_end DATE,
    source VARCHAR(256),
    data_quality VARCHAR(32),
    status VARCHAR(32) DEFAULT 'ACTIVE',
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (project_id) REFERENCES carbon_project(id)
);

CREATE TABLE IF NOT EXISTS carbon_emission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    activity_data_id BIGINT,
    emission_factor_id BIGINT,
    scope VARCHAR(32) NOT NULL,
    gas_type VARCHAR(32) NOT NULL,
    emission_value DOUBLE NOT NULL,
    unit VARCHAR(64) NOT NULL,
    calculation_method VARCHAR(64),
    period_start DATE,
    period_end DATE,
    status VARCHAR(32) DEFAULT 'ACTIVE',
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (project_id) REFERENCES carbon_project(id),
    FOREIGN KEY (activity_data_id) REFERENCES activity_data(id),
    FOREIGN KEY (emission_factor_id) REFERENCES emission_factor(id)
);

CREATE TABLE IF NOT EXISTS methodology_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    methodology_code VARCHAR(64) NOT NULL UNIQUE,
    methodology_name VARCHAR(128) NOT NULL,
    version VARCHAR(32),
    description TEXT,
    applicable_scenes VARCHAR(256),
    calculation_formula TEXT,
    parameters_config JSON,
    reference_standard VARCHAR(256),
    status VARCHAR(32) DEFAULT 'ACTIVE',
    created_at DATETIME,
    updated_at DATETIME
);

-- ==================== 遥感数据表 ====================

CREATE TABLE IF NOT EXISTS remote_sensing_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    data_type VARCHAR(64) NOT NULL,
    satellite_source VARCHAR(64),
    acquisition_date DATE NOT NULL,
    resolution VARCHAR(32),
    cloud_cover DOUBLE,
    ndvi_value DOUBLE,
    lai_value DOUBLE,
    biomass_value DOUBLE,
    file_path VARCHAR(512),
    status VARCHAR(32) DEFAULT 'ACTIVE',
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (project_id) REFERENCES carbon_project(id)
);

CREATE TABLE IF NOT EXISTS remote_sensing_index (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    area_id VARCHAR(255) NOT NULL,
    index_type VARCHAR(255) NOT NULL,
    value DOUBLE NOT NULL,
    unit VARCHAR(255),
    capture_date DATE NOT NULL,
    resolution VARCHAR(255),
    source VARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS npp_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    area_id VARCHAR(255) NOT NULL,
    year INT NOT NULL,
    month INT,
    npp_value DOUBLE NOT NULL,
    latitude DOUBLE,
    longitude DOUBLE,
    data_source VARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS land_use_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    area_id VARCHAR(255) NOT NULL,
    year INT NOT NULL,
    land_type VARCHAR(255) NOT NULL,
    area_ha DOUBLE,
    latitude DOUBLE,
    longitude DOUBLE,
    created_at DATETIME,
    updated_at DATETIME
);

-- ==================== 碳交易管理表 ====================

CREATE TABLE IF NOT EXISTS carbon_asset (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    asset_code VARCHAR(64) NOT NULL UNIQUE,
    asset_name VARCHAR(128) NOT NULL,
    asset_type VARCHAR(32) NOT NULL,
    project_id BIGINT,
    methodology_id BIGINT,
    quantity DOUBLE NOT NULL,
    unit VARCHAR(16) NOT NULL DEFAULT 'tCO2e',
    vintage_year INT,
    issuance_date DATE,
    expiry_date DATE,
    registry_id VARCHAR(64),
    serial_number VARCHAR(128),
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    owner_id BIGINT,
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS carbon_price (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    exchange VARCHAR(32) NOT NULL,
    product_code VARCHAR(32) NOT NULL,
    product_name VARCHAR(64),
    price DOUBLE NOT NULL,
    open_price DOUBLE,
    close_price DOUBLE,
    high_price DOUBLE,
    low_price DOUBLE,
    volume BIGINT,
    turnover DOUBLE,
    price_date DATE NOT NULL,
    price_time TIME,
    created_at DATETIME
);

CREATE TABLE IF NOT EXISTS carbon_transaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_code VARCHAR(64) NOT NULL UNIQUE,
    asset_id BIGINT NOT NULL,
    order_type VARCHAR(16) NOT NULL,
    quantity DOUBLE NOT NULL,
    price DOUBLE NOT NULL,
    total_amount DOUBLE NOT NULL,
    currency VARCHAR(16) NOT NULL DEFAULT 'CNY',
    exchange VARCHAR(32),
    counterparty_id BIGINT,
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    submitted_at DATETIME,
    matched_at DATETIME,
    settled_at DATETIME,
    created_by BIGINT,
    created_at DATETIME,
    updated_at DATETIME
);

-- ==================== IoT设备管理表 ====================

CREATE TABLE IF NOT EXISTS iot_device (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    device_code VARCHAR(64) NOT NULL UNIQUE,
    device_name VARCHAR(128) NOT NULL,
    device_type VARCHAR(32) NOT NULL,
    product_key VARCHAR(64),
    project_id BIGINT,
    location VARCHAR(128),
    longitude DOUBLE,
    latitude DOUBLE,
    status VARCHAR(16) NOT NULL DEFAULT 'OFFLINE',
    last_online_at DATETIME,
    config_json TEXT,
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS iot_alarm_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rule_name VARCHAR(128) NOT NULL,
    device_type VARCHAR(32),
    device_code VARCHAR(64),
    metric VARCHAR(32) NOT NULL,
    `condition` VARCHAR(32) NOT NULL,
    threshold_value DOUBLE NOT NULL,
    threshold_value2 DOUBLE,
    severity VARCHAR(16) NOT NULL,
    enabled INT NOT NULL DEFAULT 1,
    created_at DATETIME
);

CREATE TABLE IF NOT EXISTS iot_alarm_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rule_id BIGINT NOT NULL,
    device_code VARCHAR(64) NOT NULL,
    metric VARCHAR(32) NOT NULL,
    actual_value DOUBLE,
    threshold_value DOUBLE,
    severity VARCHAR(16) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    acknowledged_by BIGINT,
    acknowledged_at DATETIME,
    resolved_at DATETIME,
    created_at DATETIME
);

CREATE TABLE IF NOT EXISTS sensor_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    device_code VARCHAR(64) NOT NULL,
    device_type VARCHAR(32),
    metric VARCHAR(32) NOT NULL,
    value DOUBLE NOT NULL,
    unit VARCHAR(16),
    quality VARCHAR(16) DEFAULT 'NORMAL',
    timestamp DATETIME NOT NULL,
    created_at DATETIME,
    INDEX idx_sensor_device_time (device_code, timestamp),
    INDEX idx_sensor_metric_time (metric, timestamp),
    INDEX idx_sensor_timestamp (timestamp)
);

CREATE TABLE IF NOT EXISTS energy_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    energy_type VARCHAR(32) NOT NULL,
    energy_category VARCHAR(32) NOT NULL,
    consumption DOUBLE NOT NULL,
    unit VARCHAR(16) NOT NULL,
    emission_factor DOUBLE NOT NULL,
    emission_unit VARCHAR(16),
    carbon_emission DOUBLE NOT NULL,
    record_date DATE NOT NULL,
    source VARCHAR(64),
    quality VARCHAR(16) DEFAULT 'NORMAL',
    remark VARCHAR(256),
    created_at DATETIME,
    updated_at DATETIME,
    INDEX idx_energy_project_date (project_id, record_date),
    INDEX idx_energy_type_date (energy_type, record_date),
    FOREIGN KEY (project_id) REFERENCES carbon_project(id)
);

-- ==================== MRV报告表 ====================

CREATE TABLE IF NOT EXISTS mrv_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    report_name VARCHAR(128) NOT NULL,
    report_type VARCHAR(32) NOT NULL,
    template_id BIGINT,
    file_path VARCHAR(256),
    file_size BIGINT,
    status VARCHAR(16) NOT NULL,
    version INT NOT NULL,
    created_by BIGINT,
    reviewed_by BIGINT,
    reviewed_at DATETIME,
    created_at DATETIME,
    updated_at DATETIME
);

-- ==================== 工作流表 ====================

CREATE TABLE IF NOT EXISTS workflow_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_name VARCHAR(128) NOT NULL,
    task_type VARCHAR(32) NOT NULL,
    project_id BIGINT,
    report_id BIGINT,
    status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    priority VARCHAR(16) DEFAULT 'MEDIUM',
    assigned_to BIGINT,
    due_date DATETIME,
    description TEXT,
    created_by BIGINT,
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS workflow_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL,
    action VARCHAR(32) NOT NULL,
    operator_id BIGINT,
    operator_name VARCHAR(64),
    comment TEXT,
    created_at DATETIME
);

-- ==================== 报告模板表 ====================

CREATE TABLE IF NOT EXISTS report_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_code VARCHAR(32) NOT NULL UNIQUE,
    template_name VARCHAR(128) NOT NULL,
    report_type VARCHAR(32) NOT NULL,
    template_path VARCHAR(256),
    description TEXT,
    status VARCHAR(16) DEFAULT 'ACTIVE',
    created_at DATETIME,
    updated_at DATETIME
);

-- ==================== 初始数据 ====================

-- 用户数据
INSERT INTO sys_user (username, password, email, role, status, created_at) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@ecocarbon.com', 'ADMIN', 'ACTIVE', NOW()),
('carbon_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'manager@ecocarbon.com', 'MANAGER', 'ACTIVE', NOW()),
('auditor', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'auditor@ecocarbon.com', 'AUDITOR', 'ACTIVE', NOW()),
('user01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'user01@ecocarbon.com', 'USER', 'ACTIVE', NOW());

-- 碳项目数据
INSERT INTO carbon_project (name, scene_type, area_name, area_ha, description, status, methodology, baseline_year, target_reduction, start_date, end_date, contact_person, contact_phone, created_by, created_at) VALUES
('华北平原农业碳汇项目', 'AGRICULTURE', '山东省济南市', 1000.00, '华北平原典型农业碳汇项目，主要种植冬小麦和夏玉米，推广保护性耕作和秸秆还田技术', 'ACTIVE', 'CCER', 2023, 15.0, '2024-01-01', '2028-12-31', '张明', '13800138001', 1, NOW()),
('南方水稻甲烷减排项目', 'AGRICULTURE', '湖南省长沙市', 500.00, '南方水稻种植区甲烷减排项目，采用间歇灌溉技术减少稻田甲烷排放', 'ACTIVE', 'CCER', 2023, 20.0, '2024-03-01', '2027-12-31', '李华', '13800138002', 1, NOW()),
('东北林业碳汇项目', 'FORESTRY', '黑龙江省哈尔滨市', 5000.00, '东北林区人工造林碳汇项目，种植杨树和落叶松', 'ACTIVE', 'VCS', 2022, 25.0, '2023-04-01', '2033-03-31', '王伟', '13800138003', 2, NOW()),
('西北草原碳汇项目', 'GRASSLAND', '内蒙古自治区呼和浩特市', 10000.00, '西北草原生态修复碳汇项目，通过围栏封育和补播改良恢复草原植被', 'DRAFT', 'CCER', 2024, 10.0, '2025-01-01', '2030-12-31', '赵强', '13800138004', 2, NOW()),
('海南热带雨林保护项目', 'FORESTRY', '海南省五指山市', 8000.00, '热带雨林生物多样性保护与碳汇提升项目', 'ACTIVE', 'VCS', 2023, 30.0, '2024-06-01', '2034-05-31', '陈晓', '13800138005', 1, NOW()),
('江苏沿海湿地碳汇项目', 'WETLAND', '江苏省盐城市', 3000.00, '沿海湿地生态系统保护与修复项目，提升蓝碳储量', 'ACTIVE', 'CCER', 2023, 18.0, '2024-01-01', '2029-12-31', '刘芳', '13800138006', 2, NOW()),
('新疆光伏治沙项目', 'AGRICULTURE', '新疆维吾尔自治区库尔勒市', 2000.00, '光伏发电与沙漠治理相结合的碳减排项目', 'COMPLETED', 'CCER', 2021, 35.0, '2022-01-01', '2024-12-31', '孙磊', '13800138007', 1, NOW()),
('云南普洱茶园碳汇项目', 'AGRICULTURE', '云南省普洱市', 1500.00, '生态茶园建设与碳汇提升项目，推广有机种植', 'ACTIVE', 'CCER', 2023, 12.0, '2024-04-01', '2029-03-31', '周婷', '13800138008', 2, NOW());

-- 排放因子数据
INSERT INTO emission_factor (factor_code, factor_name, category, subcategory, gas_type, factor_value, unit, source, region, year, status) VALUES
('EF-COAL-001', '无烟煤燃烧排放因子', 'ENERGY', NULL, 'CO2', 2.66, 'kgCO2/kg', 'IPCC 2006', '中国', 2024, 'ACTIVE'),
('EF-COAL-002', '烟煤燃烧排放因子', 'ENERGY', NULL, 'CO2', 2.42, 'kgCO2/kg', 'IPCC 2006', '中国', 2024, 'ACTIVE'),
('EF-OIL-001', '柴油燃烧排放因子', 'ENERGY', NULL, 'CO2', 2.68, 'kgCO2/L', 'IPCC 2006', '中国', 2024, 'ACTIVE'),
('EF-OIL-002', '汽油燃烧排放因子', 'ENERGY', NULL, 'CO2', 2.30, 'kgCO2/L', 'IPCC 2006', '中国', 2024, 'ACTIVE'),
('EF-GAS-001', '天然气燃烧排放因子', 'ENERGY', NULL, 'CO2', 2.16, 'kgCO2/m3', 'IPCC 2006', '中国', 2024, 'ACTIVE'),
('EF-ELEC-001', '全国电网排放因子', 'ELECTRICITY', NULL, 'CO2', 0.5810, 'kgCO2/kWh', '生态环境部', '中国', 2024, 'ACTIVE'),
('EF-ELEC-002', '华北电网排放因子', 'ELECTRICITY', NULL, 'CO2', 0.8843, 'kgCO2/kWh', '生态环境部', '华北', 2024, 'ACTIVE'),
('EF-AGRI-001', '稻田甲烷排放因子', 'AGRICULTURE', NULL, 'CH4', 1.00, 'kgCH4/m2', 'CCER方法学', '中国', 2024, 'ACTIVE'),
('EF-AGRI-002', '化肥氧化亚氮排放因子', 'AGRICULTURE', NULL, 'N2O', 0.01, 'kgN2O/kgN', 'IPCC 2006', '中国', 2024, 'ACTIVE'),
('EF-AGRI-003', '牛肠道发酵甲烷排放因子', 'AGRICULTURE', NULL, 'CH4', 56.00, 'kgCH4/head/year', 'IPCC 2006', '中国', 2024, 'ACTIVE'),
('EF-FOREST-001', '杨树碳汇因子', 'FORESTRY', NULL, 'CO2', 5.50, 'tCO2/ha/year', 'CCER方法学', '中国', 2024, 'ACTIVE'),
('EF-FOREST-002', '松树碳汇因子', 'FORESTRY', NULL, 'CO2', 4.80, 'tCO2/ha/year', 'CCER方法学', '中国', 2024, 'ACTIVE');

-- 活动数据
INSERT INTO activity_data (project_id, data_type, data_value, unit, period_start, period_end, source, status, created_at) VALUES
(1, 'FUEL_DIESEL', 5000.00, 'L', '2024-01-01', '2024-12-31', '手动录入', 'ACTIVE', NOW()),
(1, 'FUEL_GASOLINE', 2000.00, 'L', '2024-01-01', '2024-12-31', '手动录入', 'ACTIVE', NOW()),
(1, 'ELECTRICITY', 100000.00, 'kWh', '2024-01-01', '2024-12-31', '电力账单', 'ACTIVE', NOW()),
(1, 'FERTILIZER_N', 200.00, 'ton', '2024-01-01', '2024-12-31', '采购记录', 'ACTIVE', NOW()),
(2, 'PADDY_AREA', 500.00, 'ha', '2024-01-01', '2024-12-31', '实地测量', 'ACTIVE', NOW()),
(2, 'IRRIGATION', 300000.00, 'm3', '2024-01-01', '2024-12-31', '灌溉记录', 'ACTIVE', NOW()),
(3, 'FOREST_AREA', 5000.00, 'ha', '2024-01-01', '2024-12-31', '遥感数据', 'ACTIVE', NOW()),
(3, 'TREE_VOLUME', 25000.00, 'm3', '2024-01-01', '2024-12-31', '实地测量', 'ACTIVE', NOW());

-- 碳排放数据
INSERT INTO carbon_emission (project_id, activity_data_id, emission_factor_id, scope, gas_type, emission_value, unit, calculation_method, period_start, period_end, status, created_at) VALUES
(1, 1, 3, 'SCOPE_1', 'CO2', 13400.00, 'kgCO2', 'IPCC-2006', '2024-01-01', '2024-12-31', 'ACTIVE', NOW()),
(1, 2, 4, 'SCOPE_1', 'CO2', 4600.00, 'kgCO2', 'IPCC-2006', '2024-01-01', '2024-12-31', 'ACTIVE', NOW()),
(1, 3, 6, 'SCOPE_2', 'CO2', 58100.00, 'kgCO2', 'IPCC-2006', '2024-01-01', '2024-12-31', 'ACTIVE', NOW()),
(1, 4, 9, 'SCOPE_1', 'N2O', 2000.00, 'kgN2O', 'IPCC-2006', '2024-01-01', '2024-12-31', 'ACTIVE', NOW()),
(2, 5, 8, 'SCOPE_1', 'CH4', 500.00, 'kgCH4', 'CCER-AGRI', '2024-01-01', '2024-12-31', 'ACTIVE', NOW()),
(3, 7, 11, 'SEQUESTRATION', 'CO2', -27500.00, 'tCO2', 'CCER-FOREST', '2024-01-01', '2024-12-31', 'ACTIVE', NOW());

-- 方法学配置
INSERT INTO methodology_config (methodology_code, methodology_name, version, description, applicable_scenes, calculation_formula, parameters_config, reference_standard, status, created_at) VALUES
('IPCC-2006', 'IPCC 2006指南', '2006', '基于IPCC 2006年国家温室气体清单指南的核算方法', 'GENERAL', NULL, '{"tier": 1, "gwp_source": "AR5"}', 'IPCC 2006', 'ACTIVE', NOW()),
('CCER-AGRI-001', 'CCER农业方法学', '2023', '中国核证自愿减排量农业项目方法学', 'AGRICULTURE', NULL, '{"methodology_type": "CCER", "project_type": "agriculture"}', 'CCER', 'ACTIVE', NOW()),
('CCER-FOREST-001', 'CCER林业方法学', '2023', '中国核证自愿减排量林业碳汇项目方法学', 'FORESTRY', NULL, '{"methodology_type": "CCER", "project_type": "forestry"}', 'CCER', 'ACTIVE', NOW()),
('VCS-VM001', 'VCS VM001方法学', '2023', '自愿碳标准VM001造林再造林方法学', 'FORESTRY', NULL, '{"methodology_type": "VCS", "project_type": "afforestation"}', 'VCS', 'ACTIVE', NOW());

-- IoT设备数据
INSERT INTO iot_device (device_code, device_name, device_type, product_key, project_id, location, longitude, latitude, status, last_online_at, config_json, created_at, updated_at) VALUES
('WS-001', '气象站001', 'WEATHER', NULL, 1, '山东省济南市历城区', 117.02, 36.67, 'ONLINE', NOW(), NULL, NOW(), NOW()),
('WS-002', '气象站002', 'WEATHER', NULL, 2, '湖南省长沙市望城区', 112.93, 28.23, 'ONLINE', NOW(), NULL, NOW(), NOW()),
('SOIL-001', '土壤传感器001', 'SOIL', NULL, 1, '山东省济南市历城区', 117.02, 36.67, 'ONLINE', NOW(), NULL, NOW(), NOW()),
('GAS-001', '气体分析仪001', 'GAS', NULL, 1, '山东省济南市历城区', 117.02, 36.67, 'OFFLINE', NOW(), NULL, NOW(), NOW());

-- 碳资产数据
INSERT INTO carbon_asset (asset_code, asset_name, asset_type, project_id, methodology_id, quantity, unit, vintage_year, issuance_date, expiry_date, registry_id, serial_number, status, owner_id, created_at, updated_at) VALUES
('CCER-2024-001', '华北农业碳汇信用', 'CCER', 1, NULL, 1000.00, 'tCO2e', 2024, '2024-06-01', '2034-06-01', NULL, NULL, 'ACTIVE', 1, NOW(), NOW()),
('CCER-2024-002', '南方水稻减排信用', 'CCER', 2, NULL, 500.00, 'tCO2e', 2024, '2024-06-01', '2034-06-01', NULL, NULL, 'ACTIVE', 1, NOW(), NOW()),
('VCS-2024-001', '东北林业碳汇信用', 'VCS', 3, NULL, 5000.00, 'tCO2e', 2024, '2024-06-01', '2044-06-01', NULL, NULL, 'ACTIVE', 2, NOW(), NOW());

-- MRV报告数据
INSERT INTO mrv_report (project_id, report_name, report_type, template_id, file_path, file_size, status, version, created_by, reviewed_by, reviewed_at, created_at, updated_at) VALUES
(1, '2024年华北农业碳汇项目碳盘查报告', 'CARBON_INVENTORY', NULL, NULL, NULL, 'DRAFT', 1, 1, NULL, NULL, NOW(), NOW()),
(2, '2024年南方水稻减排项目MRV报告', 'CCER', NULL, NULL, NULL, 'SUBMITTED', 1, 2, NULL, NULL, NOW(), NOW()),
(3, '2024年东北林业碳汇项目核查报告', 'VCS', NULL, NULL, NULL, 'APPROVED', 1, 3, NULL, NULL, NOW(), NOW());

-- 碳价数据
INSERT INTO carbon_price (exchange, product_code, product_name, price, open_price, close_price, high_price, low_price, volume, turnover, price_date, price_time, created_at) VALUES
('全国碳市场', 'CEA', '碳排放配额', 85.50, 84.00, 85.50, 86.00, 83.50, 150000, 12825000, '2024-06-01', NULL, NOW()),
('全国碳市场', 'CEA', '碳排放配额', 86.20, 85.50, 86.20, 87.00, 85.00, 180000, 15516000, '2024-06-02', NULL, NOW()),
('全国碳市场', 'CEA', '碳排放配额', 87.00, 86.20, 87.00, 88.00, 86.00, 200000, 17400000, '2024-06-03', NULL, NOW()),
('全国碳市场', 'CCER', '核证自愿减排量', 45.00, 44.00, 45.00, 46.00, 43.50, 50000, 2250000, '2024-06-01', NULL, NOW()),
('全国碳市场', 'CCER', '核证自愿减排量', 46.50, 45.00, 46.50, 47.00, 44.50, 60000, 2790000, '2024-06-02', NULL, NOW());
