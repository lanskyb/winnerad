package com.wellgo.wad.contentprovider.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;


/**
 * Created by Robin on 2015-12-16.
 * <p>
 * General transfer object, used to access the header of unknown-type messages.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Packet {
    private Header header;

    public Packet() {
    }

    public String getAction() {
    	if (header == null)
    		return null;
    	
    	return header.getAction();
    }

    public Header getHeader() {
    	if (header == null)
    		return null;
    	
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }
}
