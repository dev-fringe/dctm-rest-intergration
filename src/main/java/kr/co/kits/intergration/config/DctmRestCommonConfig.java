package kr.co.kits.intergration.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;

import kr.co.kits.intergration.Application.ApplicationProfile;
import lombok.extern.log4j.Log4j2;

@Configuration
@PropertySource("classpath:app-${spring.profiles.active}.properties")
@ComponentScan("kr.co.kits.intergration.service")
@Log4j2
public class DctmRestCommonConfig implements InitializingBean{
	
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

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("initialized");
	}
}
