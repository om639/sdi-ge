package com.sdi.ge.net.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by Oliver on 20/02/2016.
 */
public final class TextureOutboundPacket implements OutboundPacket {

    private final int id;

    private final int texture;

    public TextureOutboundPacket(int id, int texture) {
        this.id = id;
        this.texture = texture;
    }

    @Override
    public int getOpcode() {
        return 7;
    }

    @Override
    public ByteBuf getPayload() {
        return Unpooled.buffer(8)
                .writeInt(id)
                .writeInt(texture);
    }
}
