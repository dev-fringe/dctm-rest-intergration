package kr.co.kits.intergration.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.emc.documentum.rest.client.sample.client.DCTMRestClient;
import com.emc.documentum.rest.client.sample.model.LinkRelation;
import com.emc.documentum.rest.client.sample.model.RestObject;

import kr.co.kits.intergration.Application;
import kr.co.kits.intergration.mapper.DistributeMapper;
import kr.co.kits.intergration.model.Distribute;
import kr.co.kits.intergration.service.support.DistributeServiceSupport;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class DistributeService extends DistributeServiceSupport{

	@Autowired DistributeMapper mapper;
    @Autowired FindByIndexNameSessionRepository<? extends Session> findByIndexNameSessionRepository;
    @Value("${app.session.size:10}") Integer maxSessionSize;
	
	//for samsung e
    public RestObject requestDistributedUpload(String cabinetName, String objectName, String username, String password, String format, String contentLength, String networkLocation, String... params) {
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
    		 return this.requestRestObjectForDistributeWrite(dctmRestUserClient, cabinetName, objectName, arrayparams);
    	}else {
    		return this.requestRestObjectForDistributeWrite(cabinetName, objectName, arrayparams);
    	}
    }

	public String requestHrefDistributedUpload(Distribute d) {
		if(maxSessionSize <= findByIndexNameSessionRepository.findByPrincipalName(Application.NAME).values().size()) {
			return "sorry. The number of users on the current server has been exceeded.";
		}
		String href = this.requestDistributedUpload(d.getCabinetName(), d.getObjectName(), d.getUsername(), d.getPassword(), d.getFormat(), d.getContentLength(), d.getNetworkLocation()).getHref(LinkRelation.DISTRIBUTED_UPLOAD);
		d.setHref(href);
		getSession().setAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, Application.NAME);
		d.setSessionId(getSession().getId());
		mapper.insertDistribute(d);
		return href;
	}
	
	public String requestHrefDistributedUploadComplete(Distribute d) {
		getSession().invalidate();
		mapper.insertDistribute(d);
		return "success";
	}

}
