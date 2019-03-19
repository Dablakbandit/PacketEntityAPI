package net.blitzcube.peapi.event;

import org.bukkit.entity.Player;

import net.blitzcube.peapi.PacketEntityAPI;
import net.blitzcube.peapi.api.event.IEntityPacketContext;
import net.blitzcube.peapi.api.event.IEntityPacketEvent;
import net.blitzcube.peapi.api.packet.IEntityPacket;
import net.blitzcube.peapi.entity.EntityPacketContext;

/**
 * Created by iso2013 on 4/21/2018.
 */
public class EntityPacketEvent implements IEntityPacketEvent{
	private final IEntityPacket		packet;
	private final EntityPacketType	packetType;
	private final Player			target;
	private boolean					cancelled;
	private IEntityPacketContext	context;
	
	public EntityPacketEvent(IEntityPacket packet, EntityPacketType packetType, Player target){
		this.packet = packet;
		this.packetType = packetType;
		this.target = target;
		this.context = new EntityPacketContext(PacketEntityAPI.getChainFactory(), target);
	}
	
	@Override
	public boolean isCancelled(){
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled){
		this.cancelled = cancelled;
	}
	
	@Override
	public Player getPlayer(){
		return target;
	}
	
	@Override
	public IEntityPacket getPacket(){
		return packet;
	}
	
	@Override
	public EntityPacketType getPacketType(){
		return packetType;
	}
	
	@Override
	public IEntityPacketContext context(){
		return context;
	}
}
