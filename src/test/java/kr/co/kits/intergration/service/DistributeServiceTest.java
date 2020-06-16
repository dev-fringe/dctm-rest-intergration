package kr.co.kits.intergration.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import kr.co.kits.intergration.config.DctmRestClientConfig;
import kr.co.kits.intergration.config.DctmRestCommonConfig;
import kr.co.kits.intergration.config.DctmRestContextInit;
import kr.co.kits.intergration.config.DctmRestRibbonLBConfig;
import kr.co.kits.intergration.config.DctmRestSessionConfig;
import kr.co.kits.intergration.model.Distribute;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { DctmRestClientConfig.class,DctmRestCommonConfig.class, DctmRestRibbonLBConfig.class, DctmRestSessionConfig.class}, initializers = DctmRestContextInit.class)
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("ACS Test")
public class DistributeServiceTest {

	@Autowired DistributeService service;
	
	@DisplayName("samsung e ACS HREF get ")
	@Test
	@Order(1)
	public void downloadfile_success_ie_pdf() throws Exception {
//		String test = service.requestHrefkDistributedUpload(new Distribute());
		String test2 = service.requestObjectDistributedUpload(new Distribute());
		
		System.out.println(test2);
	}
	
}
