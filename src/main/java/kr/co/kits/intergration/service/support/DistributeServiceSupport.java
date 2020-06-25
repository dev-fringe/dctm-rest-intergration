package kr.co.kits.intergration.service.support;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.emc.documentum.rest.client.sample.client.DCTMRestClient;
import com.emc.documentum.rest.client.sample.client.DCTMRestClientBinding;
import com.emc.documentum.rest.client.sample.client.DCTMRestClientBuilder;
import com.emc.documentum.rest.client.sample.model.Repository;
import com.emc.documentum.rest.client.sample.model.Repository.Server;
import com.emc.documentum.rest.client.sample.model.RestObject;
import com.emc.documentum.rest.client.sample.model.plain.PlainRestObject;

public class DistributeServiceSupport {

	@Value("${app.contextRoot}")
	private String contextRoot;
	@Value("${app.repository}")
	private String repository;	
	@Autowired
	private DCTMRestClient dctmRestClient;
	
    public boolean supported() {
    	return supported(this.dctmRestClient);
    }
    
    public boolean supported(DCTMRestClient dctmRestClient) {
        Repository repo = dctmRestClient.getRepository();
        Server server = repo.getServers().get(0);
        String[] version = server.getVersion().split("\\.");
        if(Integer.parseInt(version[0]) < 16) {
            System.out.println("The BOCS Write is not supported by the repository " + server.getVersion());
            return false;
        } else {
            return true;
        }
    }
    
    public DCTMRestClient getDctmRestClientByUsernameAndPassword(String username, String password) {
		return DCTMRestClientBuilder.buildSilently(DCTMRestClientBinding.JSON, contextRoot, repository, username, password);
    }
    
    public RestObject requestRestObjectForDistributeWrite(String cabinetName, String objectName, String... params) {
        return this.requestRestObjectForDistributeWrite(this.dctmRestClient, cabinetName, objectName, params);
    }
    
    public RestObject requestRestObjectForDistributeWrite(DCTMRestClient dctmRestClient, String cabinetName, String objectName, String... params) {
        if(!supported(dctmRestClient)) {
            return null;
        }
        RestObject cabinet = dctmRestClient.getCabinet(cabinetName);
        RestObject newObjectWithoutContent = new PlainRestObject("object_name", objectName);
        return dctmRestClient.createDocument(cabinet, (RestObject)newObjectWithoutContent, (Object)null, null, params);
    }
    
    public static HttpSession getSession(){
    	return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
    }
}
