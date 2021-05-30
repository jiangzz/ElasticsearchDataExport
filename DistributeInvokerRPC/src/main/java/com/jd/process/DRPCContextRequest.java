package com.jd.process;

import java.util.HashMap;
import java.util.Map;

public class DRPCContextRequest {
    private static final ThreadLocal<Map<String,Object>> THREAD_LOCAL=new ThreadLocal<Map<String,Object>>();

    public static void put(String key,Object value){
        Map<String, Object> attchments = THREAD_LOCAL.get();
        if(attchments==null){
            attchments=new HashMap<String,Object>();
            THREAD_LOCAL.set(attchments);
        }
        attchments.put(key,value);
    }
    public static Object get(String key){
        Map<String, Object> attchments = THREAD_LOCAL.get();
        if(attchments==null){
            attchments=new HashMap<String,Object>();
            THREAD_LOCAL.set(attchments);
        }
        return attchments.get(key);
    }
    public static Map<String,Object> getAttachments(){
        return THREAD_LOCAL.get();
    }
    public static void clear(){
       THREAD_LOCAL.remove();
    }
}

