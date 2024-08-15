package com.kasunjay.miigras_app.data.model;

public class GradientDTO {

    private long id;
    private String gradientType;
    private PersonDTO person;

    public GradientDTO(long id, String gradientType, PersonDTO person) {
        this.id = id;
        this.gradientType = gradientType;
        this.person = person;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGradientType() {
        return gradientType;
    }

    public void setGradientType(String gradientType) {
        this.gradientType = gradientType;
    }

    public PersonDTO getPerson() {
        return person;
    }

    public void setPerson(PersonDTO person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "GradientDTO{" +
                "id=" + id +
                ", gradientType='" + gradientType + '\'' +
                ", person=" + person +
                '}';
    }
}
