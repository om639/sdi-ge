package com.sdi.ge.net.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by Oliver on 20/02/2016.
 */
public final class PositionOutboundPacket implements OutboundPacket {

    private final int id;

    private final double x, y;

    public PositionOutboundPacket(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    @Override
    public int getOpcode() {
        return 5;
    }

    @Override
    public ByteBuf getPayload() {
        return Unpooled.buffer(20)
                .writeInt(id)
                .writeDouble(x)
                .writeDouble(y);
    }
}
