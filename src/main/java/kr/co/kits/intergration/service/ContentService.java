package kr.co.kits.intergration.service;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.emc.documentum.rest.client.sample.model.RestObject;

import kr.co.kits.intergration.service.support.DistributeServiceSupport;

@Service
public class ContentService extends DistributeServiceSupport{

	public static final String REST_OBJECT = "REST_OBJECT";
	public static final String HREF_URL = "HREF_URL";
	public static final String LOCAL_URL = "LOCAL_URL";
	
	public Object getContentByType(RestObject createdObjectWithContent){
		return this.getContentByType(createdObjectWithContent, HREF_URL);
	}
	
	public Object getContentByType(RestObject createdObjectWithContent, String type){
     	String[] params = Arrays.asList("media-url-policy","all").stream().toArray(String[]::new);
     	if(type.equals(LOCAL_URL)) {
     		return this.getHrefLocalPrimaryContent(createdObjectWithContent, params);
     	}else if(type.equals(HREF_URL)) {
     		return this.getHrefAcsPrimaryContent(createdObjectWithContent, params);
     	}else {
     		return this.getPrimaryContent(createdObjectWithContent, params);
     	}
	}
}
