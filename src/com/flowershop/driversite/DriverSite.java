package com.flowershop.driversite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.flowershop.ServerUtils;
import com.jeffrey.server.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;

public class DriverSite {
	
	private final static String ACCOUNT_SID = "AC15f0c7bd26137ed319deffa2022b84cb";
	private final static String AUTH_TOKEN = "1eba8cbc1c6de15ad1c454b55f3aebf3";

	private static class Twilio {
		
		/*
		 * Used in Twilio SMS event generation
		 */
		public static void sendMessage(String phone, String body) 
		{
			try
	        {
	            TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
	       	 
	            // Build a filter for the MessageList
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            params.add(new BasicNameValuePair("Body", body));
	            params.add(new BasicNameValuePair("To", phone));
	            params.add(new BasicNameValuePair("From", "+13852194380"));
	         
	            MessageFactory messageFactory = client.getAccount().getMessageFactory();
	            Message message = messageFactory.create(params);
	            System.out.println(message.getSid());
	    	                        	
	        } 
	        catch (TwilioRestException e)
	        {
	        	e.printStackTrace();
	        	ServerUtils.addToLog(e.getErrorMessage());
	        }
		}
	}
	
	public static void main(String[] args) {
		JServer s;
        try {
            s = new JServer(8000);
            s.start();
            
            String flowerShopURL = "localhost:8080/";
            String driversGuildURL = "localhost:8080/";
                        
            // TODO Setup DB
            
            s.register("/main", new JHandler() {
                @Override
                public Response handle(Request r) {
					return new Response(200, ServerUtils.getFileContents("web/driver_site_main.html"));
                }
            });
                        
            s.register("/register", new JHandler() {
                @Override
                public Response handle(Request r) {
                	return new Response(200, ServerUtils.getFileContents("web/register.html"));
                }
            });
            
            s.register("/add_driver", new JHandler() {
                @Override
                public Response handle(Request r) {
                    if(r.getMethod().equals("POST")){
                        try {
                        	
                        	// Parse query string
	                        String query = ServerUtils.inputStreamToString(r.getBody());
                        	Map<String, String> values = ServerUtils.getQueryMap(query);

                            // TODO Add driver to the system.

                        	System.out.println(values.toString());
                        	ServerUtils.addToLog(values.toString());
                        	
                        	return new Response(200, query);
                        	
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
            
            
            /**
             * 	EVENT PRODUCERS (3):
             * 		- Twilio Send SMS Event Producer
             * 		- Delivery Complete Event Producer
             * 		- Bid Available Event Producer              
             */
            
            /**
             * 
             * 	EVENT CONSUMERS (4)
             * 
             */
            
            /*
             * RFQ Delivery Ready Event Consumer
             * 
             * 		Will contain:
             * 			- Twilio Send SMS Event Producer
             */
            s.register("/rfq_delivery_ready", new JHandler() {
                @Override
                public Response handle(Request r) {
                	       
                	/**
                	 * TODO:
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
                    
            /*
             * Foursquare Checkin Event Consumer
             */
            s.register("/checkin", new JHandler() {             
                @Override
                public Response handle(Request r) {
                	if(r.getMethod().equals("POST")){
                        try {
                        	
                        	// TODO Update user's most recent checkin in database
                        	
                            return new Response(200).pipe(r.getBody());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return new Response(200, "Checkin");
                }
            });
            
            /*
             * Twilio Receive SMS Event Consumer
             * 
             * 		Will include:
             * 			- Delivery Complete Event Producer
             * 			- Bid Available Event Producer
             */
            s.register("/twilio_receive_sms", new JHandler() {             
                @Override
                public Response handle(Request r) {
                	
                	/*
                	 * type 1: Bid Available
                	*/
                	
                	
                    // Type 2: Delivery Complete
                    // TODO send event to driver's guild                   
                    // TODO send event to flower shop website
                	
                    if(r.getMethod().equals("POST")){
                        try {
                            return new Response(200).pipe(r.getBody());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ServerUtils.addToLog(ServerUtils.inputStreamToString(r.getBody()));
                    return new Response(200, "SMS");
                }
            });
            
            /*
             * RFQ Bid Awarded Event Consumer
             * 
             * 		Will include:
             * 			- Twilio Send SMS Event Producer
             */
            s.register("/rfq_bid_awarded", new JHandler() {             
                @Override
                public Response handle(Request r) {
                    if(r.getMethod().equals("POST")){
                        try {
                        	
                        	// TODO Retrieve driver phone number
                        	// TODO Send Twilio message to driver
                        	
                            return new Response(200).pipe(r.getBody());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ServerUtils.addToLog(ServerUtils.inputStreamToString(r.getBody()));
                    return new Response(200, "SMS");
                }
            });

            
            /**
             * 
             * LOGGING UTILS
             * 
             */
            s.register("/view_log", new JHandler() {             
                @Override
                public Response handle(Request r) {                	
                    return new Response(200, ServerUtils.getFileContents("log.xml"));
                }
            });
            
            s.register("/clear_log", new JHandler() {             
                @Override
                public Response handle(Request r) { 
                	ServerUtils.clearLog();
                    return new Response(200, ServerUtils.getFileContents("log.xml"));
                }
            });
            
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
	}
}
