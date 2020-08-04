package com.collabcreation.kapda.model;

import java.io.Serializable;

public class Address implements Serializable {
    String receiverName, phoneNo, addressId, city, address, state, postcode, landMark;

    public Address() {
    }

    public Address(String addressId, String city, String address, String state, String postcode, String landMark, String receiverName, String phoneNo) {
        this.city = city;
        this.phoneNo = phoneNo;
        this.receiverName = receiverName;
        this.addressId = addressId;
        this.state = state;
        this.address = address;
        this.landMark = landMark;
        this.postcode = postcode;
    }


    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getCity() {
        return city;
    }

    public String getLandMark() {
        return landMark;
    }

    public void setLandMark(String landMark) {
        this.landMark = landMark;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}
