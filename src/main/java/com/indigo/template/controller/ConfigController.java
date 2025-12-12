package com.indigo.template.controller;

import com.indigo.template.common.response.ApiResponse;
import com.indigo.template.service.ConfigCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Config Controller
 * 配置管理控制器
 */
@Tag(name = "Config Management", description = "配置管理接口")
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigCacheService configCacheService;

    @Operation(summary = "获取配置值", description = "根据配置键从缓存中获取配置值")
    @GetMapping("/{key}")
    public ApiResponse<String> getConfig(@PathVariable String key) {
        String value = configCacheService.getConfigValue(key);
        return ApiResponse.success(value);
    }

    @Operation(summary = "获取所有配置", description = "获取缓存中的所有配置")
    @GetMapping("/all")
    public ApiResponse<Map<String, String>> getAllConfigs() {
        Map<String, String> configs = configCacheService.getAllCachedConfigs();
        return ApiResponse.success(configs);
    }

    @Operation(summary = "更新配置", description = "更新配置到缓存和数据库")
    @PutMapping
    public ApiResponse<Void> updateConfig(
            @RequestParam String key,
            @RequestParam String value,
            @RequestParam(required = false) String description) {
        configCacheService.updateConfig(key, value, description);
        return ApiResponse.success();
    }

    @Operation(summary = "删除配置", description = "从缓存和数据库删除配置")
    @DeleteMapping("/{key}")
    public ApiResponse<Void> deleteConfig(@PathVariable String key) {
        configCacheService.deleteConfig(key);
        return ApiResponse.success();
    }

    @Operation(summary = "刷新缓存", description = "重新从数据库加载所有配置到缓存")
    @PostMapping("/refresh")
    public ApiResponse<Void> refreshCache() {
        configCacheService.refreshCache();
        return ApiResponse.success();
    }

    @Operation(summary = "清空缓存", description = "清空所有缓存条目")
    @PostMapping("/clear")
    public ApiResponse<Void> clearCache() {
        configCacheService.clearCache();
        return ApiResponse.success();
    }

    @Operation(summary = "获取缓存统计", description = "获取Caffeine缓存统计信息")
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cacheStats", configCacheService.getCacheStats());
        stats.put("cachedConfigCount", configCacheService.getAllCachedConfigs().size());
        return ApiResponse.success(stats);
    }
}
