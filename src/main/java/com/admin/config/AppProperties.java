package com.admin.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.xie
 * @since 2024/12/5
 */
@ConfigurationProperties(prefix = "admin.app")
@Data
public class AppProperties {

    private String appUrl;
}
