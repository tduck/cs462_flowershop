package com.flowershop.driversite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompleteUser;

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
	
	private static class Checkin {
		
		private Venue venue;
		private User user;
					
		public Venue getVenue() {
			return venue;
		}
		public User getUser() {
			return user;
		}
	}
	
	private static class User {
		
		private String id;
		
		public String getId() {
			return id;
		}
	}
	
	private static class Venue
	{
		private Location location;
				
		public Location getLocation() {
			return location;
		}
	}
	
	private static class Access {
	
		private String access_token;
		
		public String getAccessToken() {
			return access_token;
		}
	}
	
	public static void main(String[] args) {
		JServer s;
        try {

            s = new JServer(443, "https", System.getenv("keystorelocation"), System.getenv("keystorepassword"));
            s.start();
                                               
            s.register("/home", new WebsiteHandler("web/driver_site"));
            
            s.register("/login", new JHandler() 
            {            
            	 @Override
                 public Response handle(Request request) 
            	 {      
            		 try
            		 {
	            		 String tail = request.getURI().toString().substring(request.getPath().length());
	                     if(tail.indexOf("/") == 0)
	                         tail = tail.substring(1);
	                     
	                     String code = tail.replaceAll("\\Q?\\Ecode=", "");
	                     System.out.println(code);

	                     if (!code.equals(""))
	                     {                     
		                     FoursquareApi foursquareApi = new FoursquareApi(
		                    		 "Z5RSCN1KCRNULHOGJRPLQSTXDRUGHIKSASD5KBYM1EB31CNV", 
		                    		 "OM4XSTGXFGFKTZP4V0CIALIA4ZOAZNSS25CMYYRNCCUPNXN3", 
		                    		 "https://52.8.41.111/login/");
		                     
		                     // Callback url contains authorization code 
		                     try 
		                     {
		                       // finally we need to authenticate that authorization code 
		                       foursquareApi.setUseCallback(false);
		                       foursquareApi.authenticateCode(code);
		                       
		                       String token = foursquareApi.getOAuthToken();
		                       System.out.println(token);
		                       		                       
		                       Result<CompleteUser> userResult = foursquareApi.user(null);
		                       String error = userResult.getMeta().getErrorDetail();
		                       
		                       if (userResult.getResult() != null)
		                       {		   
		                    	   CompleteUser user = userResult.getResult();
			                       Driver driver = new Driver();
			                       driver.setId(user.getId());
			                       driver.setName(user.getFirstName() + " " + user.getLastName());
			                       driver.setPhone(user.getContact().getPhone());
			                       driver.setAvailable(false);
			                       driver.setClockedin(false);
			                       
			                       fi.foyt.foursquare.api.entities.Checkin[] checkins = foursquareApi.usersCheckins("self", null, null, null, null).getResult().getItems();
			                       fi.foyt.foursquare.api.entities.Checkin c = checkins[0];
			                       Location l = new Location();
			                       l.setLat(c.getLocation().getLat().floatValue());
			                       l.setLng(c.getLocation().getLng().floatValue());
			                       driver.setLastLocation(l);
			                       
			                       Gson dgson = new Gson();
			                       String jsonString = dgson.toJson(driver);
			                       
			                       System.out.println(jsonString);
			                       return new Response(200, jsonString);
		                       }
		                       else
		                       {
		                    	   return new Response(200, error);
		                       }
		                       
		                     } 
		                     catch (FoursquareApiException e) 
		                     {
		                      // TODO: Error handling
		                    	 return new Response(500);
		                     } 	                     
	                     }
	                     
	                     else
	                     {
	                    	 return new Response(500);
	                     }
            		 }
            		 catch (Exception e)
            		 {
            			 e.printStackTrace();
            			 return new Response(500);
            		 }
            	 }            	
            });
            
            // Add new driver
            s.register("/drivers/post", new JHandler() {
                @Override
                public Response handle(Request r) {
                	
                	if(r.getMethod().equals("POST"))
                    {                        	                   	
                    	// Parse query string
                        String query = ServerUtils.inputStreamToString(r.getBody());
                    	Map<String, String> values = ServerUtils.getQueryMap(query);

                        // Add driver to the system.
                    	if (values.get("phone") != null 
                    			&& values.get("email") != null
                    			&& values.get("first_name") != null
                    			&& values.get("last_name") != null) 
                    	{                  	
                    		Driver driver = new Driver();
                    		driver.setPhone(values.get("phone"));
                    		driver.setName(values.get("first_name") + " " + values.get("last_name"));
                    		driver.setLastLocation(new Location());
                    		
							Gson gson = new Gson();
							String jsonString = gson.toJson(driver);
							
							String postURL = driversGuildURL + "/drivers/";
							int code = ServerUtils.postJson(postURL, jsonString);
							if (code == 200 || code == 201)
							{
								 return new Response(200, "Driver successfully added.");
							}
							else
							{
								return new Response(code);
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
                	list += "<tr><th>Phone</th>"
                			+ "<th>Name</th>"
                			+ "<th>Clocked In?</th>"
                			+ "<th>Available?</th></tr>";
                	for (Driver driver : drivers)
                	{
                		list += "<tr><td>" + driver.getPhone() + "</td>";
                		list += "<td>" + driver.getName() + "</td>";
                		list += "<td>" + driver.isClockedin() + "</td>";
                		list += "<td>" + driver.isAvailable() + "</td>";
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
                public Response handle(Request r) 
                {                              	
                	if(r.getMethod().equals("POST"))
                	{
                    	String checkinString = ServerUtils.inputStreamToString(r.getBody());
                    	System.out.println(checkinString);   
                		
                		Gson gson = new Gson();
                    	Checkin checkin = gson.fromJson(checkinString, Checkin.class);
                    	
                    	System.out.println("HEre");
                    	System.out.println(checkin.getVenue().getLocation().getLat());
                    	                        	
                    	Driver driver = new Driver();
                    	driver.setId(checkin.getUser().getId());
                    	driver.setLastLocation(checkin.getVenue().getLocation());
                    	
                    	Gson dgson = new Gson();
                    	String jsonString = dgson.toJson(driver);
                    	
                    	System.out.println(jsonString);
                    	
                    	String postURL = driversGuildURL + "/drivers/checkin";
                    	int code = ServerUtils.postJson(postURL, jsonString);
                    	if (code == 200 || code == 201)
    					{
    						 return new Response(200, "Checkin successful.");
    					}
    					else
    					{
    						return new Response(code);
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
                    	                	               		
                		// Parse query string
                        String query = ServerUtils.inputStreamToString(r.getBody());
                    	Map<String, String> values = ServerUtils.getQueryMap(query);

                        System.out.println(values.toString());
                        
                        if (values.get("From") == null)
                        {
                        	return new Response(400);
                        }
                                               
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
		                   			
		                   			int code = ServerUtils.postJson(postURL, jsonString);
									System.out.println(code);   
									
									Twilio.sendMessage(values.get("From").replaceAll("%2B", "+"), "Delivery completion confirmed for order " + order.getId());
		                       	}
	                        	
	                        	else if (values.get("Body").toLowerCase().contains("clock"))
	                        	{
	                        		String[] messageParts = values.get("Body").toLowerCase().replace('+', ' ').split(" ");
	                        		Driver driver = new Driver();
	                        		driver.setPhone(values.get("From").replaceAll("%2B1", ""));
	                        		if (messageParts[1].equals("in") || messageParts[1].equals("out"))
	                        		{
		                        		String postURL = driversGuildURL + "/drivers/clockin";

	                        			driver.setClockedin(messageParts[1].equals("in") ? true : false);
	                        			driver.setAvailable(driver.isClockedin());
	                        			Gson gson = new Gson();
	                        			String jsonString = gson.toJson(driver);
	                        			
	                        			int code = ServerUtils.postJson(postURL, jsonString);
	        							System.out.println(code);
	        							
	        							Twilio.sendMessage("+1" + driver.getPhone(), "Time punch received.");
	        						}	                    
	                        	}
	                        }
	                        
                        }
                        catch (Exception e)
                        {
                        	e.printStackTrace();
                        	return new Response(500);
                        }                        
                		
                		return new Response(200, "<Response/>");	
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
