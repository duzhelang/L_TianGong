package com.ecocarbon.mrv.repository;

import com.ecocarbon.mrv.entity.IotAlarmRule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IotAlarmRuleRepository extends JpaRepository<IotAlarmRule, Long> {
    List<IotAlarmRule> findByEnabled(Integer enabled);

    List<IotAlarmRule> findByDeviceType(String deviceType);

    List<IotAlarmRule> findByDeviceCode(String deviceCode);
}
