package com.google.estimator.dto;

public class Region {
	String continent;
	String regionName;
	String resourceGroup;
	String price;
	String operational;
	String operationalGroup;
	String operationalPrice;
	double size;
	@Override
	public String toString() {
		return "Region [continent=" + continent + ", " + "regionName=" + regionName + ", " + "resourceGroup=" + resourceGroup + ", price=" + price + "]";
	}



	public double getSize() {
		return size;
	}



	public void setSize(double size) {
		this.size = size;
	}



	public String getOperational() {
		return operational;
	}



	public void setOperational(String operational) {
		this.operational = operational;
	}



	public String getOperationalGroup() {
		return operationalGroup;
	}



	public void setOperationalGroup(String operationalGroup) {
		this.operationalGroup = operationalGroup;
	}



	public String getOperationalPrice() {
		return operationalPrice;
	}



	public void setOperationalPrice(String operationalPrice) {
		this.operationalPrice = operationalPrice;
	}



	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getResourceGroup() {
		return resourceGroup;
	}

	public void setResourceGroup(String resourceGroup) {
		this.resourceGroup = resourceGroup;
	}

	public String getContinent() {
		return continent;
	}

	public void setContinent(String continent) {
		this.continent = continent;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

}
