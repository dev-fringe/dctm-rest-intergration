package kr.co.kits.intergration.config;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.transaction.PlatformTransactionManager;

import com.emc.documentum.rest.client.sample.client.DCTMRestClient;
import com.emc.documentum.rest.client.sample.client.DCTMRestClientBinding;
import com.emc.documentum.rest.client.sample.client.DCTMRestClientBuilder;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.LoadBalancerBuilder;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;

import kr.co.kits.intergration.Application.ApplicationProfile;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Configuration
@PropertySource("classpath:app-${spring.profiles.active}.properties")
@ComponentScan("kr.co.kits.intergration.service")
@MapperScan("kr.co.kits.intergration.mapper")
@Log4j2
public class ConfigDctmRestCommon implements InitializingBean{
	
	@Autowired Environment env;

	
	@Profile("LOC")
	@Bean
	public void loc() {
		System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, ApplicationProfile.LOC);
	}

	@Profile("!LOC")
	@Bean
	public void notLoc() {
		System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME));
	}

	@SneakyThrows
	public void afterPropertiesSet(){
		log.info("initialized");
	}
	
	
	public class DctmRestClientConfig {
		@Value("${app.contextRoot}")
		private String contextRoot;
		@Value("${app.username}")
		private String username;	
		@Value("${app.password}")
		private String password;	
		@Value("${app.repository}")
		private String repository;	
		
		@Bean
		public DCTMRestClient dctmRestClient() {
			return DCTMRestClientBuilder.buildSilently(DCTMRestClientBinding.JSON, contextRoot, repository, username, password);
		}
	}
	
	public class DctmRestRibbonLBConfig {
//		@Autowired
//		IClientConfig iClientConfig;
	//	
//		@Bean
//		public ServerList<Server> ribbonServerList(IClientConfig config) {
//			ConfigurationBasedServerList serverList = new ConfigurationBasedServerList();
//			serverList.initWithNiwsConfig(iClientConfig);
//			return serverList;
//		}
		    
		@Bean
		public ServerList<Server> serverList() {
			return new ServerList<Server>() {
				public List<Server> getUpdatedListOfServers() {
					List<Server> serverList = Arrays.asList(new Server("http", "www.google.com", 80), new Server("http", "www.linkedin.com", 80));				
					return serverList;
				}
				public List<Server> getInitialListOfServers() {
					return Arrays.asList(new Server("http", "www.google.com", 80), new Server("http", "www.linkedin.com", 80));
				}
			};
		}
		
		@Bean
	    public ILoadBalancer loadBalancer(ServerList<Server> serverList) {
	       return LoadBalancerBuilder.newBuilder().buildFixedServerListLoadBalancer(serverList.getInitialListOfServers());
	    }
	}
	
	@EnableJdbcHttpSession
	public class DctmRestSessionConfig {
		@Value("${app.jdbc.driver}")
		private String jdbcDriver;
		@Value("${app.jdbc.url}")
		private String jdbcUrl;	
		@Value("${app.jdbc.user}")
		private String jdbcUser;	
		@Value("${app.jdbc.password}")
		private String jdbcPassword;	
		
		@Value("${app.session.seconds:1800}")
		private Integer maxInactiveIntervalInSeconds;

		
		public Integer getMaxInactiveIntervalInSeconds() {
			return maxInactiveIntervalInSeconds;
		}

		public void setMaxInactiveIntervalInSeconds(Integer maxInactiveIntervalInSeconds) {
			this.maxInactiveIntervalInSeconds = maxInactiveIntervalInSeconds;
		}

		@Bean
		public DataSource dataSource() {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName(jdbcDriver);
			dataSource.setUrl(jdbcUrl);
			dataSource.setUsername(jdbcUser);
			dataSource.setPassword(jdbcPassword);
			return dataSource;
		}
	}


	@Bean
	public DataSourceInitializer initializeDatabase(DataSource dataSource) {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScripts(new ClassPathResource("script/schema-h2.sql"), new ClassPathResource("script/import-h2.sql"));
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(populator);
        return dataSourceInitializer;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource); 
	}
	
	@Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		return sqlSessionFactoryBean.getObject();
	}
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(){
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage("kr.co.kits.intergration.mapper");
        configurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        return configurer;
    }	

}
