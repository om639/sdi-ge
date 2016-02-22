package com.sdi.ge.entity;

import com.sdi.ge.World;
import com.sdi.ge.net.packets.OutboundPacket;

import java.util.Deque;

/**
 * Created by Oliver on 19/02/2016.
 */
public class MobileEntity extends Entity {

    private double velocityX, velocityY;

    public MobileEntity(double x, double y, int width, int height, Direction direction, int texture, World parent, double velocityX, double velocityY) {
        super(x, y, width, height, direction, texture, parent);

        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public MobileEntity(double x, double y, int width, int height, Direction direction, int texture, World parent) {
        this(x, y, width, height, direction, texture, parent, 0, 0);
    }

    public double getVelocityX() {
        return velocityX;
    }

    protected void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    protected void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    /**
     * Ensures that this MobileEntity stays within the map area.
     */
    public void checkBounds() {
        if (getX() < 0) {
            setX(0);
        } else if (getX() > getParent().getMapWidth() - getWidth()) {
            setX(getParent().getMapWidth() - getWidth());
        }

        if (getY() < 0) {
            setY(0);
        } else if (getY() > getParent().getMapHeight() - getHeight()) {
            setY(getParent().getMapHeight() - getHeight());
        }
    }

    /**
     * Gets whether the specified x delta will cause a collision on the left or right.
     *
     * @param deltaX the x delta
     * @return whether a collision would occur on the left or right
     */
    public boolean collidesX(double deltaX) {
        World parent = getParent();

        // Check for entity collisions.
        for (Entity entity : parent.getEntities()) {
            if (entity != this && entity instanceof Character && entity.intersects(getX() + deltaX, getY(), getWidth(), getHeight())) {
                return true;
            }
        }

        // Check for tile collisions.
        double newLeft = Math.floor(getX() + deltaX), newRight = Math.floor(getX() + getWidth() + deltaX);

        // If entity is smaller than tile we can just check to see if each corner collides instead of all points along the edge.
        if (getHeight() <= parent.getTileHeight()) {
            return parent.isTileBlocked(newLeft, getY()) || parent.isTileBlocked(newRight, getY()) ||
                    parent.isTileBlocked(newLeft, getY() + getHeight()) || parent.isTileBlocked(newRight, getY() + getHeight());
        } else {
            for (int i = (int) getY(); i < getY() + getHeight(); i++) {
                if (parent.isTileBlocked(newLeft, i) || parent.isTileBlocked(newRight, i)) {
                    return true;
                }
            }
        }

        // No collisions!
        return false;
    }

    /**
     * Gets whether the specified y delta will cause a collision on the bottom or top.
     *
     * @param deltaY the y delta
     * @return whether a collision would occur on the bottom or top
     */
    public boolean collidesY(double deltaY) {
        World parent = getParent();

        // Check for Character collisions.
        for (Entity entity : parent.getEntities()) {
            if (entity != this && entity instanceof Character && entity.intersects(getX(), getY() + deltaY, getWidth(), getHeight())) {
                return true;
            }
        }

        // Check for tile collisions.
        double newBottom = Math.floor(getY() + deltaY), newTop = Math.floor(getY() + getHeight() + deltaY);

        // If entity is smaller than tile we can just check to see if each corner collides instead of all points along the edge.
        if (getWidth() <= parent.getTileWidth()) {
            return parent.isTileBlocked(getX(), newBottom) || parent.isTileBlocked(getX(), newTop) ||
                    parent.isTileBlocked(getX() + getWidth(), newBottom) || parent.isTileBlocked(getX() + getWidth(), newTop);
        } else {
            for (int i = (int) getX(); i < getX() + getWidth(); i++) {
                if (parent.isTileBlocked(i, newBottom) || parent.isTileBlocked(i, newTop)) {
                    return true;
                }
            }
        }

        // No collisions!
        return false;
    }

//    /**
//     * Gets whether specified x delta will cause a collision from an arbitrary position
//     * Used in AI path detection.
//     *
//     * @param deltaX the x delta
//     * @param fromX  arbitrary x position
//     * @param fromY  arbitrary y position
//     * @return whether collides
//     */
//    public boolean collidesXfrom(double deltaX, double fromX, double fromY) {
//        double tempX = this.x;
//        double tempY = this.y;
//        this.x = fromX;
//        this.y = fromY;
//        boolean result = collidesX(deltaX);
//        this.x = tempX;
//        this.y = tempY;
//        return result;
//    }

//    /**
//     * Gets whether specified y delta will cause a collision from an arbitrary position
//     * Used in AI path detection.
//     *
//     * @param deltaY the y delta
//     * @param fromX  arbitrary x position
//     * @param fromY  arbitrary y position
//     * @return whether collides
//     */
//    public boolean collidesYfrom(double deltaY, double fromX, double fromY) {
//        double tempX = this.x;
//        double tempY = this.y;
//        this.x = fromX;
//        this.y = fromY;
//        boolean result = collidesY(deltaY);
//        this.x = tempX;
//        this.y = tempY;
//        return result;
//    }

    /**
     * Updates the state of this MobileEntity.
     *
     * @param delta how much time has passed since the last update
     */
    @Override
    public void update(float delta, Deque<OutboundPacket> outbound) {
        // Update direction.
        if (velocityX < 0) {
            setDirection(Direction.LEFT);
        } else if (velocityX > 0) {
            setDirection(Direction.RIGHT);
        }

        if (velocityY < 0) {
            setDirection(Direction.DOWN);
        } else if (velocityY > 0) {
            setDirection(Direction.UP);
        }

        // Collision detection.
        double deltaX = velocityX * delta;
        double deltaY = velocityY * delta;

        if (deltaX != 0 && collidesX(deltaX)) {
            deltaX = 0;
        }

        if (deltaY != 0 && collidesY(deltaY)) {
            deltaY = 0;
        }

        setX(getX() + deltaX);
        setY(getY() + deltaY);
        checkBounds();
    }
}
