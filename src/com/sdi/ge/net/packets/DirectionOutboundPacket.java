package com.sdi.ge.net.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by Oliver on 20/02/2016.
 */
public final class DirectionOutboundPacket implements OutboundPacket {

    private final int id;

    private final int direction;

    public DirectionOutboundPacket(int id, int direction) {
        this.id = id;
        this.direction = direction;
    }


    @Override
    public int getOpcode() {
        return 6;
    }

    @Override
    public ByteBuf getPayload() {
        return Unpooled.buffer(5)
                .writeInt(id)
                .writeByte(direction);
    }
}
