package net.blitzcube.peapi.event.engine.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import me.dablakbandit.core.players.CorePlayers;
import me.dablakbandit.core.players.packets.WrappedPacketListener;
import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.PacketEntityAPI;
import net.blitzcube.peapi.api.event.IEntityPacketEvent;
import net.blitzcube.peapi.api.packet.*;
import net.blitzcube.peapi.entity.fake.FakeEntity;
import net.blitzcube.peapi.event.EntityPacketEvent;
import net.blitzcube.peapi.event.engine.PacketEventDispatcher;
import net.blitzcube.peapi.packet.EntityClickPacket;
import net.blitzcube.peapi.packet.EntityPacket;

/**
 * Created by iso2013 on 2/24/2018.
 */
public class GenericListener extends WrappedPacketListener{
	private final PacketEntityAPI											parent;
	private final PacketEventDispatcher										dispatcher;
	private boolean															sendForFake;
	private boolean															collidable;
	private static final Map<Class<?>, IEntityPacketEvent.EntityPacketType>	TYPES						= new HashMap<>();
	private static Class<?>													classPacketPlayOutEntity	= NMSUtils.getNMSClass("PacketPlayOutEntity");
	
	static{
		TYPES.put(NMSUtils.getNMSClass("PacketPlayOutEntityMetadata"), IEntityPacketEvent.EntityPacketType.DATA);
		TYPES.put(NMSUtils.getNMSClass("PacketPlayOutEntityEquipment"), IEntityPacketEvent.EntityPacketType.EQUIPMENT);
		TYPES.put(NMSUtils.getNMSClass("PacketPlayOutMount"), IEntityPacketEvent.EntityPacketType.MOUNT);
		TYPES.put(NMSUtils.getNMSClass("PacketPlayOutEntityDestroy"), IEntityPacketEvent.EntityPacketType.DESTROY);
		TYPES.put(NMSUtils.getNMSClass("PacketPlayOutEntityStatus"), IEntityPacketEvent.EntityPacketType.STATUS);
		TYPES.put(NMSUtils.getNMSClass("PacketPlayOutAnimation"), IEntityPacketEvent.EntityPacketType.ANIMATION);
		TYPES.put(NMSUtils.getNMSClass("PacketPlayOutEntityEffect"), IEntityPacketEvent.EntityPacketType.ADD_EFFECT);
		TYPES.put(NMSUtils.getNMSClass("PacketPlayOutRemoveEntityEffect"), IEntityPacketEvent.EntityPacketType.REMOVE_EFFECT);
		TYPES.put(NMSUtils.getNMSClass("PacketPlayOutEntityHeadRotation"), IEntityPacketEvent.EntityPacketType.HEAD_ROTATION);
		TYPES.put(NMSUtils.getInnerClass(classPacketPlayOutEntity, "PacketPlayOutRelEntityMove"), IEntityPacketEvent.EntityPacketType.MOVE);
		TYPES.put(NMSUtils.getInnerClass(classPacketPlayOutEntity, "PacketPlayOutRelEntityMoveLook"), IEntityPacketEvent.EntityPacketType.MOVE);
		TYPES.put(NMSUtils.getInnerClass(classPacketPlayOutEntity, "PacketPlayOutEntityLook"), IEntityPacketEvent.EntityPacketType.MOVE);
		TYPES.put(NMSUtils.getNMSClass("PacketPlayOutEntityTeleport"), IEntityPacketEvent.EntityPacketType.MOVE);
	}
	
	public GenericListener(PacketEntityAPI parent, PacketEventDispatcher dispatcher){
		this.parent = parent;
		this.dispatcher = dispatcher;
		this.sendForFake = false;
		this.collidable = false;
	}
	
	public void setSendForFake(boolean sendForFake){
		this.sendForFake = sendForFake;
	}
	
	public void setCollidable(boolean collidable){
		this.collidable = collidable;
	}
	
	private static Class<?>	classPacketPlayInUseEntity		= NMSUtils.getNMSClass("PacketPlayInUseEntity");
	private static Class<?>	classPacketPlayInArmAnimation	= NMSUtils.getNMSClass("PacketPlayInArmAnimation");
	
	public boolean isWriteWhitelisted(WrappedPacket packet){
		return TYPES.keySet().contains(packet.getRawPacket().getClass());
	}
	
	public boolean isReadWhitelisted(WrappedPacket packet){
		Class<?> clazz = packet.getRawPacket().getClass();
		return clazz.equals(classPacketPlayInUseEntity) || clazz.equals(classPacketPlayInArmAnimation);
	}
	
	@Override
	public boolean readWrapped(CorePlayers pl, WrappedPacket packet){
		boolean read = true;
		Player target = pl.getPlayer();
		
		IEntityPacket w = null;
		if(packet.getRawPacket().getClass().equals(classPacketPlayInUseEntity)){
			int entityID = packet.getInts().get(0);
			
			boolean fake = parent.isFakeID(entityID);
			if(fake){
				if(!sendForFake)
					return read;
			}
			
			w = EntityPacket.unwrapFromType(entityID, IEntityPacketEvent.EntityPacketType.CLICK, packet, target);
		}else if(packet.getRawPacket().getClass().equals(classPacketPlayInArmAnimation)){
			if(!collidable)
				return true;
			FakeEntity lookingAt = parent.getFakeEntities().stream().filter(e -> parent.isVisible(e.getLocation(), target, 1) && e.checkIntersect(target)).findAny().orElse(null);
			if(lookingAt == null)
				return true;
			read = false;
			
			w = new EntityClickPacket(lookingAt.getIdentifier(), packet);
			((EntityClickPacket)w).setClickType(IEntityClickPacket.ClickType.getByEnum(packet.getEnums().get(0)));
		}
		IEntityPacketEvent e = new EntityPacketEvent(w, IEntityPacketEvent.EntityPacketType.CLICK, target);
		dispatcher.dispatch(e, null);
		if(e.isCancelled()){
			read = false;
		}
		return read;
	}
	
	@Override
	public boolean writeWrapped(CorePlayers pl, WrappedPacket packet){
		boolean write = true;
		Player target = pl.getPlayer();
		
		IEntityPacketEvent.EntityPacketType eT = TYPES.get(packet.getRawPacket().getClass());
		int entityID = 0;
		if(!eT.equals(IEntityPacketEvent.EntityPacketType.DESTROY)){
			entityID = packet.getInts().get(0);
			if(!sendForFake && parent.isFakeID(entityID))
				return true;
		}
		
		IEntityPacket w = EntityPacket.unwrapFromType(entityID, eT, packet, target);
		if(w == null)
			return true;
		IEntityPacketEvent e = new EntityPacketEvent(w, eT, target);
		dispatcher.dispatch(e, null);
		if(w instanceof IEntityGroupPacket){
			if(w instanceof IEntityDestroyPacket && ((IEntityDestroyPacket)w).getGroup().size() == 0){ return false; }
			((IEntityGroupPacket)e.getPacket()).apply();
		}else if(w instanceof IEntityDataPacket){
			if(((IEntityDataPacket)w).getMetadata().size() == 0){ return write; }
		}
		if(e.isCancelled()){
			write = false;
		}
		if(w.getRawPacket() != packet.getRawPacket()){
			packet.setRawPacket(w.getRawPacket());
		}
		return write;
	}
}
