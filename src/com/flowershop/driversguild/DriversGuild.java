package com.flowershop.driversguild;

import java.io.IOException;
import java.util.Scanner;

import com.flowershop.ServerUtils;
import com.flowershop.driversguild.dao.DriverDAO;
import com.flowershop.model.Driver;
import com.google.gson.Gson;
import com.jeffrey.server.JHandler;
import com.jeffrey.server.JServer;
import com.jeffrey.server.Request;
import com.jeffrey.server.Response;

public class DriversGuild {
    static DriverDAO driverDAO;

	public static void main(String[] args) {
		driverDAO = new DriverDAO();
		JServer s;
        try {
            s = new JServer(8080);
            s.start();


            s.register("/shops", new JHandler() {
                @Override
                public Response handle(Request request) {
                    return new Response(200, request.getURI().toString());
                }
            });

            s.register("/drivers", new JHandler() {
                @Override
                public Response handle(Request request) {
                    if(request.getMethod().equals("POST")){
                        Scanner scanner = new Scanner(request.getBody());
                        scanner.useDelimiter("\\A");
                        Gson gson = new Gson();
                        Driver driver;
                        try {
                            driver = gson.fromJson(scanner.next(), Driver.class);
                        } catch(Exception e){
                            return new Response(500);
                        }
                        driver = driverDAO.createDriver(driver);
                        if(driver == null){
                            return new Response(500);
                        } else{
                            return new Response(201).send(driver);
                        }
                    }
                    return new Response(405);
                }
            });

            s.register("/shops", new JHandler() {
                @Override
                public Response handle(Request request) {
                    return null;
                }
            });

            s.register("/orders", new JHandler() {
                @Override
                public Response handle(Request request) {
                    return null;
                }
            });
                       
            /*s.register("/main", new JHandler() {
                @Override
                public Response handle(Request r) {
                     if(r.getMethod().equals("POST")){
                         try {
                             return new Response(200).pipe(r.getBody());
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                     return new Response(405);
                }
            });*/
            
            
            /**
             * EVENT PRODUCERS (2):
             * 		- RFQ Delivery Ready Event Producer/Forward
             * 		- RFQ Bid Awarded Event Producer/Forward
             */
            
            /**
             * EVENT CONSUMERS (4)
             */
            
            /*
             * RFQ Delivery Ready Event Consumer
             * 
             * 		Will include:
             * 			- RFQ Delivery Ready Event Producer/Forward
             */
            /*s.register("/rfq_delivery_ready", new JHandler() {
                @Override
                public Response handle(Request r) {
                     if(r.getMethod().equals("POST")){
                         try {
                             return new Response(200).pipe(r.getBody());
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                     return new Response(405);
                }
            });*/
            
            /*
             * RFQ Bid Awarded Event Consumer
             * 
             * 		Will include:
             * 			- RFQ Bid Awarded Event Producer/Forward
             */
            /*s.register("/rfq_delivery_ready", new JHandler() {
                @Override
                public Response handle(Request r) {
                     if(r.getMethod().equals("POST")){
                         try {
                             return new Response(200).pipe(r.getBody());
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                     return new Response(405);
                }
            });*/
            
            /*
             * Delivery Pickup Event Consumer 
             */
            /*s.register("/delivery_pickup", new JHandler() {
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
            });*/
            
            /*
             * Delivery Complete Event Consumer
             */
            /*s.register("/delivery_complete", new JHandler() {
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
            });*/
            
            
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
