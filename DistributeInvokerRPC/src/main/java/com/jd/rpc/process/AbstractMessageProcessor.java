package com.jd.rpc.process;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public abstract class AbstractMessageProcessor<T,R>  {
  protected abstract R process(T msg);
  private ThreadPoolExecutor messageExecutor;
  public AbstractMessageProcessor(){
      messageExecutor= new ThreadPoolExecutor(8,32,1000,
              TimeUnit.MICROSECONDS,new LinkedBlockingDeque<Runnable>(1000),
              new ThreadFactoryBuilder()
                      .setDaemon(true)
                      .setNameFormat("Message ThreadPool-%d")
                      .setPriority(5)
                      .build(),new ThreadPoolExecutor.CallerRunsPolicy());
  }

  public Future<R> processMessage(T t){
    return  messageExecutor.submit(new Callable<R>() {
          @Override
          public R call() throws Exception {
              try{
                  return  process(t);
              }catch (Exception e){
                  log.error("处理消息信息出错了");
              }
              return null;
          }
      });
  }
}
