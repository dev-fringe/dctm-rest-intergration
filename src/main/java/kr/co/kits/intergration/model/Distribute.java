package kr.co.kits.intergration.model;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Distribute {
	@NotEmpty(message = "Please provide a cabinetName")
	private String cabinetName;
	@NotEmpty(message = "Please provide a objectName")
	private String objectName;
	@NotEmpty(message = "Please provide a format")
	private String format;
	@NotEmpty(message = "Please provide a contentLength")
	private String contentLength;
	private String networkLocation,username, password;

	public Distribute() {
		super();
		this.cabinetName = "Temp";
		this.objectName = "test";
		this.format = "crtext";
		this.contentLength = "23";
	}
	
}
