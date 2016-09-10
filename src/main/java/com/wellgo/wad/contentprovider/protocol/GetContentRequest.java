package com.wellgo.wad.contentprovider.protocol;

import java.util.Map;

public class GetContentRequest {
	
	 public static final String ACTION = "get-content-req";
	    private String sender;
	    private String publisherId;
	    private String location;
	    private Map<String, String> args;
	    private Header header;

	    public GetContentRequest() {
	        this("", "", null);
	    }


	    public GetContentRequest(String publisherId, String location, Map<String, String> args) {
	        this.header = new Header(ACTION);
	        this.setPublisherId(publisherId);
	        this.setLocation(location);
	        this.setArgs(args);
	        
	    }

	    public GetContentRequest resetHeader() {
	        this.header = new Header(ACTION);
	        return this;
	    }


	    public Header getHeader() {
	        return header;
	    }

	    public void setHeader(Header header) {
	        this.header = header;
	    }


		public String getSender() {
			return sender;
		}


		public void setSender(String sender) {
			this.sender = sender;
		}


		public String getPublisherId() {
			return publisherId;
		}


		public void setPublisherId(String publisherId) {
			this.publisherId = publisherId;
		}


		public String getLocation() {
			return location;
		}


		public void setLocation(String location) {
			this.location = location;
		}


		public Map<String, String> getArgs() {
			return args;
		}


		public void setArgs(Map<String, String> args) {
			this.args = args;
		}
}
