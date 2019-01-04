package me.xwbz.flowable.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * User: luc
 * Date: 2016/11/29 19:28
 * Desc: this is simple description about this class
 */
@Getter
@Setter
@ConfigurationProperties(prefix = DruidDataSourceProperties.DRUID_PROPERTIES_PREFIX)
public class DruidDataSourceProperties {

    public static final String DRUID_PROPERTIES_PREFIX = "druid";

    private String driverClassName;

    private String url;

    private String username;

    private String password;

    private int initialSize;

    private int minIdle;

    private int maxActive;

    private long maxWait;

    private long timeBetweenEvictionRunsMillis;

    private long minEvictableIdleTimeMillis;

    private String validationQuery;

    private boolean testWhileIdle;

    private boolean testOnBorrow;

    private boolean testOnReturn;

    private boolean poolPreparedStatements;

    private int maxPoolPreparedStatementPerConnectionSize;

    private String filters;

    private boolean useGlobalDataSourceStat;


}
