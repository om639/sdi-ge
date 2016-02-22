package com.sdi.ge.entity.item;

import com.sdi.ge.World;
import com.sdi.ge.entity.Player;

/**
 * Created by Oliver on 21/02/2016.
 */
public class Powerup extends Item {

    public static final double POWERUP_SUPER_SPEED_MULTIPLIER = 2;

    private Type type;

    private float duration;

    public Powerup(double x, double y, World parent, Type type, float duration) {
        super(x, y, 15, 15, Direction.DOWN, type.getTexture(), parent);
        this.type = type;
        this.duration = duration;
    }

    @Override
    public void pickup(Player player) {
        player.setPowerup(type, duration);
        destroy();
    }

    /**
     * Represents the different types of powerups.
     */
    public enum Type {
        SCORE_MULTIPLIER(5),
        SUPER_SPEED(6),
        RATE_OF_FIRE(7),
        INVULNERABLE(8),
        REGENERATION(9);

        private static Type[] values = values();

        private int texture;

        Type(int texture) {
            this.texture = texture;
        }

        public int getTexture() {
            return texture;
        }

        public static int count() {
            return values.length;
        }

        public static Type forOrdinal(int ordinal) {
            return values[ordinal];
        }
    }
}
