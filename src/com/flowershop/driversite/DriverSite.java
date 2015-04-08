package com.flowershop.driversite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jeffrey.server.*;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class DriverSite {
	
	private final static String ACCOUNT_SID = "AC15f0c7bd26137ed319deffa2022b84cb";
	private final static String AUTH_TOKEN = "1eba8cbc1c6de15ad1c454b55f3aebf3";

	public static void main(String[] args) {
		JServer s;
        try {
            s = new JServer(8000);
            s.start();
            
            String flowerShopURL = "localhost:8080/";
            String driversGuildURL = "localhost:8080/";
            
            s.register("/main", new JHandler() {
                @Override
                public Response handle(Request r) {
                    if(r.getMethod().equals("POST")){
                        try {                        	
                            return new Response(200).pipe(r.getBody());                            
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
//                    try
//                    {
//	                    TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
//	               	 
//	                    // Build a filter for the MessageList
//	                    List<NameValuePair> params = new ArrayList<NameValuePair>();
//	                    params.add(new BasicNameValuePair("Body", "I am listening to Bayside!"));
//	                    params.add(new BasicNameValuePair("To", "+18013765960"));
//	                    params.add(new BasicNameValuePair("From", "+13852194380"));
//	                 
//	                    MessageFactory messageFactory = client.getAccount().getMessageFactory();
//	                    Message message = messageFactory.create(params);
//	                    System.out.println(message.getSid());
//                	                        	
//	                    return new Response(200, "Hello world");
//                    } 
//                    catch (Exception e)
//                    {
//                    	return new Response(405);
//                    }
					return new Response(200, JServer.getFileContents("web/driver_site_main.html"));
                }
            });
            
            s.register("/register", new JHandler() {
                @Override
                public Response handle(Request r) {
                	return new Response(200, JServer.getFileContents("web/register.html"));
                }
            });
            
            s.register("/add_driver", new JHandler() {
                @Override
                public Response handle(Request r) {
                    if(r.getMethod().equals("POST")){
                        try {
                        	
                        // TODO Add driver to the system.
                        	
                        	
                            return new Response(200).pipe(r.getBody());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return new Response(200, "Hello world");
                }
            });
            
            s.register("/list_drivers", new JHandler() {
                @Override
                public Response handle(Request r) {
                    if(r.getMethod().equals("POST")){
                        try {
                            return new Response(200).pipe(r.getBody());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return new Response(200, "Hello world");
                }
            });
            
            s.register("/delivery_ready", new JHandler() {
                @Override
                public Response handle(Request r) {
                	       
                	/**
                	 * You will be signaling one event in this lab. 
                	 	The event domain and name must be the following:

							Event domain: rfq
							Event name: bid_available
						
						That event will need several attributes, such as the following:

							Driver name
							Estimated delivery time
						
						When the delivery driver site receives an rfq:delivery_ready event, it

							Runs the algorithm given above to determine whether or not to respond
							If a response is needed, signals a rfq:bid_available event to the 
								flower shop's ESL.
                	 */
                	
                    if(r.getMethod().equals("POST")){
                        try {
                            return new Response(200).pipe(r.getBody());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return new Response(200, "Delivery ready");
                }
            });
                    
            s.register("/checkin", new JHandler() {             
                @Override
                public Response handle(Request r) {
                	                	
                    if(r.getMethod().equals("POST")){
                        try {
                            return new Response(200).pipe(r.getBody());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return new Response(200, "Checkin");
                }
            });
            
            s.register("/receive_sms", new JHandler() {             
                @Override
                public Response handle(Request r) {
                	                	
                    if(r.getMethod().equals("POST")){
                        try {
                            return new Response(200).pipe(r.getBody());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return new Response(200, "Checkin");
                }
            });
            
            s.register("/order_complete", new JHandler() {             
                @Override
                public Response handle(Request r) {
                	
                    // TODO Receive order complete message
                    
                    // TODO Process message
                    
                    // TODO Alert driver's guild
                    
                    // TODO Alert flower shop website
                	
                    if(r.getMethod().equals("POST")){
                        try {
                            return new Response(200).pipe(r.getBody());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return new Response(200, "Order complete");
                }
            });
            
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
	}
}
