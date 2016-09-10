package com.wellgo.wad.contentprovider.protocol;

public class Wizard {
	
	private String id;
	
	private String url;
	
	public Wizard() {
		
	}
	
	public Wizard(String id, String url) {
		this.id = id;
		this.url = url;
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
	

}
