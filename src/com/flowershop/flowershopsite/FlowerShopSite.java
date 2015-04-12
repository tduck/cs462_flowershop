package com.flowershop.flowershopsite;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import com.flowershop.ServerUtils;
import com.jeffrey.server.JHandler;
import com.jeffrey.server.JServer;
import com.jeffrey.server.Request;
import com.jeffrey.server.Response;

public class FlowerShopSite {

	private static boolean loggedIn = false;
	private static String currentShop = "";
	
	private static String driversGuildURL = "http://localhost:8000";
	
	private static String jdbcConnection = 
			"jdbc:mysql://jflowershop.cii4ylx5phxt.us-west-1.rds.amazonaws.com:3306/employees?user=sa&password=mypassword";

	private static String username = "flowershop";
	
	public static void main(String[] args) {
		
		JServer s;
        try {
            s = new JServer(8001);
            s.start();
                       
            s.register("/main", new JHandler() {
                @Override
                public Response handle(Request r) {
                    if(r.getMethod().equals("POST"))
                    {
                         try {
                        	 // TODO Create dynamic page with locations from DB
                        	 
                             return new Response(200).pipe(r.getBody());
                         } catch (Exception e) {
                             e.printStackTrace();
                             return new Response(405);
                         }                         
                    }
                    else try 
                    {
						return new Response(200).pipe(new FileInputStream(new File("web/flower_shop/main.html")));
					} 
                    catch (FileNotFoundException e) 
                    {
						e.printStackTrace();
						return new Response(404);
					}
                }
            });
            
            s.register("/shops", new JHandler() {
                @Override
                public Response handle(Request r) {
                     if(r.getMethod().equals("POST")){
                    	 String flowerShopID = UUID.randomUUID().toString().replaceAll("-", "");
                    	 
                    	 String query = ServerUtils.inputStreamToString(r.getBody());
                    	 Map<String, String> values = ServerUtils.getQueryMap(query);
                    	 
                    	 if (values.get("location_name") != null
                    			 && values.get("latitude") != null 
                    			 && Math.abs(Double.parseDouble(values.get("latitude"))) <= 180
                    			 && values.get("longitude") != null 
                    			 && Math.abs(Double.parseDouble(values.get("longitude"))) <= 180)
                    	 {
                    		 try
                    		 {
                    			 query += "&shop_id=" + flowerShopID;
                        		 String postURL = driversGuildURL + "/shops/";
                        		 URL obj = new URL(postURL);
                        		 HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                        		 
                        		 con.setRequestMethod("POST");
                        		 con.setDoOutput(true);
                        		 
                        		 DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                        		 wr.writeBytes(query);
                        		 
                        		 wr.flush();
                        		 wr.close();
                        		                         		                        		                         		 							
								 if (con.getResponseCode() == 200)
								 {
									 return new Response(200, "Location successfully added with ID " + 
                                		 flowerShopID + ".");
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
                    	 else
                    	 {
                    		 return new Response(200, "Invalid latitude/longitude value(s). Try again.");
                    	 }
                     }
                     else try
                     {
                    	 return new Response(200).pipe(new FileInputStream(new File("web/flower_shop/add_shop.html")));
                     }
                     catch (FileNotFoundException e)
                     {
                    	 return new Response(404);
                     }
                }
            });
            
            
                       
            s.register("/login", new JHandler() {
                @Override
                public Response handle(Request r) {
                     if(r.getMethod().equals("POST")){
                         
                    	// Parse query string
                        String query = ServerUtils.inputStreamToString(r.getBody());
                     	Map<String, String> values = ServerUtils.getQueryMap(query);
                     	
                     	if (values.get("password") != null && values.get("password").equals("1234"))
                     	{
                     		loggedIn = true;
                     		currentShop = values.get("location");
                     		
                            // TODO Retrieve available bids for orders at the given flower shop location
                     		try
                     		{
                     			return new Response(200).pipe(new FileInputStream(new File(("web/flower_shop/admin.html"))));
                     		}
                     		catch (FileNotFoundException e)
                     		{
                     			return new Response(404);
                     		}
                     	}
                     	else
                     	{
                     		return new Response(200, "Incorrect login. Click your browser's Back button to try again.");
                     	}
                     }
                     else try
                     {
                    	 return new Response(200).pipe(new FileInputStream(new File(("web/flower_shop/login.html"))));
                     }
                     catch (FileNotFoundException e)
                     {
                    	 return new Response(404);
                     }
                }
            });
            
            s.register("/logout", new JHandler() {
                @Override
                public Response handle(Request r) {
                     loggedIn = false;
                     try
                     {
                    	 return new Response(200).pipe(new FileInputStream(new File("web/flower_shop/main.html")));
                     }
                     catch (FileNotFoundException e)
                     {
                    	 return new Response(404);
                     }
                }
            });
            
           
            
            /**
             * 
             * 	EVENT PRODUCERS (3)
             * 
             */
                        
            /*
             * RFQ Delivery Ready Event Producer
             */
            s.register("/place_order", new JHandler() {
                @Override
                public Response handle(Request r) {
                     if(r.getMethod().equals("POST")){
                                                 	
                    	 String orderID = UUID.randomUUID().toString().replaceAll("-", "");
                    	
                    	 String query = ServerUtils.inputStreamToString(r.getBody());
                    	 Map<String, String> values = ServerUtils.getQueryMap(query);

                    	 if (values.get("email") != null
                    			 && values.get("first_name") != null
                    			 && values.get("last_name") != null
                    			 && values.get("address") != null)
                    	 {
                    		 try
                    		 {
                    			 query += "&order_id=" + orderID;
                        		 String postURL = driversGuildURL + "/orders/";
                        		 URL obj = new URL(postURL);
                        		 HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                        		 
                        		 con.setRequestMethod("POST");
                        		 con.setDoOutput(true);
                        		 
                        		 DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                        		 wr.writeBytes(query);
                        		 
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
									 return new Response(200, "Order placed successfully. Your order ID is " + 
                                		orderID + ".");
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
                    	 else return new Response(200, "There was an error in processing your order. One or more required fields were not completed.");                       
                     }
                     return new Response(405);
                }
            });
            
            /*
             * Delivery Pickup Event Producer
             */
            s.register("/delivery_pickup", new JHandler() {
                @Override
                public Response handle(Request r) {
                     if(r.getMethod().equals("POST")){
                         try {
                        	 
                        	 // TODO Send event to Drivers' Guild
                        	 
                        	 
                             return new Response(200).pipe(r.getBody());
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                     return new Response(405);
                }
            });
            
            /*
             * RFQ Bid Awarded Event Producer
             */
            s.register("/rfq_bid_awarded", new JHandler() {
                @Override
                public Response handle(Request r) {
                	if (r.getMethod().equals("POST")){
                         try {         
                        	 
                        	 // TODO Send event to Drivers' Guild
                        	 
                             return new Response(200).pipe(r.getBody());
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                	}
                	return new Response(405);
                }
            });
            
            
            /**
             * 
             * 	EVENT CONSUMERS (2)
             * 
             */
            
            /*
             * Delivery Complete Event Consumer
             */
            s.register("/delivery_complete", new JHandler() {
                @Override
                public Response handle(Request r) {
                     if(r.getMethod().equals("POST")){
                         try {
                        	 
                         // TODO Determine what this event consumer does
                        	 
                        	 return new Response(200).pipe(r.getBody());
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                     return new Response(405);
                }
            });
            
            /*
             * RFQ Bid Available Event Consumer
             */
            s.register("/rfq_bid_available", new JHandler() {
                @Override
                public Response handle(Request r) {
                     if(r.getMethod().equals("POST")){
                         try {
                        	 
                        	 // TODO Store bid information in database
                        	 
                        	 return new Response(200).pipe(r.getBody());
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                     return new Response(405);
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
        }
        
        catch (IOException e) {
        	e.printStackTrace();
        	return;
		}
	}
}
