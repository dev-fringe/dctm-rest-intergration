package kr.co.kits.intergration.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.emc.documentum.rest.client.sample.client.DCTMRestClient;
import com.emc.documentum.rest.client.sample.model.LinkRelation;
import com.emc.documentum.rest.client.sample.model.RestObject;

import kr.co.kits.intergration.Application;
import kr.co.kits.intergration.mapper.DistributeMapper;
import kr.co.kits.intergration.model.Distribute;
import kr.co.kits.intergration.model.Folder;
import kr.co.kits.intergration.service.support.DistributeServiceSupport;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class DistributeService extends DistributeServiceSupport{

	@Autowired DistributeMapper mapper;
	
	//for samsung e 
    public RestObject requestDistributedUpload(String cabinetName, String objectName, String username, String password, String format, String contentLength, String networkLocation, Folder folder,String itemsPerPage, String objectType, String... params) {
    	List<String> ses = new ArrayList<>();
    	ses.add("require-dc-write"); ses.add("true");
    	ses.add("media-url-policy"); ses.add("dc-pref");
    	if(StringUtils.hasText(format)) {
    		ses.add("format"); ses.add(format);
    	}
    	if(StringUtils.hasText(contentLength)) {
    		ses.add("content-length"); ses.add(contentLength);
    	}
    	if(StringUtils.hasText(networkLocation)) {
    		ses.add("network-location"); ses.add(networkLocation);
    	}
    	if(params != null) {
	    	for (String param : params) {
	    		ses.add(param); 
			}
    	}
    	String[] arrayparams = ses.stream().toArray(String[]::new);
    	if(log.isDebugEnabled()) {
    		log.debug("params = " + Arrays.toString(arrayparams));
    	}
    	if(StringUtils.hasText(username) && StringUtils.hasText(password)) {
    		 DCTMRestClient dctmRestUserClient = this.getDctmRestClientByUsernameAndPassword(username, password);
    		 return this.requestRestObjectForDistributeWrite(dctmRestUserClient, cabinetName, objectName, objectType, itemsPerPage, folder, arrayparams);
    	}else {
    		return this.requestRestObjectForDistributeWrite(cabinetName, objectName, objectType, itemsPerPage, folder, arrayparams);
    	}
    }


	public String requestHrefDistributedUpload(Distribute d) {
		String href = this.requestDistributedUpload(d.getCabinetName(), d.getObjectName(), d.getUsername(), d.getPassword(),d.getFormat(), d.getContentLength(), d.getNetworkLocation(), d.getFolder(), d.getItemsPerPage(), d.getObjectType()).getHref(LinkRelation.DISTRIBUTED_UPLOAD);
		if (StringUtils.hasText(href) && href.contains("http")) {
			d.setHref(href);
			getSession().setAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, Application.NAME);
			d.setSessionId(getSession().getId());
			log.info("Data is " + d);
			mapper.insertDistribute(d);
		}
		return href;
	}
	
	public String requestHrefDistributedUploadComplete(Distribute d) {
		log.info("session size is " + getSessionSize(Application.NAME));
		d.setSessionId(getSession().getId());
		mapper.insertDistribute(d);
		getSession().invalidate();
		log.info("session size is " + getSessionSize(Application.NAME));
		return "success";
	}

}
