package com.flowershop.model;

/**
 * Created by jeffrey on 4/14/15.
 */
public class DeliveryInfo {
    private int id;
    private Location shopLocation;
    private int orderId;

    public Location getShopLocation() {
        return shopLocation;
    }

    public void setShopLocation(Location shopLocation) {
        this.shopLocation = shopLocation;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
