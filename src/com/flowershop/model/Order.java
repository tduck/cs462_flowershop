package com.flowershop.model;

/**
 * Created by jeffrey on 4/12/15.
 */
public class Order {
    private int id;
    private String shopid;
    private String emailaddress;
	private String driverphone;
    private String address;
    private boolean delivered;
    private boolean pickedup;
	private Location deliverylocation;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopId) {
        this.shopid = shopId;
    }

    public Location getDeliverylocation() {
        return deliverylocation;
    }

    public void setDeliverylocation(Location deliveryLocation) {
        this.deliverylocation = deliveryLocation;
    }
    
    public String getEmailaddress() {
		return emailaddress;
	}

	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}

	public String getDriverphone() {
		return driverphone;
	}

	public void setDriverphone(String driverphone) {
		this.driverphone = driverphone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	 public boolean isDelivered() {
		return delivered;
	}

	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	public boolean isPickedup() {
		return pickedup;
	}

	public void setPickedup(boolean pickedup) {
		this.pickedup = pickedup;
	}
}
