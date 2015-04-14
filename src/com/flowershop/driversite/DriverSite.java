package com.flowershop.driversite;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.flowershop.ServerUtils;
import com.flowershop.driversguild.dao.DriverDAO;
import com.flowershop.model.Driver;
import com.flowershop.model.Location;
import com.flowershop.model.Order;
import com.google.gson.Gson;
import com.jeffrey.server.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;

public class DriverSite {
	
	private final static String ACCOUNT_SID = "AC15f0c7bd26137ed319deffa2022b84cb";
	private final static String AUTH_TOKEN = "1eba8cbc1c6de15ad1c454b55f3aebf3";

	private static String driversGuildURL = "http://52.8.36.38:8080";
	private static String flowerShopURL = "http://52.8.9.109:8080";
	
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
            s = new JServer(8080);
            s.start();
                                    
            s.register("/home", new WebsiteHandler("web/driver_site"));
            
            // Add new driver
            s.register("/drivers/post", new JHandler() {
                @Override
                public Response handle(Request r) {
                    if(r.getMethod().equals("POST"))
                    {                        	                   	
                    	// Parse query string
                        String query = ServerUtils.inputStreamToString(r.getBody());
                    	Map<String, String> values = ServerUtils.getQueryMap(query);

                        // TODO Add driver to the system.
                    	if (values.get("phone") != null 
                    			&& values.get("email") != null
                    			&& values.get("first_name") != null
                    			&& values.get("last_name") != null) 
                    	{
                        	try
	                   		{
                        		Driver driver = new Driver();
                        		driver.setPhone(values.get("phone"));
                        		driver.setName(values.get("first_name") + " " + values.get("last_name"));
                        		driver.setLastLocation(new Location());
                        		
								Gson gson = new Gson();
								String jsonString = gson.toJson(driver);
								
								String postURL = driversGuildURL + "/drivers/";
								URL obj = new URL(postURL);
								HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	                       		 
								con.setRequestMethod("POST");
								con.setDoOutput(true);
								con.setRequestProperty("Content-Type", "application/json");
								 
								DataOutputStream wr = new DataOutputStream(con.getOutputStream());
								wr.writeBytes(jsonString);
								 
								wr.flush();
								wr.close();
	                       		 
	                       		/* 
	                       		 	int responseCode = con.getResponseCode();
		                    		System.out.println("\nSending 'POST' request to URL : " + postURL);
		                    		System.out.println("Post parameters : " + query);
		                    		System.out.println("Response Code : " + responseCode);
		                     
		                    		BufferedReader in = new BufferedReader(
		                    		        new InputStreamReader(con.getInputStream()));
		                    		String inputLine;
		                    		StringBuffer response = new StringBuffer();
		                     
		                    		while ((inputLine = in.readLine()) != null) {
		                    			response.append(inputLine);
		                    		}
		                    		in.close();
		                    	
	                     
		                    		//print result
		                    		System.out.println(response.toString());
	                       		*/
	                       		 
								 if (con.getResponseCode() == 200)
								 {
									 return new Response(200, "Driver successfully added.");
								 }
								 else
								 {
									 return new Response(500);
								 }
	                   		 } 
	                   		 catch (IOException e) 
	                   		 {
	                   			 e.printStackTrace();
	                   			 return new Response(500);
	                   		 }
                    	}
                    }                                   	
                    return new Response(200, "Hello world");
            	}
            });
            
