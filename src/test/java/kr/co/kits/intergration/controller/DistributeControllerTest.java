package kr.co.kits.intergration.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import kr.co.kits.intergration.config.DctmRestClientConfig;
import kr.co.kits.intergration.config.DctmRestCommonConfig;
import kr.co.kits.intergration.config.DctmRestContextInit;
import kr.co.kits.intergration.config.DctmRestContextServlet;
import kr.co.kits.intergration.config.DctmRestRibbonLBConfig;
import kr.co.kits.intergration.config.DctmRestSessionConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { DctmRestClientConfig.class,DctmRestCommonConfig.class, DctmRestRibbonLBConfig.class, DctmRestSessionConfig.class, DctmRestContextServlet.class}, initializers = DctmRestContextInit.class)
@WebAppConfiguration
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("samsung e ACS Controller Mock")
public class DistributeControllerTest {
	@Autowired private WebApplicationContext wac;
	protected MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}
	
	@DisplayName("distributed-upload")
	@Test
	@Order(1)
//	@Disabled
	public void distributedUpload() throws Exception {
			MvcResult res = mockMvc.perform(MockMvcRequestBuilders.post("/distributed-upload-href")
						.param("cabinet", "Temp")
						.param("object_name", "test_object")
						.param("format", "crtext")
						.param("content-length", "23")
					).andDo(print())
					.andExpect(status().isOk()).andReturn();		
	}
}
