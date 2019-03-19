package net.blitzcube.peapi.packet;

import java.lang.reflect.Constructor;
import java.util.*;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.google.common.base.Preconditions;

import me.dablakbandit.core.server.packet.wrapped.WrappedObject;
import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.IEntityIdentifier;
import net.blitzcube.peapi.api.packet.IEntitySpawnPacket;
import net.blitzcube.peapi.entity.EntityIdentifier;

/**
 * Created by iso2013 on 4/21/2018.
 */
public class EntitySpawnPacket extends EntityPacket implements IEntitySpawnPacket{
	private static final Map<EntityType, Integer> ENTITY_TYPE_IDS = new EnumMap<>(EntityType.class);
	
	static{
		ENTITY_TYPE_IDS.put(EntityType.BAT, 3);
		ENTITY_TYPE_IDS.put(EntityType.BLAZE, 4);
		ENTITY_TYPE_IDS.put(EntityType.CAVE_SPIDER, 6);
		ENTITY_TYPE_IDS.put(EntityType.CHICKEN, 7);
		ENTITY_TYPE_IDS.put(EntityType.COD, 8);
		ENTITY_TYPE_IDS.put(EntityType.COW, 9);
		ENTITY_TYPE_IDS.put(EntityType.CREEPER, 10);
		ENTITY_TYPE_IDS.put(EntityType.DONKEY, 11);
		ENTITY_TYPE_IDS.put(EntityType.DOLPHIN, 12);
		ENTITY_TYPE_IDS.put(EntityType.DROWNED, 14);
		ENTITY_TYPE_IDS.put(EntityType.ELDER_GUARDIAN, 15);
		ENTITY_TYPE_IDS.put(EntityType.ENDER_DRAGON, 17);
		ENTITY_TYPE_IDS.put(EntityType.ENDERMAN, 18);
		ENTITY_TYPE_IDS.put(EntityType.ENDERMITE, 19);
		ENTITY_TYPE_IDS.put(EntityType.EVOKER, 21);
		ENTITY_TYPE_IDS.put(EntityType.GHAST, 26);
		ENTITY_TYPE_IDS.put(EntityType.GIANT, 27);
		ENTITY_TYPE_IDS.put(EntityType.GUARDIAN, 28);
		ENTITY_TYPE_IDS.put(EntityType.HORSE, 29);
		ENTITY_TYPE_IDS.put(EntityType.HUSK, 30);
		ENTITY_TYPE_IDS.put(EntityType.ILLUSIONER, 31);
		ENTITY_TYPE_IDS.put(EntityType.LLAMA, 36);
		ENTITY_TYPE_IDS.put(EntityType.MAGMA_CUBE, 38);
		ENTITY_TYPE_IDS.put(EntityType.MULE, 46);
		ENTITY_TYPE_IDS.put(EntityType.MUSHROOM_COW, 47);
		ENTITY_TYPE_IDS.put(EntityType.OCELOT, 48);
		ENTITY_TYPE_IDS.put(EntityType.PARROT, 50);
		ENTITY_TYPE_IDS.put(EntityType.PIG, 51);
		ENTITY_TYPE_IDS.put(EntityType.PUFFERFISH, 52);
		ENTITY_TYPE_IDS.put(EntityType.PIG_ZOMBIE, 53);
		ENTITY_TYPE_IDS.put(EntityType.POLAR_BEAR, 54);
		ENTITY_TYPE_IDS.put(EntityType.RABBIT, 56);
		ENTITY_TYPE_IDS.put(EntityType.SALMON, 57);
		ENTITY_TYPE_IDS.put(EntityType.SHEEP, 58);
		ENTITY_TYPE_IDS.put(EntityType.SHULKER, 59);
		ENTITY_TYPE_IDS.put(EntityType.SILVERFISH, 61);
		ENTITY_TYPE_IDS.put(EntityType.SKELETON, 62);
		ENTITY_TYPE_IDS.put(EntityType.SKELETON_HORSE, 63);
		ENTITY_TYPE_IDS.put(EntityType.SLIME, 64);
		ENTITY_TYPE_IDS.put(EntityType.SNOWMAN, 66);
		ENTITY_TYPE_IDS.put(EntityType.SPIDER, 69);
		ENTITY_TYPE_IDS.put(EntityType.SQUID, 70);
		ENTITY_TYPE_IDS.put(EntityType.STRAY, 71);
		ENTITY_TYPE_IDS.put(EntityType.TROPICAL_FISH, 72);
		ENTITY_TYPE_IDS.put(EntityType.TURTLE, 73);
		ENTITY_TYPE_IDS.put(EntityType.VEX, 78);
		ENTITY_TYPE_IDS.put(EntityType.VILLAGER, 79);
		ENTITY_TYPE_IDS.put(EntityType.IRON_GOLEM, 80);
		ENTITY_TYPE_IDS.put(EntityType.VINDICATOR, 81);
		ENTITY_TYPE_IDS.put(EntityType.WITCH, 82);
		ENTITY_TYPE_IDS.put(EntityType.WITHER, 83);
		ENTITY_TYPE_IDS.put(EntityType.WITHER_SKELETON, 84);
		ENTITY_TYPE_IDS.put(EntityType.WOLF, 86);
		ENTITY_TYPE_IDS.put(EntityType.ZOMBIE, 87);
		ENTITY_TYPE_IDS.put(EntityType.ZOMBIE_HORSE, 88);
		ENTITY_TYPE_IDS.put(EntityType.ZOMBIE_VILLAGER, 89);
		ENTITY_TYPE_IDS.put(EntityType.PHANTOM, 90);
	}
	
