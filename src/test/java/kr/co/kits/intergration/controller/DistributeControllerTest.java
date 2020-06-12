package kr.co.kits.intergration.controller;

import org.junit.jupiter.api.BeforeEach;
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
@DisplayName("samsung e ACS Controller Mock ")
public class DistributeControllerTest {
	@Autowired private WebApplicationContext wac;
	protected MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}
	
	@DisplayName("test1")
	@Test
	@Order(1)
	public void test1() throws Exception {
		System.out.println();
	}
}
