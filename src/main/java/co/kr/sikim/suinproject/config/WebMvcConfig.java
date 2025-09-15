package co.kr.sikim.suinproject.config;

import java.text.SimpleDateFormat;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 확장:
 * - 정적 리소스 매핑(필요 시)
 * - 메시지 컨버터(Jackson) 전역 날짜/시간 포맷
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /** 정적 리소스 매핑.. 필요 시 사용 */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    /** 전역 메시지 컨버터(Jackson) 설정 */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> c : converters) {
            if (c instanceof MappingJackson2HttpMessageConverter jackson) {
                ObjectMapper om = jackson.getObjectMapper();
                om.registerModule(new JavaTimeModule());
                om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            }
        }
    }
}
