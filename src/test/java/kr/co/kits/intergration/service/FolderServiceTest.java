package kr.co.kits.intergration.service;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.emc.documentum.rest.client.sample.client.DCTMRestClient;
import com.emc.documentum.rest.client.sample.model.Entry;
import com.emc.documentum.rest.client.sample.model.RestObject;
import com.emc.documentum.rest.client.sample.model.plain.PlainRestObject;

import kr.co.kits.intergration.config.ConfigDctmRestCommon;
import kr.co.kits.intergration.config.InitDctmRestContext;
import kr.co.kits.intergration.model.Folder;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ConfigDctmRestCommon.class}, initializers = InitDctmRestContext.class)
@TestMethodOrder(OrderAnnotation.class)
public class FolderServiceTest {

	@Autowired FolderService folderService;
	@Autowired DistributeService service;
	@Autowired DCTMRestClient dctmRestClient;
	
	@Test public void createOrGetFolder() throws Exception {
		Object obj = folderService.createFolderOrGetFolderByCabinetName("Temp", "TEST7");
		if(obj instanceof RestObject) {
	        RestObject test1= new PlainRestObject("object_name", "TEST2");
			folderService.createFolder((RestObject)obj, test1);
		}else if (obj instanceof Entry<?>) {
			Object obj2 = folderService.createFolderOrGetFolderByEntry((Entry<?>)obj, "TEST3");
			if(obj2 instanceof RestObject) {
				System.out.println("new Folder");
			}else if (obj2 instanceof Entry<?>) {
				System.out.println("exists Folder");
			}
		}
	}
	@Test
	public void getFolder() throws Exception {
		String cabinetName = "Temp";
		Folder folder = new Folder("ABC", new Folder("DEF"));
		String dql = String.format("SELECT * FROM DM_FOLDER where any r_folder_path = '/%s/%s'", cabinetName, folder);
		dctmRestClient.dql(dql);
	}
}
