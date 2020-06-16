package kr.co.kits.intergration.model;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Distribute {
	
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

	public Distribute() {
		super();
		this.cabinetName = "Temp";
		this.objectName = "test";
		this.format = "crtext";
		this.contentLength = "23";
	}
	
}
