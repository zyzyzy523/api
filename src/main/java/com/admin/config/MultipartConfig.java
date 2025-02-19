package com.admin.config;

import com.admin.exception.BizException;
import jakarta.servlet.MultipartConfigElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.File;

@Configuration
@Slf4j
@EnableConfigurationProperties(MultipartProperties.class)
public class MultipartConfig {

    private static final String DATA_TEMP = "/data-temp";

    private final MultipartProperties multipartProperties;

    public MultipartConfig(MultipartProperties multipartProperties) {
        this.multipartProperties = multipartProperties;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        String userDir = "";
        File file;
        userDir = System.getProperty("user.home");
        try {
            if (StringUtils.hasText(userDir)) {
                file = new File(userDir + DATA_TEMP);
                try {
                    createFile(file);
                } catch (Exception e) {
                    userDir = System.getProperty("user.dir");
                    file = new File(userDir + DATA_TEMP);
                    createFile(file);
                }
            } else {
                userDir = System.getProperty("user.dir");
                file = new File(userDir + DATA_TEMP);
                createFile(file);
            }
        } catch (Exception e) {
            userDir = "";
            file = new File(userDir + DATA_TEMP);
            createFile(file);
        }
        log.info("MultipartConfigElement set  Location is {}", userDir + DATA_TEMP);
        factory.setLocation(userDir + DATA_TEMP);
        factory.setMaxFileSize(this.multipartProperties.getMaxFileSize());
        factory.setMaxRequestSize(this.multipartProperties.getMaxRequestSize());
        return factory.createMultipartConfig();
    }


    private void createFile(File file) {
        if (!file.exists()) {
            boolean dirCreated = file.mkdir();
            if (!dirCreated) {
                throw new BizException("create file " + file.getAbsolutePath() + " failed!");
            }
        }
        // 该文件夹需要写以及执行权限
        if (!(file.canWrite() && file.canExecute())) {
            throw new BizException(file.getAbsolutePath() + " Permission denied!");
        }
    }
}
