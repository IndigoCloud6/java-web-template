package com.indigo.template.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.indigo.template.entity.Config;
import com.indigo.template.mapper.ConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Config Service
 * 系统配置服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigService {

    private final ConfigMapper configMapper;

    /**
     * 获取所有配置
     */
    public List<Config> getAllConfigs() {
        return configMapper.selectList(new LambdaQueryWrapper<>());
    }

    /**
     * 根据配置键获取配置
     */
    public Config getConfigByKey(String configKey) {
        return configMapper.selectOne(
            new LambdaQueryWrapper<Config>()
                .eq(Config::getConfigKey, configKey)
        );
    }

    /**
     * 根据配置键获取配置值
     */
    public String getConfigValue(String configKey) {
        Config config = getConfigByKey(configKey);
        return config != null ? config.getConfigValue() : null;
    }

    /**
     * 保存或更新配置
     */
    public void saveOrUpdate(Config config) {
        Config existingConfig = getConfigByKey(config.getConfigKey());
        if (existingConfig != null) {
            config.setId(existingConfig.getId());
            configMapper.updateById(config);
            log.info("Updated config: key={}, value={}", config.getConfigKey(), config.getConfigValue());
        } else {
            configMapper.insert(config);
            log.info("Inserted new config: key={}, value={}", config.getConfigKey(), config.getConfigValue());
        }
    }

    /**
     * 删除配置
     */
    public void deleteByKey(String configKey) {
        configMapper.delete(
            new LambdaQueryWrapper<Config>()
                .eq(Config::getConfigKey, configKey)
        );
        log.info("Deleted config: key={}", configKey);
    }
}
