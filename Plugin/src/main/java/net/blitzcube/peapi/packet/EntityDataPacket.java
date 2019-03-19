package net.blitzcube.peapi.packet;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import me.dablakbandit.core.server.packet.wrapped.WrappedObject;
import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.IEntityIdentifier;
import net.blitzcube.peapi.api.packet.IEntityDataPacket;
import net.blitzcube.peapi.entity.EntityIdentifier;

/**
 * Created by iso2013 on 4/21/2018.
 */
public class EntityDataPacket extends EntityPacket implements IEntityDataPacket{
	
	private static Class<?>		classPacketPlayOutEntityData	= NMSUtils.getClass("PacketPlayOutEntityData");
	private static Constructor	conPacketPlayOutEntityData		= NMSUtils.getConstructor(classPacketPlayOutEntityData);
	
	public static WrappedPacket getEmptyPacket(){
		try{
			return new WrappedPacket(conPacketPlayOutEntityData.newInstance());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private List<WrappedObject> metadata;
	
	EntityDataPacket(IEntityIdentifier identifier, WrappedPacket wrappedPacket){
		super(identifier, wrappedPacket, true);
	}
	
	private EntityDataPacket(IEntityIdentifier identifier, WrappedPacket wrappedPacket, List<WrappedObject> metadata){
		super(identifier, wrappedPacket, false);
		this.metadata = metadata;
	}
	
	public static EntityPacket unwrap(int entityID, WrappedPacket packet, Player p){
		return new EntityDataPacket(new EntityIdentifier(entityID, p), packet, packet.getLists().get(0));
	}
	
	@Override
	public List<WrappedObject> getMetadata(){
		return metadata;
	}
	
	@Override
	public void setMetadata(List<WrappedObject> metadata){
		this.metadata = metadata;
		super.wrappedPacket.writeList(0, metadata);
	}
	
	@Override
	public void rewriteMetadata(){
		super.wrappedPacket.writeList(0, metadata);
	}
	
	@Override
	public Object getRawPacket(){
		assert metadata != null && metadata.size() > 0;
		return super.getRawPacket();
	}
	
	@Override
	public EntityPacket clone(){
		EntityDataPacket p = new EntityDataPacket(getIdentifier(), getEmptyPacket());
		p.setMetadata(new ArrayList(metadata));
		return p;
	}
}
