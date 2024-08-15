package kcworks.docs.docfomat.controllers;

import com.google.common.base.Throwables;
import kcworks.util.unit.DataSizeEx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception e){
		log.error(e.getMessage());
		log.error(Throwables.getStackTraceAsString(e));
		if(e instanceof MaxUploadSizeExceededException){
			MaxUploadSizeExceededException sizeException = (MaxUploadSizeExceededException) e;
			log.debug("maxUploadSize: {}", sizeException.getMaxUploadSize());
			int uploadSize = getUploadSize(e.getMessage());
			int maxSize = getMaxSize(e.getMessage());
			return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
					.body(String.format("the file is too large to parse( %s > %s )!!"
							, DataSizeEx.ofBytes(uploadSize)
							, DataSizeEx.ofBytes(maxSize)));
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
	}

	private int getUploadSize(String message){
		Pattern pattern = Pattern.compile("\\(\\d+\\)");
		Matcher matcher = pattern.matcher(message);
		if (matcher.find()) {
			String numberString = matcher.group().replaceAll("\\D+","");
			int number = Integer.parseInt(numberString);
			return number;
		}
		return -1;
	}

	private int getMaxSize(String message){
		Pattern pattern = Pattern.compile("\\(\\d+\\)");
		Matcher matcher = pattern.matcher(message);
		int count = 0;
		int result = -1;
		while (matcher.find() && count < 2) {
			String numberString = matcher.group().replaceAll("\\D+","");
			int number = Integer.parseInt(numberString);
			result = number;
			count++;
		}
		return result;
	}
}
