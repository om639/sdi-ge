package com.sdi.ge.net.packets;

import io.netty.channel.Channel;

/**
 * Created by Oliver on 21/02/2016.
 */
public interface SpecificOutboundPacket extends OutboundPacket {

    Channel getChannel();
}
