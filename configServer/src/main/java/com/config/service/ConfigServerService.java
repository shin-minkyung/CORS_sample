package com.config.service;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.config.vo.EditConfigVO;

@Service
public class ConfigServerService {

	public ResponseEntity<?> fetchGitApiJson(Map<String, Object> body){	
		 //information needed
		  //username, reponame, rootPath(of Service), keyname(toEdit), newValue, token
		  String username = (String) body.get("username");
		  String repo = (String) body.get("repo");
		  String rootPath = (String) body.get("rootPath");
		  String token = (String) body.get("token");		
		 		  
		  //file name is not included in "rootPath"
		  String url = "https://api.github.com/repos/"+username+"/"+repo+"/contents"+rootPath;
		  		  
		  //get JSON from Git API enpoint
		  RestTemplate restTemplate = new RestTemplate();		  
   		//repository > directory	 
		  ResponseEntity<ArrayList> response = restTemplate.getForEntity(url, ArrayList.class);	
		return response;		
	}
	
	public String generateGitApiPath(Map<String, Object> body) {
		String username = (String) body.get("username");
		String repo = (String) body.get("repo");
		String rootPath = (String) body.get("rootPath");
		String token = (String) body.get("token");
		String url = "https://api.github.com/repos/"+username+"/"+repo+"/contents"+rootPath;
		return url;
	}
}
