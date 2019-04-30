package com.config.vo;

public class EditConfigVO {

	private String username;
	private String repo;
	private String rootPath;
	private String token;
	private String key;
	private Object newValue;
	private String sha;
	private String commitMsg;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRepo() {
		return repo;
	}
	public void setRepo(String repo) {
		this.repo = repo;
	}
	public String getRootPath() {
		return rootPath;
	}
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}	
	
	public Object getNewValue() {
		return newValue;
	}
	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}
	public String getSha() {
		return sha;
	}
	public void setSha(String sha) {
		this.sha = sha;
	}
	public String getCommitMsg() {
		return commitMsg;
	}
	public void setCommitMsg(String commitMsg) {
		this.commitMsg = commitMsg;
	}	
	
	
}
