package com.flowershop.driversguild.dao;

import com.flowershop.model.Location;
import com.flowershop.model.Order;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeffrey on 4/11/15.
 */
public class OrderDAO {
    Connection connection;

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://jflowershop.cii4ylx5phxt.us-west-1.rds.amazonaws.com/" +
                "flowershop?user=" + System.getenv("username") + "&password=" + System.getenv("password"));
    }

    public List<Order> getOrders(String shopid) {
        try {
            List<Order> orders = new ArrayList<>();
            connection = getConnection();
            PreparedStatement s = connection.prepareStatement("SELECT * FROM flowershop.orders WHERE shopid = ?");
            s.setString(1, shopid);
            ResultSet rs = s.executeQuery();
            while(rs.next()){
                Order order = new Order();
                order.setShopid(shopid);
                order.setId(rs.getInt("id"));
                order.setAddress(rs.getString("address"));
                order.setEmailaddress(rs.getString("emailaddress"));
                order.setDriverphone(rs.getString("driverphone"));
                order.setDelivered(rs.getBoolean("delivered"));
                order.setPickedup(rs.getBoolean("pickedup"));
                Location deliveryLocation = new Location();
                deliveryLocation.setLat(rs.getFloat("latitude"));
                deliveryLocation.setLng(rs.getFloat("longitude"));
                order.setDeliverylocation(deliveryLocation);
                orders.add(order);
            }
            connection.close();
            return orders;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Order createOrder(Order order) {
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement s = connection.prepareStatement("INSERT INTO flowershop.orders(shopid, latitude, longitude, emailaddress, driverphone, address, delivered, pickedup) VALUES (?,?,?,?,?,?,0,0)", PreparedStatement.RETURN_GENERATED_KEYS);
            s.setString(1, order.getShopid());
            s.setFloat(2, order.getDeliverylocation().getLat());
            s.setFloat(3, order.getDeliverylocation().getLng());
            s.setString(4, order.getEmailaddress().replaceAll("%40", "@"));
            s.setString(5, order.getDriverphone());
            s.setString(6, order.getAddress());
            s.executeUpdate();
            ResultSet rs = s.getGeneratedKeys();
            rs.next();
            order.setId(rs.getInt(1));
            connection.commit();
            return order;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            return null;
        }
    }
    
    public Order setOrderComplete(Order order)
    {
        try 
        {
			connection = getConnection();
	        connection.setAutoCommit(false);
	        PreparedStatement s = connection.prepareStatement("UPDATE flowershop.orders SET delivered = ? WHERE id = ?");
	        s.setBoolean(1, order.isDelivered()); 
	        s.setInt(2, order.getId());
	        System.out.println(s.toString());
	        s.executeUpdate();
	        connection.commit();
	        connection.close();	        
	        return order;
        } 
        catch (SQLException e) 
        {
			e.printStackTrace();
			return null;
		}
    }
    
    public Order pickupOrder(Order order)
    {
        try 
        {
			connection = getConnection();
	        connection.setAutoCommit(false);
	        PreparedStatement s = connection.prepareStatement("UPDATE flowershop.orders SET pickedup = ? WHERE id = ?");
	        s.setBoolean(1, order.isPickedup()); 
	        s.setInt(2, order.getId());
	        System.out.println(s.toString());
	        s.executeUpdate();
	        connection.commit();
	        connection.close();	        
	        return order;
        } 
        catch (SQLException e) 
        {
			e.printStackTrace();
			return null;
		}
    }
}
