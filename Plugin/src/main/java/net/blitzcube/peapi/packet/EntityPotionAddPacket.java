package net.blitzcube.peapi.packet;

import java.lang.reflect.Constructor;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.IEntityIdentifier;
import net.blitzcube.peapi.api.packet.IEntityPotionAddPacket;
import net.blitzcube.peapi.entity.EntityIdentifier;

/**
 * Created by iso2013 on 6/8/2018.
 */
public class EntityPotionAddPacket extends EntityPacket implements IEntityPotionAddPacket{
	
	private static Class<?>			classPacketPlayOutEntityEffect	= NMSUtils.getNMSClass("PacketPlayOutEntityEffect");
	private static Constructor<?>	conPacketPlayOutEntityEffect	= NMSUtils.getConstructor(classPacketPlayOutEntityEffect);
	
	public static WrappedPacket getEmptyPacket(){
		try{
			return new WrappedPacket(conPacketPlayOutEntityEffect.newInstance());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private PotionEffect effect;
	
	EntityPotionAddPacket(IEntityIdentifier identifier, WrappedPacket packet){
		super(identifier, packet, true);
		effect = null;
	}
	
	private EntityPotionAddPacket(IEntityIdentifier identifier, WrappedPacket rawPacket, PotionEffect effect){
		super(identifier, rawPacket, false);
		this.effect = effect;
	}
	
	public static EntityPacket unwrap(int entityID, WrappedPacket packet, Player p){
		byte flags = packet.getBytes().get(2);
		return new EntityPotionAddPacket(new EntityIdentifier(entityID, p), packet, new PotionEffect(PotionEffectType.getById(packet.getBytes().get(0)), packet.getInts().get(1), packet.getBytes().get(1), (flags & 0x01) > 0, (flags & 0x02) > 0));
	}
	
	@Override
	public PotionEffect getEffect(){
		return effect;
	}
	
	@Override
	public void setEffect(PotionEffect value){
		this.effect = value;
		wrappedPacket.writeByte(0, (byte)value.getType().getId());
		wrappedPacket.writeByte(1, (byte)value.getAmplifier());
		wrappedPacket.writeInt(1, value.getDuration());
		byte flags = 0;
		if(value.isAmbient())
			flags = (byte)(flags | 0x01);
		if(value.hasParticles())
			flags = (byte)(flags | 0x02);
		wrappedPacket.writeByte(2, flags);
	}
	
	@Override
	public Object getRawPacket(){
		return super.getRawPacket();
	}
	
	@Override
	public EntityPacket clone(){
		EntityPotionAddPacket p = new EntityPotionAddPacket(getIdentifier(), getEmptyPacket());
		p.setEffect(effect);
		return p;
	}
}
