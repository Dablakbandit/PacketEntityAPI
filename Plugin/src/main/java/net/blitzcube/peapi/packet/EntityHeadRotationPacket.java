/*
 * Copyright (c) 2019 Ashley Thew
 */

package net.blitzcube.peapi.packet;

import java.lang.reflect.Constructor;

import org.bukkit.entity.Player;

import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.IEntityIdentifier;
import net.blitzcube.peapi.api.packet.IEntityHeadRotationPacket;
import net.blitzcube.peapi.entity.EntityIdentifier;

public class EntityHeadRotationPacket extends EntityPacket implements IEntityHeadRotationPacket{
	
	private static Class<?>		classPacketPlayOutEntityHeadRotation	= NMSUtils.getNMSClass("PacketPlayOutEntityHeadRotation");
	private static Constructor	conPacketPlayOutEntityHeadRotation		= NMSUtils.getConstructor(classPacketPlayOutEntityHeadRotation);
	
	public static WrappedPacket getEmptyPacket(){
		try{
			return new WrappedPacket(conPacketPlayOutEntityHeadRotation.newInstance());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private double yaw = 0;
	
	public EntityHeadRotationPacket(IEntityIdentifier identifier, WrappedPacket packet){
		super(identifier, packet, false);
	}
	
	public static EntityPacket unwrap(int entityID, WrappedPacket rawPacket, Player p){
		EntityHeadRotationPacket packet = new EntityHeadRotationPacket(new EntityIdentifier(entityID, p), rawPacket);
		packet.setYaw(rawPacket.getBytes().get(0));
		return packet;
	}
	
	@Override
	public double getYaw(){
		return yaw;
	}
	
	@Override
	public void setYaw(double yaw){
		this.yaw = yaw;
		wrappedPacket.writeByte(0, (byte)yaw);
		
	}
	
	@Override
	public EntityPacket clone(){
		EntityHeadRotationPacket p = new EntityHeadRotationPacket(getIdentifier(), getEmptyPacket());
		p.setYaw(getYaw());
		return p;
	}
}
