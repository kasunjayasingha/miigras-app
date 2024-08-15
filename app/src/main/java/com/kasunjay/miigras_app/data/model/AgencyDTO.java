package com.kasunjay.miigras_app.data.model;

public class AgencyDTO {

    private long id;
    private String email;
    private String fax;
    private String name;
    private String phone;
    private String phone2;
    private String regNum;
    private AddressDTO addressAgency;
    private DomainMinistryDTO domainMinistry;

    public AgencyDTO(long id, String email, String fax, String name, String phone, String phone2, String regNum, AddressDTO addressAgency, DomainMinistryDTO domainMinistry) {
        this.id = id;
        this.email = email;
        this.fax = fax;
        this.name = name;
        this.phone = phone;
        this.phone2 = phone2;
        this.regNum = regNum;
        this.addressAgency = addressAgency;
        this.domainMinistry = domainMinistry;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getRegNum() {
        return regNum;
    }

    public void setRegNum(String regNum) {
        this.regNum = regNum;
    }

    public AddressDTO getAddressAgency() {
        return addressAgency;
    }

    public void setAddressAgency(AddressDTO addressAgency) {
        this.addressAgency = addressAgency;
    }

    public DomainMinistryDTO getDomainMinistry() {
        return domainMinistry;
    }

    public void setDomainMinistry(DomainMinistryDTO domainMinistry) {
        this.domainMinistry = domainMinistry;
    }

    @Override
    public String toString() {
        return "AgencyDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", fax='" + fax + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", phone2='" + phone2 + '\'' +
                ", regNum='" + regNum + '\'' +
                ", addressAgency=" + addressAgency +
                ", domainMinistry=" + domainMinistry +
                '}';
    }
}
