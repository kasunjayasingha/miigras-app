package com.kasunjay.miigras_app.data.model;

public class PersonDTO {

    private long id;
    private String firstName;
    private String lastName;
    private String nic;
    private String email;
    private String mobile1;
    private String mobile2;
    private String passport;
    private String dob;
    private AddressDTO address;

    public PersonDTO(long id, String firstName, String lastName, String nic, String email, String mobile1, String mobile2, String passport, String dob, AddressDTO address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nic = nic;
        this.email = email;
        this.mobile1 = mobile1;
        this.mobile2 = mobile2;
        this.passport = passport;
        this.dob = dob;
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile1() {
        return mobile1;
    }

    public void setMobile1(String mobile1) {
        this.mobile1 = mobile1;
    }

    public String getMobile2() {
        return mobile2;
    }

    public void setMobile2(String mobile2) {
        this.mobile2 = mobile2;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "PersonDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", nic='" + nic + '\'' +
                ", email='" + email + '\'' +
                ", mobile1='" + mobile1 + '\'' +
                ", mobile2='" + mobile2 + '\'' +
                ", passport='" + passport + '\'' +
                ", dob='" + dob + '\'' +
                ", address=" + address +
                '}';
    }
}
