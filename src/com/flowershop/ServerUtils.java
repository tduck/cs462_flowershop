package com.flowershop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ServerUtils {

	public static String getFileContents(String pathname)
    {
		String result = "";
    	try
    	{
    		  FileInputStream fstream = new FileInputStream(pathname);
    		  DataInputStream in = new DataInputStream(fstream);
    		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		  String strLine;

    		  while ((strLine = br.readLine()) != null)   
    		  {
    			  result += strLine;
    		  }
    		  in.close();
    	}
    	catch (Exception e)
    	{
    		System.err.println("Error: " + e.getMessage());
    	}   
		return result;
    }
	
	public static void addToLog(String newContent)
	{
		String existingContent = getFileContents("log.xml");
		existingContent += "<item>\n" + newContent + "\n</item>\n";
		
		try
		{
			File file = new File("log.xml");
			if (!file.exists())
			{
				file.createNewFile();
			}
			FileWriter fstream = new FileWriter("log.xml");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(existingContent);
			
			out.close();
		}
		catch (Exception e) 
		{
			System.err.println("Error: " + e.getMessage());
		}		
	}
	
	public static void clearLog()
	{
		File file = new File("log.xml");
		file.delete();
	}
	
	public static String inputStreamToString(InputStream is)
	{
		Scanner s = new Scanner(is,"UTF-8");
		String result = s.useDelimiter("\\A").next();
		s.close();
		return result;
	}
	
	public static Map<String, String> getQueryMap(String query)
	{
	    String[] params = query.split("&");
	    Map<String, String> map = new HashMap<String, String>();
	    for (String param : params)
	    {
	    	String[] parts = param.split("=");
	    	if (parts.length > 1)
	    	{
	    		map.put(parts[0], parts[1]);
	    	}
	    }
	    return map;
	}
	
	public static int postJson(String postURL, String jsonString)
	{
		try
		{			
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
	  				 
	   		 return responseCode;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 500;
		}
	}
	
	public static double GreatCircleDistance(double x1, double y1, double x2, double y2) 
	{    
       /*************************************************************************
        * Compute using law of cosines
        *************************************************************************/
        // great circle distance in radians
        double angle1 = Math.acos(Math.sin(x1) * Math.sin(x2)
                      + Math.cos(x1) * Math.cos(x2) * Math.cos(y1 - y2));

        // convert back to degrees
        angle1 = Math.toDegrees(angle1);

        // each degree on a great circle of Earth is 60 nautical miles
        double distance1 = 60 * angle1;        
        return distance1;
   }
}
