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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.flowershop.ServerUtils;
import com.flowershop.driversguild.dao.ShopDAO;
import com.flowershop.model.Delivery;
import com.flowershop.model.Location;
import com.flowershop.model.Order;
import com.flowershop.model.Shop;
import com.google.gson.Gson;
import com.jeffrey.server.JHandler;
import com.jeffrey.server.JServer;
import com.jeffrey.server.Request;
import com.jeffrey.server.Response;

public class FlowerShopSite {
	
	private static String driversGuildURL = "http://52.8.36.38:8080";
	private static ShopDAO shopDAO;
	
	public static void main(String[] args) {
		
		JServer s;
        try {
            s = new JServer(8080);
            s.start();
            
            shopDAO = new ShopDAO();
                       
            s.register("/main", new JHandler() {
                @Override
                public Response handle(Request r) {

					List<Shop> shops = shopDAO.getShops();
					if (shops == null)
					{
						return new Response(500);
					}
					
					String options = "";
					for (Shop shop : shops)
					{
						options += "<option value='" + shop.getID() + "'>" + shop.getName().replace('+', ' ') + "</option>";
					}
					
					String content = ServerUtils.getFileContents("web/flower_shop/main.html").replaceAll("<option value='0'>Error loading shops</option>", options);
					
					return new Response(200, content);
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
                    		 Shop toAdd = new Shop();
                    		 toAdd.setID(flowerShopID);
                    		 toAdd.setName(values.get("location_name"));
                    		 Location location = new Location();
                    		 location.setLatitude(Float.parseFloat(values.get("latitude")));
                    		 location.setLongitude(Float.parseFloat(values.get("longitude")));
                    		 toAdd.setLocation(location);
                    		 toAdd = shopDAO.createShop(toAdd);
                    		 
                    		 if (toAdd == null)
                    		 {
                    			 return new Response(500);
                    		 }
                    		 else
                    		 {
                    			 return new Response(200, "Shop created successfully with ID " + flowerShopID);
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
                     	String shopID = values.get("location");
                     	
                        // Retrieve available bids for orders at the given flower shop location

                     	String getURL = driversGuildURL + "/shops/" + shopID + "/orders";
                     	
 	                	try
                     	{
 	                		/**
 	                		 * Get all orders for the selected shop
 	                		 */
	                     	URL obj = new URL(getURL);
		               		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		               		 
		               		String contents = ServerUtils.inputStreamToString(con.getInputStream());
	                   		contents = contents.substring(1, contents.length() - 1);

 	                   		System.out.println("Sending 'GET' request to URL : " + getURL);
	                   		System.out.println("Results: " + contents);
	                   		System.out.println("Response Code : " + con.getResponseCode());
	                   		
	                   		Gson gson = new Gson();
	                   		String[] orderStrings = contents.split("\\Q}\\E,\\Q{\\E");
	                   		for (int i=0; i<orderStrings.length; i++)
	                   		{
	                   			if (i > 0)
	                   			{
	                   				orderStrings[i] = "{" + orderStrings[i];
	                   			}
	                   			
	                   			if (i < orderStrings.length - 1)
	                   			{
	                   				orderStrings[i] += "}";
	                   			}
	                   		}
	                   		// System.out.println(Arrays.toString(orderStrings));
	                   		
	                   		String content = ServerUtils.getFileContents("web/flower_shop/admin.html");
	                     	String table = "<table>";
	                     	
	                     	for (String orderString : orderStrings)
	                   		{
	                     		table += "<tr><th>ID</th>"
		                     			+ "<th>Delivery Address</th>"
		                     			+ "<th>Customer Email</th>"
		                     			+ "<th>Order Picked Up?</th>"
		                     			+ "<th>Order Delivered?</th></tr>";
	                     		
	                   			Order order = gson.fromJson(orderString.trim(), Order.class);
	                   			table += "<tr><td>" + order.getId() + "</td>";
	                     		table += "<td>" + order.getAddress() + "</td>";
	                     		table += "<td>" + order.getEmailaddress() + "</td>";
	                     		if (order.isPickedup())
	                     		{
	                     			table += "<td>" + order.isPickedup() + "</td>";
	                     		}
	                     		else
	                     		{
	                     			table += "<td><form action='/delivery_pickup' method='POST'><input type='hidden' name='id' value='" + order.getId() + "'>";
	                     			table += "<input type='submit' value='Confirm'></form></td>";
	                     		}
	                     		table += "<td>" + order.isDelivered() + "</td></tr>";
	                     		
	                     		/**
	                     		 *  Make another HTTP Request to see all drivers with delivery "bids" on each order
	                     		 */
	                     		getURL = driversGuildURL + "/orders/" + order.getId() + "/deliveries";
	                     		
	                     		URL deliveryURL = new URL(getURL);
			               		HttpURLConnection deliveryCon = (HttpURLConnection) deliveryURL.openConnection();
			               		 
			               		String deliveryResult = ServerUtils.inputStreamToString(deliveryCon.getInputStream());
			               		deliveryResult = deliveryResult.substring(1, deliveryResult.length() - 1);
	                     		if (deliveryResult.length() > 0)
	                     		{			               		
				               		String[] deliveryStrings = deliveryResult.split("\\Q}\\E,\\Q{\\E");
				               		for (int i=0; i<deliveryStrings.length; i++)
			                   		{
			                   			if (i > 0)
			                   			{
			                   				deliveryStrings[i] = "{" + deliveryStrings[i];
			                   			}
			                   			
			                   			if (i < deliveryStrings.length - 1)
			                   			{
			                   				deliveryStrings[i] += "}";
			                   			}
			                   		}
				               		
				               		// System.out.println(Arrays.toString(deliveryStrings));
				               		table += "<tr><th>Driver Phone</th><th>Assigned?</th></tr>";
		                     		for (String deliveryString : deliveryStrings)
		                     		{		          
		                     			if (deliveryString != "[]")
		                     			{
			                     			Delivery delivery = gson.fromJson(deliveryString.trim(), Delivery.class);
			                     			table += "<tr><td>" + delivery.getDriverphone() + "</td>";
			                     			table += "<td>" + delivery.isAccepted() + "</td></tr>";
		                     			}
		                     		}
	                     		}
	                   		}
	                     		                     	
	                     	table += "</table>";
	                     	
	                     	return new Response(200, content.replaceAll("<table></table>", table));
	                    }
                     	catch (Exception e)
                     	{
                     		e.printStackTrace();
                     		return new Response(500);
                     	}               		 
               		 }
                     else 
                     {
                    	 List<Shop> shops = shopDAO.getShops();
                    	 if (shops == null)
                    	 {
                    		 return new Response(500);
                    	 }
                    	 String content = ServerUtils.getFileContents("web/flower_shop/login.html");
                    	 String options = "";
                    	 for (Shop shop : shops)
                    	 {
                    		options += "<option value='" + shop.getID() + "'>" + shop.getName().replace('+', ' ') + "</option>"; 
                    	 }              
                    	 content = content.replaceAll("<option value='0'>Error loading locations</option>", options);
                    	 return new Response(200, content);
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
                                                 	                    	
                    	 String query = ServerUtils.inputStreamToString(r.getBody());
                    	 Map<String, String> values = ServerUtils.getQueryMap(query);
                    	 System.out.println(values.toString());
                    	 
                    	 if (values.get("shop_id") != null
                    			 && values.get("email") != null
                    			 && values.get("customer_name") != null
                    			 && values.get("address") != null)
                    	 {
                    		 try
                    		 {
                    			 Order order = new Order();
                    			 order.setShopid(values.get("shop_id"));
                    			 order.setAddress(values.get("address") + "," + values.get("city") + "," + values.get("state"));
                    			 order.setDelivered(false);
                    			 order.setEmailaddress(values.get("email"));
                    			 order.setDeliverylocation(new Location());
                    			                     			 
                    			 Gson gson = new Gson();
                    			 String jsonString = gson.toJson(order);
                    			 
                        		 String postURL = driversGuildURL + "/orders/";
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
		                    		System.out.println("Post parameters : " + jsonString);
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
									 return new Response(200, "Order placed successfully.");
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
                         try 
                         {                        	 
                        	 String query = ServerUtils.inputStreamToString(r.getBody());
                        	 Map<String, String> values = ServerUtils.getQueryMap(query);
                        	 System.out.println(values.toString());
                        	 
                        	 Order order = new Order();
                        	 order.setId(Integer.parseInt(values.get("id")));
                        	 order.setPickedup(true);
                        	 
                        	 Gson gson = new Gson();
                        	 String jsonString = gson.toJson(order);
                        	
                        	 // TODO Send event to Drivers' Guild
                        	 String postURL = driversGuildURL + "/orders/pickup";
                    		 URL obj = new URL(postURL);
                    		 HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    		 
                    		 con.setRequestMethod("POST");
                    		 con.setDoOutput(true);
                    		 con.setRequestProperty("Content-Type", "application/json");
                    		 
                    		 DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    		 wr.writeBytes(jsonString);
                   		 
                    		 wr.flush();
                    		 wr.close();
                    		 
                    		 
                       		 	int responseCode = con.getResponseCode();
	                    		System.out.println("\nSending 'POST' request to URL : " + postURL);
	                    		System.out.println("Post parameters : " + jsonString);
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
                       		
                    		                         		                        		                         		 							
							 if (con.getResponseCode() == 200)
							 {
								 return new Response(200, "Order pickup marked successfully.");
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
             *
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
            */
            
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
