package com.admin.config;

import com.admin.exception.BizException;
import com.admin.exception.Msg;
import com.admin.mybatis.plugin.CuxPaginationInnerInterceptor;
import com.admin.mybatis.plugin.DomainMetaInterceptor;
import com.admin.mybatis.plugin.DomainObjectMetaObjectHandler;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * @description:
 * @version: 1.0
 * @author: wenzhou.tang@
 * @date: 2017/7/21 10:59
 */
@Configuration
@Slf4j
@EnableConfigurationProperties({MybatisPlusProperties.class, DataSourceProperties.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
public class DatabaseConfiguration {


    private final MybatisPlusProperties properties;
    private final List<Interceptor> interceptors;


    public DatabaseConfiguration(MybatisPlusProperties properties,
                                 ObjectProvider<List<Interceptor>> interceptorsProvider
    ) {
        this.properties = properties;
        if (this.properties.getMapperLocations().length == 1
                && "classpath*:/mapper/**/*.xml".equals(this.properties.getMapperLocations()[0])) {
            this.properties.setMapperLocations(new String[]{"com/admin/**/*.xml"});
        }
        if (!StringUtils.hasText(this.properties.getTypeAliasesPackage())) {
            this.properties.setTypeAliasesPackage("com.admin.**.entity");
        }
        if (!StringUtils.hasText(this.properties.getTypeHandlersPackage())) {
            this.properties.setTypeHandlersPackage("com.admin.**.typehandler");
        }
        this.interceptors = interceptorsProvider.getIfAvailable();
        GlobalConfig globalConfig = this.properties.getGlobalConfig();
        globalConfig.setBanner(false);
        // 逻辑删除配置
        globalConfig.getDbConfig().setLogicDeleteValue("1");
        globalConfig.getDbConfig().setLogicNotDeleteValue("0");
        globalConfig.setEnableSqlRunner(true);
    }

    @Bean
    public IdentifierGenerator identifierGenerator() {
        return new DefaultIdentifierGenerator(getWorkId(), getDatacenterId());
    }

    @Bean
    public MetaObjectHandler domainObjectMetaObjectHandler() {
        return new DomainObjectMetaObjectHandler();
    }






    /**
     * 插件  登录信息注入 -> 填充 -> 分页 -> 乐观锁 -> 多语言 -> 数据权限
     *
     * @return Interceptor[]
     */
    @Bean
    public Interceptor[] cuxMybatisInterceptors() {
        List<Interceptor> interceptors = new ArrayList<Interceptor>();


        // 考虑到公用字段填充与部分方法冲突，现使用拦截器创建时公用字段enabled、deleted、versionNumber
        DomainMetaInterceptor domainMetaInterceptor = new DomainMetaInterceptor();
        interceptors.add(domainMetaInterceptor);
        // 分页 乐观锁 租户拦截器等内置插件
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 分页拦截器
        CuxPaginationInnerInterceptor cuxPaginationInnerInterceptor = new CuxPaginationInnerInterceptor();
        cuxPaginationInnerInterceptor.setOverflow(true);

        // 乐观锁重写, 只拦截 updateById
        OptimisticLockerInnerInterceptor cuxOptimisticLockerInnerInterceptor = new OptimisticLockerInnerInterceptor();
        cuxOptimisticLockerInnerInterceptor.setException(new BizException(Msg.SYS_VERSION_NUMBER_CHANGED));
        mybatisPlusInterceptor.addInnerInterceptor(cuxOptimisticLockerInnerInterceptor);
        mybatisPlusInterceptor.addInnerInterceptor(cuxPaginationInnerInterceptor);
        interceptors.add(mybatisPlusInterceptor);
        // 自定义的插件最先执行
        if (!CollectionUtils.isEmpty(this.interceptors)) {
            interceptors.addAll(this.interceptors);
        }
        return interceptors.toArray(new Interceptor[]{});
    }

    @Bean
    public DatabaseIdProvider getDatabaseIdProvider() {
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("Oracle", "oracle");
        properties.setProperty("MySQL", "mysql");
        properties.setProperty("DB2", "db2");
        properties.setProperty("Derby", "derby");
        properties.setProperty("H2", "h2");
        properties.setProperty("HSQL", "hsql");
        properties.setProperty("Informix", "informix");
        properties.setProperty("MS-SQL", "ms-sql");
        properties.setProperty("PostgreSQL", "postgresql");
        properties.setProperty("Sybase", "sybase");
        properties.setProperty("Hana", "hana");
        properties.setProperty("DM", "dm");
        properties.setProperty("XuguDB", "xugu");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }




    private long getDatacenterId() {
        long id = 0L;

        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                if (null != mac) {
                    id = (255L & (long)mac[mac.length - 2] | 65280L & (long)mac[mac.length - 1] << 8) >> 6;
                    id %= 32L;
                }
            }
        } catch (Exception e) {
            log.warn(" getDatacenterId: " + e.getMessage());
            id = RandomUtils.nextLong(0L, 31L);
        }

        return id;
    }

    private Long getWorkId() {
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            int[] ints = org.apache.commons.lang3.StringUtils.toCodePoints(hostAddress);
            int sums = 0;

            for(int b : ints) {
                sums += b;
            }

            return (long)(sums % 32);
        } catch (UnknownHostException e) {
            log.warn(" getWorkId: " + e.getMessage());
            return RandomUtils.nextLong(0L, 31L);
        }
    }

}