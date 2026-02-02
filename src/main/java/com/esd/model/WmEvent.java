package com.esd.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wm_events")
public class WmEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer unitId;
    private String operator1;
    private String operator2;
    private String mat1;
    private String mat2;
    private LocalDateTime eventTime;

    // ✅ REQUIRED no-arg constructor
    public WmEvent() {
    }

    // (Optional) convenience constructor
    public WmEvent(String operator1, String operator2, String mat1, String mat2) {
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.mat1 = mat1;
        this.mat2 = mat2;
    }

    // ✅ Getters & Setters
    public Long getId() {
        return id;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public String getOperator1() {
        return operator1;
    }

    public void setOperator1(String operator1) {
        this.operator1 = operator1;
    }

    public String getOperator2() {
        return operator2;
    }

    public void setOperator2(String operator2) {
        this.operator2 = operator2;
    }

    public String getMat1() {
        return mat1;
    }

    public void setMat1(String mat1) {
        this.mat1 = mat1;
    }

    public String getMat2() {
        return mat2;
    }

    public void setMat2(String mat2) {
        this.mat2 = mat2;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }
}
