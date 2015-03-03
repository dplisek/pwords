package org.plech.pwords.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PersistenceConfig.class, PropertyConfig.class})
@ComponentScan("org.plech.pwords.filters")
public class RootConfig {
}
