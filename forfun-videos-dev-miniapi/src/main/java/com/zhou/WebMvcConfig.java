package com.zhou;

import com.zhou.controller.interceptor.MiniInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    @Override
    //配置虚拟路径
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("file:C:/forfun_host/");
//        registry.addResourceHandler("/v/**").addResourceLocations("file:C:/forfun_host/");

    }

    @Bean(initMethod="init")
    public ZKCuratorClient zkCuratorClient() {
        return new ZKCuratorClient();
    }

    @Bean
    public MiniInterceptor miniInterceptor(){
        return new MiniInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(miniInterceptor()).addPathPatterns("/user/**")
                .addPathPatterns("/video/upload","/video/uploadCover")
                .addPathPatterns("/video/userLike","/video/userUnLike","/video/saveComment")
                .addPathPatterns("/bgm/**")
            .excludePathPatterns("/user/queryPublisher");
        super.addInterceptors(registry);
    }


}
