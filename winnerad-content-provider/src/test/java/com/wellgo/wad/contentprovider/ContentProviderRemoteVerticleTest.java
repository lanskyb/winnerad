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
public class ContentProviderRemoteVerticleTest {
	
  private static String remoteHost = "52.57.24.6";
  
  private static int remoteHostPort = 8080;
  
  private static String awsLb = "wad-content-provider-lb-1982910833.eu-central-1.elb.amazonaws.com";
  
  private static int awsLbPort = 80;
  
  
  private static String localHost = "localhost";
	

  private Vertx vertx;
  
  private EmbeddedElasticsearchServer embeddedElasticsearchServer;  

  @Before
  public void setUp(TestContext context) {
	
  }

  @After
  public void tearDown(TestContext context) {
	System.out.println("tearDown >>>");
	
   
 
    System.out.println("tearDown <<<");
  }

  @Test
  public void testContentProviderVerticle(TestContext context) {
	
	
	  
    final Async async = context.async();
 
    
   Vertx.vertx().createHttpClient().websocket(awsLbPort, awsLb, "", websocket -> {
   //Vertx.vertx().createHttpClient().websocket(remoteHostPort, remoteHost, "", websocket -> {
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