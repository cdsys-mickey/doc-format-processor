package kcworks.docs.docfomat.controllers;

import kcworks.spring.rest.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/odf/lab")
@RestController
public class LabController {
	@GetMapping("noop")
	public ResponseEntity<ApiResponse> noop(){
		ApiResponse response = ApiResponse.initAsFailed(HttpStatus.INTERNAL_SERVER_ERROR);

		return ResponseEntity.status(response.getStatus()).body(response);
	}
}
