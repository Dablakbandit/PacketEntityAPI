package net.blitzcube.peapi.api.event;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

/**
 * Created by iso2013 on 2/24/2018.
 */
public interface IPacketObjectSpawnEvent extends IPacketEntityEvent {
    EntityType getEntityType();

    void setEntityType(EntityType type);

    Location getLocation();

    void setLocation(Location location);

    int getData();

    void setData(int data);

    Vector getVelocity();

    void setVelocity(Vector velocity);

    Integer getOrbCount();

    void setOrbCount(Integer count);

    String getTitle();

    void setTitle(String title);

    BlockFace getDirection();

    void setDirection(BlockFace direction);
}
