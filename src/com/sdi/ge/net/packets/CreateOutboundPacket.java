package com.sdi.ge.net.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by Oliver on 20/02/2016.
 */
public final class CreateOutboundPacket implements OutboundPacket {

    private final int id;

    private final double x, y;

    private final int direction;

    private final int texture;

    public CreateOutboundPacket(int id, double x, double y, int direction, int texture) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.texture = texture;
    }


    @Override
    public int getOpcode() {
        return 3;
    }

    @Override
    public ByteBuf getPayload() {
        return Unpooled.buffer(25)
                .writeInt(id)
                .writeDouble(x)
                .writeDouble(y)
                .writeByte(direction)
                .writeInt(texture);
    }
}
