package kr.co.kits.intergration.service.support;

import static com.emc.documentum.rest.client.sample.model.LinkRelation.ENCLOSURE;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.emc.documentum.rest.client.sample.client.DCTMRestClient;
import com.emc.documentum.rest.client.sample.client.DCTMRestClientBinding;
import com.emc.documentum.rest.client.sample.client.DCTMRestClientBuilder;
import com.emc.documentum.rest.client.sample.model.Entry;
import com.emc.documentum.rest.client.sample.model.FolderLink;
import com.emc.documentum.rest.client.sample.model.Repository;
import com.emc.documentum.rest.client.sample.model.Repository.Server;
import com.emc.documentum.rest.client.sample.model.RestObject;
import com.emc.documentum.rest.client.sample.model.plain.PlainRestObject;

import kr.co.kits.intergration.Application;
import kr.co.kits.intergration.model.Folder;
import kr.co.kits.intergration.service.FolderService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DistributeServiceSupport {

	@Autowired DCTMRestClient dctmRestClient;
	@Autowired FindByIndexNameSessionRepository<? extends Session> findByIndexNameSessionRepository;
	@Autowired FolderService folderService;
	@Value("${app.contextRoot}") String contextRoot;
	@Value("${app.repository}") String repository;
	@Value("${app.session.seconds:1800}") Integer interval;
	@Value("${app.session.size:50}")  Integer sessionSize;
	
    public boolean supported() {
    	return supported(this.dctmRestClient);
    }
    
    public boolean supported(DCTMRestClient dctmRestClient) {
        Repository repo = dctmRestClient.getRepository();
        Server server = repo.getServers().get(0);
        String[] version = server.getVersion().split("\\.");
        if(Integer.parseInt(version[0]) < 16) {
            return false;
        } else {
            return true;
        }
    }
    
    public DCTMRestClient getDctmRestClientByUsernameAndPassword(String username, String password) {
		return DCTMRestClientBuilder.buildSilently(DCTMRestClientBinding.JSON, contextRoot, repository, username, password);
    }
    
    public RestObject requestRestObjectForDistributeWrite(String cabinetName, String objectName,Folder folder, String... params) {
//    	RestObject restObject = null;
//    	synchronized (this.dctmRestClient) {// thread not safe 
//    		restObject = this.requestRestObjectForDistributeWrite(this.dctmRestClient, cabinetName, objectName, params);
//		}
    	return this.requestRestObjectForDistributeWrite(this.dctmRestClient, cabinetName, objectName, folder, params);
    }
    
    public RestObject requestRestObjectForDistributeWrite(String cabinetName, String objectName, String... params) {
//    	RestObject restObject = null;
//    	synchronized (this.dctmRestClient) {// thread not safe 
//    		restObject = this.requestRestObjectForDistributeWrite(this.dctmRestClient, cabinetName, objectName, params);
//		}
    	return this.requestRestObjectForDistributeWrite(this.dctmRestClient, cabinetName, objectName, null, params);
    }
    
    @SneakyThrows
	public RestObject requestRestObjectForDistributeWrite(DCTMRestClient dctmRestUserClient, String cabinetName, String objectName, Folder folder, String... params) {
    	Integer appSessionSize = getSessionSize(Application.NAME);
    	log.info("this servlet session size is " + appSessionSize);
		if (sessionSize <= appSessionSize) {
			throw new Exception("sorry. The number of users on the current server has been exceeded.");
		}
		if (!supported(dctmRestClient)) {
			throw new Exception("The BOCS Write is not supported by the repository " + dctmRestClient.getRepository().getServers().get(0).getVersion().split("\\.")[0]);
		}
        RestObject cabinet = dctmRestClient.getCabinet(cabinetName);
        RestObject newObjectWithoutContent = new PlainRestObject("object_name", objectName);
        if(folder == null || folder.getName() == null) {
        	return dctmRestClient.createDocument(cabinet, (RestObject)newObjectWithoutContent, (Object)null, null, params);
        }else {
    		Object obj = folderService.createFolderOrGetFolderByCabinetName(cabinetName, folder.getName());
    		if(obj instanceof RestObject) {
    			Folder subFolder = folder.getFolder();
    			if(subFolder != null && subFolder.getName() != null) {
	    	        RestObject subFolderObject = new PlainRestObject("object_name", subFolder.getName());
	    			RestObject restObject = folderService.createFolder((RestObject)obj, subFolderObject);
	    			return dctmRestClient.createDocument(restObject, (RestObject)newObjectWithoutContent, (Object)null, null, params);
    			}else{
	    			return dctmRestClient.createDocument((RestObject)obj, (RestObject)newObjectWithoutContent, (Object)null, null, params);
    				
    			}
    		}else if (obj instanceof Entry<?>) {
      			Folder subFolder = folder.getFolder();
    			if(subFolder != null && subFolder.getName() != null ) {
	    			Object obj2 = folderService.createFolderOrGetFolderByEntry((Entry<?>)obj,  subFolder.getName());
	    			if(obj2 instanceof RestObject) {
		    			return dctmRestClient.createDocument((RestObject)obj2, (RestObject)newObjectWithoutContent, (Object)null, null, params);
	    			}else if (obj2 instanceof Entry<?>) {
	    				FolderLink movedLink = dctmRestClient.getFolderLink(((Entry)obj2).getContentSrc());
		    			return dctmRestClient.createDocument(movedLink, (RestObject)newObjectWithoutContent, (Object)null, null, params);
	    			}
    			}else {
    				FolderLink movedLink = dctmRestClient.getFolderLink(((Entry)obj).getContentSrc());
	    			return dctmRestClient.createDocument(movedLink, (RestObject)newObjectWithoutContent, (Object)null, null, params);
    			}
    		}
        	return null;
        }
	}

	
    public RestObject requestRestObjectForDistributeWrite(DCTMRestClient dctmRestClient, String cabinetName, String objectName, String... params) {
    	return this.requestRestObjectForDistributeWrite(dctmRestClient, cabinetName, objectName, null, params);
    }
    
    public HttpSession getSession(){
    	HttpSession session = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
    	session.setMaxInactiveInterval(interval);
    	return session;
    }
    
    public Integer getSessionSize(String principalName){
    	return findByIndexNameSessionRepository.findByPrincipalName(principalName).values().size();
    }
    
    
	public RestObject getPrimaryContent(RestObject createdObjectWithContent, String... params){
		return dctmRestClient.getPrimaryContent(createdObjectWithContent, params);
	}
	
	public String getHrefAcsPrimaryContent(RestObject createdObjectWithContent, String... params){
		return this.getPrimaryContent(createdObjectWithContent, params).getHref(ENCLOSURE, "ACS");
	}
	
	public String getHrefLocalPrimaryContent(RestObject createdObjectWithContent, String... params){
		return this.getPrimaryContent(createdObjectWithContent,params).getHref(ENCLOSURE, "LOCAL");
	}
	
}
