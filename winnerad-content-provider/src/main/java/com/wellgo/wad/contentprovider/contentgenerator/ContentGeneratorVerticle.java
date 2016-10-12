package com.wellgo.wad.contentprovider.contentgenerator;


import org.json.JSONArray;
import org.json.JSONObject;

import com.wellgo.wad.contentprovider.Configuration;
import com.wellgo.wad.contentprovider.contentstorage.ContentStorageVerticle;



import io.vertx.core.Future;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class ContentGeneratorVerticle extends AbstractVerticle {
	
	
	private ContentStorageVerticle storageVerticle;
	
	
	private final static Logger logger = LoggerFactory.getLogger(ContentGeneratorVerticle.class);

	
	
	   @Override
	    public void start(Future<Void> startFuture) throws Exception {
		   
		   storageVerticle = new ContentStorageVerticle();
		   
		   DeploymentOptions options = new DeploymentOptions()
		    	    .setConfig(this.getVertx().getOrCreateContext().config());
		   
		   logger.info("config: " + this.getVertx().getOrCreateContext().config().encodePrettily());
		  
		   vertx.deployVerticle(storageVerticle, options);
		   
		   
		   MessageConsumer<String> consumer = vertx.eventBus().consumer(Configuration.EB_ADDR_CONTENT_GENERATOR);
		   
		   consumer.handler(message -> {
		     logger.debug("ContentGeneratorVerticle - I have received a message: " + message.body());
		     
		     
		     vertx.eventBus().send(Configuration.EB_ADDR_CONTENT_STORAGE, message.body(), 
		    		 ar -> {
		    			 		if (ar.succeeded()) {
           		
					           		  logger.debug("ContentGeneratorVerticle - received from storage data: " + ar.result().body());
					           		  
					           		  
					           		  String resultJsTemplate = "<a href=\"javascript:\" onclick=\"window.open('%s');\" target=\"_blank\">Sample Code id=%s</a>";
					           		  String resultJs="";
					           		  
					           		  JSONArray jWizards = new JSONArray(ar.result().body().toString());
						    		  
						    		  for (int i = 0, size = jWizards.length(); i < size; i++) {
						    		      JSONObject objectInArray = jWizards.getJSONObject(i);
						    		      logger.debug("ContentGeneratorVerticle - " + objectInArray.get("id") + "<--->" + objectInArray.get("url"));
						    		      resultJs += String.format(resultJsTemplate, objectInArray.get("url"), objectInArray.get("id"));
						    		    }
					           		        
					           		    
					           		// Generating content
						    		message.reply( ar.result().body());
					       		     
					      		    // message.reply(message.body());
					           	  }
					           	  else {
					           		logger.error("Failed to get data from storage");
					           		message.reply("{\"error\": \"Failed to get data from storage\"}");
					           	  }
		    		 });
		     
		     
		      // Generating content
   		      // message.reply(message.body());
		     
		   });
	        
	    }
	   

		  @Override
		    public void stop(Future<Void> stopFuture) throws Exception {
				// on shutdown
			  logger.info("ContentGeneratorVerticle - stop >>>");
			  
			  vertx.undeploy(storageVerticle.deploymentID());
			
			  logger.info("ContentGeneratorVerticle - stop <<<");
		    }
	
	
}
