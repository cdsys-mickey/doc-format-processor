package kcworks.docs.docfomat.lifecycle;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import kcworks.docs.docfomat.config.JODConverterConfig;
import lombok.extern.slf4j.Slf4j;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Slf4j
@Configuration
public class JODConverterOfficeManagerFactory {

	@Autowired
	protected JODConverterConfig converterConfig;

	private OfficeManager officeManager;

	@PostConstruct
	public void onAppStarted(){
		if(converterConfig.isEnabled()){
			log.info("starting OfficeManager...");
			try {
				getOfficeManager().start();
			} catch (OfficeException e) {
				log.error("failed to start OfficeManager: {}", e.getMessage());
				log.error(Throwables.getStackTraceAsString(e));
			}
		}else{
			log.debug("jod.enabled is false, office manager is NOT installed");
		}
	}

	@PreDestroy
	public void onAppStopped(){
		if(getOfficeManager() != null){
			log.debug("shutting down OfficeManager...");
			try {
				getOfficeManager().stop();
			} catch (OfficeException e) {
				log.error("failed to stop OfficeManager: {}", e.getMessage());
				log.error(Throwables.getStackTraceAsString(e));
			}
		}
	}

	private OfficeManager installOfficeManager(){
		try {
			// officeManager = LocalOfficeManager.install();
			LocalOfficeManager.Builder officeManagerBuilder = LocalOfficeManager.builder().install();

			//officeHome
			// if(!config.get("jodconverter", "office.home").isNullOrEmpty()){
			if(!Strings.isNullOrEmpty(converterConfig.getOfficeHome())){
				// Path officeHomePath = config.get("jodconverter", "office.home").asPath();
				Path officeHomePath = Paths.get(converterConfig.getOfficeHome());
				if(Files.isDirectory(officeHomePath)){
					officeManagerBuilder.officeHome(officeHomePath.toString());
				}else{
					log.error("jodconverter/office.home [{}] does not exists!", officeHomePath.toString());
				}
			}

			//portNumbers
			// if(!config.get("jodconverter", "ports").isNullOrEmpty()){
			if(!Strings.isNullOrEmpty(converterConfig.getPorts())){
				// String portsValue = config.get("jodconverter", "ports").asString();
				officeManagerBuilder.portNumbers(
						Arrays.stream(converterConfig.getPorts().split("\\s*,\\s*"))
								.mapToInt(Integer::parseInt)
								.toArray()
				);
			}
			officeManager = officeManagerBuilder.build();
		} catch (Exception e) {
			log.error("officeManager failed to install: {}", e.getMessage());
			log.error(Throwables.getStackTraceAsString(e));
		}
		return officeManager;
	}

	private OfficeManager getOfficeManager(){
		if(this.officeManager == null && converterConfig.isEnabled()){
			this.officeManager = installOfficeManager();
		}
		return this.officeManager;
	}
}
