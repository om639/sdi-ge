package com.sdi.ge;

import com.sdi.ge.entity.Player;
import com.sdi.ge.entity.item.Powerup;
import com.sdi.ge.net.ConnectionHandler;
import com.sdi.ge.net.packets.*;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Oliver on 18/02/2016.
 */
public class Engine implements Runnable {

    /**
     * How long to sleep for each cycle.
     */
    private static final long SLEEP_TIME = 1000 / 60;

    /**
     * The parent of this Engine.
     */
    private Server parent;

    /**
     * The number of players allowed in the server at once.
     */
    private int numPlayers;

    /**
     * Executor service for the game loop.
     */
    private ExecutorService gameService;

    /**
     * True if the game should continue running.
     */
    private boolean gameRunning = false;

    /**
     * The game world.
     */
    private World world;

    /**
     * Channels of each player.
     */
    private ChannelGroup players = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * The queue of outbound packets.
     */
    private Deque<OutboundPacket> outbound = new LinkedList<>();

    public Engine(Server parent, int numPlayers) {
        this.parent = parent;
        this.numPlayers = numPlayers;

        gameService = Executors.newSingleThreadExecutor();

        // Attempt to load the map.
        try {
            world = new World(outbound);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean playerConnected(Channel channel) {
        if (players.size() < numPlayers) {
            players.add(channel);
            outbound.addFirst(new WaitingOutboundPacket(numPlayers - players.size()));

            if (players.size() == numPlayers)
                setup();

            return true;
        } else {
            return false;
        }
    }

    public void playerDisconnected(Channel channel) {
        players.remove(channel);

        Player player = channel.attr(ConnectionHandler.PLAYER_KEY).get();

        if (player != null) {
            player.destroy();
            outbound.addFirst(new DestroyOutboundPacket(player.getId()));
        }
        // Stop if there are no more players.
        if (players.size() == 0)
            parent.shutdown();
    }

    private void setup() {
        for (Channel channel : players) {
            setupPlayer(channel);
        }

        setupWorld();
    }

    private void setupPlayer(Channel channel) {
        Player player = world.createPlayer(channel);

        // Let player know their entity ID and initial interface values.
        channel.attr(ConnectionHandler.PLAYER_KEY).set(player);
        outbound.addLast(new StartOutboundPacket(channel, player.getId()));
        outbound.addLast(new InterfaceOutboundPacket(channel, player.getHealthPercentage(), player.getFlightCharge(), player.getPowerupCharge()));
    }

    private void setupWorld() {
        world.createPowerup(100, 100, Powerup.Type.SUPER_SPEED, 10);
        world.createPowerup(150, 100, Powerup.Type.RATE_OF_FIRE, 5);
    }

    private void processOutbound() {
        OutboundPacket packet;

        while ((packet = outbound.poll()) != null) {
            if (packet instanceof SpecificOutboundPacket) {
                ((SpecificOutboundPacket) packet).getChannel().write(packet);
            } else {
                players.write(packet);
            }
        }

        players.flush();
    }

    /**
     * Starts the Engine.
     */
    public void start() {
        gameRunning = true;
        gameService.submit(this);
    }

    /**
     * Waits for the Engine to stop.
     */
    public void stop() {
        // Stop game engine.
        gameRunning = false;
        gameService.shutdown();
    }

    @Override
    public void run() {
        long current = System.nanoTime(), elapsed;

        while (gameRunning) {
            elapsed = System.nanoTime() - current;
            current += elapsed;

            // Update world.
            world.update(elapsed / 1000000000f);
            processOutbound();

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
