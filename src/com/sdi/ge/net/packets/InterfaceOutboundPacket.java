package com.sdi.ge.net.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

/**
 * Created by Oliver on 21/02/2016.
 */
public final class InterfaceOutboundPacket implements SpecificOutboundPacket {

    private final Channel channel;

    private final float health, flightCharge;

    private final float[] powerupCharge;

    public InterfaceOutboundPacket(Channel channel, float health, float flightCharge, float[] powerupCharge) {
        this.channel = channel;
        this.health = health;
        this.flightCharge = flightCharge;
        this.powerupCharge = powerupCharge;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public int getOpcode() {
        return 8;
    }

    @Override
    public ByteBuf getPayload() {
        ByteBuf payload = Unpooled.buffer(28);

        payload.writeFloat(health);
        payload.writeFloat(flightCharge);

        for (float value : powerupCharge) {
            payload.writeFloat(value);
        }

        return payload;
    }
}
