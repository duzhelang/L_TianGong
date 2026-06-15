USE sznl;

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