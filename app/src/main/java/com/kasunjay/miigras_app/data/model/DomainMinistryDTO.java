package com.kasunjay.miigras_app.data.model;

public class DomainMinistryDTO {

    private Long id;
    private String email;
    private String fax;
    private String name;
    private String phone;

    public DomainMinistryDTO(Long id, String email, String fax, String name, String phone) {
        this.id = id;
        this.email = email;
        this.fax = fax;
        this.name = name;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    @Override
    public String toString() {
        return "DomainMinistryDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", fax='" + fax + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
