package com.config;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.config.vo.JsonMessageVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

@RestController
public class WebController {

	  @PostMapping("/refreshAll")
	  public String refreshAll(HttpServletRequest servletRequest){		  
		StringBuffer url = servletRequest.getRequestURL();
		String uri = servletRequest.getRequestURI();
		String target = url.toString().replaceAll(uri, "/monitor");
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		
		LinkedMultiValueMap<String, String> param =  new LinkedMultiValueMap<String, String>();	  
		param.add("path", "*");
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, headers);		
		ResponseEntity<String> response = restTemplate.postForEntity(target, request, String.class);	
		

		return "targetUrl "+target;
	  }
	  
	  
	  @PostMapping("/editConfig")	  
	  public String getConfig(@RequestBody JsonMessageVO configReq) throws IOException{
		  //information needed
		  //username, reponame, rootPath(of Service), keyname(toEdit), newValue, token
		  Map<String, Object> body = configReq.getBody();
		  String username = (String) body.get("username");
		  String repo = (String) body.get("repo");
		  String rootPath = (String) body.get("rootPath");
		  String token = (String) body.get("token");
		  String keys = (String) body.get("key");
		  Object newValue= configReq.getNewValue();
		  System.err.println("newValue ::::"+newValue);
		  System.err.println("newValue type:::: "+newValue.getClass().getSimpleName());
		 		  
		  //file name is not included in "rootPath"
		  String url = "https://api.github.com/repos/"+username+"/"+repo+"/contents"+rootPath;
		  		  
		  //get JSON from Git API enpoint
		  RestTemplate restTemplate = new RestTemplate();		  
   		//repository > directory	 
		  ResponseEntity<ArrayList> response = restTemplate.getForEntity(url, ArrayList.class);

		  
		  
		  //returns list of JSON with files' information in the directory
		  //the 1st JSON. 
		  LinkedHashMap<String, Object> json = (LinkedHashMap<String, Object>) response.getBody().get(0);
		  //"name":"serviceA.yml"
		  Object name = json.get("name");
		  System.err.println("name "+name);
		  
		  String file_url = url+"/"+name;
		  System.err.println("file url "+file_url);
		  
		  //returns JSON that contains information of the file(name, path, sha, content..)
		  ResponseEntity<LinkedHashMap> fileJson = restTemplate.getForEntity(file_url, LinkedHashMap.class);
		  System.err.println("file json "+fileJson);
		  
		    
		  
		  
		  
		  //decoding the content
		  byte[] decodedContent = Base64.decodeBase64((String) fileJson.getBody().get("content"));
		  //converting decoded content into String
		  String ymlString = new String(decodedContent);
		  System.err.println("content : "+new String(decodedContent));
		  //converting decoded String into LinkedHashMap
		  Yaml yaml = new Yaml();
		  LinkedHashMap<String, Object> obj = yaml.load(ymlString);
		  
		  //LinkedHashMap obj is generated from yaml
		  System.err.println(" Obj from Yaml "+obj);
		  
		  
		  
		  
		  
		  /*parameter example case
		   * Changing the value of the key "~.defaultZone" with newValue
		   */
	
		 //Convert keys into ArrayList
		  String[] keyList = keys.split("\\.");
		  LinkedHashMap<String, Object> map = obj;
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
		  
//		  //depth 1 Structure
//		  obj.put("a", "new aaaaa");
//		  //System.err.println("a type :"+obj.get("a").getClass().getSimpleName());  //String
//		  //System.err.println("eureka type :"+obj.get("eureka").getClass().getSimpleName()); //LinkedHashMap
//		  //
//		  if(obj.get("eureka") instanceof LinkedHashMap<?, ?>) {
//			  System.err.println("yes");
//		  }
//		  //depth 2
//		  LinkedHashMap<String, String> obj2 = (LinkedHashMap<String, String>) obj.get("my");
//		  obj2.put("greeting", "new greeting");
//
//		  //depth 3 Structure
//		  LinkedHashMap<String, LinkedHashMap<String, Object>> obj3= (LinkedHashMap<String, LinkedHashMap<String, Object>>) obj.get("eureka");		  
//		  obj3.get("client").put("fetchRegistry", false);
//		  
		  //depth4
		  //LinkedHashMap<String, Object> obj4 = obj3.get("client");
		  //obj4.get("serviceUrl").
		  //put("defaultZone", "http://localhost:8787/eureka/");
		  
		  
		  
		  //conversion result
		  System.err.println("result obj"+obj);
		//  System.err.println("result map"+map);
		  
		  //map -> json -> yml
		//  Yaml newYaml = new Yaml();
		//  ObjectMapper mapper = new ObjectMapper();		 
		//   mapper.writeValue(w, value);
		//String objJSON = mapper.writeValueAsString(obj).replaceAll("\"", "");
		 // System.err.println("objByte:::::"+new String(objString));		  
	//	  JsonNode jsonNodeTree = new ObjectMapper().;
		  //System.err.println("JsonNode.textValue()::::"+j);
		  //System.err.println("JsonNode :::::"+jsonNodeTree);
//		  Iterator<Entry<String, JsonNode>> nodes = jsonNodeTree.fields();
//		  while(nodes.hasNext()) {
//			  //Map.Entry<String, JsonNode> entry = jsonNodeTree.fields().next();
//			  System.err.println(nodes.next().getKey()+" : "+nodes.next().getValue());
//		  }
		  		  
		  //String newYmlString = new YAMLMapper().writeValueAsString(jsonNodeTree);
		  
		  DumperOptions options = new DumperOptions();
		  options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		  options.setPrettyFlow(true);
		  options.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
		  Yaml newyml = new Yaml(options);
		  String output = newyml.dump(obj);
		  
		  System.err.println("NEW YAML :::"+output);
		  
		  //String newYmlString = new YAMLMapper().
		  //String newYmlString =  newYaml.dumpAll((java.util.Iterator<? extends Object>) obj);
		//  String encodedString = Base64.encodeBase64String(newYmlString.getBytes());
		//  System.err.println("newYmlString "+newYmlString);
		 // System.err.println("encodedString "+encodedString);
  
	  HttpHeaders headers = new HttpHeaders();
	  headers.setContentType(MediaType.APPLICATION_JSON);
	  headers.set("Authorization", "token "+token);
	  
	  
//	  JSONObject putJson = new JSONObject();
//	  putJson.put("message","ffff" );
//	  putJson.put("content", encodedString );		  
	  String sha = (String) fileJson.getBody().get("sha");
//	  putJson.put("sha", sha );
	  
//	  String param = "{\"message\":\"this is a message\",";
//	  param += "\"content\":\""+encodedString+"\", ";
//	  param += "\"sha\": \""+sha+"\"}";
//	  
//	  System.err.println("param "+param);
	
		 // HttpEntity<String> request = new HttpEntity<>(param, headers);		 
		 // ResponseEntity<String> final_response= restTemplate.exchange(file_url, HttpMethod.PUT, request, String.class);		  
		  return output;
	  }
	  
	  

}
