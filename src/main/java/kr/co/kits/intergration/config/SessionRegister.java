package kr.co.kits.intergration.config;

import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class SessionRegister extends AbstractHttpSessionApplicationInitializer {

    public SessionRegister() {
        super(DctmRestSessionConfig.class);
        log.info("Success to register SessionConfig.");
    }
}