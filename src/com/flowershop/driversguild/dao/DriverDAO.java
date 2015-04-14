package com.flowershop.driversguild.dao;

import com.flowershop.ServerUtils;
import com.flowershop.driversguild.DriversGuild;
import com.flowershop.model.*;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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
    DeliveryDAO deliveryDAO = new DeliveryDAO();
    
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

    public List<Driver> getAvailableDrivers(Connection connection){
        List<Driver> drivers = new ArrayList<>();
        try{
            PreparedStatement s = connection.prepareStatement("SELECT * FROM flowershop.drivers" +
                    " WHERE available = true AND lastlat IS NOT NULL");
            ResultSet rs = s.executeQuery();
            while(rs.next()){
                Driver driver = new Driver();
                Location location = new Location();
                location.setLat(rs.getFloat("lastlat"));
                location.setLng(rs.getFloat("lastlong"));
                driver.setLastLocation(location);
                driver.setPhone(rs.getString("phone"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drivers;
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
            DriversGuild.flagChanged();
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
            DriversGuild.flagChanged();
            return driver;
        } 
    	catch (SQLException e) 
    	{
            e.printStackTrace();
            return null;
        }
    }

    public void assignDrivers() {
        try {
            connection = getConnection();
            List<DeliveryInfo> info = deliveryDAO.getUndeliveredOrders(connection);
            List<Driver> drivers = getAvailableDrivers(connection);

            for(DeliveryInfo d: info){
                double min = Double.MAX_VALUE;
                Driver best = null;
                for(Driver driver: drivers){
                    double m = ServerUtils.GreatCircleDistance(d.getShopLocation().getLat(), driver.getLastLocation().getLat(),
                            d.getShopLocation().getLng(), driver.getLastLocation().getLng());
                    if(m < min){
                        min = m;
                        best = driver;
                    }
                }
                drivers.remove(best);
                assign(best, d);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void assign(Driver d, DeliveryInfo i){
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE flowershop.deliveries SET driverphone = ?");
            ps.setString(1, d.getPhone());
            ps.executeUpdate();
            Twilio.sendMessage("+1" + d.getPhone(), "Would you like to accept a delivery from " + String.valueOf(i.getShopLocation().getLat()) + ", "
            + String.valueOf(i.getShopLocation().getLng()) + "? Respond with an accept message and the id " + String.valueOf(i.getOrderId()) + " to accept.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return;
    }

    private static class Twilio {

        private final static String ACCOUNT_SID = "AC15f0c7bd26137ed319deffa2022b84cb";
        private final static String AUTH_TOKEN = "1eba8cbc1c6de15ad1c454b55f3aebf3";
        /*
         * Used in Twilio SMS event generation
         */
        public static void sendMessage(String phone, String body)
        {
            try
            {
                TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

                // Build a filter for the MessageList
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("Body", body));
                params.add(new BasicNameValuePair("To", phone));
                params.add(new BasicNameValuePair("From", "+13852194380"));

                MessageFactory messageFactory = client.getAccount().getMessageFactory();
                Message message = messageFactory.create(params);
                System.out.println(message.getSid());

            }
            catch (TwilioRestException e)
            {
                e.printStackTrace();
                ServerUtils.addToLog(e.getErrorMessage());
            }
        }
    }
}