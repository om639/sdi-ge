package com.sdi.ge.net;

import com.sdi.ge.net.packets.OutboundPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by Oliver on 20/02/2016.
 */
public class OutboundPacketEncoder extends MessageToByteEncoder<OutboundPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, OutboundPacket msg, ByteBuf out) throws Exception {
        ByteBuf payload = msg.getPayload();

        out.writeByte(msg.getOpcode());
        out.writeBytes(payload);

        payload.release();
    }
}
