package com.jeffrey.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

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
}
