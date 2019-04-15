package me.xwbz.flowable.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import lombok.extern.slf4j.Slf4j;
import me.xwbz.flowable.common.api.ApiResult;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.charset.StandardCharsets;

@Slf4j
@ControllerAdvice
public class ControllerConfig {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ApiResult exceptionHandler(Throwable e) {
        return ApiResult.handleException(e, log);
    }

    /**
     * 数据绑定
     *
     * @param binder WebDataBinder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
//        binder.registerCustomEditor(Date.class, new DateEditor(true));
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
