package com.sdi.ge.net;

import com.sdi.ge.Engine;
import com.sdi.ge.entity.Player;
import com.sdi.ge.net.packets.InboundPacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

/**
 * Created by Oliver on 19/02/2016.
 */
@ChannelHandler.Sharable
public class ConnectionHandler extends SimpleChannelInboundHandler<InboundPacket> {

    public static final AttributeKey<Player> PLAYER_KEY = AttributeKey.valueOf("player");

    private final Engine engine;

    public ConnectionHandler(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Connection received from " + ctx.channel().remoteAddress().toString());

        // If the game has already started.
        if (!engine.playerConnected(ctx.channel())) {
            ctx.close();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, InboundPacket packet) throws Exception {
        Player player = channelHandlerContext.channel().attr(PLAYER_KEY).get();

        if (player != null) {
            player.enqueue(packet);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Connection closed " + ctx.channel().remoteAddress().toString());

        engine.playerDisconnected(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
