package kr.co.kits.intergration.service;

import org.springframework.stereotype.Service;

import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.LoadBalancerStats;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class LoadBalancerService {

	private final ILoadBalancer loadBalancer;

	@SneakyThrows
	public String call(final String path) {
//		return LoadBalancerCommand.<String>builder().withLoadBalancer(loadBalancer).build()
//				.submit(new ServerOperation<String>() {
//					@Override
//					public Observable<String> call(Server server) {
//						URL url;
//						try {
//							url = new URL("http://" + server.getHost() + ":" + server.getPort() + path);
//							HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//							return Observable.just(conn.getResponseMessage());
//						} catch (Exception e) {
//							return Observable.error(e);
//						}
//					}
//				}).toBlocking().first();
		return null;
	}

	public LoadBalancerStats getLoadBalancerStats() {
		return ((BaseLoadBalancer) loadBalancer).getLoadBalancerStats();
	}
	
	public void test() {
		for (int i = 0; i < 6; i++) {
			System.out.println(this.call("/"));
		}
		System.out.println("=== Load balancer stats ===");
		System.out.println(this.getLoadBalancerStats());
	}
}
