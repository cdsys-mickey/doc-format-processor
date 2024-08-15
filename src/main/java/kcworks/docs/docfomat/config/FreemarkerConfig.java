package kcworks.docs.docfomat.config;

import kcworks.freemarker.FreemarkerTemplateBuilder;
import kcworks.freemarker.Freemarkers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

@Configuration
public class FreemarkerConfig implements ServletContextAware {
	private ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Bean
	public FreemarkerTemplateBuilder newFreemarkerTemplateBuilder(){
		return Freemarkers.builderFromAppFolder(
				servletContext.getRealPath("WEB-INF")
				, "ftl-templates");
	}
}
