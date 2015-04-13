package com.flowershop.model;

public class Shop {
	
	private String id;
	private String name;
	private Location location;
		
	public Shop() {}
	
	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public String getName()	{
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
