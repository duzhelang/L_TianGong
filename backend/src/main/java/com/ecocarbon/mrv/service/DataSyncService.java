package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.client.*;
import com.ecocarbon.mrv.entity.ActivityData;
import com.ecocarbon.mrv.entity.CarbonProject;
import com.ecocarbon.mrv.entity.EmissionFactor;
import com.ecocarbon.mrv.entity.RemoteSensingData;
import com.ecocarbon.mrv.repository.ActivityDataRepository;
import com.ecocarbon.mrv.repository.CarbonProjectRepository;
import com.ecocarbon.mrv.repository.EmissionFactorRepository;
import com.ecocarbon.mrv.repository.RemoteSensingDataRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSyncService {

    private final WeatherApiClient weatherApiClient;
    private final EmissionFactorApiClient emissionFactorApiClient;
    private final RemoteSensingApiClient remoteSensingApiClient;
    private final SoilDataApiClient soilDataApiClient;
    private final ActivityDataRepository activityDataRepository;
    private final EmissionFactorRepository emissionFactorRepository;
    private final RemoteSensingDataRepository remoteSensingDataRepository;
    private final CarbonProjectRepository carbonProjectRepository;

    @Async
    public void syncWeatherData(double longitude, double latitude, Long projectId) {
        try {
            log.info("开始同步气象数据: 项目={}, 坐标=({}, {})", projectId, longitude, latitude);

            CarbonProject project = carbonProjectRepository.findById(projectId).orElse(null);
            if (project == null) {
                log.error("项目不存在: {}", projectId);
                return;
            }

            JsonNode weatherData = weatherApiClient.getCurrentWeather(longitude, latitude);

            if (weatherData != null && weatherData.has("now")) {
                JsonNode now = weatherData.get("now");

                ActivityData tempData = new ActivityData();
                tempData.setProject(project);
                tempData.setDataType("TEMPERATURE");
                tempData.setActivityCategory("WEATHER");
                tempData.setValue(now.path("temp").asDouble());
                tempData.setUnit("°C");
                tempData.setPeriodStart(LocalDate.now());
                tempData.setSource("QWEATHER");
                tempData.setStatus("ACTIVE");
                tempData.setCreatedAt(LocalDateTime.now());
                activityDataRepository.save(tempData);

                ActivityData humidityData = new ActivityData();
                humidityData.setProject(project);
                humidityData.setDataType("HUMIDITY");
                humidityData.setActivityCategory("WEATHER");
                humidityData.setValue(now.path("humidity").asDouble());
                humidityData.setUnit("%");
                humidityData.setPeriodStart(LocalDate.now());
                humidityData.setSource("QWEATHER");
                humidityData.setStatus("ACTIVE");
                humidityData.setCreatedAt(LocalDateTime.now());
                activityDataRepository.save(humidityData);

                ActivityData windData = new ActivityData();
                windData.setProject(project);
                windData.setDataType("WIND_SPEED");
                windData.setActivityCategory("WEATHER");
                windData.setValue(now.path("windSpeed").asDouble());
                windData.setUnit("km/h");
                windData.setPeriodStart(LocalDate.now());
                windData.setSource("QWEATHER");
                windData.setStatus("ACTIVE");
                windData.setCreatedAt(LocalDateTime.now());
                activityDataRepository.save(windData);

                log.info("气象数据同步完成: 温度={}°C, 湿度={}%, 风速={}km/h",
                        now.path("temp").asDouble(),
                        now.path("humidity").asDouble(),
                        now.path("windSpeed").asDouble());
            }
        } catch (Exception e) {
            log.error("气象数据同步失败: {}", e.getMessage());
        }
    }

    @Async
    public void syncEmissionFactors(String keyword, String category) {
        try {
            log.info("开始同步排放因子数据: keyword={}, category={}", keyword, category);

            JsonNode factors = emissionFactorApiClient.searchEmissionFactors(keyword, category);

            if (factors != null && factors.has("data")) {
                JsonNode data = factors.get("data");
                for (JsonNode factor : data) {
                    EmissionFactor ef = new EmissionFactor();
                    ef.setFactorCode(UUID.randomUUID().toString());
                    ef.setFactorName(factor.path("name").asText(keyword));
                    ef.setCategory(factor.path("category").asText(category));
                    ef.setGasType(factor.path("gasType").asText("CO2"));
                    ef.setFactorValue(factor.path("value").asDouble());
                    ef.setUnit(factor.path("unit").asText("kgCO2e"));
                    ef.setSource(factor.path("source").asText("EPA"));
                    ef.setYear(factor.path("year").asInt(LocalDate.now().getYear()));
                    ef.setStatus("ACTIVE");
                    ef.setCreatedAt(LocalDateTime.now());
                    emissionFactorRepository.save(ef);
                }
                log.info("排放因子同步完成: {}条记录", data.size());
            }
        } catch (Exception e) {
            log.error("排放因子同步失败: {}", e.getMessage());
        }
    }

    @Async
    public void syncRemoteSensingData(double longitude, double latitude, Long projectId) {
        try {
            log.info("开始同步遥感数据: 项目={}, 坐标=({}, {})", projectId, longitude, latitude);

            CarbonProject project = carbonProjectRepository.findById(projectId).orElse(null);
            if (project == null) {
                log.error("项目不存在: {}", projectId);
                return;
            }

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(16);

            JsonNode ndviData = remoteSensingApiClient.getNDVIData(
                    longitude, latitude,
                    startDate.toString(), endDate.toString()
            );

            if (ndviData != null && ndviData.has("data")) {
                RemoteSensingData rsData = new RemoteSensingData();
                rsData.setProject(project);
                rsData.setDataType("NDVI");
                rsData.setSatelliteSource("MODIS");
                rsData.setNdviValue(ndviData.path("data").path("MOD13A2_061__1_km_16_days_NDVI").asDouble());
                rsData.setAcquisitionDate(LocalDate.now());
                rsData.setResolution("1km");
                rsData.setStatus("ACTIVE");
                rsData.setCreatedAt(LocalDateTime.now());
                remoteSensingDataRepository.save(rsData);

                log.info("NDVI数据同步完成: {}", rsData.getNdviValue());
            }

            JsonNode nppData = remoteSensingApiClient.getNPPData(
                    longitude, latitude,
                    endDate.getYear()
            );

            if (nppData != null && nppData.has("data")) {
                RemoteSensingData nppRsData = new RemoteSensingData();
                nppRsData.setProject(project);
                nppRsData.setDataType("NPP");
                nppRsData.setSatelliteSource("MODIS");
                nppRsData.setBiomassValue(nppData.path("data").path("MOD17A3HGF_061__NPP").asDouble());
                nppRsData.setAcquisitionDate(LocalDate.now());
                nppRsData.setResolution("500m");
                nppRsData.setStatus("ACTIVE");
                nppRsData.setCreatedAt(LocalDateTime.now());
                remoteSensingDataRepository.save(nppRsData);

                log.info("NPP数据同步完成: {}", nppRsData.getBiomassValue());
            }
        } catch (Exception e) {
            log.error("遥感数据同步失败: {}", e.getMessage());
        }
    }

    @Async
    public void syncSoilData(double longitude, double latitude, Long projectId) {
        try {
            log.info("开始同步土壤数据: 项目={}, 坐标=({}, {})", projectId, longitude, latitude);

            CarbonProject project = carbonProjectRepository.findById(projectId).orElse(null);
            if (project == null) {
                log.error("项目不存在: {}", projectId);
                return;
            }

            JsonNode soilOC = soilDataApiClient.getSoilOrganicCarbon(longitude, latitude);

            if (soilOC != null && soilOC.has("properties")) {
                JsonNode properties = soilOC.path("properties");
                JsonNode layers = properties.path("layers");

                for (JsonNode layer : layers) {
                    String name = layer.path("name").asText();
                    JsonNode depths = layer.path("depths");

                    for (JsonNode depth : depths) {
                        String depthLabel = depth.path("label").asText();
                        JsonNode values = depth.path("values");
                        double meanValue = values.path("mean").asDouble();

                        ActivityData soilData = new ActivityData();
                        soilData.setProject(project);
                        soilData.setDataType("SOIL_" + name.toUpperCase());
                        soilData.setActivityCategory("SOIL");
                        soilData.setValue(meanValue);
                        soilData.setUnit("dg/kg");
                        soilData.setPeriodStart(LocalDate.now());
                        soilData.setSource("SOILGRIDS");
                        soilData.setStatus("ACTIVE");
                        soilData.setCreatedAt(LocalDateTime.now());
                        activityDataRepository.save(soilData);

                        log.info("土壤{}数据同步完成 ({}): {}", name, depthLabel, meanValue);
                    }
                }
            }
        } catch (Exception e) {
            log.error("土壤数据同步失败: {}", e.getMessage());
        }
    }

    public void syncAllData(double longitude, double latitude, Long projectId) {
        log.info("开始全量数据同步: 项目={}, 坐标=({}, {})", projectId, longitude, latitude);

        syncWeatherData(longitude, latitude, projectId);
        syncRemoteSensingData(longitude, latitude, projectId);
        syncSoilData(longitude, latitude, projectId);

        log.info("全量数据同步已启动: 项目={}", projectId);
    }
}