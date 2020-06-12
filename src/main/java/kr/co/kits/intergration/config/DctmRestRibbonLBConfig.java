package kr.co.kits.intergration.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.LoadBalancerBuilder;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;

@Configuration
public class DctmRestRibbonLBConfig {
//	@Autowired
//	IClientConfig iClientConfig;
//	
//	@Bean
//	public ServerList<Server> ribbonServerList(IClientConfig config) {
//		ConfigurationBasedServerList serverList = new ConfigurationBasedServerList();
//		serverList.initWithNiwsConfig(iClientConfig);
//		return serverList;
//	}
	    
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
