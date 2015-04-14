package com.flowershop.driversguild.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.flowershop.model.Delivery;
import com.flowershop.model.DeliveryInfo;
import com.flowershop.model.Location;

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

	String undeliveredSql = "SELECT s.longitude, s.latitude, o.id AS orderid FROM flowershop.orders o, flowershop.shops s " +
			"WHERE o.delivered = false AND s.id = o.shopid";
	public List<DeliveryInfo> getUndeliveredOrders(Connection connection) {
		List<DeliveryInfo> deliveries = new ArrayList<>();
		try {
			PreparedStatement ps = connection.prepareStatement(undeliveredSql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				Location location = new Location();
				location.setLng(rs.getFloat("longitude"));
				location.setLat(rs.getFloat("latitude"));
				DeliveryInfo info = new DeliveryInfo();
				info.setShopLocation(location);
				info.setOrderId(rs.getInt("orderid"));
				deliveries.add(info);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return deliveries;
	}

}
