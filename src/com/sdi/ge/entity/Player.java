package com.sdi.ge.entity;

import com.sdi.ge.World;
import com.sdi.ge.entity.item.Powerup;
import com.sdi.ge.net.packets.InboundPacket;
import com.sdi.ge.net.packets.InterfaceOutboundPacket;
import com.sdi.ge.net.packets.OutboundPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.BitSet;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Oliver on 18/02/2016.
 */
public class Player extends Character {

    /**
     * The width of the player character, used in collision detection.
     */
    private static final int PLAYER_WIDTH = 14;

    /**
     * The height of the player character, used in collision detection.
     */
    private static final int PLAYER_HEIGHT = 18;

    /**
     * The index of the texture to use for the player normally.
     */
    private static final int PLAYER_TEXTURE = 0;

    /**
     * The index of the texture to use for the player when the player is flying.
     */
    private static final int PLAYER_TEXTURE_FLIGHT = 1;

    /**
     * The player's maximum (and initial) health.
     */
    private static final int PLAYER_HEALTH = 6;

    /**
     * The player's movement speed in pixels per second.
     */
    private static final double PLAYER_SPEED = 200;

    /**
     * How much the player's movement speed should be multiplied by when flying.
     */
    private static final double PLAYER_FIGHT_SPEED_MULTIPLIER = 2;

    /**
     * How long the player can fly for at once, in seconds.
     */
    private static final float PLAYER_FLIGHT_DURATION = 1;

    /**
     * How long it takes before the player can fly again, in seconds.
     */
    private static final float PLAYER_FLIGHT_COOLDOWN = 5;

    private Channel channel;

    private Queue<InboundPacket> inbound = new ConcurrentLinkedQueue<>();

    private float flightTimer = 0;

    private float[] powerupTimers = new float[Powerup.Type.count()];

    private float[] powerupTimersInitial = new float[Powerup.Type.count()];

    public Player(double x, double y, World parent, Channel channel) {
        super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT, Direction.DOWN, PLAYER_TEXTURE, parent, PLAYER_HEALTH);
        this.channel = channel;
    }

    public boolean isFlying() {
        return flightTimer > PLAYER_FLIGHT_COOLDOWN;
    }

    public float getFlightCharge() {
        if (flightTimer <= 0) {
            // Flight ready.
            return 1;
        } else if (flightTimer > 0 && flightTimer <= PLAYER_FLIGHT_COOLDOWN) {
            // Recharging.
            return 1 - flightTimer / PLAYER_FLIGHT_COOLDOWN;
        } else {
            // Flying.
            return (flightTimer - PLAYER_FLIGHT_COOLDOWN) / PLAYER_FLIGHT_DURATION;
        }
    }

    public void startFlying() {
        flightTimer = PLAYER_FLIGHT_DURATION + PLAYER_FLIGHT_COOLDOWN;

        setVelocityX(getVelocityX() * PLAYER_FIGHT_SPEED_MULTIPLIER);
        setVelocityY(getVelocityY() * PLAYER_FIGHT_SPEED_MULTIPLIER);
    }

    public boolean hasPowerup(Powerup.Type type) {
        return powerupTimers[type.ordinal()] > 0;
    }

    public float[] getPowerupCharge() {
        float[] powerupCharge = new float[Powerup.Type.count()];

        // Represent time remaining on poweups as a percentage.
        for (int i = 0; i < powerupCharge.length; i++) {
            powerupCharge[i] = getPowerupCharge(Powerup.Type.forOrdinal(i));
        }

        return powerupCharge;
    }

    public float getPowerupCharge(Powerup.Type type) {
        float current = powerupTimers[type.ordinal()], initial = powerupTimersInitial[type.ordinal()];

        if (current <= 0 || initial <= 0) {
            // Powerup off.
            return 0;
        } else {
            // Powerup on.
            return current / initial;
        }
    }

    public void setPowerup(Powerup.Type type, float duration) {
        powerupTimers[type.ordinal()] = duration;
        powerupTimersInitial[type.ordinal()] = duration;
    }

    /**
     * Adds a packet to this Player's inbound packet queue
     *
     * @param packet the packet to add
     */
    public void enqueue(InboundPacket packet) {
        inbound.add(packet);
    }

    /**
     * Processes all pending inbound packets for this Player.
     */
    private void process() {
        InboundPacket packet;

        while ((packet = inbound.poll()) != null) {
            switch (packet.getType()) {
                case MOVEMENT:
                    processMovement(packet.getPayload());
                    break;
                case ATTACK:
                    processAttack(packet.getPayload());
                    break;
                case FLIGHT:
                    // Flight doesn't have a payload.
                    processFlight();
                    break;
            }

            packet.release();
        }
    }

    /**
     * Processes a movement packet.
     *
     * @param payload the payload
     */
    private void processMovement(ByteBuf payload) {
        // Don't allow movement via keys while flying.
        if (isFlying())
            return;

        BitSet direction = BitSet.valueOf(payload.array());

        // Left/right movement.
        if (direction.get(0)) {
            setVelocityX(-PLAYER_SPEED);
        } else if (direction.get(1)) {
            setVelocityX(PLAYER_SPEED);
        }

        // Up/down movement.
        if (direction.get(2)) {
            setVelocityY(PLAYER_SPEED);
        } else if (direction.get(3)) {
            setVelocityY(-PLAYER_SPEED);
        }
    }

    /**
     * Processes an attack packet.
     *
     * @param payload the payload
     */
    private void processAttack(ByteBuf payload) {
        double targetX = payload.readDouble(), targetY = payload.readDouble();

        // TODO: attack
    }

    /**
     * Process a flight packet.
     */
    private void processFlight() {
        if (flightTimer <= 0 && (getVelocityX() != 0 || getVelocityY() != 0)) {
            startFlying();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void update(float delta, Deque<OutboundPacket> outbound) {
        // Leave velocity alone if flying.
        if (!isFlying()) {
            setVelocityX(0);
            setVelocityY(0);
            setTexture(PLAYER_TEXTURE);
        } else {
            setTexture(PLAYER_TEXTURE_FLIGHT);
        }

        // Whether an interface update is needed.
        boolean updateInterface = false;

        // If the flight timer is greater than 0, update the interface.
        if (flightTimer > 0) {
            flightTimer -= delta;
            updateInterface = true;
        }

        // Update powerup times.
        for (int i = 0; i < powerupTimers.length; i++) {
            if (powerupTimers[i] > 0) {
                powerupTimers[i] -= delta;

                if (powerupTimers[i] <= 0) {
                    powerupTimers[i] = 0;
                    powerupTimersInitial[i] = 0;
                }

                updateInterface = true;
            }
        }

        if (updateInterface)
            outbound.addFirst(new InterfaceOutboundPacket(channel, getHealthPercentage(), getFlightCharge(), getPowerupCharge()));

        // Process all pending inbound packets.
        process();

        if (!isFlying()) {
            // Apply speed powerup bonus.
            if (hasPowerup(Powerup.Type.SUPER_SPEED)) {
                setVelocityX(getVelocityX() * Powerup.POWERUP_SUPER_SPEED_MULTIPLIER);
                setVelocityY(getVelocityY() * Powerup.POWERUP_SUPER_SPEED_MULTIPLIER);
            }

            // Make diagonal movement slower.
            if (getVelocityX() != 0 && getVelocityY() != 0) {
                setVelocityX(getVelocityX() / Math.sqrt(2));
                setVelocityY(getVelocityY() / Math.sqrt(2));
            }
        }

        super.update(delta, outbound);
    }
}
