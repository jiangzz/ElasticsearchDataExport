package com.jd.rpc.transport.netty;


import com.jd.rpc.model.DRPCContextRequest;
import com.jd.rpc.model.MethodInvokeContext;
import com.jd.rpc.model.ResultContext;
import com.jd.rpc.process.AbstractMessageProcessor;
import com.jd.rpc.transport.RPCServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.ReferenceCountUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

@Slf4j
public class NettyRPCServer implements RPCServer {


   private ServerBootstrap bootstrapServer;
   private EventLoopGroup master;
   private EventLoopGroup worker;
   private Integer port;
   private Thread startThread;

   private AbstractMessageProcessor<MethodInvokeContext, ResultContext> messageProcessor;
   public NettyRPCServer(Integer port, AbstractMessageProcessor<MethodInvokeContext,ResultContext> messageProcessor){
       master=new NioEventLoopGroup();
       worker=new NioEventLoopGroup();
       bootstrapServer=new ServerBootstrap().group(master,worker)
                  .channel(NioServerSocketChannel.class);
       this.port=port;
       this.messageProcessor=messageProcessor;
   }

   @Override
   @SneakyThrows
   public void start(){

       bootstrapServer.childHandler(new ChannelInitializer<SocketChannel>() {
           @Override
           protected void initChannel(SocketChannel socketChannel) throws Exception {
               //注册通道选择器
               ChannelPipeline pipeline = socketChannel.pipeline();
               pipeline.addLast("数据帧解码",new LengthFieldBasedFrameDecoder(65535, 0, 2,0,2));
               pipeline.addLast("数据帧编码",new LengthFieldPrepender(2));
               pipeline.addLast("消息对象编解码",new MessageObjectCodec());
               //添加最终消息处理
               pipeline.addLast("最终处理者", new ChannelInboundHandlerAdapter() {
                   @Override
                   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                       //处理消息逻辑
                       try {
                           MethodInvokeContext invokeContext = (MethodInvokeContext) msg;

                           long begin = System.currentTimeMillis();
                           Future<ResultContext> processMessage = messageProcessor.processMessage(invokeContext);
                           ResultContext resultContext = new ResultContext();
                           try {
                               resultContext = processMessage.get(invokeContext.getTimeout(),invokeContext.getTimeUnit());
                           } catch (TimeoutException e) {
                               resultContext.setRuntimeError(e);
                           }
                           long end = System.currentTimeMillis();

                           resultContext.setTook(end-begin)
                                        .setResponseHost(InetAddress.getLocalHost().getHostAddress()+":"+port);

                           ChannelFuture channelFuture = ctx.writeAndFlush(resultContext);
                           //添加异常处理监听
                           channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                           channelFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                           channelFuture.addListener(ChannelFutureListener.CLOSE);
                       } finally {
                           ReferenceCountUtil.release(msg);
                       }
                   }
                   @Override
                   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                       log.error("Netty 服务器错误，原因{}",cause.getMessage());
                   }
               });

           }
       });
       startThread =new Thread(()->{
           try {
               ChannelFuture future = bootstrapServer.bind(port).sync();
               log.info("启动服务，服务在端口{}处监听！",port);
               future.channel().closeFuture().sync();
           } catch (InterruptedException e) {
               log.info("在端口{} 启动服务失败！原因{}",port,e.getCause());
           }
       });
       startThread.setName("NettyServer Thread ");
       startThread.setDaemon(true);
       startThread.setPriority(5);
       startThread.start();
   }

   @Override
   public void close(){
       master.shutdownGracefully();
       master.shutdownGracefully();
   }
}
