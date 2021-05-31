package com.jd.rpc.transport.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.SerializationUtils;

import java.util.List;

@Slf4j
public class MessageObjectCodec extends MessageToMessageCodec<ByteBuf, Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        // TODO Auto-generated method stub
        byte[] serialize = SerializationUtils.serialize(msg);
        ByteBuf buffer= Unpooled.buffer();
        buffer.writeBytes(serialize);
        out.add(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        // TODO Auto-generated method stub
        byte[] bytes=new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        Object object = SerializationUtils.deserialize(bytes);
        out.add(object);
    }


}
