package kr.co.kits.intergration.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.emc.documentum.rest.client.sample.client.DCTMRestClient;
import com.emc.documentum.rest.client.sample.model.LinkRelation;

import kr.co.kits.intergration.model.Distribute;
import kr.co.kits.intergration.service.support.DistributeServiceSupport;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class DistributeService extends DistributeServiceSupport{

	//for samsung e
    public String requestHrefDistributedUpload(String cabinetName, String objectName, String username, String password, String format, String contentLength, String networkLocation, String... params) {
    	List<String> ses = new ArrayList<>();
    	ses.add("require-dc-write"); ses.add("true");
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
    	if(log.isInfoEnabled()) {
    		log.info("params = " + Arrays.toString(arrayparams) );
    	}
    	if(StringUtils.hasText(username) && StringUtils.hasText(password)) {
    		 DCTMRestClient dctmRestUserClient = this.getDctmRestClientByUsernameAndPassword(username, password);
    		 // session check - spring session 
    		 return this.requestRestObjectForDistributeWrite(dctmRestUserClient, cabinetName, objectName, arrayparams).getHref(LinkRelation.DISTRIBUTED_UPLOAD);
    	}else {
    		
    		return this.requestRestObjectForDistributeWrite(cabinetName, objectName, arrayparams).getHref(LinkRelation.DISTRIBUTED_UPLOAD);
    	}
    }

	public String requestHrefDistributedUpload(Distribute d) {
		return this.requestHrefDistributedUpload(d.getCabinetName(), d.getObjectName(), d.getUsername(), d.getPassword(), d.getFormat(), d.getContentLength(), d.getCabinetName());
	}

}
