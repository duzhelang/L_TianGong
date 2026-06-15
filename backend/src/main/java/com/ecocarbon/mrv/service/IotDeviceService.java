package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.entity.IotDevice;
import com.ecocarbon.mrv.repository.IotDeviceRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IotDeviceService {
    private final IotDeviceRepository deviceRepository;

    public Page<IotDevice> list(Pageable pageable) {
        return deviceRepository.findAll(pageable);
    }

    public IotDevice getById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("设备不存在"));
    }

    public IotDevice getByCode(String deviceCode) {
        return deviceRepository.findByDeviceCode(deviceCode)
                .orElseThrow(() -> new IllegalArgumentException("设备不存在: " + deviceCode));
    }

    public List<IotDevice> getByType(String deviceType) {
        return deviceRepository.findByDeviceType(deviceType);
    }

    public List<IotDevice> getByProject(Long projectId) {
        return deviceRepository.findByProjectId(projectId);
    }

    public List<IotDevice> getOnlineDevices() {
        return deviceRepository.findByStatus("ONLINE");
    }

    @Transactional
    public IotDevice create(IotDevice device) {
        if (deviceRepository.existsByDeviceCode(device.getDeviceCode())) {
            throw new IllegalArgumentException("设备编码已存在: " + device.getDeviceCode());
        }
        device.setStatus("OFFLINE");
        device.setCreatedAt(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());
        return deviceRepository.save(device);
    }

    @Transactional
    public IotDevice update(Long id, IotDevice device) {
        IotDevice existing = getById(id);
        existing.setDeviceName(device.getDeviceName());
        existing.setDeviceType(device.getDeviceType());
        existing.setLocation(device.getLocation());
        existing.setLongitude(device.getLongitude());
        existing.setLatitude(device.getLatitude());
        existing.setProjectId(device.getProjectId());
        existing.setUpdatedAt(LocalDateTime.now());
        return deviceRepository.save(existing);
    }

    @Transactional
    public void updateStatus(String deviceCode, String status) {
        IotDevice device = getByCode(deviceCode);
        device.setStatus(status);
        if ("ONLINE".equals(status)) {
            device.setLastOnlineAt(LocalDateTime.now());
        }
        device.setUpdatedAt(LocalDateTime.now());
        deviceRepository.save(device);
    }

    @Transactional
    public void delete(Long id) {
        deviceRepository.deleteById(id);
    }
}
