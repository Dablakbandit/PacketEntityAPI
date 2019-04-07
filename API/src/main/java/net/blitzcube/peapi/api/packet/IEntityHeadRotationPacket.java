/*
 * Copyright (c) 2019 Ashley Thew
 */

package net.blitzcube.peapi.api.packet;

public interface IEntityHeadRotationPacket extends IEntityPacket{
	
	/**
	 * @return the yaw for the new direction
	 */
	double getYaw();
	
	/**
	 * Set the pitch and yaw for the new direction.
	 *
	 * @param yaw   the yaw to set
	 */
	void setYaw(double yaw);
}
