package com.jd.model;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
public class HostAndPort implements Serializable {
    private String name;
    private Integer port;
}
