package com.sdi.ge;

import com.sdi.ge.entity.Entity;
import com.sdi.ge.entity.EntityList;
import com.sdi.ge.entity.Player;
import com.sdi.ge.entity.item.Powerup;
import com.sdi.ge.net.packets.OutboundPacket;
import io.netty.channel.Channel;
import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.TileLayer;
import tiled.io.TMXMapReader;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;

/**
 * Represents the game world, containing entities.
 */
public class World {

    private static final String MAP_FILENAME = "map.tmx";

    /**
     * The maximum number of entities allowed in the game at once.
     */
    private static final int MAX_ENTITIES = 1024;

    private Map map;

    private double startX, startY;

    private TileLayer collisionLayer;

    private EntityList entities;

    private ArrayList<Player> players = new ArrayList<>();

    /**
     * The list of outbound packets on each update.
     */
    private final Deque<OutboundPacket> outbound;

    public World(Deque<OutboundPacket> outbound) throws Exception {
        this.outbound = outbound;

        loadMap();
        loadEntites();
    }

    /**
     * Loads the map.
     *
     * @throws Exception if loading failed
     */
    private void loadMap() throws Exception {
        // Load map from file.
        map = new TMXMapReader().readMap(MAP_FILENAME);

        // Load spawn point.
        startX = Integer.parseInt(map.getProperties().getProperty("startX", "50"));
        startY = Integer.parseInt(map.getProperties().getProperty("startY", "50"));

        // Get collision later.
        if ((collisionLayer = (TileLayer) layerByName("Collision")) == null) {
            throw new IllegalStateException("Invalid map.");
        }
    }

    /**
     * Loads entities.
     */
    private void loadEntites() {
        entities = new EntityList(MAX_ENTITIES);
    }

    /**
     * Returns the width of the map in pixels.
     *
     * @return the width of the map in pixels
     */
    public int getMapWidth() {
        return map.getWidth() * map.getTileWidth();
    }

    /**
     * Returns the height of the map in pixels.
     *
     * @return the height of the map in pixels
     */
    public int getMapHeight() {
        return map.getHeight() * map.getTileHeight();
    }

    /**
     * Returns the width of one tile in pixels.
     *
     * @return the width of one tile in pixels
     */
    public int getTileWidth() {
        return map.getTileWidth();
    }

    /**
     * Returns the height of one tile in pixels.
     *
     * @return the height of one tile in pixels
     */
    public int getTileHeight() {
        return map.getTileHeight();
    }

    public boolean isTileBlocked(double x, double y) {
        return isTileBlocked((int) x / getTileWidth(), (int) y / getTileHeight());
    }

    public boolean isTileBlocked(int tileX, int tileY) {
        return collisionLayer.getTileAt(tileX, map.getHeight() - tileY - 1) != null;
    }

    /**
     * Returns the list of entities in this map.
     *
     * @return the list of entities in this map
     */
    public EntityList getEntities() {
        return entities;
    }

    /**
     * Returns the list of players in this map.
     *
     * @return the list of players in this map
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Player createPlayer(Channel channel) {
        Player player = new Player(startX, startY, this, channel);
        entities.add(player);
        players.add(player);

        // Spawn players away from each other to prevent collisions on start.
        startX += player.getWidth() * 2;

        return player;
    }

    public void createPowerup(double x, double y, Powerup.Type type, float duration) {
        entities.add(new Powerup(x, y, this, type, duration));
    }

    public void update(float delta) {
        for (Iterator<Entity> iterator = entities.iterator(); iterator.hasNext(); ) {
            Entity entity = iterator.next();

            if (entity.isDestroyed()) {
                iterator.remove();

                if (entity instanceof Player)
                    players.remove(entity);
            } else {
                entity.updateEntity(delta, outbound);
            }
        }
    }

    /**
     * Gets a map layer by its name.
     *
     * @param name the name of the layer
     * @return the map layer, or null if there isn't a layer with the specified name
     */
    private MapLayer layerByName(String name) {
        for (MapLayer mapLayer : map.getLayers()) {
            if (mapLayer.getName().equals(name))
                return mapLayer;
        }

        return null;
    }
}