	private static Class<?>			classPacketPlayOutNamedEntitySpawn	= NMSUtils.getNMSClass("PacketPlayOutNamedEntitySpawn");
	private static Constructor<?>	conPacketPlayOutNamedEntitySpawn	= NMSUtils.getConstructor(classPacketPlayOutNamedEntitySpawn);
	private static Class<?>			classPacketPlayOutSpawnEntityLiving	= NMSUtils.getNMSClass("PacketPlayOutSpawnEntityLiving");
	private static Constructor<?>	conPacketPlayOutSpawnEntityLiving	= NMSUtils.getConstructor(classPacketPlayOutSpawnEntityLiving);
	
	public static Constructor getPacketConstructor(EntityType type){
		return type == EntityType.PLAYER ? conPacketPlayOutNamedEntitySpawn : conPacketPlayOutSpawnEntityLiving;
	}
	
	public static WrappedPacket getEmptyPacket(EntityType type){
		try{
			Constructor<?> constructor = getPacketConstructor(type);
			Object packet = constructor.newInstance();
			// TODO blank player stuff?
			return new WrappedPacket(packet);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private EntityType			type;
	private Location			location;
	private Vector				velocity;
	private float				headPitch;
	private List<WrappedObject>	metadata;
	
	EntitySpawnPacket(IEntityIdentifier identifier, WrappedPacket packet){
		super(identifier, packet, true);
		this.velocity = new Vector(0, 0, 0);
		this.headPitch = 0.0f;
		this.metadata = new ArrayList<>();
		
		super.wrappedPacket.writeUUID(0, identifier.getUUID());
	}
	
	private EntitySpawnPacket(IEntityIdentifier identifier, WrappedPacket wrappedPacket, EntityType type, Location location, Vector velocity, float headPitch, List<WrappedObject> metadata){
		super(identifier, wrappedPacket, false);
		this.type = type;
		this.location = location;
		this.velocity = velocity;
		this.headPitch = headPitch;
		this.metadata = metadata;
	}
	
	public static EntityPacket unwrap(int entityID, WrappedPacket c, Player p){
		UUID uuid = c.getUUIDs().get(0);
		Location location;
		IEntityIdentifier identifier = new EntityIdentifier(entityID, uuid, p);
		if(c.getRawPacket().getClass().equals(classPacketPlayOutNamedEntitySpawn)){
			location = new Location(p.getWorld(), c.getDoubles().get(0), c.getDoubles().get(1), c.getDoubles().get(2));
			return new EntitySpawnPacket(identifier, c, EntityType.PLAYER, location, new Vector(0, 0, 0), 0.0f, c.getLists().get(0));
		}else{
			location = new Location(p.getWorld(), c.getDoubles().get(0), c.getDoubles().get(1), c.getDoubles().get(2), c.getInts().get(2).floatValue() * (360.0F / 256.0F), c.getInts().get(3).floatValue() * (360.0F / 256.0F));
			EntityType type = typeFromID(c.getInts().get(1));
			float headPitch = c.getInts().get(4).floatValue() * (360.0F / 256.0F);
			Vector velocity = new Vector(c.getBytes().get(0), c.getBytes().get(1), c.getBytes().get(2));
			return new EntitySpawnPacket(identifier, c, type, location, velocity, headPitch, c.getLists().get(0));
		}
	}
	
	private static EntityType typeFromID(int id){
		for(Map.Entry<EntityType, Integer> e : ENTITY_TYPE_IDS.entrySet()){
			if(e.getValue() == null)
				continue;
			if(e.getValue() == id)
				return e.getKey();
		}
		return EntityType.UNKNOWN;
	}
	
	@Override
	public EntityType getEntityType(){
		return type;
	}
	
	@Override
	public void setEntityType(EntityType type){
		Preconditions.checkArgument(this.type != EntityType.PLAYER, "You cannot modify the " + "type of a player spawn packet!");
		this.type = type;
		super.wrappedPacket.writeInt(1, ENTITY_TYPE_IDS.get(type));
	}
	
	@Override
	public Location getLocation(){
		return location;
	}
	
	@Override
	public void setLocation(Location location){
		this.location = location;
		Vector v;
		if(this.location == null){
			v = new Vector(0, 0, 0);
		}else
			v = this.location.toVector();
		super.wrappedPacket.writeDouble(0, v.getX());
		super.wrappedPacket.writeDouble(1, v.getY());
		super.wrappedPacket.writeDouble(2, v.getZ());
		if(this.location != null){
			super.wrappedPacket.writeInt(2, (int)(location.getYaw() * (256.0F / 360.0F)));
			super.wrappedPacket.writeInt(3, (int)(location.getPitch() * (256.0F / 360.0F)));
		}
	}
	
	@Override
	public float getHeadPitch(){
		return headPitch;
	}
	
	@Override
	public void setHeadPitch(float headPitch){
		this.headPitch = headPitch;
		super.wrappedPacket.writeInt(4, (int)(headPitch * (256.0F / 360.0F)));
	}
	
	@Override
	public Vector getVelocity(){
		return velocity;
	}
	
	@Override
	public void setVelocity(Vector velocity){
		Preconditions.checkArgument(type != EntityType.PLAYER, "You cannot set the velocity of a player!");
		this.velocity = velocity;
		super.wrappedPacket.writeByte(0, (byte)velocity.getX());
		super.wrappedPacket.writeByte(1, (byte)velocity.getY());
		super.wrappedPacket.writeByte(2, (byte)velocity.getZ());
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
		assert type != null && location != null;
		// TODO super.wrappedPacket.getDataWatcherModifier().write(0, new WrappedDataWatcher(metadata));
		return super.getRawPacket();
	}
	
	@Override
	public EntityPacket clone(){
		return new EntitySpawnPacket(getIdentifier(), getEmptyPacket(type), type, location, velocity, headPitch, metadata);
	}
}
