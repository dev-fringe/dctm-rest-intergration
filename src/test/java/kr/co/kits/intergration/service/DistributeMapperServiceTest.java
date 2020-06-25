package kr.co.kits.intergration.service;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import kr.co.kits.intergration.config.ConfigDctmRestCommon;
import kr.co.kits.intergration.config.InitDctmRestContext;
import kr.co.kits.intergration.mapper.DistributeMapper;
import kr.co.kits.intergration.model.Distribute;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ConfigDctmRestCommon.class}, initializers = InitDctmRestContext.class)
@TestMethodOrder(OrderAnnotation.class)
public class DistributeMapperServiceTest {

	@Autowired DistributeMapper mapper;
	
	@Test
	public void downloadfile_success_ie_pdf() throws Exception {
		mapper.insertDistribute(new Distribute("Temp", "test", "crtext", "23"));
	}
	
}
