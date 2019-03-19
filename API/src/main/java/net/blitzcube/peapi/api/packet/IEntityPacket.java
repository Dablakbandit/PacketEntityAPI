package net.blitzcube.peapi.api.packet;

import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import net.blitzcube.peapi.api.entity.IEntityIdentifier;

/**
 * @author iso2013
 * @version 0.1
 * @since 2018-04-21
 */
public interface IEntityPacket{
	/**
	 * Get the identifier that this packet is acting on. This will be null for entity destroy packets
	 *
	 * @return the identifier of the entity being changed by this packet
	 */
	IEntityIdentifier getIdentifier();
	
	/**
	 * Set the identifier that this packet is acting on. Null values will be ignored, and the underlying packet will
	 * not change.
	 *
	 * @param identifier the identifier of the entity being changed by this packet.
	 */
	void setIdentifier(IEntityIdentifier identifier);
	
	WrappedPacket getWrappedPacket();
	
	/**
	 * Gets the raw packet that is used by the engine to send this packet to the client.
	 *
	 * @return the raw packet with modifications
	 */
	Object getRawPacket();
	
	/**
	 * Creates a copy of this packet
	 *
	 * @return The new copy of this packet
	 */
	IEntityPacket clone();
}
