package kcworks.docs.docfomat.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Setter
@Getter
@ConfigurationProperties(
		prefix = "d4j"
)
public class Documents4jConverterConfig extends BaseConverterConfig {
	private boolean enabled;
	private String tempFolder;
	private int processTimeout;
	private WorkerPool workerPool;

	@Getter
	@Setter
	public static class WorkerPool{
		private int core;
		private int max;
		private int keepAlive;
	}
}
