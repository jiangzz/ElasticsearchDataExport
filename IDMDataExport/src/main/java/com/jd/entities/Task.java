package com.jd.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class Task implements Serializable {
    private String id;
    private String name;
    private String platform;
    private String alias;
    private String timeCost;
    private String runDate;
    private String status;
    private String type;
    private String description;
    private String personInCharge;
}
