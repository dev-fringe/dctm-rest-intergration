package kr.co.kits.intergration.model;

import java.util.Date;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Distribute {
	
	public static final String REQUEST_ACS_REQUEST = "REQUEST_ACS_REQUEST";
	public static final String REQUEST_ACS_COMPLETE = "REQUEST_ACS_COMPLETE";
	
	@JsonProperty(value = "cabinet")
	@NotEmpty(message = "Please provide a cabinetName")
	private String cabinetName;
	@JsonProperty(value = "object_name")
	@NotEmpty(message = "Please provide a objectName")
	private String objectName;
	@JsonProperty(value = "format")
	@NotEmpty(message = "Please provide a format")
	private String format;
	@JsonProperty(value = "content-length")
	@NotEmpty(message = "Please provide a contentLength")
	private String contentLength;
	@JsonProperty(value = "network-location") // if you want to use bocs
	private String networkLocation;
	private String username, password;
	private String type;
	private String href;
	private Date creationTime;
	private String sessionId;
	private Folder folder;
	private String tree ="";
	private String objectType;
	private String itemsPerPage;
	

	public Distribute() {
		super();
		this.cabinetName = "Temp";
		this.objectName = "test";
		this.format = "crtext";
		this.contentLength = "23";
		this.creationTime = new Date();
	}

	public Distribute(String cabinetName, String objectName, String format, String contentLength, Folder folder) {
		this(cabinetName, objectName, format,contentLength);
		this.folder = folder;
		this.setTree(folder);
	}
	
	public Distribute(String cabinetName, String objectName, String format, String contentLength) {
		this();
		this.cabinetName = cabinetName;
		this.objectName = objectName;
		this.format = format;
		this.contentLength = contentLength;
		this.creationTime = new Date();
		this.type = REQUEST_ACS_REQUEST;
	}

	public Distribute(String username) {
		this.username = username;
		this.type = REQUEST_ACS_COMPLETE;
		this.creationTime = new Date();
	}

	public Distribute(String username, String sessionId) {
		this(username);
		this.sessionId = sessionId;
	}
	
	public String getTree() {
		if(this.folder != null) {
			return folder.toString().replace("null", "");
		}
		return this.tree;

	}
	public void setTree(String tree) {
		this.tree = tree;
	}
	public void setFolder(Folder folder) {
		this.folder = folder;
		this.setTree(folder);
	}	
	public void setTree(Folder folder) {
		if(folder != null) {
			this.tree = folder.toString().replace("null", "");
		}else {
			this.tree = "";
		}
	}
	@Override
	public String toString() {
		return "Distribute [cabinetName=" + cabinetName + ", objectName=" + objectName + ", format=" + format
				+ ", contentLength=" + contentLength + ", networkLocation=" + networkLocation + ", username=" + username
				+ ", password=" + password + ", type=" + type + ", href=" + href + ", creationTime=" + creationTime
				+ ", sessionId=" + sessionId + ", folder=" + folder + ", tree=" + tree + "]";
	}

	public Distribute(String cabinetName, String objectName, String format, String contentLength, Folder folder,String objectType, String itemsPerPage) {
		this(cabinetName, objectName, format,contentLength);
		this.folder = folder;
		this.setTree(folder);
		this.objectType = objectType;
		this.itemsPerPage = itemsPerPage;
	}
	
}
