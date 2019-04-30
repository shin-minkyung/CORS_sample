
package com.config.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
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
	   editConfigVO.setRepo((String) body.get("repo"));
	   editConfigVO.setRootPath((String) body.get("rootPath"));
	   editConfigVO.setToken((String) body.get("token"));
	   editConfigVO.setCommitMsg((String) body.get("commitMsg"));
	   editConfigVO.setUsername((String) body.get("username")); 
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
	
	public String getTargetContent(String url, String file_url, ResponseEntity<ArrayList> response, EditConfigVO editConfigVO) {
		 String keys = editConfigVO.getKey();
		 System.err.println("keys "+keys);
		 String[] keyList = keys.split("\\.");
		 System.err.println("keyList "+keyList);
		 RestTemplate restTemplate = new RestTemplate();		
		 int fileConunt = response.getBody().size();
		  ArrayList<String> contentList = new ArrayList<>();
		  ArrayList<String> nameList = new ArrayList<>();
		  for(int i=0; i< fileConunt; i++){
			  LinkedHashMap<String, Object> json = (LinkedHashMap<String, Object>) response.getBody().get(i);
			  String name = (String) json.get("name");	
			  String fileUrl = url+"/"+name;
			  ResponseEntity<LinkedHashMap> fileJson = restTemplate.getForEntity(fileUrl, LinkedHashMap.class);		
			  byte[] decodedContent = Base64.decodeBase64((String) fileJson.getBody().get("content"));			  
			  String ymlString = new String(decodedContent);			  
			  if(ymlString.contains(keyList[0])) {
				 // firstKeyFileCount++;
				  contentList.add(ymlString);
				  nameList.add(name);
			  }
		  }		  
		  if(contentList.size()==1) {
			  file_url = url+"/"+nameList.get(0);
			  return contentList.get(0);
		  }else {
			return "error";			  
		  }		
	}
	
	public LinkedHashMap<String, Object> ymlStringToMap(String ymlString){
		Yaml yml = new Yaml();
		LinkedHashMap<String, Object> map = yml.load(ymlString);
		return map;
	}
	
	
	public void mergeNewValue(EditConfigVO editConfigVO, LinkedHashMap<String, Object> map) {
		//Convert keys into ArrayList
		  String keys= editConfigVO.getKey();
		  String[] keyList = keys.split("\\.");
		  LinkedHashMap<String, Object> insertMap = new LinkedHashMap<>();
		  Object newValue = editConfigVO.getNewValue();
		  System.err.println("newValue ::: "+newValue);
		  System.err.println("insertMap.isEmpty() :: "+insertMap.isEmpty());
		  LinkedHashMap<String, Object> newMap = map;
		  System.err.println("keyList "+keyList);
		  System.err.println("keyList length ::"+keyList.length);

		  for(int i=0; i<keyList.length; i++) {		
			  System.err.println("i - a :: "+i);
			  if(!(newMap.get(keyList[i]) instanceof LinkedHashMap<?, ?>)) {
				  System.err.println("i - b :: "+i);
				  if(newMap.get(keyList[i]) instanceof Object) {
					  System.err.println("i - c :: "+i);
					  System.err.println("newMap "+newMap);
					  newMap.put(keyList[i], newValue);
					  break;
				  }else if(newMap.get(keyList[i])==null) {	
					  System.err.println("i - d :: "+i);
					  for(int j=keyList.length-1; j>i; j--) {
						  System.err.println("i - e :: "+i);
						  if(insertMap.isEmpty()==true) {
							  System.err.println("i - f :: "+i);
							  insertMap.put(keyList[j], newValue);
							  System.err.println("insertMap "+insertMap);
						  }else {
							  System.err.println("i - g :: "+i);
							  LinkedHashMap<String, Object> frameMap = new LinkedHashMap<>();
							  frameMap.put(keyList[j], insertMap);
							  insertMap = frameMap;	
						  }													 						  
					  }//for
					  System.err.println("i - h :: "+i);
					  newMap.put(keyList[i], insertMap.isEmpty()? newValue: insertMap);
					  break;
				  }				  
			  }else {
				  newMap = (LinkedHashMap<String, Object>) newMap.get(keyList[i]);
				  System.err.println("newMap :: "+newMap);
				  System.err.println("i - i :: "+i);
			  }
			  System.err.println("newMap :: "+newMap);
			  System.err.println("i - j :: "+i);
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
