package com.flowershop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
}
