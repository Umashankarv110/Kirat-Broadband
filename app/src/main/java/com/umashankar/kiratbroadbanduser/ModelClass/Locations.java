package com.umashankar.kiratbroadbanduser.ModelClass;

public class Locations {

    int locationId;
    String locationName;

    public Locations() {
    }

    public Locations(int locationId, String locationName) {
        this.locationId = locationId;
        this.locationName = locationName;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Override
    public String toString() {
        return this.getLocationName(); // What to display in the Spinner list.
    }
}
