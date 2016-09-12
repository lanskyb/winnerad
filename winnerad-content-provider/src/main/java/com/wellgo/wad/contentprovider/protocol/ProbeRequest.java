package com.wellgo.wad.contentprovider.protocol;

import java.util.Map;

public class ProbeRequest {
	
	 public static final String ACTION = "probe-req";
	    private String sender;
	    private String publisherId;
	    private String location;
	    private Map<String, String> args;
	    private Header header;

	    public ProbeRequest() {
	        this("", "", null);
	    }


	    public ProbeRequest(String publisherId, String location, Map<String, String> args) {
	        this.header = new Header(ACTION);
	        this.setPublisherId(publisherId);
	        this.setLocation(location);
	        this.setArgs(args);
	        
	    }

	    public ProbeRequest resetHeader() {
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
