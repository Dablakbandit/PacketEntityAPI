package net.blitzcube.peapi.event.engine.listeners;

import org.bukkit.entity.Player;

import me.dablakbandit.core.players.CorePlayers;
import me.dablakbandit.core.players.packets.WrappedPacketListener;
import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.PacketEntityAPI;
import net.blitzcube.peapi.api.event.IEntityPacketEvent;
import net.blitzcube.peapi.event.EntityPacketEvent;
import net.blitzcube.peapi.event.engine.PacketEventDispatcher;
import net.blitzcube.peapi.packet.EntityPacket;

/**
 * Created by iso2013 on 2/23/2018.
 */
public class EntityListener extends WrappedPacketListener{
	private final PacketEntityAPI		parent;
	private final PacketEventDispatcher	dispatcher;
	private boolean						sendForFake;
	
	public EntityListener(PacketEntityAPI parent, PacketEventDispatcher dispatcher){
		this.parent = parent;
		this.dispatcher = dispatcher;
		this.sendForFake = false;
	}
	
	public void setSendForFake(boolean sendForFake){
		this.sendForFake = sendForFake;
	}
	
	@Override
	public boolean readWrapped(CorePlayers cp, WrappedPacket packet){
		return true;
	}
	
	private static Class<?>	classPacketPlayOutSpawnEntityLiving	= NMSUtils.getNMSClass("PacketPlayOutSpawnEntityLiving");
	private static Class<?>	classPacketPlayOutNamedEntitySpawn	= NMSUtils.getNMSClass("PacketPlayOutNamedEntitySpawn");
	
	public boolean isWriteWhitelisted(WrappedPacket packet){
		Class<?> clazz = packet.getRawPacket().getClass();
		return clazz.equals(classPacketPlayOutSpawnEntityLiving) || clazz.equals(classPacketPlayOutNamedEntitySpawn);
	}
	
	@Override
	public boolean writeWrapped(CorePlayers cp, WrappedPacket packet){
		boolean write = true;
		Player target = cp.getPlayer();
		
		int entityID = packet.getInts().get(0);
		if(!sendForFake && parent.isFakeID(entityID))
			return true;
		
		IEntityPacketEvent e = new EntityPacketEvent(EntityPacket.unwrapFromType(entityID, IEntityPacketEvent.EntityPacketType.ENTITY_SPAWN, packet, target), IEntityPacketEvent.EntityPacketType.ENTITY_SPAWN, target);
		if(e == null)
			return true;
		dispatcher.dispatch(e, false);
		if(e.isCancelled()){
			write = false;
		}
		return write;
	}
}
