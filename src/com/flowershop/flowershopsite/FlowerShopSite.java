package com.flowershop.flowershopsite;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
		// TODO Auto-generated method stub
		
		JServer s;
        try {
            s = new JServer(8001);
            s.start();
                       
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
                     return new Response(200, ServerUtils.getFileContents("web/flower_shop_main.html"));
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
                        	 // TODO Send location info to Drivers' Guild

                    		 try
                    		 {
                    			 query += "&id=" + flowerShopID;
                        		 String postURL = driversGuildURL + "/shops/";
                        		 URL obj = new URL(postURL);
                        		 HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                        		 
                        		 con.setRequestMethod("POST");
                        		 con.setDoOutput(true);
                        		 
                        		 DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                        		 wr.writeBytes(query);
                        		 
                        		 wr.flush();
                        		 wr.close();
                        		 
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
                     return new Response(200, ServerUtils.getFileContents("web/flower_shop_add.html"));
                }
            });
            
            
                       
            s.register("/login", new JHandler() {
                @Override
                public Response handle(Request r) {
                     if(r.getMethod().equals("POST")){
                         try 
                         {
                        	// Parse query string
 	                        String query = ServerUtils.inputStreamToString(r.getBody());
                         	Map<String, String> values = ServerUtils.getQueryMap(query);
                         	
                         	if (values.get("password") != null && values.get("password").equals("1234"))
                         	{
                         		loggedIn = true;
                         		currentShop = values.get("location");
                         		
                                // TODO Retrieve available bids for orders at the given flower shop location
                         		
                         		return new Response(200, ServerUtils.getFileContents("web/flower_shop_assign_bids.html"));
                         	}
                         	else
                         	{
                         		return new Response(200, "Incorrect login. Click your browser's Back button to try again.");
                         	}
                         } 
                         catch (Exception e) 
                         {
                             e.printStackTrace();
                         }
                     }
                     return new Response(200, ServerUtils.getFileContents("web/flower_shop_login.html"));
                }
            });
            
            s.register("/logout", new JHandler() {
                @Override
                public Response handle(Request r) {
                     loggedIn = false;
                     return new Response(200, ServerUtils.getFileContents("web/flower_shop_main.html"));
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
                         try {
                        	
                        	 String orderID = UUID.randomUUID().toString();
                        	 
                        	 String result = "<html><head></head><body>";
                        	 result += "<p>Thank you, your order has been processed. Your order ID is "
                        			 + orderID.replaceAll("-", "") + "</p>";
                        	 
                        	 String query = ServerUtils.inputStreamToString(r.getBody());
                        	 Map<String, String> values = ServerUtils.getQueryMap(query);

                             result += "<p>Location: " + values.get("location") + "<br>";
                             result += "Customer: " + values.get("first_name") + " " + values.get("last_name") + "<br>";
                             result += "Address: " + values.get("address") + "<br>";
                             result += "Email: " + values.get("email");
                        	 
                        	 // TODO Send event to Drivers' Guild

                        	 result += "</p><a href=''>Home</a></body></html>";
                             return new Response(200, result);
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
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
        }
        
        catch (IOException e) {
        	e.printStackTrace();
        	return;
		}
	}
}
