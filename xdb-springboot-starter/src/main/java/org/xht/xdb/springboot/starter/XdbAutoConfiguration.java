package org.xht.xdb.springboot.starter;

import xht.xdb.Xdb;
import xht.xdb.XdbConfig;
import xht.xdb.enums.DbType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(XdbProp.class)
@Slf4j
public class XdbAutoConfiguration {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public XdbAutoConfiguration(XdbProp xdbProp, DataSourceProperties DataSourceProperties, DataSource dataSource) {
        XdbConfig.setSqlDir(xdbProp.getSqlDir());
        String driverClassName = DataSourceProperties.getDriverClassName();
        Xdb.init().addDataSourceDefault(dataSource, DbType.of(driverClassName));
    }

////    Constructor(构造方法) -> @Autowired(依赖注入) -> @PostConstruct(注释的方法)
//    @PostConstruct
//    public void postConstruct() {
//    }

}
