package mip.mva.sp.config.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import mip.mva.sp.comm.enums.MipErrorEnum;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.config.ConfigBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.ObjectUtils;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * @Project 모바일 운전면허증 서비스 구축 사업
 * @PackageName mip.mva.sp.config.db
 * @FileName SpDataSource.java
 * @Author Min Gi Ju
 * @Date 2022. 6. 3.
 * @Description DataSource 관리 Controller
 *
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2024. 5. 28.    민기주           최초생성
 *              </pre>
 */
@Configuration
public class SpDataSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpDataSource.class);

    private final ApplicationContext applicationContext;

    /**
     * 설정정보
     */
    private final ConfigBean configBean;

    /**
     * 생성자
     *
     * @param applicationContext ApplicationContext
     * @param configBean         설정정보
     */
    SpDataSource(ApplicationContext applicationContext, ConfigBean configBean) {
        this.applicationContext = applicationContext;
        this.configBean = configBean;
    }

    /**
     * DataSource 생성 및 Bean 등록
     *
     * @return DataSource
     * @throws SpException
     * @MethodName dataSource
     */
    @Bean
    DataSource dataSource() {
        final HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setDriverClassName(configBean.getVerifyConfig().getDb().getDriverClassName());
        hikariConfig.setJdbcUrl(configBean.getVerifyConfig().getDb().getUrl());
        hikariConfig.setUsername(configBean.getVerifyConfig().getDb().getUsername());
        hikariConfig.setPassword(configBean.getVerifyConfig().getDb().getPassword());

        return new HikariDataSource(hikariConfig);
    }

    /**
     * DataSourceTransactionManager 생성 및 Bean 등록
     *
     * @return DataSourceTransactionManager
     * @throws SpException
     * @MethodName transactionManager
     */
    @Bean
    DataSourceTransactionManager transactionManager() throws SpException {
        DataSourceTransactionManager dataSourceTransactionManager = null;

        try {
            dataSourceTransactionManager = new DataSourceTransactionManager(dataSource());
        } catch (Exception e) {
            throw new SpException(MipErrorEnum.SP_DB_ERROR, null, "DataSourceTransactionManager Create Error!");
        }

        return dataSourceTransactionManager;
    }

    /**
     * SqlSessionFactory 생성 및 Bean 등록
     *
     * @param dataSource DataSource
     * @return SqlSessionFactory
     * @throws SpException
     * @MethodName sqlSessionFactory
     */
    @Bean
    SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws SpException {
        SqlSessionFactory sqlSessionFactory = null;

        try {
            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();

            sqlSessionFactoryBean.setDataSource(dataSource);
            sqlSessionFactoryBean.setTypeAliasesPackage("mip.mva.sp.**.vo");

            if (ObjectUtils.isEmpty(configBean.getVerifyConfig().getDb())) {
                sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:/mapper/h2/*.xml"));
            } else {
                sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources(
                        "classpath:/mapper/" + configBean.getVerifyConfig().getDb().getProvider() + "/*.xml"));
            }

            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();

            configuration.setMapUnderscoreToCamelCase(true);
            configuration.setJdbcTypeForNull(JdbcType.VARCHAR);

            sqlSessionFactoryBean.setConfiguration(configuration);

            sqlSessionFactory = sqlSessionFactoryBean.getObject();
        } catch (IOException e) {
            throw new SpException(MipErrorEnum.SP_DB_ERROR, null, "MapperFile Load Error!");
        } catch (Exception e) {
            throw new SpException(MipErrorEnum.SP_DB_ERROR, null, "SqlSessionFactory Create Error!");
        }

        return sqlSessionFactory;
    }

    /**
     * SqlSessionTemplate 생성 및 Bean 등록
     *
     * @param sqlSessionFactory SqlSessionFactory
     * @return SqlSessionTemplate
     * @throws SpException
     * @MethodName sqlSession
     */
    @Bean
    SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory) throws SpException {
        SqlSessionTemplate sqlSessionTemplate = null;

        try {
            sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
        } catch (Exception e) {
            throw new SpException(MipErrorEnum.SP_DB_ERROR, null, "SqlSessionTemplate Create Error!");
        }

        return sqlSessionTemplate;
    }

}
