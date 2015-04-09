package com.flowershop.flowershopsite;

import java.io.IOException;
import java.util.Map;

import com.flowershop.ServerUtils;
import com.jeffrey.server.JHandler;
import com.jeffrey.server.JServer;
import com.jeffrey.server.Request;
import com.jeffrey.server.Response;

public class FlowerShopSite {

	private static boolean loggedIn = false;
	private static String currentShop = "";
	
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
            
            /*
             * Delivery Ready Event Producer
             */
            s.register("/place_order", new JHandler() {
                @Override
                public Response handle(Request r) {
                     if(r.getMethod().equals("POST")){
                         try {
                        	 
                        	 // TODO Notify Drivers Guild of event
                        	 
                        	 
                             return new Response(200).pipe(r.getBody());
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
                        	 
                        	 // TODO Notify Drivers Guild of event
                        	 
                        	 
                             return new Response(200).pipe(r.getBody());
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                     return new Response(405);
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
            
            s.register("/assign_bids", new JHandler() {
                @Override
                public Response handle(Request r) {
                	if (loggedIn == true && r.getMethod().equals("POST")){
                         try {
                        	 
                // TODO Retrieve available bids for orders at the given flower shop location
                        	 
                             return new Response(200).pipe(r.getBody());
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                	}
                	return new Response(200, ServerUtils.getFileContents("web/flower_shop_main.html"));
                }
            });
            
            
            
        }
        
        catch (IOException e) {
        	e.printStackTrace();
        	return;
		}
	}
}
