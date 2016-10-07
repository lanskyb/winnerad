package com.wellgo.wad.contentprovider;

import com.wellgo.wad.contentprovider.protocol.GetContentRequest;
import com.wellgo.wad.contentprovider.protocol.Message;
import com.wellgo.wad.contentprovider.protocol.ProbeRequest;
import com.wellgo.wad.contentprovider.protocol.Serializer;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;



/**
 * Created by Robin on 2015-12-17.
 * <p>
 * Handles incoming messages from clients.
 */
enum MessageHandler {
	
	
	
	
    MESSAGE() {
        @Override
        public void invoke(Parameters params) {
            Message message = (Message) Serializer.unpack(params.data, Message.class);
            params.handler.getVertx().eventBus().send(Configuration.EB_ADDR_CONTENT_GENERATOR, Serializer.pack(message), ar -> {
            	  if (ar.succeeded()) {
            		    logger.debug("Sending generated content: " + ar.result().body());
            		    params.socket.writeFinalTextFrame((String)ar.result().body());
            	  }
            	  else {
            		logger.debug("Failed generate content");
          		    params.socket.writeFinalTextFrame("{\"error\": \"Failed generate content\"}");
            	  }
            });
        }
    },

    GET_CONTENT_REQ() {
        @Override
        public void invoke(Parameters params) {
            GetContentRequest getContentReq = (GetContentRequest) Serializer.unpack(params.data, GetContentRequest.class);

            params.handler.getVertx().eventBus().send(Configuration.EB_ADDR_CONTENT_GENERATOR, Serializer.pack(getContentReq), ar -> {
          	  if (ar.succeeded()) {
          		    logger.debug("Sending generated content: " + ar.result().body());
          		    params.socket.writeFinalTextFrame((String)ar.result().body());
          	  }
          	  else {
          		logger.debug("Failed generate content");
        		    params.socket.writeFinalTextFrame("{\"error\": \"Failed generate content\"}");
          	  }
          });
        }
    },
    PROBE_REQ() {
        @Override
        public void invoke(Parameters params) {
            ProbeRequest probeReq = (ProbeRequest) Serializer.unpack(params.data, GetContentRequest.class);

            params.handler.getVertx().eventBus().send(Configuration.EB_ADDR_CONTENT_GENERATOR, Serializer.pack(probeReq), ar -> {
          	  if (ar.succeeded()) {
          		    logger.debug("Sending generated content: " + ar.result().body());
          		    params.socket.writeFinalTextFrame((String)ar.result().body());
          	  }
          	  else {
          		logger.debug("Failed generate content");
        		    params.socket.writeFinalTextFrame("{\"error\": \"Failed generate content\"}");
          	  }
          });
        }
    };
/*
    TOPIC() {
        @Override
        public void invoke(Parameters params) {
            Topic topic = (Topic) Serializer.unpack(params.data, Topic.class);
            params.handler.trySetTopic(topic.setRoom(params.client.getRoom()), params.client);
        }
    },

    HELP() {
        @Override
        public void invoke(Parameters params) {
            CommandList commands = new CommandList();
            commands.add(new Command("/authenticate <username> <passwd>", ""));
            commands.add(new Command("/join <room>", ""));
            commands.add(new Command("/topic <topic>", ""));
            commands.add(new Command("/help", ""));
            commands.add(new Command("/servers", ""));

            params.handler.sendBus(params.client.getId(), commands);
        }
    },

    SERVERS() {
        @Override
        public void invoke(Parameters params) {
            params.handler.sendBus(Configuration.UPSTREAM, new ServerList(params.client.getId()));
        }
        
    };
    
    */

    public abstract void invoke(Parameters params);
    
    private final static Logger logger = LoggerFactory.getLogger(MessageHandler.class);
}
