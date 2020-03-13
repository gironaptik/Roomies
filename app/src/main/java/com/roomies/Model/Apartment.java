package com.roomies.Model;

public class Apartment {

    String address;
    String imageUrl;
    String name;

    public Apartment() {
    }

    public Apartment(String address, String imageUrl, String name) {
        this.address = address;
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }
}
