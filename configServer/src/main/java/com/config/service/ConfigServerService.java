
package com.config.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.config.vo.EditConfigVO;

@Service
public class ConfigServerService {
	
	public EditConfigVO getEditConfigVO(Map<String, Object> body){
	   EditConfigVO editConfigVO = new EditConfigVO();
	   editConfigVO.setKey((String) body.get("key"));
	   editConfigVO.setNewValue(body.get("newValue"));
	   editConfigVO.setRepo((String) body.get("repo"));
	   editConfigVO.setRootPath((String) body.get("rootPath"));
	   editConfigVO.setToken((String) body.get("token"));
	   editConfigVO.setKey((String) body.get("key"));
	   editConfigVO.setKey((String) body.get("commitMsg"));
	   return editConfigVO;	
	}

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
	
	public String getTargetContent(ResponseEntity<?> response) {
		//make file name list
		//get content of the files in the list
		//search key
		//return the content in which the key is included		
		return "";
	}
	
	public LinkedHashMap<String, Object> ymlStringToMap(String ymlString){
		Yaml yml = new Yaml();
		LinkedHashMap<String, Object> map = yml.load(ymlString);
		return map;
	}
	
	
	public void mergeNewValue(String keys, Object newValue, LinkedHashMap<String, Object> map) {
		//Convert keys into ArrayList
		  String[] keyList = keys.split("\\.");
		  LinkedHashMap<String, Object> insertMap = new LinkedHashMap<>();

		  System.err.println("keyList "+keyList.toString());
		  for(int i=0; i<keyList.length; i++) {		
			  //System.err.println("keyList[i] "+keyList[i]);
				  if(!(map.get(keyList[i]) instanceof LinkedHashMap<?, ?>)) {
					  //the value of the key is String or null
					  //System.err.println("Type of map.get(keyList[i])"+map.get(keyList[i]));
					  if(map.get(keyList[i]) instanceof Object) {
						  map.put(keyList[i], newValue);
						  break;
					  }else if(map.get(keyList[i])==null) {						  
						  for(int j=keyList.length-1; j>i; j--) {
							  System.err.println("insertMap "+insertMap);
							  if(insertMap.isEmpty()==true) {
								  insertMap.put(keyList[j], newValue);
								 // System.err.println("insertMap "+insertMap);
							  }else {
								  LinkedHashMap<String, Object> frameMap = new LinkedHashMap<>();
								  frameMap.put(keyList[j], insertMap);
								  insertMap = frameMap;	
								 // System.err.println("insertMap "+insertMap);
							  }													 						  
						  }//for
						  map.put(keyList[i], insertMap);
						  break;
					  }									  
				  }else {
					  //the value of the key is LinkedHashMap Object
					  map = (LinkedHashMap<String, Object>) map.get(keyList[i]);
					  System.err.println("map "+map);
				  }		  
		  }
	}
	
	public String mapToYmlString(LinkedHashMap<String, Object> map){
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		options.setPrettyFlow(true);
		options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
		Yaml newyml = new Yaml(options);
		String output = newyml.dump(map);
		return output;
	}	
	
	public ResponseEntity<String> updateGitRepoContent(String url, EditConfigVO editConfigVO, String newYmlString){
		String key = editConfigVO.getKey();
		String commitMsg = editConfigVO.getCommitMsg()!=null? editConfigVO.getCommitMsg():"update "+key;
		HttpHeaders headers = new HttpHeaders();
		RestTemplate restTemplate = new RestTemplate();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "token "+editConfigVO.getToken());
		String sha = editConfigVO.getSha();
		String param = "{\"message\":\""+commitMsg+"\", ";
		  	   param += "\"content\":\""+newYmlString+"\", ";
		  	   param += "\"sha\": \""+sha+"\"}";
		//HttpEntity<String> request = new HttpEntity<>(param, headers);		
		//ResponseEntity<String> finalResponse = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
		ResponseEntity<String> emptyResponse = null;
		return emptyResponse;
	}
	
	
}
