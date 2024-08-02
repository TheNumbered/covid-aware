package com.example.covidaware;

public class Location {
    private String locationName;
    private String address;
    private double latitude;
    private double longitude;
    private String id;
    private int infected;

    public Location(String id,String locationName, String address, double latitude, double longitude, int infected) {
        this.id =id;
        this.locationName = locationName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.infected = infected;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getInfected() {
        return infected;
    }

    public String getId() {return id;}
}
