package com.sdi.ge.net.packets;

import io.netty.buffer.ByteBuf;

/**
 * Created by Oliver on 19/02/2016.
 */
public interface OutboundPacket {

    int getOpcode();

    ByteBuf getPayload();
}
