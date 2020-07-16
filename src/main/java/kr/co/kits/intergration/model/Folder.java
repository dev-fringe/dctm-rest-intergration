package kr.co.kits.intergration.model;

import org.springframework.util.StringUtils;

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
		if(folder != null) {
			if(StringUtils.hasText(folder.getName())){
				return name + "/" + folder;
			}else {
				return name;
			}
		}else {
			return name  ;
		}
	}

	public Folder(String folder, String subfolder) {
		super();
		this.name = folder;
		this.folder = new Folder(subfolder);  
	}
	public Folder(String folder) {
		super();
		if(folder != null) {
			this.name = folder;
		}
	}
}
