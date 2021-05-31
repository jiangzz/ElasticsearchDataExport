package com.jd.rpc.process.impl;

import com.jd.rpc.model.MethodInvokeContext;
import com.jd.rpc.model.ProviderBeanMapContext;
import com.jd.rpc.model.ResultContext;
import com.jd.rpc.process.AbstractMessageProcessor;
import com.jd.rpc.model.DRPCContextRequest;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MethodInvokeMessageProcessor extends AbstractMessageProcessor<MethodInvokeContext,ResultContext> {
    @SneakyThrows
    @Override
    protected ResultContext process(MethodInvokeContext msg) {
        ResultContext resultContext = new ResultContext();
        try {
            Class targetClass = msg.getTargetClass();
            Object targetBean = ProviderBeanMapContext.getBean(targetClass);
            Method method = targetClass.getDeclaredMethod(msg.getMethodName(), msg.getParameterTypes());
            Object returnValue = null;
            //将调用端的附件信息放置到当前调用栈中
            DRPCContextRequest.put("host",msg.getHost());
            DRPCContextRequest.getAttachments().putAll(msg.getAttachments());
            returnValue=method.invoke(targetBean,msg.getArgs());
            resultContext.setReturnValue(returnValue);
            resultContext.getAttachments().remove("host");
            //设置附件信息
            resultContext.setAttachments(resultContext.getAttachments());
        } catch (Exception e) {
           log.warn("调用远程本地方法出错了，由于{},改错误会同步到远程调用端！",e.getCause());
           resultContext.setRuntimeError(e);
        }
        return resultContext;
    }
}
