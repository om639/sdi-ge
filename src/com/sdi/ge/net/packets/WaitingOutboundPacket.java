package com.sdi.ge.net.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by Oliver on 20/02/2016.
 */
public final class WaitingOutboundPacket implements OutboundPacket {

    private int waiting;

    public WaitingOutboundPacket(int waiting) {
        this.waiting = waiting;
    }

    @Override
    public int getOpcode() {
        return 1;
    }

    @Override
    public ByteBuf getPayload() {
        return Unpooled.buffer(1)
                .writeByte(waiting);
    }
}
