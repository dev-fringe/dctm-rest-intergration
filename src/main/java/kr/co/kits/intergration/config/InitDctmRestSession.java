package kr.co.kits.intergration.config;

import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

public class InitDctmRestSession extends AbstractHttpSessionApplicationInitializer{
	protected String getDispatcherWebApplicationContextSuffix() {
		return AbstractDispatcherServletInitializer.DEFAULT_SERVLET_NAME;
	}
}