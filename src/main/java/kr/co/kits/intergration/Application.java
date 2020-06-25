package kr.co.kits.intergration;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.util.StringUtils;

import io.undertow.Undertow;
import lombok.SneakyThrows;

@Import({UndertowServer.class})
public class Application implements InitializingBean{
   
	public static final String NAME = "rest-dctm-intergration";
	@Autowired Undertow undertow;

	public static class ApplicationProfile{
		public static final String DEV = "DEV";
		public static final String STG = "STG";
		public static final String LOC = "LOC";
		public static final String PRD = "PRD";
		
	}
	
	public Application() {
		super();
		if(StringUtils.hasText(System.getProperty("spring.profiles.active")) == false){
			System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME,  ApplicationProfile.LOC);			
		}else {
			System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME));
		}
	}

	@SneakyThrows
	public static void main(String[] args) {
    	new AnnotationConfigApplicationContext(Application.class);   
    }

	@SneakyThrows
	public void afterPropertiesSet(){
		undertow.start();
	}
}