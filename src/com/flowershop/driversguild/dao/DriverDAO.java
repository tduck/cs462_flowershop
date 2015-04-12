package com.flowershop.driversguild.dao;

import com.flowershop.model.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by jeffrey on 4/12/15.
 */
public class DriverDAO {
    Connection connection;
    public DriverDAO(){

    }

    public Driver createDriver(Driver driver){
        try {
            connection = DriverManager.getConnection("jdbc:mysql://jflowershop.cii4ylx5phxt.us-west-1.rds.amazonaws.com/" +
                    "flowershop?user=&password=");
            PreparedStatement s = connection.prepareStatement("INSERT INTO flowershop.drivers(number, name) VALUES(?, ?)");
            s.setString(1, driver.getPhone());
            s.setString(2, driver.getName());
            s.executeUpdate();
            connection.close();
            return driver;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
