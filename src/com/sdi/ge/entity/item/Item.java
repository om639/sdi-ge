package com.sdi.ge.entity.item;

import com.sdi.ge.World;
import com.sdi.ge.entity.Entity;
import com.sdi.ge.entity.Player;
import com.sdi.ge.net.packets.OutboundPacket;

import java.util.Deque;

/**
 * Created by Oliver on 21/02/2016.
 */
public abstract class Item extends Entity {

    public Item(double x, double y, int width, int height, Direction direction, int texture, World parent) {
        super(x, y, width, height, direction, texture, parent);
    }

    @Override
    public void update(float delta, Deque<OutboundPacket> outbound) {
        for (Player player : getParent().getPlayers()) {
            if (intersects(player)) {
                pickup(player);
                break;
            }
        }
    }

    public abstract void pickup(Player player);
}
