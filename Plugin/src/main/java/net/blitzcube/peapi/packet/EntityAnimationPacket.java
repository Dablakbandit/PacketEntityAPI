package net.blitzcube.peapi.packet;

import java.lang.reflect.Constructor;

import org.bukkit.entity.Player;

import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.IEntityIdentifier;
import net.blitzcube.peapi.api.packet.IEntityAnimationPacket;
import net.blitzcube.peapi.entity.EntityIdentifier;

/**
 * Created by iso2013 on 4/21/2018.
 */
public class EntityAnimationPacket extends EntityPacket implements IEntityAnimationPacket{
	
	private static Class<?>		classPacketPlayOutAnimation	= NMSUtils.getNMSClass("PacketPlayOutAnimation");
	private static Constructor	conPacketPlayOutAnimation	= NMSUtils.getConstructor(classPacketPlayOutAnimation);
	
	public static WrappedPacket getEmptyPacket(){
		try{
			return new WrappedPacket(conPacketPlayOutAnimation.newInstance());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private AnimationType type;
	
	EntityAnimationPacket(IEntityIdentifier identifier, WrappedPacket packet){
		super(identifier, packet, true);
	}
	
	private EntityAnimationPacket(IEntityIdentifier identifier, WrappedPacket packet, AnimationType type){
		super(identifier, packet, false);
		this.type = type;
	}
	
	static EntityPacket unwrap(int entityID, WrappedPacket packet, Player p){
		return new EntityAnimationPacket(new EntityIdentifier(entityID, p), packet, AnimationType.values()[packet.getInts().get(1)]);
	}
	
	@Override
	public AnimationType getAnimation(){
		return type;
	}
	
	@Override
	public void setAnimation(AnimationType type){
		this.type = type;
		wrappedPacket.writeInt(1, type.ordinal());
	}
	
	@Override
	public Object getRawPacket(){
		assert type != null;
		return super.getRawPacket();
	}
	
	@Override
	public EntityPacket clone(){
		EntityAnimationPacket p = new EntityAnimationPacket(getIdentifier(), getEmptyPacket());
		p.setAnimation(type);
		return p;
	}
}
