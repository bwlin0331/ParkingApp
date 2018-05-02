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

@DynamoDBTable(tableName = "parkinggarage-mobilehub-1238327462-PurchaseHistory")

public class PurchaseHistoryDO {
    private String _userId;
    private List<String> _date;
    private List<String> _price;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBAttribute(attributeName = "Date")
    public List<String> getDate() {
        return _date;
    }

    public void setDate(final List<String> _date) {
        this._date = _date;
    }
    @DynamoDBAttribute(attributeName = "Price")
    public List<String> getPrice() {
        return _price;
    }

    public void setPrice(final List<String> _price) {
        this._price = _price;
    }

}
