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

    public List<Order> getOrders(String id) {
        try {
            List<Order> orders = new ArrayList<>();
            connection = getConnection();
            PreparedStatement s = connection.prepareStatement("SELECT * FROM flowershop.orders WHERE shopid = ?");
            s.setString(1, id);
            ResultSet rs = s.executeQuery();
            while(rs.next()){
                Order order = new Order();
                order.setShopid(id);
                order.setId(rs.getInt("id"));
                Location deliveryLocation = new Location();
                deliveryLocation.setLatitude(rs.getFloat("latitude"));
                deliveryLocation.setLongitude(rs.getFloat("longitude"));
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
            PreparedStatement s = connection.prepareStatement("INSERT INTO flowershop.orders(shopid, latitude, longitude) VALUES (?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            s.setString(1, order.getShopid());
            s.setFloat(2, order.getDeliverylocation().getLatitude());
            s.setFloat(3, order.getDeliverylocation().getLongitude());
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
}
