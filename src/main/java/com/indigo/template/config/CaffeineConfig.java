package com.indigo.template.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine Cache Configuration
 * Caffeine本地缓存配置
 */
@Configuration
public class CaffeineConfig {

    /**
     * 创建配置缓存实例
     * - 最大容量: 1000个条目
     * - 写入后过期时间: 24小时
     * - 访问后过期时间: 12小时
     */
    @Bean
    public Cache<String, String> configCache() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(24, TimeUnit.HOURS)
                .expireAfterAccess(12, TimeUnit.HOURS)
                .recordStats()
                .build();
    }
}
