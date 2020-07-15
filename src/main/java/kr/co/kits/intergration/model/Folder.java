package kr.co.kits.intergration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Folder {
	
	private String name="";
	private Folder folder;
	
	public String toString() {
		return name + "/" + folder;
	}

	public Folder(String folder, String subfolder) {
		super();
		this.name = folder;
		this.folder = new Folder(subfolder);  
	}
	public Folder(String folder) {
		super();
		this.name = folder;
	}
}
