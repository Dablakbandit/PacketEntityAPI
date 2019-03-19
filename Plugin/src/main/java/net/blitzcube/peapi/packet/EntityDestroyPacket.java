package net.blitzcube.peapi.packet;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;

import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.IEntityIdentifier;
import net.blitzcube.peapi.api.packet.IEntityDestroyPacket;
import net.blitzcube.peapi.entity.EntityIdentifier;

/**
 * Created by iso2013 on 4/21/2018.
 */
public class EntityDestroyPacket extends EntityPacket implements IEntityDestroyPacket{
	
	private static Class<?>			classPacketPlayOutEntityDestroy	= NMSUtils.getClass("PacketPlayOutEntityDestroy");
	private static Constructor<?>	conPacketPlayOutEntityDestroy	= NMSUtils.getConstructor(classPacketPlayOutEntityDestroy);
	
	public static WrappedPacket getEmptyPacket(){
		try{
			return new WrappedPacket(conPacketPlayOutEntityDestroy.newInstance());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private final List<IEntityIdentifier>	targets;
	private boolean							changed	= false;
	
	EntityDestroyPacket(WrappedPacket packet){
		super(null, packet, true);
		this.targets = new ArrayList<>();
	}
	
	private EntityDestroyPacket(WrappedPacket rawPacket, List<IEntityIdentifier> targets){
		super(null, rawPacket, false);
		this.targets = targets;
	}
	
	public static EntityPacket unwrap(WrappedPacket packet, Player p){
		return new EntityDestroyPacket(packet, Arrays.stream(packet.getIntArrays().get(0)).mapToObj(value -> new EntityIdentifier(value, p)).collect(Collectors.toList()));
	}
	
	@Override
	public ImmutableList<IEntityIdentifier> getGroup(){
		return ImmutableList.copyOf(targets);
	}
	
	@Override
	public void removeFromGroup(IEntityIdentifier e){
		targets.remove(e);
		changed = true;
	}
	
	@Override
	public void addToGroup(IEntityIdentifier e){
		targets.add(e);
		changed = true;
	}
	
	@Override
	public void apply(){
		if(!changed)
			return;
		wrappedPacket.writeIntArray(0, targets.stream().mapToInt(IEntityIdentifier::getEntityID).toArray());
	}
	
	@Override
	public Object getRawPacket(){
		return super.getRawPacket();
	}
	
	@Override
	public EntityPacket clone(){
		EntityDestroyPacket p = new EntityDestroyPacket(getEmptyPacket());
		for(IEntityIdentifier e : targets)
			p.addToGroup(e);
		return p;
	}
}
