package com.jd.rpc.transport;

import com.jd.rpc.model.HostAndPort;
import com.jd.rpc.model.MethodInvokeContext;
import com.jd.rpc.model.ResultContext;
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

public interface RPCClient {
    ResultContext invoke(HostAndPort hostAndPort, MethodInvokeContext methodInvokeContext);
    void close();
}
