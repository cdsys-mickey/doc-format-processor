package kcworks.docs.docfomat.lifecycle;

import kcworks.docs.docfomat.provider.IFileProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
public class TaskSchedulingConfig {

	@Value("${dfc.housekeeping-cron}")
	private String houseKeeping;

	@Autowired
	protected IFileProvider fileProvider;

	@Scheduled(cron = "${dfc.housekeeping-cron}")
	public void runHouseKeepingTask(){
		log.debug("house keeping starts..., cron: \"{}\"", houseKeeping);
		// fileProvider.housekeeping();
		log.debug("house keeping finished.");
	}
}
