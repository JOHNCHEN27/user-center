package com.lncanswer.usercenterbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/4/28 18:17
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //覆盖所有请求
       registry.addMapping("/**")
               //允许发送cookiet
               .allowCredentials(true)
               //放行哪些域名用Patterns 不然 * 会和allowCredentials冲突
               .allowedOriginPatterns("*")
               .allowedHeaders("*")
               .exposedHeaders("*")
               .allowedMethods("GET","POST", "DELETE", "OPTIONS")
               //跨域允许时间
               .maxAge(3600);
    }
}
