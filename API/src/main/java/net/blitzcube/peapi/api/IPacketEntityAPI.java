package net.blitzcube.peapi.api;

import java.util.Collection;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.blitzcube.peapi.api.entity.IEntityIdentifier;
import net.blitzcube.peapi.api.entity.fake.IFakeEntity;
import net.blitzcube.peapi.api.entity.fake.IFakeEntityFactory;
import net.blitzcube.peapi.api.entity.modifier.IEntityModifierRegistry;
import net.blitzcube.peapi.api.listener.IListener;
import net.blitzcube.peapi.api.packet.IEntityPacket;
import net.blitzcube.peapi.api.packet.IEntityPacketFactory;

/**
 * @author iso2013
 * @version 0.1
 * @since 2018-02-13
 */
public interface IPacketEntityAPI{
	/**
	 * Add a new listener to be called when a packet event is fired.
	 *
	 * @param eventListener the new listener to add
	 */
	void addListener(IListener eventListener);
	
	/**
	 * Remove a listener from the queue of listeners to be fired
	 *
	 * @param eventListener the listener to remove
	 */
	void removeListener(IListener eventListener);
	
	/**
	 * Creates a new entity identifier object for the given entity
	 *
	 * @param e the entity that should be identified
	 * @return the new identifier
	 */
	IEntityIdentifier wrap(Entity e);
	
	/**
	 * Check if a listener has already been registered
	 *
	 * @param eventListener the event listener to check
	 * @return whether or not it was found in the queue
	 */
	boolean isListenerRegistered(IListener eventListener);
	
	/**
	 * Check if the entity ID given belongs to a fake entity that has been created by this API.
	 *
	 * @param entityID the entity ID to check
	 * @return whether or not it is a fake entity
	 */
	boolean isFakeID(int entityID);
	
	/**
	 * Gets the modifier registry for this instance of the API.
	 *
	 * @return the modifier registry
	 */
	IEntityModifierRegistry getModifierRegistry();
	
	/**
	 * Gets the entity factory for this instance of the API.
	 *
	 * @return the entity factory
	 */
	IFakeEntityFactory getEntityFactory();
	
	/**
	 * Get a fake entity by its entity ID.
	 *
	 * @param entityID the entity ID to retrieve
	 * @return the fake entity object, or null if the ID does not belong to a fake entity created by this API.
	 */
	IFakeEntity getFakeByID(int entityID);
	
	/**
	 * Checks whether or not an entity at the given location would be visible to a player, based on the server's entity
	 * render distance settings.
	 *
	 * @param location the location of the entity
	 * @param target   the player who is viewing the entity
	 * @param err      the multiplier to multiply the max distance by before performing the calculation, use this to
	 *                 ensure a
	 *                 value is obtained even if the server is lagging slightly
	 * @return whether or not the player would be able to see an entity at that location
	 */
	boolean isVisible(Location location, Player target, double err);
	
	/**
	 * Get a stream representing all of the entities that the given player can see.
	 *
	 * @param viewer the player who is viewing the entities
	 * @param err    the error factor to include in distance calculations. Should be 1 ~ 1.03.
	 * @param fake   whether or not to include fake entities
	 * @return the stream of entity identifiers
	 */
	Stream<IEntityIdentifier> getVisible(Player viewer, double err, boolean fake);
	
	/**
	 * Get a stream representing all of the players that can see a given entity.
	 *
	 * @param object the entity to calculate for
	 * @param err    the error factor to include in distance calculations. Should be 1 ~ 1.03.
	 * @return the stream of players
	 */
	Stream<Player> getViewers(IEntityIdentifier object, double err);
	
	/**
	 * Gets the packet factory for this instance of the API.
	 *
	 * @return the packet factory
	 */
	IEntityPacketFactory getPacketFactory();
	
	void dispatchPacket(Player target, IEntityPacket... packets);
	
	void dispatchPacket(Collection<Player> targets, IEntityPacket... packets);
	
	void dispatchPacket(Collection<Player> targets, Collection<IEntityPacket> packets);
	
	void dispatchPacket(Player target, int delay, IEntityPacket... packets);
	
	void dispatchPacket(Player target, int delay, Collection<IEntityPacket> packets);
	
	void dispatchPacket(Collection<Player> targets, int delay, IEntityPacket... packets);
}
