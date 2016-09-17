package com.wellgo.wad.contentprovider.healthcheck;



import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;

public class ContentProviderHealthcheckVerticle extends AbstractVerticle {

  

  @Override
  public void start(Future<Void> fut) {
    
	    Router router = Router.router(vertx);
	
	    router.route("/health-check").handler(routingContext -> {
	       routingContext.response().putHeader("content-type", "text/html").end("wad-cp-active");
	    });
	
	    vertx.createHttpServer().requestHandler(router::accept).listen(80);
    
    }
}
  
  
