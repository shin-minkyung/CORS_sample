package com.cors.web;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class WebRestController {
	   
	  @GetMapping("/corsRest")
	  public ResponseEntity<?> cors() {
		  String url = "http://localhost:8761/actuator/httptrace";
		  RestTemplate restTemplate = new RestTemplate();
		  ResponseEntity<?> response = restTemplate.getForEntity(url, Map.class);
		  		  
		  return response;
	  }
	  
	  
	 
	 

}
