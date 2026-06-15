package com.ecocarbon.mrv.config;

import com.ecocarbon.mrv.entity.EmissionFactor;
import com.ecocarbon.mrv.entity.LandUseData;
import com.ecocarbon.mrv.entity.NPPData;
import com.ecocarbon.mrv.entity.RemoteSensingIndex;
import com.ecocarbon.mrv.entity.User;
import com.ecocarbon.mrv.repository.EmissionFactorRepository;
import com.ecocarbon.mrv.repository.LandUseDataRepository;
import com.ecocarbon.mrv.repository.NPPDataRepository;
import com.ecocarbon.mrv.repository.RemoteSensingIndexRepository;
import com.ecocarbon.mrv.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final EmissionFactorRepository emissionFactorRepository;
    private final RemoteSensingIndexRepository remoteSensingIndexRepository;
    private final NPPDataRepository nppDataRepository;
    private final LandUseDataRepository landUseDataRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }

        if (emissionFactorRepository.count() == 0) {
            initEmissionFactors();
        }

        if (remoteSensingIndexRepository.count() == 0) {
            initRemoteSensingData();
        }
    }

    private void initEmissionFactors() {
        initFuelFactors();
        initElectricityFactors();
        initFertilizerFactors();
        initLivestockFactors();
        initAgricultureFactors();
    }

    private void initFuelFactors() {
        createFactor("FUEL_DIESEL", "柴油", "FUEL", "COMBUSTION", "CO2", 3.17, "kgCO2/liter", "IPCC", "CHINA", 2023);
        createFactor("FUEL_GASOLINE", "汽油", "FUEL", "COMBUSTION", "CO2", 2.93, "kgCO2/liter", "IPCC", "CHINA", 2023);
        createFactor("FUEL_NATURAL_GAS", "天然气", "FUEL", "COMBUSTION", "CO2", 2.16, "kgCO2/m3", "IPCC", "CHINA", 2023);
        createFactor("FUEL_LPG", "液化石油气", "FUEL", "COMBUSTION", "CO2", 2.99, "kgCO2/kg", "IPCC", "CHINA", 2023);
        createFactor("FUEL_FUEL_OIL", "燃料油", "FUEL", "COMBUSTION", "CO2", 3.07, "kgCO2/liter", "IPCC", "CHINA", 2023);
        createFactor("FUEL_COAL", "煤炭", "FUEL", "COMBUSTION", "CO2", 2.66, "kgCO2/kg", "IPCC", "CHINA", 2023);
        createFactor("FUEL_ETHANOL", "乙醇", "FUEL", "COMBUSTION", "CO2", 1.91, "kgCO2/liter", "IPCC", "CHINA", 2023);
        createFactor("FUEL_BIODIESEL", "生物柴油", "FUEL", "COMBUSTION", "CO2", 2.54, "kgCO2/liter", "IPCC", "CHINA", 2023);
    }

    private void initElectricityFactors() {
        createFactor("ELEC_NATIONAL_GRID", "国家电网", "ELECTRICITY", "GRID", "CO2", 0.5703, "kgCO2/kWh", "CEMS", "CHINA", 2023);
        createFactor("ELEC_COAL_POWER", "煤电", "ELECTRICITY", "GENERATION", "CO2", 0.8842, "kgCO2/kWh", "CEMS", "CHINA", 2023);
        createFactor("ELEC_GAS_POWER", "气电", "ELECTRICITY", "GENERATION", "CO2", 0.4284, "kgCO2/kWh", "CEMS", "CHINA", 2023);
        createFactor("ELEC_RENEWABLE", "可再生能源", "ELECTRICITY", "GENERATION", "CO2", 0.0, "kgCO2/kWh", "CEMS", "CHINA", 2023);
        createFactor("ELEC_SOLAR", "太阳能", "ELECTRICITY", "GENERATION", "CO2", 0.0, "kgCO2/kWh", "CEMS", "CHINA", 2023);
        createFactor("ELEC_WIND", "风能", "ELECTRICITY", "GENERATION", "CO2", 0.0, "kgCO2/kWh", "CEMS", "CHINA", 2023);
        createFactor("ELEC_HYBRID", "混合电力", "ELECTRICITY", "GENERATION", "CO2", 0.35, "kgCO2/kWh", "CEMS", "CHINA", 2023);
    }

    private void initFertilizerFactors() {
        createFactor("FERT_UREA", "尿素", "FERTILIZER", "SYNTHETIC", "N2O", 0.46, "kgN/kg", "IPCC", "CHINA", 2023);
        createFactor("FERT_AMMONIUM_BICARBONATE", "碳酸氢铵", "FERTILIZER", "SYNTHETIC", "N2O", 0.17, "kgN/kg", "IPCC", "CHINA", 2023);
        createFactor("FERT_AMMONIUM_SULFATE", "硫酸铵", "FERTILIZER", "SYNTHETIC", "N2O", 0.21, "kgN/kg", "IPCC", "CHINA", 2023);
        createFactor("FERT_AMMONIUM_NITRATE", "硝酸铵", "FERTILIZER", "SYNTHETIC", "N2O", 0.34, "kgN/kg", "IPCC", "CHINA", 2023);
        createFactor("FERT_COMPOUND", "复合肥", "FERTILIZER", "SYNTHETIC", "N2O", 0.15, "kgN/kg", "IPCC", "CHINA", 2023);
        createFactor("FERT_ORGANIC", "有机肥", "FERTILIZER", "ORGANIC", "N2O", 0.02, "kgN/kg", "IPCC", "CHINA", 2023);
        createFactor("FERT_FARMYARD", "农家肥", "FERTILIZER", "ORGANIC", "N2O", 0.008, "kgN/kg", "IPCC", "CHINA", 2023);
        createFactor("FERT_COMPOST", "堆肥", "FERTILIZER", "ORGANIC", "N2O", 0.01, "kgN/kg", "IPCC", "CHINA", 2023);
        createFactor("FERT_GREEN_MANURE", "绿肥", "FERTILIZER", "ORGANIC", "N2O", 0.005, "kgN/kg", "IPCC", "CHINA", 2023);
    }

    private void initLivestockFactors() {
        createFactor("LIVE_DAIRY_CATTLE_ENTERIC", "奶牛肠道发酵", "LIVESTOCK", "ENTERIC", "CH4", 117.0, "kgCH4/head/yr", "IPCC", "CHINA", 2023);
        createFactor("LIVE_BEEF_CATTLE_ENTERIC", "肉牛肠道发酵", "LIVESTOCK", "ENTERIC", "CH4", 53.0, "kgCH4/head/yr", "IPCC", "CHINA", 2023);
        createFactor("LIVE_SHEEP_ENTERIC", "羊肠道发酵", "LIVESTOCK", "ENTERIC", "CH4", 8.0, "kgCH4/head/yr", "IPCC", "CHINA", 2023);
        createFactor("LIVE_GOATS_ENTERIC", "山羊肠道发酵", "LIVESTOCK", "ENTERIC", "CH4", 5.0, "kgCH4/head/yr", "IPCC", "CHINA", 2023);
        createFactor("LIVE_PIGS_ENTERIC", "猪肠道发酵", "LIVESTOCK", "ENTERIC", "CH4", 1.5, "kgCH4/head/yr", "IPCC", "CHINA", 2023);
        createFactor("LIVE_DAIRY_CATTLE_MANURE", "奶牛粪便管理", "LIVESTOCK", "MANURE", "CH4", 11.0, "kgCH4/head/yr", "IPCC", "CHINA", 2023);
        createFactor("LIVE_BEEF_CATTLE_MANURE", "肉牛粪便管理", "LIVESTOCK", "MANURE", "CH4", 1.5, "kgCH4/head/yr", "IPCC", "CHINA", 2023);
        createFactor("LIVE_PIGS_MANURE", "猪粪便管理", "LIVESTOCK", "MANURE", "CH4", 3.0, "kgCH4/head/yr", "IPCC", "CHINA", 2023);
        createFactor("LIVE_POULTRY_MANURE", "家禽粪便管理", "LIVESTOCK", "MANURE", "CH4", 0.12, "kgCH4/head/yr", "IPCC", "CHINA", 2023);
    }

    private void initAgricultureFactors() {
        createFactor("AGRI_PADDY_CH4", "稻田甲烷排放", "AGRICULTURE", "PADDY", "CH4", 1.3, "kgCH4/ha", "IPCC", "CHINA", 2023);
        createFactor("AGRI_N2O_DIRECT_SYNTHETIC", "化肥直接N2O排放", "AGRICULTURE", "FERTILIZER", "N2O", 0.01, "kgN2O/kgN", "IPCC", "CHINA", 2023);
        createFactor("AGRI_N2O_DIRECT_ORGANIC", "有机肥直接N2O排放", "AGRICULTURE", "FERTILIZER", "N2O", 0.006, "kgN2O/kgN", "IPCC", "CHINA", 2023);
        createFactor("AGRI_N2O_DIRECT_RESIDUE", "秸秆直接N2O排放", "AGRICULTURE", "RESIDUE", "N2O", 0.002, "kgN2O/kgN", "IPCC", "CHINA", 2023);
        createFactor("AGRI_N2O_INDIRECT_ATMOSPHERIC", "大气沉降间接N2O", "AGRICULTURE", "INDIRECT", "N2O", 0.01, "kgN2O/kgN", "IPCC", "CHINA", 2023);
        createFactor("AGRI_N2O_INDIRECT_LEACHING", "淋溶间接N2O", "AGRICULTURE", "INDIRECT", "N2O", 0.006, "kgN2O/kgN", "IPCC", "CHINA", 2023);
        createFactor("AGRI_SOC_STRAW_RETURN", "秸秆还田固碳", "AGRICULTURE", "SOIL_CARBON", "CO2", 0.3, "tC/t秸秆", "IPCC", "CHINA", 2023);
        createFactor("AGRI_SOC_NO_TILL", "免耕固碳", "AGRICULTURE", "SOIL_CARBON", "CO2", 0.30, "tC/ha/yr", "IPCC", "CHINA", 2023);
    }

    private void createFactor(String code, String name, String category, String subcategory,
                               String gasType, double value, String unit, String source,
                               String region, int year) {
        EmissionFactor factor = new EmissionFactor();
        factor.setFactorCode(code);
        factor.setFactorName(name);
        factor.setCategory(category);
        factor.setSubcategory(subcategory);
        factor.setGasType(gasType);
        factor.setFactorValue(value);
        factor.setUnit(unit);
        factor.setSource(source);
        factor.setRegion(region);
        factor.setYear(year);
        factor.setStatus("ACTIVE");
        factor.setCreatedAt(LocalDateTime.now());
        factor.setUpdatedAt(LocalDateTime.now());
        emissionFactorRepository.save(factor);
    }

    private void initRemoteSensingData() {
        initNDVIData();
        initNPPData();
        initLandUseData();
    }

    private void initNDVIData() {
        String areaId = "AREA_001";
        double[] ndviValues = {0.35, 0.42, 0.55, 0.68, 0.72, 0.65, 0.58, 0.62, 0.70, 0.45, 0.38, 0.32};
        for (int i = 0; i < 12; i++) {
            RemoteSensingIndex index = new RemoteSensingIndex();
            index.setAreaId(areaId);
            index.setIndexType("NDVI");
            index.setValue(ndviValues[i]);
            index.setUnit("无量纲");
            index.setCaptureDate(LocalDate.of(2024, i + 1, 15));
            index.setResolution("30m");
            index.setSource("Landsat-8");
            index.setCreatedAt(LocalDateTime.now());
            index.setUpdatedAt(LocalDateTime.now());
            remoteSensingIndexRepository.save(index);
        }

        double[] ndviValues2 = {0.28, 0.35, 0.48, 0.62, 0.75, 0.80, 0.72, 0.68, 0.55, 0.42, 0.35, 0.25};
        String areaId2 = "AREA_002";
        for (int i = 0; i < 12; i++) {
            RemoteSensingIndex index = new RemoteSensingIndex();
            index.setAreaId(areaId2);
            index.setIndexType("NDVI");
            index.setValue(ndviValues2[i]);
            index.setUnit("无量纲");
            index.setCaptureDate(LocalDate.of(2024, i + 1, 15));
            index.setResolution("30m");
            index.setSource("Landsat-8");
            index.setCreatedAt(LocalDateTime.now());
            index.setUpdatedAt(LocalDateTime.now());
            remoteSensingIndexRepository.save(index);
        }
    }

    private void initNPPData() {
        String areaId = "AREA_001";
        double[] nppValues = {45.2, 52.8, 78.5, 125.3, 168.7, 142.5, 98.6, 112.4, 135.2, 85.6, 62.3, 48.7};
        for (int i = 0; i < 12; i++) {
            NPPData nppData = new NPPData();
            nppData.setAreaId(areaId);
            nppData.setYear(2024);
            nppData.setMonth(i + 1);
            nppData.setNppValue(nppValues[i]);
            nppData.setLatitude(39.9042);
            nppData.setLongitude(116.4074);
            nppData.setDataSource("CASA_MODEL");
            nppData.setCreatedAt(LocalDateTime.now());
            nppData.setUpdatedAt(LocalDateTime.now());
            nppDataRepository.save(nppData);
        }

        String areaId2 = "AREA_002";
        double[] nppValues2 = {38.5, 45.2, 68.7, 115.8, 158.2, 135.6, 92.4, 108.7, 128.5, 78.9, 55.6, 42.3};
        for (int i = 0; i < 12; i++) {
            NPPData nppData = new NPPData();
            nppData.setAreaId(areaId2);
            nppData.setYear(2024);
            nppData.setMonth(i + 1);
            nppData.setNppValue(nppValues2[i]);
            nppData.setLatitude(31.2304);
            nppData.setLongitude(121.4737);
            nppData.setDataSource("CASA_MODEL");
            nppData.setCreatedAt(LocalDateTime.now());
            nppData.setUpdatedAt(LocalDateTime.now());
            nppDataRepository.save(nppData);
        }
    }

    private void initLandUseData() {
        String areaId = "AREA_001";
        String[] landTypes = {"森林", "草地", "耕地", "水域", "建设用地", "未利用地"};
        double[] areas = {1250.5, 850.2, 2100.8, 320.5, 450.2, 180.8};
        for (int i = 0; i < landTypes.length; i++) {
            LandUseData landUseData = new LandUseData();
            landUseData.setAreaId(areaId);
            landUseData.setYear(2024);
            landUseData.setLandType(landTypes[i]);
            landUseData.setAreaHa(areas[i]);
            landUseData.setLatitude(39.9042);
            landUseData.setLongitude(116.4074);
            landUseData.setCreatedAt(LocalDateTime.now());
            landUseData.setUpdatedAt(LocalDateTime.now());
            landUseDataRepository.save(landUseData);
        }

        String areaId2 = "AREA_002";
        double[] areas2 = {980.3, 620.5, 1850.6, 450.8, 580.3, 120.5};
        for (int i = 0; i < landTypes.length; i++) {
            LandUseData landUseData = new LandUseData();
            landUseData.setAreaId(areaId2);
            landUseData.setYear(2024);
            landUseData.setLandType(landTypes[i]);
            landUseData.setAreaHa(areas2[i]);
            landUseData.setLatitude(31.2304);
            landUseData.setLongitude(121.4737);
            landUseData.setCreatedAt(LocalDateTime.now());
            landUseData.setUpdatedAt(LocalDateTime.now());
            landUseDataRepository.save(landUseData);
        }
    }
}
