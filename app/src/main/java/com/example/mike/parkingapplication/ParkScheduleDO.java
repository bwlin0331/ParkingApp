package com.example.mike.parkingapplication;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "parkinggarage-mobilehub-1238327462-ParkSchedule")

public class ParkScheduleDO {
    private Double _times;
    private Double _spotsTaken;
    private Boolean _full;
    private Map<String, String> _licencePlateMap;
    private Set<Double> _parkingSpots;

    @DynamoDBHashKey(attributeName = "Times")
    @DynamoDBAttribute(attributeName = "Times")
    public Double getTimes() {
        return _times;
    }

    public void setTimes(final Double _times) {
        this._times = _times;
    }
    @DynamoDBRangeKey(attributeName = "SpotsTaken")
    @DynamoDBAttribute(attributeName = "SpotsTaken")
    public Double getSpotsTaken() {
        return _spotsTaken;
    }

    public void setSpotsTaken(final Double _spotsTaken) {
        this._spotsTaken = _spotsTaken;
    }
    @DynamoDBAttribute(attributeName = "Full")
    public Boolean getFull() {
        return _full;
    }

    public void setFull(final Boolean _full) {
        this._full = _full;
    }
    @DynamoDBAttribute(attributeName = "LicencePlateMap")
    public Map<String, String> getLicencePlateMap() {
        return _licencePlateMap;
    }

    public void setLicencePlateMap(final Map<String, String> _licencePlateMap) {
        this._licencePlateMap = _licencePlateMap;
    }
    @DynamoDBAttribute(attributeName = "ParkingSpots")
    public Set<Double> getParkingSpots() {
        return _parkingSpots;
    }

    public void setParkingSpots(final Set<Double> _parkingSpots) {
        this._parkingSpots = _parkingSpots;
    }

}
