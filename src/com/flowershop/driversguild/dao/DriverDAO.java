package com.flowershop.driversguild.dao;

import com.flowershop.model.Driver;
import com.flowershop.model.Location;
import com.flowershop.model.Order;
import com.flowershop.model.Shop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeffrey on 4/12/15.
 */
public class DriverDAO {
    
	Connection connection;
    
	public DriverDAO() {}
	
	public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://jflowershop.cii4ylx5phxt.us-west-1.rds.amazonaws.com/" +
                "flowershop?user=" + System.getenv("username") + "&password=" + System.getenv("password"));
    }
	
	public List<Driver> getAllDrivers()
	{
		try 
		{
		  List<Driver> driverList = new ArrayList<Driver>();
		  connection = getConnection();
          PreparedStatement s = connection.prepareStatement("SELECT * FROM flowershop.drivers");
          ResultSet rs = s.executeQuery();
          while (rs.next())
          {
        	  Driver driver = new Driver();
        	  driver.setPhone(rs.getString("phone"));
        	  driver.setName(rs.getString("name"));
        	  Location location = new Location();
        	  location.setLat(rs.getFloat("lastlat"));
        	  location.setLng(rs.getFloat("lastlong"));
        	  driver.setClockedin(rs.getBoolean("clockedin"));
        	  driver.setAvailable(rs.getBoolean("available"));
        	  driverList.add(driver);
          }
          connection.close();
          return driverList;			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public Driver checkin(Driver driver)
	{
		try {
            connection = getConnection();
            PreparedStatement s = connection.prepareStatement("UPDATE flowershop.drivers SET lastlat = ?, lastlong = ? WHERE id = ?");
            s.setFloat(1, driver.getLastLocation().getLat());
            s.setFloat(2, driver.getLastLocation().getLng());
            s.setString(3, driver.getId());
            
            System.out.println(s);
            s.executeUpdate();
            
            connection.close();
            return driver;
        } 
		catch (SQLException e) 
		{
            e.printStackTrace();
            return null;
        }
	}
	
	public Driver getDriverByPhone(String phone)
	{
		try {
            Driver driver;
            connection = getConnection();
            PreparedStatement s = connection.prepareStatement("SELECT * FROM flowershop.drivers WHERE number = ?");
            s.setString(1, phone);
            ResultSet rs = s.executeQuery();
            if (rs.next())
            {
            	driver = new Driver();
                driver.setPhone(phone);
                driver.setName(rs.getString("phone"));
                Location location = new Location();
                location.setLat(rs.getFloat("lastlat"));
                location.setLng(rs.getFloat("lastlong"));
                driver.setAvailable(rs.getBoolean("available"));
                driver.setClockedin(rs.getBoolean("clockedin"));
                driver.setLastLocation(location);
            }
            else
            {
            	driver = null;
            }
            connection.close();
            return driver;
        } 
		catch (SQLException e) 
		{
            e.printStackTrace();
            return null;
        }
	}

    public Driver createDriver(Driver driver){
        try {
            connection = getConnection();
            PreparedStatement s = connection.prepareStatement("INSERT INTO flowershop.drivers(phone, name, lastlat, lastlong, available, clockedin) VALUES(?, ?, ?, ?, ?, ?)");
            s.setString(1, driver.getPhone());
            s.setString(2, driver.getName());
            s.setFloat(3, driver.getLastLocation().getLat());
            s.setFloat(4, driver.getLastLocation().getLng());
            s.setBoolean(5, driver.isAvailable());
            s.setBoolean(6, driver.isClockedin());
            s.executeUpdate();
            connection.close();
            return driver;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Driver clockIn(Driver driver)
    {
    	try 
    	{
            connection = getConnection();
            PreparedStatement s = connection.prepareStatement("UPDATE flowershop.drivers SET clockedin = ?, available = ? WHERE phone = ?");
            s.setBoolean(1, driver.isClockedin());
            s.setBoolean(2, driver.isAvailable());
            s.setString(3, driver.getPhone());
            s.executeUpdate();
            connection.close();
            return driver;
        } 
    	catch (SQLException e) 
    	{
            e.printStackTrace();
            return null;
        }
    }
}
