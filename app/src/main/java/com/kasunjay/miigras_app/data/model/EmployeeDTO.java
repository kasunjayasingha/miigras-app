package com.kasunjay.miigras_app.data.model;

public class EmployeeDTO {

    private long id;
    private String empId;
    private PersonDTO person;
    private AgencyDTO agency;
    private GradientDTO gradient;
    private String jobType;

    public EmployeeDTO(long id, String empId, PersonDTO person, AgencyDTO agency, GradientDTO gradient, String jobType) {
        this.id = id;
        this.empId = empId;
        this.person = person;
        this.agency = agency;
        this.gradient = gradient;
        this.jobType = jobType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public PersonDTO getPerson() {
        return person;
    }

    public void setPerson(PersonDTO person) {
        this.person = person;
    }

    public AgencyDTO getAgency() {
        return agency;
    }

    public void setAgency(AgencyDTO agency) {
        this.agency = agency;
    }

    public GradientDTO getGradient() {
        return gradient;
    }

    public void setGradient(GradientDTO gradient) {
        this.gradient = gradient;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "id=" + id +
                ", empId='" + empId + '\'' +
                ", person=" + person +
                ", agency=" + agency +
                ", gradient=" + gradient +
                ", jobType='" + jobType + '\'' +
                '}';
    }
}
