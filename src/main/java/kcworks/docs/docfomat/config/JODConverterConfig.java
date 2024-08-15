package kcworks.docs.docfomat.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Setter
@Getter
@ConfigurationProperties(
		prefix = "jodc"
)
public class JODConverterConfig extends BaseConverterConfig {
	private boolean enabled;
	private String officeHome;
	private String ports;


}
