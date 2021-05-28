package com.jd.entities;

import lombok.Data;

import java.io.Serializable;

@Data
public class Column implements Serializable {
    private String columnName;
    private String columnComment;

}
