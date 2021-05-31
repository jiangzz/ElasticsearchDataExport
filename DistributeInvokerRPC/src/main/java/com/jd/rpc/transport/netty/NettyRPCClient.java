package com.jd.rpc.transport.netty;

import com.jd.rpc.model.HostAndPort;
import com.jd.rpc.model.MethodInvokeContext;
import com.jd.rpc.model.ResultContext;
import com.jd.rpc.transport.RPCClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class NettyRPCClient implements RPCClient {
    private Bootstrap bootstrap;
    private EventLoopGroup worker;
    public NettyRPCClient(){
        bootstrap=new Bootstrap();
        worker=new NioEventLoopGroup();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(worker);
    }

    @Override
    @SneakyThrows
    public ResultContext invoke(HostAndPort hostAndPort, MethodInvokeContext methodInvokeContext) {
        final List<Object> results = new ArrayList<Object>();
        // TODO Auto-generated method stub
        //初始化通道
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                // TODO Auto-generated method stub
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("数据帧解码",new LengthFieldBasedFrameDecoder(65535, 0, 2,0,2));
                pipeline.addLast("数据帧编码",new LengthFieldPrepender(2));
                pipeline.addLast("消息对象编解码",new MessageObjectCodec());
                pipeline.addLast(new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        ChannelFuture channelFuture = ctx.writeAndFlush(methodInvokeContext);
                        //添加异常处理监听
                        channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                        channelFuture.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                    }
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        results.add(msg);
                    }
                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        cause.printStackTrace();
                        log.error("Netty 通讯出错了！由于{}",cause.getCause());
                    }
                });
            }
        });

        ChannelFuture channelFuture = bootstrap.connect(hostAndPort.getHost(), hostAndPort.getPort()).sync();
        channelFuture.channel().closeFuture().sync();
        return (ResultContext) results.get(0);
    }

    @Override
    public void close(){
        worker.shutdownGracefully();
    }

}
