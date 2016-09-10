package com.wellgo.wad.contentprovider.protocol;

/**
 * Created by Robin on 2015-12-16.
 * <p>
 * Message transfer object.
 */
public class Message {
    public static final String ACTION = "message";
    private String sender;
    private String content;
    private Header header;

    public Message() {
        this("");
    }


    public Message(String content) {
        this.content = content;
        this.header = new Header(ACTION);
    }

    public Message resetHeader() {
        this.header = new Header(ACTION);
        return this;
    }

    public String getContent() {
        return content;
    }

    public Message setContent(String content) {
        this.content = content;
        return this;
    }

    public String getSender() {
        return sender;
    }

    public Message setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

}