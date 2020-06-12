package kr.co.kits.intergration.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.emc.documentum.rest.client.sample.model.LinkRelation;

import kr.co.kits.intergration.service.DistributeService;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class HomeController {

	private final DistributeService distributeService;
	
	@RequestMapping("/")
	public String index(Model model , HttpSession session) {
		session.setAttribute("test", "test");
		String cabinet ="Temp";
		String objectName = "distribute_upload_obj_test";
		String format = "crtext";
		String contentLength = "23";
		model.addAttribute("distributed-upload", distributeService.requestRestObjectForDistributeWrite(cabinet, objectName, "require-dc-write", "true", "format", format, "content-length", contentLength).getHref(LinkRelation.DISTRIBUTED_UPLOAD));
		model.addAttribute("cabinet", cabinet);
		model.addAttribute("object_name", objectName);
		model.addAttribute("format", format);
		model.addAttribute("content-length", contentLength);
		return "home";
	}
}
