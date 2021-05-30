package com.jd.request;

import com.jd.entity.User;
import com.jd.model.MethodInvokeContext;
import com.jd.process.DRPCContextRequest;
import com.jd.service.IUserService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.jackson.JsonObjectSerializer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.ResolvableType;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;


public class DRPCRequestTemplate <T> implements InvocationHandler, FactoryBean<T> {
     private String application;
     private Long timeout;
     private Class<? extends T> targetClass;

     public DRPCRequestTemplate(String appplication,Long timeout,Class<? extends T> targetClass){
         this.application=appplication;
         this.timeout=timeout;
         this.targetClass=targetClass;
     }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodInvokeContext invokeContext=new MethodInvokeContext()
                .setApplication(application)
                .setArgs(args)
                .setMethodName(method.getName())
                .setInvokeTime(System.currentTimeMillis())
                .setTimeout(timeout)
                .setAttachments(DRPCContextRequest.getAttachments())
                .setHost(InetAddress.getLocalHost().getHostAddress())
                .setParameterTypes(method.getParameterTypes())
                .setTargetClass(targetClass);

        return null;
    }


    @Override
    public T getObject() throws Exception {
         return (T) Proxy.newProxyInstance(targetClass.getClassLoader(),new Class[]{targetClass},this);
    }

    @Override
    public Class<?> getObjectType() {
        return targetClass;
    }
}
