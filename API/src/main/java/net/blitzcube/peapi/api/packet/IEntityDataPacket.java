package net.blitzcube.peapi.api.packet;

import java.util.List;

import me.dablakbandit.core.server.packet.wrapped.WrappedObject;

/**
 * @author iso2013
 * @version 0.1
 * @since 2018-04-21
 */
public interface IEntityDataPacket extends IEntityPacket{
	/**
	 * Gets a list containing all of the metadata stored in this packet
	 *
	 * @return the list of wrapped watchable objects
	 */
	List<WrappedObject> getMetadata();
	
	/**
	 * Sets the metadata contained by this packet to the data contained in the list given
	 *
	 * @param data the new list of data to send
	 */
	void setMetadata(List<WrappedObject> data);
	
	/**
	 * Rewrites the currently stored metadata to the packet. This is useful if you pull the list instance using
	 * {@link #getMetadata()} and modify it directly; it will cause the changes to be written to the underlying packet
	 * container.
	 */
	void rewriteMetadata();
}
