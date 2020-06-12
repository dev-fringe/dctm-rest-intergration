package kr.co.kits.intergration.config;

import org.springframework.context.annotation.Configuration;

import com.github.mjeanroy.springmvc.view.mustache.configuration.EnableMustache;
import com.github.mjeanroy.springmvc.view.mustache.configuration.MustacheProvider;

@Configuration
@EnableMustache(provider = MustacheProvider.AUTO)
public class DctmRestMustachViewConfig {
}
