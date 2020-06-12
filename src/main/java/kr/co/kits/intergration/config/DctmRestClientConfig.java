package kr.co.kits.intergration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.emc.documentum.rest.client.sample.client.DCTMRestClient;
import com.emc.documentum.rest.client.sample.client.DCTMRestClientBinding;
import com.emc.documentum.rest.client.sample.client.DCTMRestClientBuilder;

@Configuration
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
