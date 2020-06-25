package kr.co.kits.intergration.config;

import javax.servlet.ServletContext;

import org.h2.server.web.WebServlet;
import org.springframework.web.WebApplicationInitializer;

public class InitDctmRestH2Console implements WebApplicationInitializer {
	public void onStartup(ServletContext servletContext) {
		servletContext.addServlet("h2Console", new WebServlet()).addMapping("/h2-console/*");
	}

}
