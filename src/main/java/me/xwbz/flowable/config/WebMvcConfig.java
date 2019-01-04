package me.xwbz.flowable.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import lombok.extern.slf4j.Slf4j;
import me.xwbz.flowable.bean.User;
import org.flowable.engine.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private IdentityService identityService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest reqs, HttpServletResponse response, Object handler) throws Exception {
                log.info("{} {} {}", reqs.getRequestURI(), reqs.getMethod(), reqs.getQueryString());
                // 省略登陆和鉴权代码
                User user = new User("1", "张三", "admin");
                // 设置当前流程发起人为当前用户
                identityService.setAuthenticatedUserId(user.getId());
                return true;
            }
        }).addPathPatterns("/**");
    }

    /**
     * 增加fastjson解析配置
     */
    @Bean
    public HttpMessageConverters fastJsonConfigure() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // SerializerFeature.PrettyFormat 返回格式化后的json
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteNonStringKeyAsString);
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        converter.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(converter);
    }
}
