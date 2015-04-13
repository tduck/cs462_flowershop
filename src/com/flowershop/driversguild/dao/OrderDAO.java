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

    public List<Order> getOrders(String id) {
        try {
            List<Order> orders = new ArrayList<>();
            connection = DriverManager.getConnection("jdbc:mysql://jflowershop.cii4ylx5phxt.us-west-1.rds.amazonaws.com/" +
                    "flowershop?user=&password=");
            PreparedStatement s = connection.prepareStatement("SELECT FROM flowershop.orders WHERE shopid = ?");
            s.setString(1, id);
            ResultSet rs = s.executeQuery();
            while(rs.next()){
                Order order = new Order();
                order.setShopId(id);
                Location deliveryLocation = new Location();
                deliveryLocation.setLatitude(rs.getFloat("latitude"));
                deliveryLocation.setLongitude(rs.getFloat("longitude"));
                order.setDeliveryLocation(deliveryLocation);
                orders.add(order);
            }
            connection.close();
            return orders;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
