package kcworks.docs.docfomat.config;

import lombok.Getter;
import lombok.Setter;

// @Configuration
@Setter
@Getter
// @ConfigurationProperties(
// 		prefix = "dfc"
// )
public class BaseConverterConfig {
	private boolean convertAnyway;
	private int maxMb;

}
