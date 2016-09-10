package com.wellgo.wad.contentprovider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.wellgo.wad.contentprovider.protocol.*;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.core.buffer.Buffer;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class ContentProviderVerticleTest {
	
  private static String remoteHost = "52.57.24.6";
  
  private static String localHost = "localhost";
	

  private Vertx vertx;
  
  private EmbeddedElasticsearchServer embeddedElasticsearchServer;  

  @Before
  public void setUp(TestContext context) {
	  
	
	embeddedElasticsearchServer = new EmbeddedElasticsearchServer();
	
	org.apache.http.client.HttpClient httpClient = HttpClients.createDefault();
	HttpPut httpPut = new HttpPut("http://localhost:9200/publisher/wizard/publisher123?pretty");
	// + templateName);
	// httpPut.setEntity(new FileEntity(new File("template.json")));
	try {
		StringEntity input = new StringEntity("{\"publisher-name\": \"publisher 123\", \"wizards\": [{\"url\": \"http://ynet.co.il\", \"id\": \"wiz1\" }, { \"url\": \"http://walla.co.il\", \"id\": \"wiz2\"}]}");
		input.setContentType("application/json");
	    httpPut.setEntity(input);
		HttpResponse resp = httpClient.execute(httpPut);
		System.out.println("ES response for put: " + resp.getStatusLine());
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
    vertx = Vertx.vertx();
    vertx.deployVerticle(ContentProviderVerticle.class.getName(), context.asyncAssertSuccess());
    
    
    try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }

  @After
  public void tearDown(TestContext context) {
	System.out.println("tearDown >>>");
	//for (int i=0; i<vertx.deploymentIDs().size(); i++)
		//vertx.undeploy((String) vertx.deploymentIDs().toArray()[i]);
    //vertx.close(context.asyncAssertSuccess());
	 vertx.close();
    embeddedElasticsearchServer.shutdown();
   
 
    System.out.println("tearDown <<<");
  }

  @Test
  public void testContentProviderVerticle(TestContext context) {
	
	
	  
    final Async async = context.async();
     /*
    vertx.createHttpClient().getNow(8080, "localhost", "/",
     response -> {
      response.handler(body -> {
        context.assertTrue(body.toString().contains("Hello"));
        async.complete();
      });
    });
    */
    
   Vertx.vertx().createHttpClient().websocket(8080, localHost, "", websocket -> {
    	websocket.handler(data -> {
          System.out.println("Server message: ");
          System.out.println("Received data " + data.toString("ISO-8859-1"));
          
          // Message rcvdMsg = (Message)Serializer.unpack(data.toString("ISO-8859-1"), Message.class);
          // context.assertTrue(data.toString("ISO-8859-1").contains(rcvdMsg.getContent()));
         
          context.assertFalse(data.toString("ISO-8859-1").contains("error"));
          List<Wizard> wizards = Serializer.unpackList(data.toString("ISO-8859-1"), Wizard.class);
          context.assertTrue(wizards.get(0).getId().contains("wiz1"));
          context.assertTrue(wizards.get(1).getId().contains("wiz2"));
          
          async.complete();
        });
    	
    	Message sentMsg = new Message("Echo message");
       
        websocket.writeBinaryMessage(Buffer.buffer(Serializer.pack(sentMsg)));
       
    });
    
    
    
    //async.complete();
    }
  
  
  /*
  
  @Test
  public void testContentProviderVerticleErrorOnWrongPublisherId(TestContext context) {
	
	
	  
    final Async async = context.async();
     
    
   Vertx.vertx().createHttpClient().websocket(8080, localHost, "", websocket -> {
    	websocket.handler(data -> {
          System.out.println("Server message: ");
          System.out.println("Received data " + data.toString("ISO-8859-1"));
         
          context.assertFalse(data.toString("ISO-8859-1").contains("error"));
          async.complete();
        });
    	
    	Message sentMsg = new Message("Echo message");
       
        websocket.writeBinaryMessage(Buffer.buffer(Serializer.pack(sentMsg)));
       
    });
    
    }
  */
  
  
}