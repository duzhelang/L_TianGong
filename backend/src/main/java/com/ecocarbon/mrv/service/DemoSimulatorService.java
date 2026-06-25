package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.IotDevice;
import com.ecocarbon.mrv.entity.SensorData;
import com.ecocarbon.mrv.repository.IotDeviceRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemoSimulatorService {
    private final SensorDataService sensorDataService;
    private final IotDeviceRepository deviceRepository;

    private volatile boolean running = false;
    private final AtomicLong dataCount = new AtomicLong(0);
    private LocalDateTime startTime;
    private final Random random = new Random();
    private long tick = 0;

    private static final Map<String, String[]> DEVICE_CONFIG = Map.of(
        "DEMO_WEATHER_001", new String[]{"气象站", "WEATHER"},
        "DEMO_SOIL_001", new String[]{"土壤传感器", "SOIL"},
        "DEMO_GAS_001", new String[]{"气体分析仪", "GAS"}
    );

    public void startDemo() {
        if (running) return;
        registerDevices();
        running = true;
        startTime = LocalDateTime.now();
        dataCount.set(0);
        tick = 0;
        log.info("Demo模拟数据已启动");
    }

    public void stopDemo() {
        running = false;
        updateDeviceStatus("OFFLINE");
        log.info("Demo模拟数据已停止");
    }

    public boolean isRunning() {
        return running;
    }

    public Map<String, Object> getStatus() {
        Map<String, Object> status = new ConcurrentHashMap<>();
        status.put("running", running);
        status.put("dataCount", dataCount.get());
        status.put("startTime", startTime != null ? startTime.toString() : null);
        status.put("devices", DEVICE_CONFIG.keySet());
        return status;
    }

    @Scheduled(fixedRate = 5000)
    public void generateData() {
        if (!running) return;
        tick++;
        LocalDateTime now = LocalDateTime.now();
        List<SensorData> batch = new ArrayList<>();
        batch.addAll(generateWeatherData(now));
        batch.addAll(generateSoilData(now));
        batch.addAll(generateGasData(now));
        sensorDataService.saveAll(batch);
        dataCount.addAndGet(batch.size());
    }

    private void registerDevices() {
        for (Map.Entry<String, String[]> entry : DEVICE_CONFIG.entrySet()) {
            String code = entry.getKey();
            String[] info = entry.getValue();
            if (!deviceRepository.existsByDeviceCode(code)) {
                IotDevice device = new IotDevice();
                device.setDeviceCode(code);
                device.setDeviceName(info[0]);
                device.setDeviceType(info[1]);
                device.setLocation("Demo监测点");
                device.setLongitude(116.4 + random.nextDouble() * 2);
                device.setLatitude(39.9 + random.nextDouble() * 2);
                device.setStatus("ONLINE");
                device.setLastOnlineAt(LocalDateTime.now());
                device.setCreatedAt(LocalDateTime.now());
                device.setUpdatedAt(LocalDateTime.now());
                deviceRepository.save(device);
            } else {
                deviceRepository.findByDeviceCode(code).ifPresent(d -> {
                    d.setStatus("ONLINE");
                    d.setLastOnlineAt(LocalDateTime.now());
                    d.setUpdatedAt(LocalDateTime.now());
                    deviceRepository.save(d);
                });
            }
        }
    }

    private void updateDeviceStatus(String status) {
        for (String code : DEVICE_CONFIG.keySet()) {
            deviceRepository.findByDeviceCode(code).ifPresent(d -> {
                d.setStatus(status);
                d.setUpdatedAt(LocalDateTime.now());
                deviceRepository.save(d);
            });
        }
    }

    private List<SensorData> generateWeatherData(LocalDateTime now) {
        List<SensorData> list = new ArrayList<>();
        double hour = now.getHour() + now.getMinute() / 60.0;
        double dayFactor = Math.sin((hour - 6) * Math.PI / 12);
        if (dayFactor < 0) dayFactor = 0;

        list.add(createData("DEMO_WEATHER_001", "WEATHER", "temperature",
                22 + 8 * dayFactor + noise(1.5), "°C", now));
        list.add(createData("DEMO_WEATHER_001", "WEATHER", "humidity",
                65 - 20 * dayFactor + noise(5), "%", now));
        list.add(createData("DEMO_WEATHER_001", "WEATHER", "windSpeed",
                3 + 5 * Math.abs(noise(2)), "m/s", now));
        list.add(createData("DEMO_WEATHER_001", "WEATHER", "precipitation",
                Math.max(0, random.nextDouble() < 0.15 ? random.nextDouble() * 15 : 0), "mm", now));
        list.add(createData("DEMO_WEATHER_001", "WEATHER", "lightIntensity",
                Math.max(0, 80000 * dayFactor + noise(5000)), "lux", now));
        list.add(createData("DEMO_WEATHER_001", "WEATHER", "co2",
                410 + 15 * (1 - dayFactor) + noise(8), "ppm", now));
        return list;
    }

    private List<SensorData> generateSoilData(LocalDateTime now) {
        List<SensorData> list = new ArrayList<>();
        double hour = now.getHour() + now.getMinute() / 60.0;
        double dayFactor = Math.sin((hour - 8) * Math.PI / 14);
        if (dayFactor < 0) dayFactor = 0;

        list.add(createData("DEMO_SOIL_001", "SOIL", "soilTemperature",
                18 + 6 * dayFactor + noise(0.8), "°C", now));
        list.add(createData("DEMO_SOIL_001", "SOIL", "soilMoisture",
                45 + 10 * Math.sin(tick * 0.01) + noise(3), "%", now));
        list.add(createData("DEMO_SOIL_001", "SOIL", "soilPH",
                6.8 + noise(0.2), "", now));
        list.add(createData("DEMO_SOIL_001", "SOIL", "soilConductivity",
                1.5 + 0.5 * Math.sin(tick * 0.005) + noise(0.2), "mS/cm", now));
        return list;
    }

    private List<SensorData> generateGasData(LocalDateTime now) {
        List<SensorData> list = new ArrayList<>();
        list.add(createData("DEMO_GAS_001", "GAS", "co2",
                450 + 50 * Math.sin(tick * 0.02) + noise(15), "ppm", now));
        list.add(createData("DEMO_GAS_001", "GAS", "ch4",
                1.9 + 0.3 * Math.sin(tick * 0.015) + noise(0.1), "ppm", now));
        list.add(createData("DEMO_GAS_001", "GAS", "n2o",
                0.33 + 0.08 * Math.sin(tick * 0.01) + noise(0.02), "ppm", now));
        return list;
    }

    private SensorData createData(String deviceCode, String deviceType, String metric,
                                   double value, String unit, LocalDateTime timestamp) {
        SensorData data = new SensorData();
        data.setDeviceCode(deviceCode);
        data.setDeviceType(deviceType);
        data.setMetric(metric);
        data.setValue(Math.round(value * 100.0) / 100.0);
        data.setUnit(unit);
        data.setQuality("NORMAL");
        data.setTimestamp(timestamp);
        data.setCreatedAt(timestamp);
        return data;
    }

    private double noise(double amplitude) {
        return random.nextGaussian() * amplitude * 0.3;
    }
}
