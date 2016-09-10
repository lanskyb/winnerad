package com.wellgo.wad.contentprovider;

import io.vertx.core.http.ServerWebSocket;

/**
 * Created by Robin on 2015-12-16.
 * <p>
 * Passed to a Message Handler for handling messages from an user.
 */
class Parameters {
    public String data;
    public ServerWebSocket socket;
    public ContentProviderVerticle handler;

    public Parameters(String data, ServerWebSocket socket, ContentProviderVerticle handler) {
        this.data = data;
        this.socket = socket;
        this.handler = handler;
    }

    public String getAddress() {
        return socket.textHandlerID();
    }
}
