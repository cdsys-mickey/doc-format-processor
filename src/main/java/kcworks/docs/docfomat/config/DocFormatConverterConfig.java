package kcworks.docs.docfomat.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Setter
@Getter
@ConfigurationProperties(
		prefix = "dfc"
)
public class DocFormatConverterConfig {
	private String housekeepingCron;
	private int housekeepingHours;
	private String tempFolder;
}
