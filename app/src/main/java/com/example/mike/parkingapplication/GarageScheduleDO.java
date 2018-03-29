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
    private Double _startTime;
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
    @DynamoDBAttribute(attributeName = "StartTime")
    public Double getStartTime() {
        return _startTime;
    }

    public void setStartTime(final Double _startTime) {
        this._startTime = _startTime;
    }
    @DynamoDBAttribute(attributeName = "parkID")
    public Double getParkID() {
        return _parkID;
    }

    public void setParkID(final Double _parkID) {
        this._parkID = _parkID;
    }

}
