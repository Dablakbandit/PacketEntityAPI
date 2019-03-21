package net.blitzcube.peapi.packet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.IEntityIdentifier;
import net.blitzcube.peapi.api.packet.IEntityPotionRemovePacket;
import net.blitzcube.peapi.entity.EntityIdentifier;

/**
 * Created by iso2013 on 6/8/2018.
 */
public class EntityPotionRemovePacket extends EntityPacket implements IEntityPotionRemovePacket{
	
	private static Class<?>			classPacketPlayOutRemoveEntityEffect	= NMSUtils.getNMSClass("PacketPlayOutRemoveEntityEffect");
	private static Constructor<?>	conPacketPlayOutRemoveEntityEffect		= NMSUtils.getConstructor(classPacketPlayOutRemoveEntityEffect);
	private static Field			fieldEffect								= NMSUtils.getField(classPacketPlayOutRemoveEntityEffect, "b");
	private static Class<?>			classMobEffectList						= NMSUtils.getNMSClass("MobEffectList");
	private static Method			methodGetId								= NMSUtils.getMethod(classMobEffectList, "getId", classMobEffectList);
	private static Method			methodFromId							= NMSUtils.getMethod(classMobEffectList, "fromId", int.class);
	
	public static WrappedPacket getEmptyPacket(){
		try{
			return new WrappedPacket(conPacketPlayOutRemoveEntityEffect.newInstance());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static int getID(WrappedPacket packet){
		try{
			return (int)methodGetId.invoke(null, fieldEffect.get(packet.getRawPacket()));
		}catch(Exception e){
			e.printStackTrace();
		}
		return -1;
	}
	
	public static void setID(WrappedPacket packet, int id){
		try{
			fieldEffect.set(packet.getRawPacket(), methodFromId.invoke(null, id));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	// public static MobEffectList fromId(int i) {
	// return (MobEffectList)REGISTRY.getId(i);
	// }
	
	private PotionEffectType type;
	
	EntityPotionRemovePacket(IEntityIdentifier identifier, WrappedPacket packet){
		super(identifier, packet, true);
		type = null;
	}
	
	private EntityPotionRemovePacket(IEntityIdentifier identifier, WrappedPacket rawPacket, PotionEffectType type){
		super(identifier, rawPacket, false);
		this.type = type;
	}
	
	public static EntityPacket unwrap(int entityID, WrappedPacket packet, Player p){
		return new EntityPotionRemovePacket(new EntityIdentifier(entityID, p), packet, PotionEffectType.getById(getID(packet)));
	}
	
	@Override
	public PotionEffectType getEffectType(){
		return type;
	}
	
	@Override
	public void setEffectType(PotionEffectType value){
		this.type = value;
		setID(wrappedPacket, value.getId());
	}
	
	@Override
	public Object getRawPacket(){
		return super.getRawPacket();
	}
	
	@Override
	public EntityPacket clone(){
		EntityPotionRemovePacket p = new EntityPotionRemovePacket(getIdentifier(), getEmptyPacket());
		p.setEffectType(type);
		return p;
	}
}
