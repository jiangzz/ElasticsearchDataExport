package com.jd.entities;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class IDMData implements Serializable {

    private String cluster;
    private String database;
    private String tableName;
    private String tableAlias;

    private List<Column> columns;
    private String owner;
    private String tableType;
    private String inputFormat;
    private String outputFormat;
    private String tableFsPath;
    private Date createTime;
    private Date lastUpdateTime;
    private String tableSourceType="HIVE";

    private Integer aliveDays;
    private String marketOrProject;
    private String businessSystem;
    private String businessCategory;
    private String businessLine;
    private String personInCharge;

    private Integer usageCountPerDay;
    private Integer usageCountPerWeek;
    private Integer usageCountPerMonth;
    private Date latestAccessTime;
    private Date lastModifiedTime;

    private Integer clickCounts;
    private String authLevel;
    private List<String> userDefineTags;
    private String businessDescription;
    private String usageDescription;

    private List<Task> tasksMakeup;
    private List<Task> tasksUseTable;

}

