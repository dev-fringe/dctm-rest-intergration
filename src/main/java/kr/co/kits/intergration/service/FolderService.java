package kr.co.kits.intergration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emc.documentum.rest.client.sample.client.DCTMRestClient;
import com.emc.documentum.rest.client.sample.model.Entry;
import com.emc.documentum.rest.client.sample.model.Feed;
import com.emc.documentum.rest.client.sample.model.FolderLink;
import com.emc.documentum.rest.client.sample.model.RestObject;
import com.emc.documentum.rest.client.sample.model.plain.PlainRestObject;

import kr.co.kits.intergration.model.Folder;
import kr.co.kits.intergration.service.support.DistributeServiceSupport;

@Service
public class FolderService extends DistributeServiceSupport{

	public static final String REST_OBJECT = "REST_OBJECT";
	public static final String HREF_URL = "HREF_URL";
	public static final String LOCAL_URL = "LOCAL_URL";
	
	@Autowired DCTMRestClient dctmRestClient;
	
	public RestObject createFolder(String cabinetName, String folderName ) {
		RestObject cabinetObject = dctmRestClient.getCabinet(cabinetName);
        RestObject newFolder = new PlainRestObject("object_name", folderName);
        return this.dctmRestClient.createFolder(cabinetObject, newFolder);
	}
	
	public RestObject createFolder(String cabinetName, RestObject childObject ) {
		RestObject cabinetObject = dctmRestClient.getCabinet(cabinetName);
        return this.dctmRestClient.createFolder(cabinetObject, childObject);
	}
	
	public RestObject createFolder(RestObject parentObject, RestObject childObject ) {
        return this.dctmRestClient.createFolder(parentObject, childObject);
	}

	public RestObject createFolder(RestObject parentObject, String folderName ) {
        RestObject newFolder = new PlainRestObject("object_name", folderName);
        return this.dctmRestClient.createFolder(parentObject, newFolder);
	}
	
	public Entry<RestObject> isExistFolderByCabinetName(String cabinetName, Folder folder) {
		String dql = String.format("SELECT * FROM DM_FOLDER where any r_folder_path = '/%s/%s'", cabinetName, folder);
		Feed<RestObject> feed = dctmRestClient.dql(dql);
		if (feed.getEntries() != null && feed.getEntries().size() == 1) {
			return feed.getEntries().get(0);
		}	
		return null;
	}

	public Entry<RestObject> isExistFolderByCabinetName(String cabinetName, String folderPath) {
		String dql = String.format("SELECT * FROM DM_FOLDER where any r_folder_path = '/%s/%s'", cabinetName, folderPath);
		Feed<RestObject> feed = dctmRestClient.dql(dql);
		if (feed.getEntries() != null && feed.getEntries().size() == 1) {
			return feed.getEntries().get(0);
		}	
		return null;
	}

	/**
	 * RestObject는 폴더 생성, Entry는 생성된 폴더
	 * @param feed
	 * @param folderName
	 * @return
	 */
	public Object createFolderOrGetFolderByCabinetName(String cabinetName, String folderName) {
		Entry<RestObject> entry = this.isExistFolderByCabinetName(cabinetName, new Folder(folderName));
		if(entry == null) {
			RestObject tempCabinet = dctmRestClient.getCabinet(cabinetName);
			RestObject newFolder = new PlainRestObject("object_name", folderName);
			return this.dctmRestClient.createFolder(tempCabinet, newFolder);
		}
		return entry;
	}
	/**
	 * RestObject는 폴더 생성, Entry는 생성된 폴더
	 * @param feed
	 * @param folderName
	 * @return
	 */
	public Object createFolderOrGetFolderByEntry(Entry<?> feed, String folderName) {
        FolderLink movedLink = dctmRestClient.getFolderLink(feed.getContentSrc());
        Feed<RestObject> parentFolderLinks = dctmRestClient.getFolders(movedLink);
        if(parentFolderLinks.getEntries() != null) {
	        for(Entry<?> folder : parentFolderLinks.getEntries()) {
	          if(folder.getTitle().equals(folderName)) {
	        	  return folder;
	          }
	        }
        }
        RestObject newFolder = new PlainRestObject("object_name", folderName);
        return this.dctmRestClient.createFolder(movedLink, newFolder);
	}
	
	public Object createFolderOrGetFolderByEntry(Entry<?> feed, String cabinetName, Folder folder, String folderName) {
    	Object obj = this.isExistFolderByCabinetName(cabinetName, folder);
    	if(obj != null) {
    		return obj;
    	}
        RestObject newFolder = new PlainRestObject("object_name", folderName);
        FolderLink movedLink = dctmRestClient.getFolderLink(feed.getLinks().get(0).getHref());
        return this.dctmRestClient.createFolder(movedLink, newFolder);
	}
	
	public Entry<?> getFolderByCabinetName(String cabinetName, String folderName) {
		RestObject tempCabinet = dctmRestClient.getCabinet(cabinetName);
		Feed<RestObject> feed = this.dctmRestClient.getFolders(tempCabinet);
		for(Entry<?> folder : feed.getEntries()) {
			if(folder.getTitle().equals(folderName)) {
				return folder;
			}
		}
		return null;
	}
	public Entry<?> getFolderByEntry(Entry<?> feed, String folderName) {
        FolderLink movedLink = dctmRestClient.getFolderLink(feed.getContentSrc());
        Feed<RestObject> parentFolderLinks = dctmRestClient.getFolders(movedLink);
        for(Entry<?> folder : parentFolderLinks.getEntries()) {
          if(folder.getTitle().equals(folderName)) {
        	  return folder;
          }
        }
		return null;
	}
}
