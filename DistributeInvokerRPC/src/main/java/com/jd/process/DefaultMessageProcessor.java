package com.jd.process;

import com.jd.model.MethodInvokeContext;
import com.jd.model.ResultContext;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
public class DefaultMessageProcessor extends AbstractMessageProcessor<MethodInvokeContext,ResultContext>  {
    private ApplicationContext ctx;

    public DefaultMessageProcessor(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @SneakyThrows
    @Override
    protected ResultContext process(MethodInvokeContext msg) {

        Class targetClass = msg.getTargetClass();
        Object targetBean = ctx.getBean(targetClass);
        Method method = targetClass.getDeclaredMethod(msg.getMethodName(), msg.getParameterTypes());
        Object returnValue = null;

        ResultContext resultContext = new ResultContext();
        try {
            returnValue=method.invoke(targetBean);
            resultContext.setReturnValue(returnValue);
            //设置附件信息
            resultContext.setAttachments(DRPCContextRequest.getAttachments());
        } catch (Exception e) {
           log.warn("调用本地方法出错了！");
           resultContext.setRuntimeError(e);
        }
        return resultContext;
    }
}
