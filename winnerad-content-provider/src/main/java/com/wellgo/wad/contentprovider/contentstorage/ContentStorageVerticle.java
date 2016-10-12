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

import io.vertx.core.*;

import com.wellgo.wad.contentprovider.Configuration;


import io.vertx.core.Future;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class ContentStorageVerticle extends AbstractVerticle {

	private TransportClient esClient;
	
	private String esClusterName;
	
	private JsonArray esNodes;
	
	private WorkerExecutor executor;
	
	 // default value - 10 threads max 
    private int exPoolSize = 10;

    // default value - 20 seconds
    private long maxExecuteTime = 20000;
    
    private final static String  EX_POOL_NAME = "content-storage-verticle-ex-pool";
	
	
	private final static Logger logger = LoggerFactory.getLogger(ContentStorageVerticle.class);
	
	   @Override
	    public void start(Future<Void> startFuture) throws Exception {
		   
		   
		   MessageConsumer<String> consumer = vertx.eventBus().consumer(Configuration.EB_ADDR_CONTENT_STORAGE);
		   
		   consumer.handler(message -> {
		     logger.info("ContentStorageVerticle - I have received a message: " + message.body());
		     
		  
		     
		     executor.executeBlocking(future -> {
		    	 
		    	// Retrieve the content
		    	 GetResponse response = esClient.prepareGet("publisher", "wizard", "publisher123").get();
			     
		         future.complete(response); // complete(result);
		         
		       }, res -> {
		    	   
		    	   //String widgets = (String) response.getField("widgets").getValue();
				     //logger.debug("ContentGeneratorVerticle - Retrieved from the storage widgetUrl" + widgets);
		    	   
		    	   GetResponse response = (GetResponse) res.result();
				     
				     if (response == null || !response.isExists()){ // || response.getField(PATH_FIELD) == null) {
				         // doc not found
				    	 logger.error("ContentStorageVerticle - Document not found");
				    	 message.reply("{\"error\": \"Document not found\"}");
				     }
				     else {
				    	 // return response.getField(PATH_FIELD).getValue().toString();
				    	 
				    	 logger.debug("ContentStorageVerticle - set size: " + response.getSource().size());
				    	 
				    	 if (!response.getSource().isEmpty()) {

				    		 JSONObject json = new JSONObject(response.getSourceAsString());
				    		 
				    		  JSONArray jWizards = json.getJSONArray("wizards");
				    		  
				    		  for (int i = 0, size = jWizards.length(); i < size; i++) {
				    		      JSONObject objectInArray = jWizards.getJSONObject(i);
				    		      logger.debug(objectInArray.get("id") + "<--->" + objectInArray.get("url"));
				    		    }
				    		  
				    		  message.reply(jWizards.toString());
					     }
				    	 else {
				    		 logger.error("ContentStorageVerticle - Empty response received");
					    	 message.reply("{\"error\": \"Empty response received\"}");
				    	 }
				     }
		       });

		     		    
		     
		     /*
    		 for (Map.Entry<String, Object> entry : response.getSource().entrySet())
    		 {
    		     logger.debug(entry.getKey() + "/" + entry.getValue());
    		 }
    		 */

		   }); 
	    }

	private void initEsClient() {
		
		logger.debug("initEsClient >>");
	
		
		Settings settings = Settings.settingsBuilder()
		        .put("cluster.name", esClusterName).build();
		
		
		if (esNodes == null) {
			esNodes = new JsonArray("[\"127.0.0.1\"]");
			logger.warn("Received configuration has no ES Nodes, using default values: " + esNodes.encodePrettily());
		}
		
		
		logger.info("Received configuration: es.cluster.nodes: <" + esNodes.encode() + "> ");
		
		esClient = TransportClient.builder().settings(settings).build();
		
		executor.executeBlocking(future -> {
			
			for (int i = 0; i < esNodes.size(); i++) {
				
				try {
					esClient
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esNodes.getString(i)), 9300))
					.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(esNodes.getString(i), 9301)));
					logger.info("Added ES transport for host: " + esNodes.getString(i));
				} catch (UnknownHostException e) {
					logger.error("ContentStorageVerticle - initEsClient failed :" + e.getMessage(), e);
				}
			  }
				future.complete(esNodes); 
		    }, res -> {
		       logger.info("ContentStorageVerticle - initEsClient completed, the initialized nodes: " + res.result());
		    });
		
		logger.info("ContentStorageVerticle - initEsClient <<");
	}
	
	
	  @Override
	    public void stop(Future<Void> stopFuture) throws Exception {
			// on shutdown
		  logger.info("ContentStorageVerticle - stop >>>");
		  esClient.close();
		  logger.info("ContentStorageVerticle - stop <<<");
	    }
	  
	  
	  @Override
	  public void init(Vertx vertx, Context context) {
		  
		  this.vertx = vertx;
		  
		  JsonObject config = this.getVertx().getOrCreateContext().config();
			
			if (config != null) {
			
				esClusterName = config.getString("es.cluster.name", "wad-cp-es");
			
			
				logger.info("Received configuration: es.cluster.name: <" + esClusterName + "> ");
				
				esNodes = config.getJsonArray("es.cluster.nodes");
				
			}
			else {
				logger.warn("Received configuration is empty, using default values");
			}
			
		 
			executor = this.vertx.createSharedWorkerExecutor(EX_POOL_NAME, exPoolSize, maxExecuteTime);
			  

			initEsClient();
	    }

	
	
}
