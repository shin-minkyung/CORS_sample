package com.config.vo;

import java.util.Map;

public class JsonMessageVO {

    Map<String, String> head;
    Map<String, Object> body;
    private Object newValue;
    
	public Map<String, String> getHead() {
		return head;
	}
	public void setHead(Map<String, String> head) {
		this.head = head;
	}
	public Map<String, Object> getBody() {
		return body;
	}
	public void setBody(Map<String, Object> body) {
		this.body = body;
	}
	public Object getNewValue() {
		return newValue;
	}
	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}
	
	
}
