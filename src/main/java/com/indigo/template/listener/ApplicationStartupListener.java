package com.indigo.template.listener;

import com.indigo.template.service.ConfigCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Application Startup Listener
 * 应用启动监听器 - 在应用完全启动后加载配置到缓存
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    private final ConfigCacheService configCacheService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Application is ready. Loading configs to Caffeine cache...");
        try {
            configCacheService.loadConfigsToCache();
            log.info("Successfully loaded configs to Caffeine cache on startup");
        } catch (Exception e) {
            log.error("Failed to load configs to Caffeine cache on startup", e);
        }
    }
}
