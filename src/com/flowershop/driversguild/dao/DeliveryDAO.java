package com.flowershop.driversguild.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.flowershop.model.Delivery;

public class DeliveryDAO {
	
	Connection connection;

	public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://jflowershop.cii4ylx5phxt.us-west-1.rds.amazonaws.com/" +
                "flowershop?user=" + System.getenv("username") + "&password=" + System.getenv("password"));
    }
	
	public List<Delivery> getDeliveries(int orderID)
	{
		try 
		{
			List<Delivery> deliveries = new ArrayList<Delivery>();
			connection = getConnection();
			PreparedStatement s = connection.prepareStatement("SELECT * FROM flowershop.deliveries WHERE orderid = ?");
			s.setInt(1, orderID);
			
			 ResultSet rs = s.executeQuery();
	         while(rs.next())
	         {
	        	 Delivery delivery = new Delivery();
	        	 delivery.setOrderid(orderID);
	        	 delivery.setId(rs.getInt("id"));
	        	 delivery.setAccepted(rs.getBoolean("accepted"));
	        	 delivery.setDriverphone(rs.getString("driverphone"));
	        	 deliveries.add(delivery);	         
	         }			
	         connection.close();
	         return deliveries;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return null;
		}
	}

}
