package kr.co.kits.intergration.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.emc.documentum.rest.client.sample.model.LinkRelation;

import kr.co.kits.intergration.Application;
import kr.co.kits.intergration.service.DistributeService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@AllArgsConstructor
public class HomeController {

	@Autowired DistributeService distributeService;
	
    @Autowired FindByIndexNameSessionRepository<? extends Session> findByIndexNameSessionRepository;
	
	@RequestMapping("/")
	public String index(Model model) {
		HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
	    HttpSession session = req.getSession();
		session.setAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, Application.NAME);
		String cabinet ="Temp";
		String objectName = "distribute_upload_obj_test";
		String format = "crtext";
		String contentLength = "23";
		model.addAttribute("distributed-upload", distributeService.requestRestObjectForDistributeWrite(cabinet, objectName, "require-dc-write", "true", "format", format, "content-length", contentLength).getHref(LinkRelation.DISTRIBUTED_UPLOAD));
		model.addAttribute("cabinet", cabinet);
		model.addAttribute("object_name", objectName);
		model.addAttribute("format", format);
		model.addAttribute("content-length", contentLength);
		session.invalidate();
		return "home";
	}

	@RequestMapping("/session")
	public @ResponseBody Object session(Principal principal, Model model) {
		return this.findByIndexNameSessionRepository.findByPrincipalName(Application.NAME).values();
	}
		
}
