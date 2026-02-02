package com.esd.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ct_events")
public class CtEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer unitId;
    private String cardNumber;
    private Double leftFootOhms;
    private Double rightFootOhms;
    private String footResult;
    private String wristValue;
    private String wristResult;
    private String entryExit;
    private LocalDateTime eventTime;

    // ✅ REQUIRED by JPA
    public CtEvent() {
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

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Double getLeftFootOhms() {
        return leftFootOhms;
    }

    public void setLeftFootOhms(Double leftFootOhms) {
        this.leftFootOhms = leftFootOhms;
    }

    public Double getRightFootOhms() {
        return rightFootOhms;
    }

    public void setRightFootOhms(Double rightFootOhms) {
        this.rightFootOhms = rightFootOhms;
    }

    public String getFootResult() {
        return footResult;
    }

    public void setFootResult(String footResult) {
        this.footResult = footResult;
    }

    public String getWristValue() {
        return wristValue;
    }

    public void setWristValue(String wristValue) {
        this.wristValue = wristValue;
    }

    public String getWristResult() {
        return wristResult;
    }

    public void setWristResult(String wristResult) {
        this.wristResult = wristResult;
    }

    public String getEntryExit() {
        return entryExit;
    }

    public void setEntryExit(String entryExit) {
        this.entryExit = entryExit;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }
}
