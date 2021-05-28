package com.jd.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer {
   private ServerBootstrap bootstrapServer=null;
   private EventLoopGroup master;
   private EventLoopGroup worker;
   private Integer port;
   public NettyServer(Integer port){
       master=new NioEventLoopGroup();
       worker=new NioEventLoopGroup();
       bootstrapServer=new ServerBootstrap().group(master,worker)
                  .channel(NioServerSocketChannel.class);
       this.port=port;
   }

   @SneakyThrows
   public void start(){
       bootstrapServer.childHandler(new ChannelInitializer<SocketChannel>() {
           @Override
           protected void initChannel(SocketChannel socketChannel) throws Exception {
               //注册通道选择器
               ChannelPipeline pipeline = socketChannel.pipeline();
               pipeline.addLast("编码器",null);
           }
       });
       ChannelFuture future = bootstrapServer.bind(port).sync();
       log.info("启动服务，服务在端口{}处监听！",port);
       future.channel().closeFuture().sync();
   }

   public void close(){
       master.shutdownGracefully();
       master.shutdownGracefully();
   }

    public static void main(String[] args) {

    }
}
