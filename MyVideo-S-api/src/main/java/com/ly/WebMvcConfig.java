package com.ly;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.ly.interceptor.WebInterceptor;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")
		.addResourceLocations("classpath:/META-INF/resources/")
				.addResourceLocations("file:E:/MyVideo_data/");

	}
	
	@Bean
	public WebInterceptor getInterceptor() {
		return new WebInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(getInterceptor()).addPathPatterns("/user/**")
												 .addPathPatterns("/bgm/**")
												 .addPathPatterns("/video/uploadVideo","/video//uploadCover","/video/like",
														 "/video/unlike");
		super.addInterceptors(registry);
	}
	
	

}
