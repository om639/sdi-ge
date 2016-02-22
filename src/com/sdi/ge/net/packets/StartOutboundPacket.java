package com.sdi.ge.net.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

/**
 * Created by Oliver on 20/02/2016.
 */
public final class StartOutboundPacket implements SpecificOutboundPacket {

    private final Channel channel;

    private final int id;

    public StartOutboundPacket(Channel channel, int id) {
        this.channel = channel;
        this.id = id;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public int getOpcode() {
        return 2;
    }

    @Override
    public ByteBuf getPayload() {
        return Unpooled.buffer(4)
                .writeInt(id);
    }
}
