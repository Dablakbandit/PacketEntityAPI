package net.blitzcube.peapi.packet;

import java.lang.reflect.Constructor;

import org.bukkit.entity.Player;

import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.IEntityIdentifier;
import net.blitzcube.peapi.api.packet.IEntityClickPacket;
import net.blitzcube.peapi.entity.EntityIdentifier;

/**
 * Created by iso2013 on 4/21/2018.
 */
public class EntityClickPacket extends EntityPacket implements IEntityClickPacket{
	
	private static Class<?>		classPacketPlayInUseEntity	= NMSUtils.getNMSClass("PacketPlayInUseEntity");
	private static Class<?>		classPacketPlayInUseItem	= NMSUtils.getNMSClass("PacketPlayInUseItem");
	private static Constructor	conPacketPlayInUseEntity	= NMSUtils.getConstructor(classPacketPlayInUseEntity);
	
	public static WrappedPacket getEmptyPacket(){
		try{
			return new WrappedPacket(conPacketPlayInUseEntity.newInstance());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private ClickType type;
	
	public EntityClickPacket(IEntityIdentifier identifier, WrappedPacket packet){
		super(identifier, packet, true);
	}
	
	public EntityClickPacket(IEntityIdentifier identifier, WrappedPacket rawPacket, ClickType type){
		super(identifier, rawPacket, false);
		this.type = type;
	}
	
	static EntityPacket unwrap(int entityID, WrappedPacket packet, Player p){
		return new EntityClickPacket(new EntityIdentifier(entityID, p), packet, ClickType.getByEnum(packet.getEnums().get(0)));
	}
	
	@Override
	public ClickType getClickType(){
		return type;
	}
	
	@Override
	public void setClickType(ClickType type){
		this.type = type;
		wrappedPacket.writeEnum(getRawPacket().getClass().equals(classPacketPlayInUseItem) ? 1 : 0, type.getPacketEquivalent(getRawPacket().getClass()));
	}
	
	@Override
	public Object getRawPacket(){
		return super.getRawPacket();
	}
	
	@Override
	public EntityPacket clone(){
		EntityClickPacket p = new EntityClickPacket(getIdentifier(), getEmptyPacket());
		p.setClickType(type);
		return p;
	}
}
