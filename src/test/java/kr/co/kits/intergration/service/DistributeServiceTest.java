package kr.co.kits.intergration.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import kr.co.kits.intergration.config.ConfigDctmRestCommon;
import kr.co.kits.intergration.config.InitDctmRestContext;
import kr.co.kits.intergration.model.Distribute;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ConfigDctmRestCommon.class}, initializers = InitDctmRestContext.class)
@TestMethodOrder(OrderAnnotation.class)
public class DistributeServiceTest {

	@Autowired DistributeService service;
	
	protected MockHttpSession session;
	protected MockHttpServletRequest request;
	
	@BeforeEach
	public void init() {
	    request = new MockHttpServletRequest();
	    request.setSession(session);
	    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}
	@Test
	public void downloadfile_success_ie_pdf() throws Exception {
		String test2 = service.requestHrefDistributedUpload(new Distribute());
		System.out.println(test2);
		service.requestHrefDistributedUploadComplete(new Distribute(""));
	}
	
}
