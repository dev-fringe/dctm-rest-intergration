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

	public Distribute() {
		super();
		this.cabinetName = "Temp";
		this.objectName = "test";
		this.format = "crtext";
		this.contentLength = "23";
		this.creationTime = new Date();
	}

	public Distribute(String cabinetName, String objectName, String format, String contentLength) {
		super();
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
	

	
}