            // Show all drivers
            s.register("/drivers/list", new JHandler() {
                @Override
                public Response handle(Request r) {
                	
                	DriverDAO driverDAO = new DriverDAO();
                	List<Driver> drivers = driverDAO.getAllDrivers();
                	if (drivers == null)
                	{
                		return new Response(500);
                	}
                	
                	String list = "<html><head><title>Driver List</title></head><body><table>";
                	for (Driver driver : drivers)
                	{
                		list += "<tr><td>" + driver.getPhone() + "</td>";
                		list += "<td>" + driver.getName() + "</td>";
                		list += "</tr>";
                	}
                	list += "</table></body></html>";
                	
                    return new Response(200, list);
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
            s.register("/sms", new JHandler() {             
                @Override
                public Response handle(Request r) {
                	
                	if (r.getMethod().equals("POST"))
        			{
                		/*
                    	 * type 1: Bid Available
                    	*/
                        // Type 2: Delivery Complete
                        // TODO send event to driver's guild                   
                        // TODO send event to flower shop website
                    	                	               		
                		// Parse query string
                        String query = ServerUtils.inputStreamToString(r.getBody());
                    	Map<String, String> values = ServerUtils.getQueryMap(query);

                        System.out.println(values.toString());
                        
                        if (values.get("From") == null)
                        {
                        	return new Response(400);
                        }
                        
                        String driverphone = values.get("From").replaceAll("%2B", "");
                        
                        try
                        {                  	
                        	if (values.get("Body") != null)
	                        {
	                        	if (values.get("Body").toLowerCase().contains("delivered"))
	                        	{
	                        		String[] body = values.get("Body").replace('+', ' ').split(" ");
	                        		
	                        		String postURL = driversGuildURL + "/orders/complete";
	                        		
	                        		Order order = new Order();
	                        		order.setId(Integer.parseInt(body[1]));
	                        		order.setDelivered(true);
	                        		Gson gson = new Gson();
		                   			String jsonString = gson.toJson(order) + "\n";
		                   			 
		                       		URL obj = new URL(postURL);
		                       		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		                       		
		                       		con.setRequestMethod("POST");
		                       		con.setDoOutput(true);
		                       		con.setRequestProperty("Content-Type", "application/json");
		                       		 
		                       		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		                       		wr.writeBytes(jsonString);
		                      		 
		                       		wr.flush();
		                       		wr.close();
		                       				                       		
		                    		System.out.println("\nSending 'POST' request to URL : " + postURL);
		                    		System.out.println("Post parameters : " + jsonString);
		                    		
	                       		 	/* 
	                       		 	 	int responseCode = con.getResponseCode();
			                    		System.out.println("Response Code : " + responseCode);
			                     
			                    		BufferedReader in = new BufferedReader(
			                    		        new InputStreamReader(con.getInputStream()));
			                    		String inputLine;
			                    		StringBuffer response = new StringBuffer();
			                     
			                    		while ((inputLine = in.readLine()) != null) {
			                    			response.append(inputLine);
			                    		}
			                    		in.close();			                    	
		                     
			                    		//print result
			                    		System.out.println(response.toString());
		                    		*/
	                        	}
	                        	
	                        	else if (values.get("Body").toLowerCase().contains("delivered"))
	                        	{
	                        		
	                        	}
	                        }
	                        
                        }
                        catch (Exception e)
                        {
                        	e.printStackTrace();
                        	return new Response(500);
                        }                        
                		
                		try 
                		{
							return new Response(200).pipe(new FileInputStream(new File("sms/echo.xml")));
						} 
                		catch (FileNotFoundException e) 
                		{
							e.printStackTrace();
							return new Response(500);
						}
                		
        			}
                	
                	else try 
                    {
                    	return new Response(200).pipe(new FileInputStream(new File("sms/echo.xml")));
                    } 
                    catch (Exception e) 
                    {
                        e.printStackTrace();
                        return new Response(500);
                    }
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
                	try
                	{
                		return new Response(200).pipe(new FileInputStream(new File(("log.xml"))));
                	}
                	catch (FileNotFoundException e)
                	{
                		return new Response(404);
                	}
                }
            });
            
            s.register("/clear_log", new JHandler() {             
                @Override
                public Response handle(Request r) { 
                	ServerUtils.clearLog();
                	try
                	{
                		return new Response(200).pipe(new FileInputStream(new File(("log.xml"))));
                	}
                	catch (FileNotFoundException e)
                	{
                		return new Response(404);
                	}
                }
            });
            
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
	}
}
