-- Create config table for storing application configuration
CREATE TABLE IF NOT EXISTS `config` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` VARCHAR(500) NOT NULL COMMENT '配置值',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '配置描述',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '删除标记(0:未删除,1:已删除)',
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- Insert some sample configuration data
INSERT INTO `config` (`config_key`, `config_value`, `description`) VALUES
('system.name', 'Java Web Template', '系统名称'),
('system.version', '1.0.0', '系统版本'),
('cache.enabled', 'true', '缓存是否启用'),
('max.upload.size', '10485760', '最大上传文件大小(字节)');
