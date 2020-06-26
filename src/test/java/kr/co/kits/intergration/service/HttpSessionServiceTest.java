package kr.co.kits.intergration.service;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import kr.co.kits.intergration.config.ConfigDctmRestCommon;
import kr.co.kits.intergration.config.ConfigDctmRestServlet;
import kr.co.kits.intergration.config.InitDctmRestContext;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ConfigDctmRestCommon.class, ConfigDctmRestServlet.class}, initializers = InitDctmRestContext.class)
@TestMethodOrder(OrderAnnotation.class)
@WebAppConfiguration
public class HttpSessionServiceTest {

// nob	
}
