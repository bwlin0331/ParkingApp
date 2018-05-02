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

@DynamoDBTable(tableName = "parkinggarage-mobilehub-1238327462-GarageSchedule")

public class GarageScheduleDO {
    private String _userId;
    private Double _endTime;
    private String _licencePlate;
    private Double _price;
    private Double _startTime;
    private Boolean _checkedIn;
    private Double _parkID;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBAttribute(attributeName = "EndTime")
    public Double getEndTime() {
        return _endTime;
    }

    public void setEndTime(final Double _endTime) {
        this._endTime = _endTime;
    }
    @DynamoDBAttribute(attributeName = "LicencePlate")
    public String getLicencePlate() {
        return _licencePlate;
    }

    public void setLicencePlate(final String _licencePlate) {
        this._licencePlate = _licencePlate;
    }
    @DynamoDBAttribute(attributeName = "Price")
    public Double getPrice() {
        return _price;
    }

    public void setPrice(final Double _price) {
        this._price = _price;
    }
    @DynamoDBAttribute(attributeName = "StartTime")
    public Double getStartTime() {
        return _startTime;
    }

    public void setStartTime(final Double _startTime) {
        this._startTime = _startTime;
    }
    @DynamoDBAttribute(attributeName = "checkedIn")
    public Boolean getCheckedIn() {
        return _checkedIn;
    }

    public void setCheckedIn(final Boolean _checkedIn) {
        this._checkedIn = _checkedIn;
    }
    @DynamoDBAttribute(attributeName = "parkID")
    public Double getParkID() {
        return _parkID;
    }

    public void setParkID(final Double _parkID) {
        this._parkID = _parkID;
    }

}
