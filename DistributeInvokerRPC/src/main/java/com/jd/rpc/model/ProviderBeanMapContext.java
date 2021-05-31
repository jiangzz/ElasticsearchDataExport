package com.jd.rpc.model;

import java.util.HashMap;
import java.util.Map;

public class ProviderBeanMapContext {
    private static Map<Class,Object> beanMapInfo=new HashMap<Class, Object>();
    public static void register(Class key,Object bean){
        beanMapInfo.put(key,bean);
    }
    public static Object getBean(Class key){
       return  beanMapInfo.get(key);
    }
}
