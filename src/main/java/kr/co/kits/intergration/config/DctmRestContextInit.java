package kr.co.kits.intergration.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.util.StringUtils;

import kr.co.kits.intergration.Application.ApplicationProfile;

public class DctmRestContextInit implements ApplicationContextInitializer<GenericApplicationContext> {
	
	public void initialize(GenericApplicationContext context) {
		if(StringUtils.hasText(System.getProperty("spring.profiles.active")) == false){
			context.getEnvironment().getSystemProperties().put(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, ApplicationProfile.LOC);
			System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME,  ApplicationProfile.LOC);			
		}else {
			context.getEnvironment().getSystemProperties().put(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME));		
			System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME));
		}
	}
}