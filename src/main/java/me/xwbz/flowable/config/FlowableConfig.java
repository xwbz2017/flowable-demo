package me.xwbz.flowable.config;

import me.xwbz.flowable.method.AuditMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlowableConfig {

    @Bean(name = "auditMethod")
    public AuditMethod auditMethod(){
        return new AuditMethod();
    }
}
