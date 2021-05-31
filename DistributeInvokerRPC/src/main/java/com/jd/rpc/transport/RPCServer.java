package com.jd.rpc.transport;


import com.jd.rpc.model.MethodInvokeContext;
import com.jd.rpc.model.ResultContext;
import com.jd.rpc.process.AbstractMessageProcessor;
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

public interface RPCServer {

   public void start();
   public void close();
}
