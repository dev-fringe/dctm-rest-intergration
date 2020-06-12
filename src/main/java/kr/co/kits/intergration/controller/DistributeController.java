package kr.co.kits.intergration.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.kits.intergration.model.Distribute;
import kr.co.kits.intergration.service.DistributeService;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@Validated
public class DistributeController {

	private final DistributeService distributeService;
	
	@RequestMapping("/requestUploadHref")
	public @ResponseBody Map<String, Object> requestUploadHref(
			  @RequestParam(name = "cabinet", required = true) String cabinetName
			, @RequestParam(name = "object_name", required = true) String  objectName
			, @RequestParam(name = "format", required = true) String format
			, @RequestParam(name = "content-length", required = true) String contentLength
			, @RequestParam(name = "network-location", required = false) String networkLocation //Please input the network location used by the bocs
			, @RequestParam(name = "username", required = false) String username
			, @RequestParam(name = "password", required = false) String password
			) {
		Map<String, Object> map = new HashMap<>();
		map.put("distributed-upload", distributeService.requestHrefDistributedUpload(cabinetName, objectName, username, password, format, contentLength, networkLocation));
		return map;
	}
	
	@RequestMapping("/requestHrefDistributedUpload")
	public @ResponseBody Map<String, Object> requestHrefDistributedUpload(@Valid @RequestBody Distribute distribute) {
		Map<String, Object> map = new HashMap<>();
		map.put("distributed-upload", distributeService.requestHrefDistributedUpload(distribute));
		return map;
	}	
}
