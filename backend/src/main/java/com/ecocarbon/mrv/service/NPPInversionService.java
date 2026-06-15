package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.dto.NPPInversionRequest;
import com.ecocarbon.mrv.dto.NPPInversionResponse;
import com.ecocarbon.mrv.entity.NPPData;
import com.ecocarbon.mrv.repository.NPPDataRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NPPInversionService {
    private final NPPDataRepository nppDataRepository;

    public NPPInversionResponse calculateNPP(NPPInversionRequest request) {
        double par = request.par();
        double fpar = request.fpar();
        double epsilon = request.epsilon();
        double tStress = calculateTemperatureStress(request.temperature(), request.optimalTemperature());
        double wStress = calculateWaterStress(request.soilMoisture(), request.optimalSoilMoisture());

        double npp = par * fpar * epsilon * tStress * wStress;

        NPPData nppData = new NPPData();
        nppData.setAreaId(request.areaId());
        nppData.setYear(request.year());
        nppData.setMonth(request.month());
        nppData.setNppValue(npp);
        nppData.setLatitude(request.latitude());
        nppData.setLongitude(request.longitude());
        nppData.setDataSource("CASA_MODEL");
        nppData.setCreatedAt(LocalDateTime.now());
        nppData.setUpdatedAt(LocalDateTime.now());
        nppDataRepository.save(nppData);

        return new NPPInversionResponse(
                npp,
                "gC/m2/yr",
                par,
                fpar,
                epsilon,
                tStress,
                wStress,
                "CASA_MODEL",
                LocalDateTime.now()
        );
    }

    public List<NPPInversionResponse> batchCalculateNPP(List<NPPInversionRequest> requests) {
        List<NPPInversionResponse> results = new ArrayList<>();
        for (NPPInversionRequest request : requests) {
            results.add(calculateNPP(request));
        }
        return results;
    }

    private double calculateTemperatureStress(double temperature, double optimalTemperature) {
        double diff = Math.abs(temperature - optimalTemperature);
        double sigma = 10.0;
        return Math.exp(-0.5 * Math.pow(diff / sigma, 2));
    }

    private double calculateWaterStress(double soilMoisture, double optimalSoilMoisture) {
        if (optimalSoilMoisture == 0) {
            return 1.0;
        }
        double ratio = soilMoisture / optimalSoilMoisture;
        return Math.min(1.0, ratio);
    }

    public List<NPPData> getHistoricalNPP(String areaId, Integer startYear, Integer endYear) {
        return nppDataRepository.findByAreaIdAndYearBetween(areaId, startYear, endYear);
    }

    public Double getAverageNPP(String areaId, Integer year) {
        List<NPPData> nppList = nppDataRepository.findByAreaIdAndYear(areaId, year);
        if (nppList.isEmpty()) {
            return 0.0;
        }
        return nppList.stream()
                .mapToDouble(NPPData::getNppValue)
                .average()
                .orElse(0.0);
    }
}
