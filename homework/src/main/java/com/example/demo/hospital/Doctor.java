package com.example.demo.hospital;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "doctors")
public class Doctor {
    @Id
    private String id;
    private String name;
    private String departmentId;
    private String title; // 职称
    private String specialty; // 擅长
    private Double fee; // 挂号费

    public Doctor() {}

    public Doctor(String name, String departmentId, String title, String specialty, Double fee) {
        this.name = name;
        this.departmentId = departmentId;
        this.title = title;
        this.specialty = specialty;
        this.fee = fee;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public Double getFee() { return fee; }
    public void setFee(Double fee) { this.fee = fee; }
}
