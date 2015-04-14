package com.flowershop.model;

/**
 * Created by jeffrey on 4/12/15.
 */
public class Driver {
	private String id;
    private String phone;
    private String name;
    private Location lastLocation;
    private boolean available;
    private boolean clockedin;

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public boolean isClockedin() {
		return clockedin;
	}

	public void setClockedin(boolean clockedin) {
		this.clockedin = clockedin;
	}

	public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }
}
