package com.ecocarbon.mrv.controller;

import com.ecocarbon.mrv.common.ApiResponse;
import com.ecocarbon.mrv.entity.CarbonAsset;
import com.ecocarbon.mrv.service.CarbonAssetService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/carbon/assets")
@RequiredArgsConstructor
public class CarbonAssetController {
    private final CarbonAssetService assetService;

    @GetMapping
    public ApiResponse<Page<CarbonAsset>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(assetService.list(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ApiResponse<CarbonAsset> getById(@PathVariable Long id) {
        return ApiResponse.ok(assetService.getById(id));
    }

    @GetMapping("/code/{assetCode}")
    public ApiResponse<CarbonAsset> getByCode(@PathVariable String assetCode) {
        return ApiResponse.ok(assetService.getByCode(assetCode));
    }

    @GetMapping("/type/{assetType}")
    public ApiResponse<List<CarbonAsset>> getByType(@PathVariable String assetType) {
        return ApiResponse.ok(assetService.getByType(assetType));
    }

    @GetMapping("/owner/{ownerId}")
    public ApiResponse<List<CarbonAsset>> getByOwner(@PathVariable Long ownerId) {
        return ApiResponse.ok(assetService.getByOwner(ownerId));
    }

    @GetMapping("/active")
    public ApiResponse<List<CarbonAsset>> getActiveAssets() {
        return ApiResponse.ok(assetService.getActiveAssets());
    }

    @PostMapping
    public ApiResponse<CarbonAsset> create(@RequestBody CarbonAsset asset) {
        return ApiResponse.ok("碳资产登记成功", assetService.create(asset));
    }

    @PutMapping("/{id}")
    public ApiResponse<CarbonAsset> update(@PathVariable Long id, @RequestBody CarbonAsset asset) {
        return ApiResponse.ok("碳资产更新成功", assetService.update(id, asset));
    }

    @PutMapping("/{id}/retire")
    public ApiResponse<CarbonAsset> retire(@PathVariable Long id) {
        return ApiResponse.ok("碳资产注销成功", assetService.retire(id));
    }

    @PutMapping("/{id}/transfer")
    public ApiResponse<CarbonAsset> transfer(@PathVariable Long id, @RequestParam Long newOwnerId) {
        return ApiResponse.ok("碳资产转让成功", assetService.transfer(id, newOwnerId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        assetService.delete(id);
        return ApiResponse.ok("碳资产删除成功", null);
    }
}
