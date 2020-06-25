package kr.co.kits.intergration.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
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

import kr.co.kits.intergration.config.ConfigDctmRestCommon;
import kr.co.kits.intergration.config.ConfigDctmRestServlet;
import kr.co.kits.intergration.config.InitDctmRestContext;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ConfigDctmRestCommon.class, ConfigDctmRestServlet.class}, initializers = InitDctmRestContext.class)
@WebAppConfiguration
@TestMethodOrder(OrderAnnotation.class)
public class DistributeControllerTest {
	@Autowired private WebApplicationContext wac;
	protected MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}
	
	@SuppressWarnings("unused")
	@Test
	public void distributedUpload() throws Exception {
			MvcResult res = mockMvc.perform(MockMvcRequestBuilders.post("/distributed-upload-href")
						.param("cabinet", "Temp")
						.param("object_name", "test_object11")
						.param("format", "crtext")
						.param("content-length", "23")
					).andDo(print())
					.andExpect(status().isOk()).andReturn();		
	}
}
