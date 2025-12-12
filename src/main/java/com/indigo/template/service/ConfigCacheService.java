package com.indigo.template.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.indigo.template.entity.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Config Cache Service
 * 配置缓存服务 - 使用Caffeine本地缓存
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigCacheService {

    private final Cache<String, String> configCache;
    private final ConfigService configService;

    /**
     * 从数据库加载所有配置到缓存
     * 在应用启动时调用
     */
    public void loadConfigsToCache() {
        log.info("Loading configs from database to Caffeine cache...");
        List<Config> configs = configService.getAllConfigs();
        
        for (Config config : configs) {
            configCache.put(config.getConfigKey(), config.getConfigValue());
        }
        
        log.info("Loaded {} configs to Caffeine cache", configs.size());
    }

    /**
     * 从缓存获取配置值
     * 如果缓存中不存在，则从数据库加载
     * 如果数据库中也不存在，返回null
     */
    public String getConfigValue(String key) {
        return configCache.get(key, k -> {
            String value = configService.getConfigValue(k);
            log.debug("Cache miss for key: {}, loaded from database: {}", k, value != null ? "found" : "not found");
            // Caffeine allows null values to be cached, which is appropriate here
            // as it distinguishes between "key never queried" vs "key queried but not found"
            return value;
        });
    }

    /**
     * 更新配置到缓存和数据库
     */
    public void updateConfig(String key, String value, String description) {
        Config config = new Config();
        config.setConfigKey(key);
        config.setConfigValue(value);
        config.setDescription(description);
        
        configService.saveOrUpdate(config);
        configCache.put(key, value);
        
        log.info("Updated config in cache and database: key={}, value={}", key, value);
    }

    /**
     * 删除配置从缓存和数据库
     */
    public void deleteConfig(String key) {
        configService.deleteByKey(key);
        configCache.invalidate(key);
        
        log.info("Deleted config from cache and database: key={}", key);
    }

    /**
     * 刷新缓存 - 重新从数据库加载所有配置
     */
    public void refreshCache() {
        log.info("Refreshing Caffeine cache...");
        configCache.invalidateAll();
        loadConfigsToCache();
    }

    /**
     * 获取缓存中的所有配置
     */
    public Map<String, String> getAllCachedConfigs() {
        return configCache.asMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 获取缓存统计信息
     */
    public String getCacheStats() {
        return configCache.stats().toString();
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        configCache.invalidateAll();
        log.info("Cleared all entries from Caffeine cache");
    }
}
