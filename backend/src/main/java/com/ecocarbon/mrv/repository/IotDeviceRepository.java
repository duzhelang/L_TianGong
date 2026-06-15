package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.IotDevice;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IotDeviceRepository extends JpaRepository<IotDevice, Long> {
    Optional<IotDevice> findByDeviceCode(String deviceCode);

    List<IotDevice> findByDeviceType(String deviceType);

    List<IotDevice> findByProjectId(Long projectId);

    List<IotDevice> findByStatus(String status);

    boolean existsByDeviceCode(String deviceCode);
}
