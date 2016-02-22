package com.sdi.ge.entity;

import com.sdi.ge.World;
import com.sdi.ge.net.packets.OutboundPacket;

import java.util.Deque;

/**
 * Represents a character in the game.
 */
public abstract class Character extends MobileEntity {

    /**
     * Current health and the maximum health of this Character.
     */
    private int maximumHealth, currentHealth;

    public Character(double x, double y, int width, int height, Direction direction, int texture, World parent, int maximumHealth) {
        super(x, y, width, height, direction, texture, parent);
        this.maximumHealth = this.currentHealth = maximumHealth;
    }

    /**
     * Gets the current health of this Character.
     *
     * @return the current health of this Character
     */
    public int getCurrentHealth() {
        return currentHealth;
    }

    /**
     * Gets the maximum health of this Character.
     *
     * @return the maximum health of this Character
     */
    public int getMaximumHealth() {
        return maximumHealth;
    }

    /**
     * Gets the current health percentage.
     *
     * @return the current health percentage
     */
    public float getHealthPercentage() {
        return currentHealth / maximumHealth;
    }

    /**
     * Heals this Character's current health by the specified number of points.
     *
     * @param health the number of health points to heal
     */
    public void heal(int health) {
        this.currentHealth += health;

        if (currentHealth > maximumHealth) {
            currentHealth = maximumHealth;
        }
    }

    /**
     * Damages this Character's health by the specified number of points.
     *
     * @param health the number of points to damage
     */
    public void damage(int health) {
        this.currentHealth -= health;

        if (currentHealth < 0)
            currentHealth = 0;
    }

    /**
     * Returns if the character is dead
     *
     * @return whether this Character is dead (i.e. its health is 0)
     */
    public boolean isDead() {
        return currentHealth == 0;
    }

    /**
     * Updates the state of this Character.
     *
     * @param delta how much time has passed since the last update
     */
    @Override
    public void update(float delta, Deque<OutboundPacket> outbound) {
        if (isDead()) {
            destroy();
        } else {
            super.update(delta, outbound);
        }
    }
}
