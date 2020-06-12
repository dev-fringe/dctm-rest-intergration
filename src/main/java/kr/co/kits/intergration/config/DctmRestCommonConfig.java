package kr.co.kits.intergration.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;

import kr.co.kits.intergration.Application.ApplicationProfile;

@Configuration
@PropertySource("classpath:app-${spring.profiles.active}.properties")
@ComponentScan("kr.co.kits.intergration.service")
public class DctmRestCommonConfig {
	
	@Autowired Environment env;
	@Value("${netloc}") String netloc;

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
}
