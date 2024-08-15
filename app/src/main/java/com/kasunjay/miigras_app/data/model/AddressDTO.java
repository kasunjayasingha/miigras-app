package com.kasunjay.miigras_app.data.model;

public class AddressDTO {
    private long id;
    private String houseNumber;
    private String streetOne;
    private String streetTwo;
    private String village;
    private String city;
    private String district;
    private String postalCode;

    public AddressDTO(long id, String houseNumber, String streetOne, String streetTwo, String village, String city, String district, String postalCode) {
        this.id = id;
        this.houseNumber = houseNumber;
        this.streetOne = streetOne;
        this.streetTwo = streetTwo;
        this.village = village;
        this.city = city;
        this.district = district;
        this.postalCode = postalCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getStreetOne() {
        return streetOne;
    }

    public void setStreetOne(String streetOne) {
        this.streetOne = streetOne;
    }

    public String getStreetTwo() {
        return streetTwo;
    }

    public void setStreetTwo(String streetTwo) {
        this.streetTwo = streetTwo;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return "AddressDTO{" +
                "id=" + id +
                ", houseNumber='" + houseNumber + '\'' +
                ", streetOne='" + streetOne + '\'' +
                ", streetTwo='" + streetTwo + '\'' +
                ", village='" + village + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }
}
