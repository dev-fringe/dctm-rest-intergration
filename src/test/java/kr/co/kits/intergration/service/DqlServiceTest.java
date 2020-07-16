package kr.co.kits.intergration.service;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.emc.documentum.rest.client.sample.client.DCTMRestClient;
import com.emc.documentum.rest.client.sample.model.Feed;
import com.emc.documentum.rest.client.sample.model.RestObject;

import kr.co.kits.intergration.config.ConfigDctmRestCommon;
import kr.co.kits.intergration.config.InitDctmRestContext;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ConfigDctmRestCommon.class}, initializers = InitDctmRestContext.class)
@TestMethodOrder(OrderAnnotation.class)
public class DqlServiceTest {

	@Autowired DCTMRestClient dctmRestClient;
	
	@Test public void dql() throws Exception {
		Feed<RestObject> feed = dctmRestClient.dql(String.format("select * from dm_folder where object_name ='%s'","ABC"));
        if(feed.getEntries() != null && feed.getEntries().size() == 1) {
        	System.out.println("exist");
        }
	}
}
