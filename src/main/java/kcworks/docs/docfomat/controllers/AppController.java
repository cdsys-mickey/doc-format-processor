package kcworks.docs.docfomat.controllers;

import com.google.gson.JsonObject;
import kcworks.docs.docfomat.DocFormatProcessorApplication;
import kcworks.util.LocalDateTimes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@RequestMapping("/apix")
@RestController
public class AppController {
	@Autowired
	private ApplicationContext applicationContext;

	@GetMapping("info")
	public ResponseEntity getInfo() throws URISyntaxException, IOException {
		ApplicationHome home = new ApplicationHome(DocFormatProcessorApplication.class);
		Path folder = home.getDir().toPath();    // returns the folder where the jar is. This is what I wanted.
		Path jarFile = home.getSource().toPath(); // returns the jar absolute path.
		BasicFileAttributes attributes = Files.readAttributes(jarFile, BasicFileAttributes.class);
		LocalDateTime fileDateTime = LocalDateTime.ofInstant(attributes.lastModifiedTime().toInstant(), ZoneId.systemDefault());


		JsonObject result = new JsonObject();
		result.addProperty("folder", folder.toString());
		result.addProperty("jar", jarFile.getFileName().toString());
		result.addProperty("timestamp", fileDateTime.format(LocalDateTimes.FORMATTER_DATETIME_SECONDS));

		return ResponseEntity.ok(result);
	}

	@GetMapping("stop")
	public ResponseEntity stop(){
		SpringApplication.exit(applicationContext, () -> 0);
		return ResponseEntity.ok().build();
	}

}
