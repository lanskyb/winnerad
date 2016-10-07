package com.wellgo.wad.contentprovider;



import java.util.HashMap;
import java.util.Map;

import com.wellgo.wad.contentprovider.contentgenerator.ContentGeneratorVerticle;
import com.wellgo.wad.contentprovider.healthcheck.ContentProviderHealthcheckVerticle;
import com.wellgo.wad.contentprovider.protocol.Message;
import com.wellgo.wad.contentprovider.protocol.Packet;
import com.wellgo.wad.contentprovider.protocol.Serializer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


public class ContentProviderVerticle extends AbstractVerticle {
	
  private Map<String, MessageHandler> messageHandler = new HashMap<>();
  private ContentGeneratorVerticle contentGeneratorVerticle;
  private HttpServer server;
  
  
  private final static Logger logger = LoggerFactory.getLogger(ContentProviderVerticle.class);
  
  
  /*
  @Override
  public void init(Vertx vertx, Context context) {
      
      messageHandler.put(Message.ACTION, MessageHandler.MESSAGE);
     
  }
  */
  

  @Override
  public void start(Future<Void> fut) {
	  
	  /*
    vertx.createHttpServer().requestHandler(r -> {r.response().end("<h1>Hello from my first " + "Vert.x 3 application</h1>");})
        .listen(8080, result -> {
          if (result.succeeded()) {
            fut.complete();
          } else {
            fut.fail(result.cause());
          }
        });
        */
	  
	  /*
	  HttpServer server = Vertx.vertx().createHttpServer();
      server.websocketHandler(new Handler<ServerWebSocket>() {
          @Override
          public void handle(ServerWebSocket webs) {
              logger.debug("Client connected");
              webs.writeBinaryMessage(Buffer.buffer("Hello user"));
              logger.debug("Client's message: ");
              webs.handler(data -> {logger.debug("Received data " + data.toString("ISO-8859-1"));});

          }
      });

      server.listen(8080, "localhost", res -> {
          if (res.succeeded()) {
              logger.debug("Server is now listening!");
              fut.complete();
          } else {
              logger.debug("Failed to bind!");
              fut.fail(res.cause());
          }
      });
      */
	  
	  
	  
	  
	  messageHandler.put(Message.ACTION, MessageHandler.MESSAGE);
	  
	  contentGeneratorVerticle = new ContentGeneratorVerticle();
	  
	  DeploymentOptions options = new DeploymentOptions()
	    	    .setConfig(this.getVertx().getOrCreateContext().config());
	  
	  logger.info("config: " + this.getVertx().getOrCreateContext().config().encodePrettily());
	    
	  
	  vertx.deployVerticle(contentGeneratorVerticle, options);
	  
	  server = vertx.createHttpServer().
      		websocketHandler(event -> {
      									event.handler(data -> {
					                        					Packet packet = (Packet) (Serializer.unpack(data.toString(), Packet.class));
					                        					logger.debug("Received data: " + data.toString());
					                        					logger.debug("packet action " + packet.getAction());
					                        					messageHandler.get(packet.getAction()).invoke(new Parameters(data.toString(), event, this));
      														  }); // data
      									
										event.closeHandler(close -> {
											logger.info("event.closeHandler!");
										                     }); // close
                  
								
										//vertx.eventBus().send(Configuration.EB_ADDR_CONTENT_GENERATOR, Serializer.pack(data));
              							} // event
      						).listen(Configuration.LISTEN_PORT, res -> {
      				          if (res.succeeded()) {
      				              logger.info("Server is now listening!");
      				              fut.complete();
      				          } else {
      				              logger.error("Failed to bind!");
      				              fut.fail(res.cause());
      				          }
      				      });
	  
	  vertx.deployVerticle(new ContentProviderHealthcheckVerticle());
  }
  
  
  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
		// on shutdown
	  logger.info("ContentProviderVerticle - stop >>>");
	  
	  vertx.undeploy(contentGeneratorVerticle.deploymentID());
	  
  }

}