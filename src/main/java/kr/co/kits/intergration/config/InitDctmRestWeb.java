package kr.co.kits.intergration.config;

import javax.servlet.Filter;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class InitDctmRestWeb extends AbstractAnnotationConfigDispatcherServletInitializer {

	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { ConfigDctmRestServlet.class };
	}

	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { ConfigDctmRestCommon.class, InitDctmRestContext.class};
	}

	protected Filter[] getServletFilters() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		return new Filter[] { characterEncodingFilter };
	}

}
