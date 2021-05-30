package com.jd.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

@Data
@Accessors(chain = true)
public class ResultContext  implements Serializable {
    private Object returnValue;
    private Exception runtimeError;
    //上下文附件信息
    private Map<String,Object> attachments;

    //响应端信息
    private String responseHost;
    private Long responseTime;
}
