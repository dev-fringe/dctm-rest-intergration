package kr.co.kits.intergration.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.kits.intergration.service.FolderService;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@Validated
public class FolderController {

	private final FolderService folderService;
	
	@RequestMapping("/folders/create")
	public @ResponseBody Map<String, Object> create(
			  @RequestParam(name = "cabinet", required = true) String cabinetName
			, @RequestParam(name = "folder_name", required = true) String  folderName
			) {
		Map<String, Object> map = new HashMap<>();
		try {
			map.put("create", folderService.createFolderOrGetFolderByCabinetName(cabinetName,folderName));
			map.put("status","200");
			map.put("message","OK");
		}catch (Exception e) {
			map.put("status","500");
			map.put("message","Internal Server Error is : " + e.getMessage().toString());
		}
		return map;
	}
}
