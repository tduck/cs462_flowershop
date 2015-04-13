package com.flowershop.model;

/**
 * Created by jeffrey on 4/12/15.
 */
public class Order {
    private String shopId;
    private Location deliveryLocation;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public Location getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(Location deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }
}
