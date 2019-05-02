package com.config;



import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.config.service.ConfigServerService;
import com.config.vo.EditConfigVO;
import com.config.vo.JsonMessageVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

@RestController
public class WebController {
	
	@Autowired
	private ConfigServerService configServerService;

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
		 		  	  
		  Map<String, Object> body = configReq.getBody();	
		  EditConfigVO editConfigVO = configServerService.getEditConfigVO(body);
		  editConfigVO.setNewValue(configReq.getNewValue());
		  System.err.println("editConfigVO ::::"+editConfigVO.toString());
		  String url = configServerService.generateGitApiPath(body);
		  String file_url = "";
		  ResponseEntity<ArrayList> response = (ResponseEntity<ArrayList>) configServerService.fetchGitApiJson(body);
		  		  
		  
		 String ymlString = configServerService.getTargetContent(url, file_url, response, editConfigVO);
		  if(ymlString.equals("error")) {			
			  return ymlString;
		  }else {
			  //converting decoded String into LinkedHashMap		 		 
			  LinkedHashMap<String, Object> obj = configServerService.ymlStringToMap(ymlString);
			  
			  //LinkedHashMap obj is generated from yaml
			  System.err.println(" Obj from Yaml "+obj);
		
		       configServerService.mergeNewValue(editConfigVO, obj);			 
		  
			  //conversion result
			  System.err.println("result obj"+obj);
			 
			  
			  //map -> json -> yml
			//  Yaml newYaml = new Yaml();
			//  ObjectMapper mapper = new ObjectMapper();		 
			//   mapper.writeValue(w, value);
			//String objJSON = mapper.writeValueAsString(obj).replaceAll("\"", "");
			 // System.err.println("objByte:::::"+new String(objString));		  
		//	  JsonNode jsonNodeTree = new ObjectMapper().;
			  //System.err.println("JsonNode.textValue()::::"+j);
			  //System.err.println("JsonNode :::::"+jsonNodeTree);
//			  Iterator<Entry<String, JsonNode>> nodes = jsonNodeTree.fields();
//			  while(nodes.hasNext()) {
//				  //Map.Entry<String, JsonNode> entry = jsonNodeTree.fields().next();
//				  System.err.println(nodes.next().getKey()+" : "+nodes.next().getValue());
//			  }
			  		  
			  //String newYmlString = new YAMLMapper().writeValueAsString(jsonNodeTree);
			  
			  String output= configServerService.mapToYmlString(obj);		  
			  System.err.println("NEW YAML :::"+output);
			  
			  //String newYmlString = new YAMLMapper().
			  //String newYmlString =  newYaml.dumpAll((java.util.Iterator<? extends Object>) obj);
			//  String encodedString = Base64.encodeBase64String(newYmlString.getBytes());
			//  System.err.println("newYmlString "+newYmlString);
			 // System.err.println("encodedString "+encodedString);
			  System.err.println("file URL ::::"+file_url);
			  configServerService.updateGitRepoContent(file_url, editConfigVO, output);		  
			  return output;
		  }		  
		
	  }
	  
	  

}
