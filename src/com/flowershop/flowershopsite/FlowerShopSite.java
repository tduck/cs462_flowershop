package com.flowershop.flowershopsite;

import java.io.IOException;

import com.flowershop.ServerUtils;
import com.jeffrey.server.JHandler;
import com.jeffrey.server.JServer;
import com.jeffrey.server.Request;
import com.jeffrey.server.Response;

public class FlowerShopSite {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		JServer s;
        try {
            s = new JServer(8000);
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
                     return new Response(405);
                }
            });
        }
        catch (IOException e) {
        	e.printStackTrace();
        	return;
		}
	}
}
