package net.blitzcube.peapi.packet;

import java.lang.reflect.Constructor;

import org.bukkit.entity.Player;

import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.IEntityIdentifier;
import net.blitzcube.peapi.api.packet.IEntityStatusPacket;
import net.blitzcube.peapi.entity.EntityIdentifier;

/**
 * Created by iso2013 on 4/21/2018.
 */
public class EntityStatusPacket extends EntityPacket implements IEntityStatusPacket{
	
	private static Class<?>		classEntityStatusPacket	= NMSUtils.getNMSClass("ClassEntityStatusPacket");
	private static Constructor	conEntityStatusPacket	= NMSUtils.getConstructor(classEntityStatusPacket);
	
	public static WrappedPacket getEmptyPacket(){
		try{
			return new WrappedPacket(conEntityStatusPacket.newInstance());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private byte status;
	
	EntityStatusPacket(IEntityIdentifier identifier, WrappedPacket packet){
		super(identifier, packet, true);
		this.status = -1;
	}
	
	private EntityStatusPacket(IEntityIdentifier identifier, WrappedPacket rawPacket, byte status){
		super(identifier, rawPacket, false);
		this.status = status;
	}
	
	public static EntityPacket unwrap(int entityID, WrappedPacket packet, Player p){
		return new EntityStatusPacket(new EntityIdentifier(entityID, p), packet, packet.getBytes().get(0));
	}
	
	@Override
	public byte getStatus(){
		return status;
	}
	
	@Override
	public void setStatus(byte status){
		this.status = status;
		super.wrappedPacket.writeByte(0, status);
	}
	
	@Override
	public Object getRawPacket(){
		assert status > 0;
		return super.getRawPacket();
	}
	
	@Override
	public EntityPacket clone(){
		EntityStatusPacket p = new EntityStatusPacket(getIdentifier(), getEmptyPacket());
		p.setStatus(status);
		return p;
	}
}
