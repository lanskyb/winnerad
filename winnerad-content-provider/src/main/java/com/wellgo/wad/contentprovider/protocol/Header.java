package com.wellgo.wad.contentprovider.protocol;

/**
 * Created by Robin on 2015-12-16.
 *
 * All transfer messages must contain a header.
 */

public class Header {
    private String action;


    public Header() {}

    public Header(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
