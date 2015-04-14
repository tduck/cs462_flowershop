package com.flowershop.driversguild.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.flowershop.model.Location;
import com.flowershop.model.Shop;

public class ShopDAO {
	
	private Connection connection;
	
	public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://jflowershop.cii4ylx5phxt.us-west-1.rds.amazonaws.com/" +
                "flowershop?user=" + System.getenv("username") + "&password=" + System.getenv("password"));
    }
	
	public List<Shop> getShops()
	{
		try 
		{
		  List<Shop> shopList = new ArrayList<Shop>();
		  connection = getConnection();
          PreparedStatement s = connection.prepareStatement("SELECT * FROM flowershop.shops");
          ResultSet rs = s.executeQuery();
          while (rs.next())
          {
        	  Shop shop = new Shop();
        	  shop.setID(rs.getString("id"));
        	  shop.setName(rs.getString("name"));
        	  Location location = new Location();
        	  location.setLat(rs.getFloat("latitude"));
        	  location.setLng(rs.getFloat("longitude"));
        	  shop.setLocation(location);
        	  shopList.add(shop);
          }
          connection.close();
          return shopList;			
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public Shop createShop(Shop shop)
	{
		try 
		{
            connection = getConnection();
            PreparedStatement s = connection.prepareStatement("INSERT INTO flowershop.shops(id, name, latitude, longitude) VALUES(?, ?, ?, ?)");

            s.setString(1, shop.getID());
            s.setString(2, shop.getName());
            s.setFloat(3, shop.getLocation().getLat());
            s.setFloat(4, shop.getLocation().getLng());

            s.executeUpdate();
            connection.close();
            return shop;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
	}
}
