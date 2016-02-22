package com.sdi.ge.net.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by Oliver on 20/02/2016.
 */
public final class DestroyOutboundPacket implements OutboundPacket {

    private final int id;

    public DestroyOutboundPacket(int id) {
        this.id = id;
    }

    @Override
    public int getOpcode() {
        return 4;
    }

    @Override
    public ByteBuf getPayload() {
        return Unpooled.buffer(4)
                .writeInt(id);
    }
}
