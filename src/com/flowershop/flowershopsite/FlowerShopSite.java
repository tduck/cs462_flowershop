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
