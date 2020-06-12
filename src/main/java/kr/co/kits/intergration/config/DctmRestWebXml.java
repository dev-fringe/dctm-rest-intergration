package kr.co.kits.intergration.config;

import javax.servlet.Filter;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class DctmRestWebXml extends AbstractAnnotationConfigDispatcherServletInitializer{

	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { DctmRestContextServlet.class};
	}

	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] {DctmRestCommonConfig.class,DctmRestContextInit.class, DctmRestRibbonLBConfig.class, DctmRestClientConfig.class, DctmRestMustachViewConfig.class, DctmRestSessionConfig.class, DctmRestSessionConfig.class};
	}

	protected String getServletName() {
	    return "bocs-servlet";
	}
	
    protected Filter[] getServletFilters() {
      CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
      characterEncodingFilter.setEncoding("UTF-8");
      return new Filter[] { characterEncodingFilter};
    }
    
}
