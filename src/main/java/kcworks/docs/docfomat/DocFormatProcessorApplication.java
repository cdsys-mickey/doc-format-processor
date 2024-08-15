package kcworks.docs.docfomat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kcworks.docs.docfomat.config.DocFormatConverterConfig;
import kcworks.docs.docfomat.provider.FileProvider;
import kcworks.docs.docfomat.provider.IFileProvider;
import kcworks.gson.LocalDateGsonDeserializer;
import kcworks.gson.LocalDateGsonSerializer;
import kcworks.gson.LocalDateTimeGsonDeserializer;
import kcworks.gson.LocalDateTimeGsonSerializer;
import kcworks.spring.GrantedAuthoritySerializer;
import kcworks.spring.UserDetailsSerializer;
import kcworks.spring.context.DefaultMessageSources;
import kcworks.spring.context.MessageSources;
import kcworks.spring.jwt.GsonJwtTokenProvider;
import kcworks.spring.jwt.IJwtTokenProvider;
import kcworks.spring.jwt.JwtConfigProps;
import kcworks.spring.rest.ApiResponse;
import kcworks.spring.rest.RestConfigProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@EnableConfigurationProperties({JwtConfigProps.class, RestConfigProps.class})
@RequiredArgsConstructor
@Configuration
@SpringBootApplication(exclude = {JacksonAutoConfiguration.class})
public class DocFormatProcessorApplication {

	private final JwtConfigProps jwtConfig;

	private final RestConfigProps restConfig;

	private final DocFormatConverterConfig converterConfig;

	public static void main(String[] args) {
		SpringApplication.run(DocFormatProcessorApplication.class, args);
	}

	@Bean
	public IJwtTokenProvider newJwtTokenProvider(){
		return new GsonJwtTokenProvider(jwtConfig);
	}

	@Bean
	public BCryptPasswordEncoder newBCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	public Gson newGson(){
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(ApiResponse.class, new ApiResponse.GsonSerializer(restConfig))
				.registerTypeAdapter(LocalDate.class, new LocalDateGsonSerializer())
				.registerTypeAdapter(LocalDate.class, new LocalDateGsonDeserializer())
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeGsonSerializer())
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeGsonDeserializer())
				.create();
		return gson;
	}

	@Bean
	public UserDetailsSerializer newUserDetailsSerializer(){
		return new UserDetailsSerializer();
	}

	@Bean
	public MessageSources messageSources(){
		return new DefaultMessageSources();
	}

	// @Bean
	// public FreemarkerTemplateBuilder newFreemarkerTemplateBuilder(){
	// 	return Freemarkers.builderFromAppFolder("templates");
	// }

	@Bean
	public GrantedAuthoritySerializer newGrantedAuthoritySerializer(){
		return new GrantedAuthoritySerializer();
	}

	@Bean
	public IFileProvider newFileProvider(){
		return new FileProvider(converterConfig);
	}

	// @Bean
	// public MultipartConfigElement multipartConfigElement() {
	// 	MultipartConfigFactory factory = new MultipartConfigFactory();
	// 	factory.setMaxFileSize(DataSize.ofBytes(512000000L));
	// 	factory.setMaxRequestSize(DataSize.ofBytes(512000000L));
	// 	return factory.createMultipartConfig();
	// }
}
