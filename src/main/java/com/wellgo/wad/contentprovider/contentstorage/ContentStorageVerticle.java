package com.wellgo.wad.contentprovider.contentstorage;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import org.json.JSONArray;
import org.json.JSONObject;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import com.wellgo.wad.contentprovider.Configuration;

import io.vertx.core.Future;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.MessageConsumer;

public class ContentStorageVerticle extends AbstractVerticle {

	private TransportClient client;
	
	   @Override
	    public void start(Future<Void> startFuture) throws Exception {
		   
		   
		   MessageConsumer<String> consumer = vertx.eventBus().consumer(Configuration.EB_ADDR_CONTENT_STORAGE);
		   
		   consumer.handler(message -> {
		     System.out.println("ContentStorageVerticle - I have received a message: " + message.body());
		     
		     
		     // Retrieve the content
		     GetResponse response = client.prepareGet("publisher", "wizard", "publisher123").get();
		     
		    
		     
		     //String widgets = (String) response.getField("widgets").getValue();
		     //System.out.println("ContentGeneratorVerticle - Retrived from the storage widgetUrl" + widgets);
		     
		     if (!response.isExists()){ // || response.getField(PATH_FIELD) == null) {
		         // doc not found
		    	 System.out.println("ContentStorageVerticle - Document not found");
		    	 message.reply("{\"error\": \"Document not found\"}");
		     }
		     else {
		    	 // return response.getField(PATH_FIELD).getValue().toString();
		    	 
		    	 System.out.println("ContentStorageVerticle - set size: " + response.getSource().size());
		    	 
		    	 if (!response.getSource().isEmpty()) {

		    		 JSONObject json = new JSONObject(response.getSourceAsString());
		    		 
		    		  JSONArray jWizards = json.getJSONArray("wizards");
		    		  
		    		  for (int i = 0, size = jWizards.length(); i < size; i++) {
		    		      JSONObject objectInArray = jWizards.getJSONObject(i);
		    		      System.out.println(objectInArray.get("id") + "<--->" + objectInArray.get("url"));
		    		    }
		    		  
		    		  message.reply(jWizards.toString());
			     }
		    	 else {
		    		 System.out.println("ContentStorageVerticle - Empty response received");
			    	 message.reply("{\"error\": \"Empty response received\"}");
		    	 }
		     }
		     
		     /*
    		 for (Map.Entry<String, Object> entry : response.getSource().entrySet())
    		 {
    		     System.out.println(entry.getKey() + "/" + entry.getValue());
    		 }
    		 */

		   }); 
	    }

	private void initEsClient() {
		
		System.out.println("ContentStorageVerticle - initEsClient >>");
		
		
		Settings settings = Settings.settingsBuilder()
		        .put("cluster.name", "esonaws").build();
		try {
			client = TransportClient.builder().settings(settings).build()
				        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300))
				        .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress("127.0.0.1", 9301)));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ContentStorageVerticle - initEsClient failed :" + e.getMessage());
		}
		
			
		System.out.println("ContentStorageVerticle - initEsClient <<");
		
	}
	
	
	  @Override
	    public void stop(Future<Void> stopFuture) throws Exception {
			// on shutdown
		  System.out.println("ContentStorageVerticle - stop >>>");
			client.close();
		  System.out.println("ContentStorageVerticle - stop <<<");
	    }
	  
	  
	  @Override
	  public void init(Vertx vertx, Context context) {
		  this.vertx = vertx;
		  initEsClient();
	  }

	
	
}
