package com.wellgo.wad.contentprovider.contentgenerator;


import org.json.JSONArray;
import org.json.JSONObject;

import com.wellgo.wad.contentprovider.Configuration;
import com.wellgo.wad.contentprovider.contentstorage.ContentStorageVerticle;



import io.vertx.core.Future;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.MessageConsumer;

public class ContentGeneratorVerticle extends AbstractVerticle {
	
	
	private ContentStorageVerticle storageVerticle;

	
	
	   @Override
	    public void start(Future<Void> startFuture) throws Exception {
		   
		   storageVerticle = new ContentStorageVerticle();
		  
		   vertx.deployVerticle(storageVerticle);
		   
		   
		   MessageConsumer<String> consumer = vertx.eventBus().consumer(Configuration.EB_ADDR_CONTENT_GENERATOR);
		   
		   consumer.handler(message -> {
		     System.out.println("ContentGeneratorVerticle - I have received a message: " + message.body());
		     
		     
		     vertx.eventBus().send(Configuration.EB_ADDR_CONTENT_STORAGE, message.body(), ar -> {
           	  if (ar.succeeded()) {
           		
           		  System.out.println("ContentGeneratorVerticle - received from storage data: " + ar.result().body());
           		  
           		  
           		  String resultJsTemplate = "<a href=\"javascript:\" onclick=\"window.open('%s');\" target=\"_blank\">Sample Code id=%s</a>";
           		  String resultJs="";
           		  
           		  JSONArray jWizards = new JSONArray(ar.result().body().toString());
	    		  
	    		  for (int i = 0, size = jWizards.length(); i < size; i++) {
	    		      JSONObject objectInArray = jWizards.getJSONObject(i);
	    		      System.out.println("ContentGeneratorVerticle - " + objectInArray.get("id") + "<--->" + objectInArray.get("url"));
	    		      resultJs += String.format(resultJsTemplate, objectInArray.get("url"), objectInArray.get("id"));
	    		    }
           		        
           		    
           		// Generating content
	    		  message.reply( ar.result().body());
       		     
      		    // message.reply(message.body());
           	  }
           	  else {
           		System.out.println("Failed to get data from storage");
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
			  System.out.println("ContentGeneratorVerticle - stop >>>");
			  
			  vertx.undeploy(storageVerticle.deploymentID());
			
			  System.out.println("ContentGeneratorVerticle - stop <<<");
		    }
	
	
}
