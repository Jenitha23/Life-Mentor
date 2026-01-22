package com.lifementor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;


@Configuration
public class FileStorageConfig implements WebMvcConfigurer {

    @Value("${app.upload.profile-pictures-dir:uploads/profile-pictures}")
    private String uploadDir;

    @Value("${app.static-files.path:/files}")
    private String staticPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir);
        String uploadAbsolutePath = uploadPath.toFile().getAbsolutePath();

        registry.addResourceHandler(staticPath + "/profile-pictures/**")
                .addResourceLocations("file:" + uploadAbsolutePath + "/");
    }
}