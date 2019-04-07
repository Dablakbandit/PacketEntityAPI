package net.blitzcube.peapi.packet;

import org.bukkit.entity.Player;

import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import net.blitzcube.peapi.api.entity.IEntityIdentifier;
import net.blitzcube.peapi.api.event.IEntityPacketEvent;
import net.blitzcube.peapi.api.packet.IEntityPacket;

/**
 * Created by iso2013 on 4/21/2018.
 */
public abstract class EntityPacket implements IEntityPacket{
	
	protected WrappedPacket		wrappedPacket;
	private IEntityIdentifier	identifier;
	protected Class<?>			classPacket;
	
	EntityPacket(IEntityIdentifier identifier, WrappedPacket wrappedPacket, boolean writeDefaults){
		this.identifier = identifier;
		this.wrappedPacket = wrappedPacket;
		this.classPacket = wrappedPacket.getRawPacket().getClass();
		if(writeDefaults){
			// TODO ? this.rawPacket.getModifier().writeDefaults();
		}
		setIdentifier(identifier);
	}
	
	public static EntityPacket unwrapFromType(int entityID, IEntityPacketEvent.EntityPacketType type, WrappedPacket packet, Player target){
		switch(type){
		case ANIMATION:
			return EntityAnimationPacket.unwrap(entityID, packet, target);
		case CLICK:
			return EntityClickPacket.unwrap(entityID, packet, target);
		case DATA:
			return EntityDataPacket.unwrap(entityID, packet, target);
		case DESTROY:
			return EntityDestroyPacket.unwrap(packet, target);
		case EQUIPMENT:
			return EntityEquipmentPacket.unwrap(entityID, packet, target);
		case ENTITY_SPAWN:
			return EntitySpawnPacket.unwrap(entityID, packet, target);
		case MOUNT:
			return EntityMountPacket.unwrap(entityID, packet, target);
		case STATUS:
			return EntityStatusPacket.unwrap(entityID, packet, target);
		case OBJECT_SPAWN:
			return ObjectSpawnPacket.unwrap(entityID, packet, target);
		case ADD_EFFECT:
			return EntityPotionAddPacket.unwrap(entityID, packet, target);
		case REMOVE_EFFECT:
			return EntityPotionRemovePacket.unwrap(entityID, packet, target);
		case MOVE:
			return EntityMovePacket.unwrap(entityID, packet, target);
		case HEAD_ROTATION:
			return EntityHeadRotationPacket.unwrap(entityID, packet, target);
		}
		return null;
	}
	
	@Override
	public IEntityIdentifier getIdentifier(){
		return identifier;
	}
	
	@Override
	public void setIdentifier(IEntityIdentifier identifier){
		if(identifier != null){
			try{
				this.identifier = identifier;
				wrappedPacket.writeInt(0, identifier.getEntityID());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public WrappedPacket getWrappedPacket(){
		return wrappedPacket;
	}
	
	@Override
	public Object getRawPacket(){
		return wrappedPacket.getRawPacket();
	}
	
	@Override
	public abstract EntityPacket clone();
}
