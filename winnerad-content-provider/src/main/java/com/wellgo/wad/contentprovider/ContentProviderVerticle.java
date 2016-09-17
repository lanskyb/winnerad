package com.wellgo.wad.contentprovider;




import java.util.HashMap;
import java.util.Map;







import com.wellgo.wad.contentprovider.contentgenerator.ContentGeneratorVerticle;
import com.wellgo.wad.contentprovider.healthcheck.ContentProviderHealthcheckVerticle;
import com.wellgo.wad.contentprovider.protocol.Message;
import com.wellgo.wad.contentprovider.protocol.Packet;
import com.wellgo.wad.contentprovider.protocol.Serializer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;


public class ContentProviderVerticle extends AbstractVerticle {
	
  private Map<String, MessageHandler> messageHandler = new HashMap<>();
  private ContentGeneratorVerticle contentGeneratorVerticle;
  private HttpServer server;
  
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
              System.out.println("Client connected");
              webs.writeBinaryMessage(Buffer.buffer("Hello user"));
              System.out.println("Client's message: ");
              webs.handler(data -> {System.out.println("Received data " + data.toString("ISO-8859-1"));});

          }
      });

      server.listen(8080, "localhost", res -> {
          if (res.succeeded()) {
              System.out.println("Server is now listening!");
              fut.complete();
          } else {
              System.out.println("Failed to bind!");
              fut.fail(res.cause());
          }
      });
      */
	  
	  
	  messageHandler.put(Message.ACTION, MessageHandler.MESSAGE);
	  
	  contentGeneratorVerticle = new ContentGeneratorVerticle();
	  
	  vertx.deployVerticle(contentGeneratorVerticle);
	  
	  server = vertx.createHttpServer().
      		websocketHandler(event -> {
      									event.handler(data -> {
					                        					Packet packet = (Packet) (Serializer.unpack(data.toString(), Packet.class));
					                        					System.out.println("Received data: " + data.toString());
					                        					System.out.println("packet action " + packet.getAction());
					                        					messageHandler.get(packet.getAction()).invoke(new Parameters(data.toString(), event, this));
      														  }); // data
      									
										event.closeHandler(close -> {
										                    	System.out.println("event.closeHandler!");
										                     }); // close
                  
								
										//vertx.eventBus().send(Configuration.EB_ADDR_CONTENT_GENERATOR, Serializer.pack(data));
              							} // event
      						).listen(Configuration.LISTEN_PORT, res -> {
      				          if (res.succeeded()) {
      				              System.out.println("Server is now listening!");
      				              fut.complete();
      				          } else {
      				              System.out.println("Failed to bind!");
      				              fut.fail(res.cause());
      				          }
      				      });
	  
	  vertx.deployVerticle(new ContentProviderHealthcheckVerticle());
  }
  
  
  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
		// on shutdown
	  System.out.println("ContentProviderVerticle - stop >>>");
	  
	  vertx.undeploy(contentGeneratorVerticle.deploymentID());
	  
	
	  System.out.println("ContentProviderVerticle - stop <<<");
  }

}