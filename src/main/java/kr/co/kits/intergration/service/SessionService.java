package kr.co.kits.intergration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

import kr.co.kits.intergration.Application;

@Service
public class SessionService {
	
    @Autowired FindByIndexNameSessionRepository<? extends Session> findByIndexNameSessionRepository;

    public Integer count(String indexValue){
    	return findByIndexNameSessionRepository.findByPrincipalName(Application.NAME).values().size();
    }
}
