package com.flowershop.driversguild;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.flowershop.ServerUtils;
import com.flowershop.driversguild.dao.DeliveryDAO;
import com.flowershop.driversguild.dao.DriverDAO;
import com.flowershop.driversguild.dao.OrderDAO;
import com.flowershop.model.Delivery;
import com.flowershop.model.Driver;
import com.flowershop.model.Order;
import com.google.gson.Gson;
import com.jeffrey.server.JHandler;
import com.jeffrey.server.JServer;
import com.jeffrey.server.Request;
import com.jeffrey.server.Response;

public class DriversGuild {
    static DriverDAO driverDAO;
    static OrderDAO orderDAO;
    static DeliveryDAO deliveryDAO;

	public static void main(String[] args) {
		driverDAO = new DriverDAO();
        orderDAO = new OrderDAO();
        deliveryDAO = new DeliveryDAO();
        
		JServer s;
        try {
            s = new JServer(8080);
            s.start();

            s.register("/shops", new JHandler() {
                @Override
                public Response handle(Request request) {
                    if(request.getMethod().equals("GET")) {
                        String tail = request.getURI().toString().substring(request.getPath().length());
                        if(tail.indexOf("/") == 0)
                            tail = tail.substring(1);
                        
                        String id = tail.substring(0, tail.indexOf("/"));
                        if(tail.substring(tail.indexOf("/")).equals("/orders")){
                            List<Order> o = orderDAO.getOrders(id);
                            if(o == null)
                                return null;
                            return new Response(200).send(o);
                        }
                        return new Response(400);
                    }
                    return new Response(405);
                }
            });

            s.register("/drivers", new JHandler() {
                @Override
                public Response handle(Request request) {
                    if(request.getMethod().equals("POST")){
                    	String json = ServerUtils.inputStreamToString(request.getBody());
                        Gson gson = new Gson();
                        Driver driver;
                        try {
                            driver = gson.fromJson(json, Driver.class);
                        } catch(Exception e){
                        	e.printStackTrace();
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

            s.register("/orders", new JHandler() {
                @Override
                public Response handle(Request request) {
                	if(request.getMethod().equals("GET")) 
                	{
                        String tail = request.getURI().toString().substring(request.getPath().length());
                        if(tail.indexOf("/") == 0)
                            tail = tail.substring(1);
                        
                        System.out.println(tail);
                        String id = tail.substring(0, tail.indexOf("/"));
                        if(tail.substring(tail.indexOf("/")).equals("/deliveries")){
                            List<Delivery> o = deliveryDAO.getDeliveries(Integer.parseInt(id));
                            if(o == null)
                                return null;
                            return new Response(200).send(o);
                        }
                        return new Response(400);
                    }
                	else if(request.getMethod().equals("POST"))
                	{
                        Scanner scanner = new Scanner(request.getBody());
                        scanner.useDelimiter("\\A");
                        Gson gson = new Gson();
                        Order order;
                        try {
                            order = gson.fromJson(scanner.next(), Order.class);
                        } catch(Exception e){
                            return new Response(500);
                        }
                        if(order.getDeliverylocation() == null)
                            return new Response(400);
                        order = orderDAO.createOrder(order);
                        if(order == null)
                            return new Response(500);
                        else
                            return new Response(201).send(order);
                    } else{
                        return new Response(405);
                    }
                }
            });
            
            s.register("/orders/complete", new JHandler() {
            	@Override
                public Response handle(Request r) {
                     if(r.getMethod().equals("POST")){
						System.out.println(ServerUtils.inputStreamToString(r.getBody()));
						
						String json = ServerUtils.inputStreamToString(r.getBody());
						Gson gson = new Gson();
						Order order;
						try 
						{
						     order = gson.fromJson(json.trim(), Order.class);
						} 
						catch(Exception e){
						 	e.printStackTrace();
						    return new Response(500);
						}
						if (orderDAO.setOrderComplete(order.getId(), order.isDelivered()))
						{
							return new Response(200).pipe(r.getBody());
						}
						else 
						{
							System.out.println("500 section");
						    return new Response(500);
						}
					}
					return new Response(405);
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
