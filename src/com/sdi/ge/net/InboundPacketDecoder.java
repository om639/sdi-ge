package com.sdi.ge.net;

import com.sdi.ge.net.packets.InboundPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;

import java.util.List;

/**
 * Created by Oliver on 20/02/2016.
 */
public class InboundPacketDecoder extends ByteToMessageDecoder {

    private static final AttributeKey<InboundPacket.Type> TYPE_KEY = AttributeKey.valueOf("type");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        InboundPacket.Type type = ctx.channel().attr(TYPE_KEY).get();

        // Waiting for opcode.
        if (type == null && in.readableBytes() >= 1) {
            type = InboundPacket.Type.forOpcode(in.readByte());

            if (type != null) {
                ctx.channel().attr(TYPE_KEY).set(type);
            } else {
                throw new IllegalStateException();
            }
        }

        // Waiting for payload.
        if (type != null && in.readableBytes() >= type.getSize()) {
            out.add(new InboundPacket(type, in.readBytes(type.getSize())));
            ctx.channel().attr(TYPE_KEY).remove();
        }
    }
}
