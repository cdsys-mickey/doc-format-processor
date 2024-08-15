package kcworks.docs.docfomat.lifecycle;

import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import kcworks.docs.docfomat.config.Documents4jConverterConfig;
import kcworks.lang.Classes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class Documents4jConverterFactory {
	// private static final IAppConfig config = AppConfig.getConfig();

	private IConverter converter;

	@Autowired
	protected Documents4jConverterConfig converterConfig;

	public IConverter getConverter(){
		if(this.converter == null && converterConfig.isEnabled()){
			initConverter();
		}
		return this.converter;
	}

	private void initConverter(){
		try {
			LocalConverter.Builder builder = LocalConverter.builder();
			//workerPool
			if(converterConfig.getWorkerPool() != null){
				builder.workerPool(
						converterConfig.getWorkerPool().getCore()
						, converterConfig.getWorkerPool().getMax()
						, converterConfig.getWorkerPool().getKeepAlive()
						, TimeUnit.SECONDS);
			}

			//processTimeout
			if(converterConfig.getProcessTimeout() > 0){
				builder.processTimeout(converterConfig.getProcessTimeout(), TimeUnit.SECONDS);
			}

			//baseFolder
			// builder.baseFolder();
			if(!Strings.isNullOrEmpty(converterConfig.getTempFolder())){
				File tempFolder = Paths.get(converterConfig.getTempFolder()).toFile();
				builder.baseFolder(tempFolder);
				log.info("{} 使用 {} 作為暫存目錄", Classes.getSimpleName(getClass()), tempFolder.getAbsolutePath());
			}

			this.converter = builder.build();
		} catch (Exception e) {
			log.error("Converter fails to init: {}", e.getMessage());
			log.error(Throwables.getStackTraceAsString(e));
		}
	}

	@PostConstruct
	public void postConstruct(){
		if(converterConfig.isEnabled()){
			log.info("preparing LocalConverter...");
			initConverter();
		}else{
			log.debug("d4j.enabled is false, converter is not init");;
		}
	}

	@PreDestroy
	public void preDestroy(){
		log.info("shutting down LocalConverter...");
		if(this.getConverter() != null){
			this.getConverter().shutDown();
		}
	}
}
