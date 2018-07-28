package net.blitzcube.peapi.api.entity.fake;

import net.blitzcube.peapi.api.entity.hitbox.IHitbox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

/**
 * @author iso2013
 * @version 0.1
 * @since 2018-04-23
 */
public interface IFakeEntityFactory {
    /**
     * Create a hitbox that matches the hitbox of a given entity type. This will use reflection to pull from NMS if the
     * value has not been calculated already.
     *
     * @deprecated Use {@link #createHitboxFromEntity(Entity)} for real entities. This method is not stable for
     * certain types.
     * @param type the type of the entity to match the hitbox to
     * @return the generated hitbox, or null if it could not be produced.
     */
    @Deprecated
    IHitbox createHitboxFromType(EntityType type);

    /**
     * Create a hitbox that matches the hitbox of a given entity.
     *
     * @param entity the entity to match the hitbox to
     * @return the generated hitbox, or null if it could not be produced.
     */
    IHitbox createHitboxFromEntity(Entity entity);

    /**
     * Create a hitbox using a minimum and maximum {@link Vector}. The origin should be considered to be at the center
     * of the entity's feet.
     *
     * @param min the minimum XYZ vector
     * @param max the maximum XYZ vector
     * @return the generated hitbox
     */
    IHitbox createHitbox(Vector min, Vector max);

    /**
     * Creates a fake entity of the given type. By default, this does <b>NOT</b> include the modifiers. The metadata of
     * the entity will not be pre-populated with the default values. To do this, use
     * {@link #createFakeEntity(EntityType, boolean)}. This fake entity will be pre-populated with a hitbox generated by
     * {@link #createHitboxFromType(EntityType)}. It will also be given an entity ID and unique ID.
     *
     * @param type the type of the entity that should be created
     * @return the generated fake entity
     */
    IFakeEntity createFakeEntity(EntityType type);

    /**
     * Creates a fake entity of the given type. If desired, this method can also populate a map containing the possible
     * entity metadata values for this entity type. This fake entity will be pre-populated with a hitbox generated by
     * {@link #createHitboxFromType(EntityType)}. It will also be given an entity ID and unique ID.
     *
     * @param type            the type of the entity that should be created
     * @param lookupModifiers whether or not to pre-populate the metadata field map
     * @return the generated fake entity
     */
    IFakeEntity createFakeEntity(EntityType type, boolean lookupModifiers);

    /**
     * Returns true if the entity ID given belongs to a fake entity produced by this factory.
     * @param entityID the entity ID to check
     * @return whether or not the entity is fake
     */
    boolean isFakeEntity(int entityID);

    /**
     * Gets a {@link IFakeEntity} object by its entity ID.
     * @param entityID the entity ID to retrieve
     * @return the retrieved fake entity object.
     */
    IFakeEntity getFakeByID(int entityID);
}
