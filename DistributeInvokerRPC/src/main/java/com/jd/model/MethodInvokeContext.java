package com.jd.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

@Data
@Accessors(chain = true)
public class MethodInvokeContext implements Serializable {
    //目标接口
    private Class targetClass;
    //方法名
    private String methodName;
    //调用参数
    private Object[] args;
    private Class[] parameterTypes;
    //上下文附件信息
    private Map<String,Object> attachments;

    //调用者信息
    private String application;
    private String host;
    private Long invokeTime;
    private Long timeout;

}
