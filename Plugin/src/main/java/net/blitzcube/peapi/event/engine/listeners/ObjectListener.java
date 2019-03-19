package net.blitzcube.peapi.event.engine.listeners;

import org.bukkit.entity.Player;

import me.dablakbandit.core.players.CorePlayers;
import me.dablakbandit.core.players.packets.WrappedPacketListener;
import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.PacketEntityAPI;
import net.blitzcube.peapi.api.event.IEntityPacketEvent;
import net.blitzcube.peapi.api.packet.IEntityPacket;
import net.blitzcube.peapi.event.EntityPacketEvent;
import net.blitzcube.peapi.event.engine.PacketEventDispatcher;
import net.blitzcube.peapi.packet.EntityPacket;

/**
 * Created by iso2013 on 2/23/2018.
 */
public class ObjectListener extends WrappedPacketListener{
	private final PacketEntityAPI		parent;
	private final PacketEventDispatcher	dispatcher;
	private boolean						sendForFake;
	
	public ObjectListener(PacketEntityAPI parent, PacketEventDispatcher dispatcher){
		this.parent = parent;
		this.dispatcher = dispatcher;
		this.sendForFake = false;
	}
	
	public void setSendForFake(boolean sendForFake){
		this.sendForFake = sendForFake;
	}
	
	private static Class<?>	classPacketPlayOutSpawnEntity				= NMSUtils.getNMSClass("PacketPlayOutSpawnEntity");
	private static Class<?>	classPacketPlayOutSpawnEntityExperienceOrb	= NMSUtils.getNMSClass("PacketPlayOutSpawnEntityExperienceOrb");
	private static Class<?>	classPacketPlayOutSpawnEntityPainting		= NMSUtils.getNMSClass("PacketPlayOutSpawnEntityPainting");
	private static Class<?>	classPacketPlayOutSpawnEntityWeather		= NMSUtils.getNMSClass("PacketPlayOutSpawnEntityWeather");
	
	public boolean isWriteWhitelisted(WrappedPacket packet){
		Class<?> clazz = packet.getRawPacket().getClass();
		return clazz.equals(classPacketPlayOutSpawnEntity) || clazz.equals(classPacketPlayOutSpawnEntityExperienceOrb) || clazz.equals(classPacketPlayOutSpawnEntityPainting) || clazz.equals(classPacketPlayOutSpawnEntityWeather);
	}
	
	@Override
	public boolean readWrapped(CorePlayers cp, WrappedPacket packet){
		return true;
	}
	
	@Override
	public boolean writeWrapped(CorePlayers cp, WrappedPacket packet){
		boolean write = true;
		Player target = cp.getPlayer();
		
		int entityID = packet.getInts().get(0);
		if(!sendForFake && parent.isFakeID(entityID))
			return true;
		
		IEntityPacket w = EntityPacket.unwrapFromType(entityID, IEntityPacketEvent.EntityPacketType.OBJECT_SPAWN, packet, target);
		if(w == null)
			return true;
		IEntityPacketEvent e = new EntityPacketEvent(w, IEntityPacketEvent.EntityPacketType.OBJECT_SPAWN, target);
		dispatcher.dispatch(e, true);
		if(e.isCancelled()){
			write = false;
		}
		return write;
	}
}
