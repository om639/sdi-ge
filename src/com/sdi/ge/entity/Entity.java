package com.sdi.ge.entity;

import com.sdi.ge.World;
import com.sdi.ge.net.packets.*;

import java.util.Deque;

/**
 * Created by Oliver on 18/02/2016.
 */
public abstract class Entity {

    /**
     * Whether this Entity has been created yet (i.e. sent to player connections).
     */
    private boolean created = false;

    /**
     * Whether this Entity has been destroyed and should be removed from the entity list.
     */
    private boolean destroyed = false;

    /**
     * The ID of this Entity in the entity list.
     */
    private int id;

    /**
     * The coordinates of this Entity.
     */
    private double x, y;

    /**
     * The width and height of this Entity.
     */
    private int width, height;

    /**
     * The direction this Entity is facing.
     */
    private Direction direction;

    /**
     * Which texture this Entity should use.
     */
    private int texture;

    /**
     * The world this Entity belongs to.
     */
    private World parent;

    public Entity(double x, double y, int width, int height, Direction direction, int texture, World parent) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.direction = direction;
        this.texture = texture;
        this.parent = parent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    protected void destroy() {
        destroyed = true;
    }

    public double getX() {
        return x;
    }

    protected void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    protected void setY(double y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Direction getDirection() {
        return direction;
    }

    protected void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getTexture() {
        return texture;
    }

    protected void setTexture(int texture) {
        this.texture = texture;
    }

    protected World getParent() {
        return parent;
    }

    /**
     * Returns true if the specified rectangle intersects this Entity.
     *
     * @param x      the x coordinate of the rectangle's bottom left corner
     * @param y      the y coordinate of the rectangle's bottom left corner
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     * @return whether the specified rectangle intersects this Entity
     */
    public boolean intersects(double x, double y, int width, int height) {
        return this.x < x + width && this.x + width > x && this.y < y + height && this.y + height > y;
    }

    public boolean intersects(Entity entity) {
        return intersects(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
    }

    /**
     * Returns the distance between this Entity and the specified coordinates.
     *
     * @param x the x coordinate to compare with
     * @param y the y coordinate to compare with
     * @return the distance between this Entity and the coordinates, in pixels
     */
    public double distanceTo(double x, double y) {
        return Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
    }

    public double distanceTo(Entity entity) {
        return distanceTo(entity.getX(), entity.getY());
    }

    /**
     * Returns the angle between this Entity and the specified coordinates.
     *
     * @param x the x coordinate to compare with
     * @param y the y coordinate to compare with
     * @return the angle between this Entity and the coordinates, in radians
     */
    public double angleTo(double x, double y) {
        return Math.atan2(y - this.y, x - this.x);
    }

    public double angleTo(Entity entity) {
        return angleTo(entity.getX(), entity.getY());
    }

    /**
     * Returns the direction to the specified coordinates from this Entity.
     *
     * @param x the x coordinate to compare with
     * @param y the y coordinate to compare with
     * @return the direction the coordinates are in relative to this Entity
     */
    public Direction directionTo(double x, double y) {
        double angle = angleTo(x, y);

        if (angle < Math.PI * 3 / 4 && angle >= Math.PI / 4) {
            return Direction.UP;
        } else if (angle < Math.PI / 4 && angle >= -Math.PI / 4) {
            return Direction.RIGHT;
        } else if (angle < -Math.PI / 4 && angle >= -Math.PI * 3 / 4) {
            return Direction.DOWN;
        } else {
            return Direction.LEFT;
        }
    }

    /**
     * Returns the direction to the specified Entity from this Entity.
     *
     * @param entity the Entity to compare with
     * @return the direction the Entity is in relative to this Entity
     */
    public Direction directionTo(Entity entity) {
        return directionTo(entity.getX(), entity.getY());
    }

    public void updateEntity(float delta, Deque<OutboundPacket> outbound) {
        // Set created flag to let clients know this is a new entity.
        if (!created) {
            outbound.addFirst(new CreateOutboundPacket(id, x, y, direction.ordinal(), texture));
            created = true;
        }

        // Position before update.
        double oldX = x, oldY = y;

        // Direction before update.
        Direction oldDirection = direction;

        // Texture before update.
        int oldTexture = texture;

        // Update entity here.
        update(delta, outbound);

        // Set destroyed flag if this entity has been destroyed. It will be removed on the next cycle.
        if (destroyed) {
            outbound.addFirst(new DestroyOutboundPacket(id));
        } else {
            // If position has changed set flag.
            if (oldX != x || oldY != y)
                outbound.addFirst(new PositionOutboundPacket(id, x, y));

            // If direction has changed set flag.
            if (oldDirection != direction)
                outbound.add(new DirectionOutboundPacket(id, direction.ordinal()));

            // If texture has changed set flag.
            if (oldTexture != texture)
                outbound.add(new TextureOutboundPacket(id, texture));
        }
    }

    public abstract void update(float delta, Deque<OutboundPacket> outbound);

    /**
     * Represents the direction this Entity is facing.
     */
    public enum Direction {
        DOWN,
        UP,
        LEFT,
        RIGHT
    }
}
