package kr.co.kits.intergration.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ComponentScan(basePackages = "kr.co.kits.intergration.controller")
@EnableWebMvc
@EnableScheduling
@Configuration
public class DctmRestContextServlet implements WebMvcConfigurer {
}
