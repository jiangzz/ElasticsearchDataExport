package com.jd.rpc.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Data
@Accessors(chain = true)
public class MethodInvokeContext implements Serializable {
    private Class targetClass;
    private String methodName;
    private Object[] args;
    private Class[] parameterTypes;
    private Map<String,Object> attachments;
    //调用者信息
    private String host;
    private Long timeout;
    private TimeUnit timeUnit;

}
