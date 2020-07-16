package kr.co.kits.intergration.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.kits.intergration.model.Distribute;
import kr.co.kits.intergration.model.Folder;
import kr.co.kits.intergration.service.DistributeService;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@Validated
public class DistributeController {

	private final DistributeService distributeService;
	
	@RequestMapping("/distributed-upload-href")
	public @ResponseBody Map<String, Object> distributedUploadHref(
			  @RequestParam(name = "cabinet", required = true) String cabinetName
			, @RequestParam(name = "object_name", required = true) String  objectName
			, @RequestParam(name = "format", required = true) String format
			, @RequestParam(name = "content-length", required = true) String contentLength
			, @RequestParam(name = "network-location", required = false) String networkLocation //Please input the network location used by the bocs
			, @RequestParam(name = "username", required = false) String username
			, @RequestParam(name = "password", required = false) String password
			, @RequestParam(name = "folder", required = false) String folder
			, @RequestParam(name = "subfolder", required = false) String subfolder
			, @RequestParam(name = "r_object_type", required = false) String  rObjectType
			, @RequestParam(name = "items-per-page", required = false) String itemsPerPage
			
			) {
		Map<String, Object> map = new HashMap<>();
		try {
			map.put("distributed-upload", distributeService.requestHrefDistributedUpload(new Distribute(cabinetName, objectName, format, contentLength, new Folder(folder, subfolder), rObjectType, itemsPerPage)));
			map.put("status","200");
			map.put("message","OK");
		}catch (Exception e) {
			map.put("status","500");
			map.put("message","Internal Server Error is : " + e.getMessage().toString());
		}
		return map;
	}

	@RequestMapping("/distributed-upload-href-complete")
	public @ResponseBody Map<String, Object> distributedUploadHref(@RequestParam(name = "username", required = false) String username,  HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		try {
			map.put("distributed-upload-complete", distributeService.requestHrefDistributedUploadComplete(new Distribute(username, session.getId()))); 
			map.put("status","200");
			map.put("message","OK");
		}catch (Exception e) {
			map.put("status","500");
			map.put("message","Internal Server Error is : " + e.getMessage().toString());
		}
		return map;
	}
}
