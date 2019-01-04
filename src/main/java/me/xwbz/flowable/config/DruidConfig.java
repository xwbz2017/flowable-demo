package me.xwbz.flowable.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import lombok.extern.slf4j.Slf4j;
import me.xwbz.flowable.config.properties.DruidDataSourceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 *  数据库连接池配置
 * <p>User: luc
 * <p>Date: 2016/11/29 18:44
 * <p>Desc: the druid connection pool configuration
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(DruidDataSourceProperties.class)
public class DruidConfig {

    @Autowired
    private DruidDataSourceProperties druidDataSourceProperties;

    @Bean
    public ServletRegistrationBean<StatViewServlet> druidServlet() {
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        servletRegistrationBean.addInitParameter("loginUsername", "sc");
        servletRegistrationBean.addInitParameter("loginPassword", "flowable.demo");
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }

    @Bean
    public DataSource dataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(druidDataSourceProperties.getDriverClassName());
        druidDataSource.setUrl(druidDataSourceProperties.getUrl());
        druidDataSource.setUsername(druidDataSourceProperties.getUsername());
        druidDataSource.setPassword(druidDataSourceProperties.getPassword());
        druidDataSource.setInitialSize(druidDataSourceProperties.getInitialSize());
        druidDataSource.setMinIdle(druidDataSourceProperties.getMinIdle());
        druidDataSource.setMaxActive(druidDataSourceProperties.getMaxActive());
        druidDataSource.setMaxWait(druidDataSourceProperties.getMaxWait());
        druidDataSource.setTimeBetweenConnectErrorMillis(druidDataSourceProperties.getTimeBetweenEvictionRunsMillis());
        druidDataSource.setMinEvictableIdleTimeMillis(druidDataSourceProperties.getMinEvictableIdleTimeMillis());
        druidDataSource.setValidationQuery(druidDataSourceProperties.getValidationQuery());
        druidDataSource.setTestWhileIdle(druidDataSourceProperties.isTestWhileIdle());
        druidDataSource.setTestOnBorrow(druidDataSourceProperties.isTestOnBorrow());
        druidDataSource.setTestOnReturn(druidDataSourceProperties.isTestOnReturn());
        druidDataSource.setPoolPreparedStatements(druidDataSourceProperties.isPoolPreparedStatements());
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(druidDataSourceProperties.getMaxPoolPreparedStatementPerConnectionSize());
        druidDataSource.setUseGlobalDataSourceStat(druidDataSourceProperties.isUseGlobalDataSourceStat());

        try {
            druidDataSource.setFilters(druidDataSourceProperties.getFilters());
        } catch (SQLException e) {
            log.error("设置拦截器失败",e);
        }
        return druidDataSource;
    }

    @Bean
    public FilterRegistrationBean<WebStatFilter> filterRegistrationBean() {
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
//        filterRegistrationBean.addInitParameter("profileEnable", "true");
        return filterRegistrationBean;
    }
}
