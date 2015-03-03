package org.plech.pwords.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan({"org.plech.pwords.api"})
@Import({PersistenceConfig.class, PropertyConfig.class})
public class MvcConfig {
}
