package net.blitzcube.peapi.packet;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.google.common.base.Preconditions;

import me.dablakbandit.core.server.packet.wrapped.WrappedObject;
import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.PacketEntityAPI;
import net.blitzcube.peapi.api.entity.IEntityIdentifier;
import net.blitzcube.peapi.api.packet.IObjectSpawnPacket;
import net.blitzcube.peapi.entity.EntityIdentifier;

/**
 * Created by iso2013 on 4/21/2018.
 */
public class ObjectSpawnPacket extends EntityPacket implements IObjectSpawnPacket{
	
	private static Class<?>			classPacketPlayOutSpawnEntity				= NMSUtils.getNMSClass("PacketPlayOutSpawnEntity");
	private static Constructor<?>	conPacketPlayOutSpawnEntity					= NMSUtils.getConstructor(classPacketPlayOutSpawnEntity);
	private static Class<?>			classPacketPlayOutSpawnEntityExperienceOrb	= NMSUtils.getNMSClass("PacketPlayOutSpawnEntityExperienceOrb");
	private static Constructor<?>	conPacketPlayOutSpawnEntityExperienceOrb	= NMSUtils.getConstructor(classPacketPlayOutSpawnEntityExperienceOrb);
	private static Class<?>			classPacketPlayOutSpawnEntityPainting		= NMSUtils.getNMSClass("PacketPlayOutSpawnEntityPainting");
	private static Constructor<?>	conPacketPlayOutSpawnEntityPainting			= NMSUtils.getConstructor(classPacketPlayOutSpawnEntityPainting);
	private static Class<?>			classPacketPlayOutSpawnEntityWeather		= NMSUtils.getNMSClass("PacketPlayOutSpawnEntityWeather");
	private static Constructor<?>	conPacketPlayOutSpawnEntityWeather			= NMSUtils.getConstructor(classPacketPlayOutSpawnEntityWeather);
	
	private EntityType				type;
	private Location				location;
	private Vector					velocity;
	private Integer					orbCount;
	private String					title;
	private BlockFace				direction;
	private int						data;
	
	ObjectSpawnPacket(IEntityIdentifier identifier, EntityType type){
		super(identifier, entityTypeToPacket(type), true);
		this.velocity = new Vector(0, 0, 0);
		data = 0;
		
		super.wrappedPacket.writeUUID(0, identifier.getUUID());
	}
	
	private ObjectSpawnPacket(IEntityIdentifier identifier, WrappedPacket packet, EntityType type, Location location, Vector velocity, int data, UUID uuid){
		super(identifier, packet, false);
		this.type = type;
		this.location = location;
		this.velocity = velocity;
		this.data = data;
		
		super.wrappedPacket.writeUUID(0, identifier.getUUID());
	}
	
	private ObjectSpawnPacket(IEntityIdentifier identifier, WrappedPacket packet, EntityType type, Location location, String title, BlockFace direction, UUID uuid){
		super(identifier, packet, false);
		this.type = type;
		this.location = location;
		this.title = title;
		this.direction = direction;
		
		super.wrappedPacket.writeUUID(0, identifier.getUUID());
	}
	
	private ObjectSpawnPacket(IEntityIdentifier identifier, WrappedPacket packet, EntityType type, Location location, Integer orbCount){
		super(identifier, packet, false);
		this.type = type;
		this.location = location;
		this.orbCount = orbCount;
	}
	
	private ObjectSpawnPacket(IEntityIdentifier identifier, WrappedPacket packet, EntityType type, Location location){
		super(identifier, packet, false);
		this.type = type;
		this.location = location;
	}
	
	public static ObjectSpawnPacket unwrap(int entityID, WrappedPacket c, Player p){
		return unwrap(new EntityIdentifier(entityID, p), c, p.getWorld());
	}
	
	private static Class<?> classBlockPosition = NMSUtils.getNMSClass("BlockPosition");
	
	public static ObjectSpawnPacket unwrap(IEntityIdentifier i, WrappedPacket c, World w){
		EntityType t = packetTypeToEntity(c);
		float pitch = 0.0F, yaw = 0.0F;
		Vector velocity = new Vector();
		int data = 0;
		UUID uuid = null;
		Location location;
		if(t == null)
			return null;
		switch(t){
		case UNKNOWN:
			yaw = c.getInts().get(5).floatValue() * (360.0F / 256.0F);
			pitch = c.getInts().get(4).floatValue() * (360.0F / 256.0F);
			t = PacketEntityAPI.lookupObject(c.getInts().get(6));
			velocity = new Vector(c.getInts().get(1), c.getInts().get(2), c.getInts().get(3));
			data = c.getInts().get(7);
		case PAINTING:
			uuid = c.getUUIDs().get(0);
			if(EntityType.PAINTING.equals(t)){
				WrappedObject blockPositon = c.getWrappedObject(classBlockPosition).get(0);
				List<Integer> ints = blockPositon.getObjects(Integer.class, blockPositon.getRawObject().getClass().getSuperclass());
				location = new Location(w, ints.get(0), ints.get(1), ints.get(2));
				break;
			}
		default:
			location = new Location(w, c.getDoubles().get(0), c.getDoubles().get(1), c.getDoubles().get(2), yaw, pitch);
		}
		if(t == null)
			return null;
		i.setUUID(uuid);
		switch(t){
		case PAINTING:
			return new ObjectSpawnPacket(i, c, t, location, c.getStrings().get(0), directionToBlockFace(c.getEnums().get(0)), uuid);
		case EXPERIENCE_ORB:
			return new ObjectSpawnPacket(i, c, t, location, c.getInts().get(1));
		case LIGHTNING:
			return new ObjectSpawnPacket(i, c, t, location);
		default:
			return new ObjectSpawnPacket(i, c, t, location, velocity, data, uuid);
		}
	}
	
	private static EntityType packetTypeToEntity(WrappedPacket p){
		Class<?> clazz = p.getRawPacket().getClass();
		if(clazz.equals(classPacketPlayOutSpawnEntityWeather)){
			return EntityType.LIGHTNING;
		}else if(clazz.equals(classPacketPlayOutSpawnEntityExperienceOrb)){
			return EntityType.EXPERIENCE_ORB;
		}else if(clazz.equals(classPacketPlayOutSpawnEntityPainting)){ return EntityType.PAINTING; }
		return EntityType.UNKNOWN;
	}
	
	private static WrappedPacket entityTypeToPacket(EntityType type){
		try{
			switch(type){
			case LIGHTNING:
				return new WrappedPacket(conPacketPlayOutSpawnEntityWeather.newInstance());
			case EXPERIENCE_ORB:
				return new WrappedPacket(conPacketPlayOutSpawnEntityExperienceOrb.newInstance());
			case PAINTING:
				return new WrappedPacket(conPacketPlayOutSpawnEntityPainting.newInstance());
			}
			return new WrappedPacket(conPacketPlayOutSpawnEntity.newInstance());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private static BlockFace directionToBlockFace(Enum d){
		return BlockFace.valueOf(d.name());
	}
	
	private static Class<?> enumDirection = NMSUtils.getNMSClass("EnumDirection");
	
	private static Enum blockFaceToDirection(BlockFace f){
		return NMSUtils.getEnum(f.name(), enumDirection);
	}
	
	@Override
	public EntityType getEntityType(){
		return type;
	}
	
	@Override
	public void setEntityType(EntityType type){
		Preconditions.checkArgument(type != EntityType.LIGHTNING && type != EntityType.PAINTING && type != EntityType.EXPERIENCE_ORB, "You cannot override the type of a " + type.name() + " packet!");
		Preconditions.checkArgument(PacketEntityAPI.OBJECTS.containsKey(type), "You cannot spawn an entity with an object packet!");
		this.type = type;
		super.wrappedPacket.writeInt(6, PacketEntityAPI.OBJECTS.get(type));
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
		if(type != EntityType.LIGHTNING && type != EntityType.PAINTING && type != EntityType.EXPERIENCE_ORB && this.location != null){
			super.wrappedPacket.writeInt(5, (int)(location.getYaw() * (256.0F / 360.0F)));
			super.wrappedPacket.writeInt(4, (int)(location.getPitch() * (256.0F / 360.0F)));
		}
	}
	
	@Override
	public int getData(){
		Preconditions.checkArgument(type != EntityType.LIGHTNING && type != EntityType.PAINTING && type != EntityType.EXPERIENCE_ORB, type.name() + " does not have any associated data!");
		return data;
	}
	
	@Override
	public void setData(int data){
		Preconditions.checkArgument(type != EntityType.LIGHTNING && type != EntityType.PAINTING && type != EntityType.EXPERIENCE_ORB, "You cannot set data for a " + type.name() + "!");
		this.data = data;
		super.wrappedPacket.writeInt(7, data);
	}
	
	@Override
	public Vector getVelocity(){
		return velocity;
	}
	
	@Override
	public void setVelocity(Vector velocity){
		Preconditions.checkArgument(type != EntityType.LIGHTNING && type != EntityType.PAINTING && type != EntityType.EXPERIENCE_ORB, "You cannot set a velocity for a " + type.name() + "!");
		this.velocity = velocity;
		// FixMe: This implementation is not correct.
		super.wrappedPacket.writeInt(1, (int)velocity.getX());
		super.wrappedPacket.writeInt(2, (int)velocity.getY());
		super.wrappedPacket.writeInt(3, (int)velocity.getZ());
	}
	
	@Override
	public Integer getOrbCount(){
		Preconditions.checkArgument(type == EntityType.EXPERIENCE_ORB, type.name() + " is not an experience orb!");
		return orbCount;
	}
	
	@Override
	public void setOrbCount(Integer orbCount){
		Preconditions.checkArgument(type == EntityType.EXPERIENCE_ORB, type.name() + " is not an experience orb!");
		this.orbCount = orbCount;
		super.wrappedPacket.writeInt(1, orbCount);
	}
	
	@Override
	public String getTitle(){
		Preconditions.checkArgument(type == EntityType.PAINTING, type.name() + " is not a painting!");
		return title;
	}
	
	@Override
	public void setTitle(String title){
		Preconditions.checkArgument(type == EntityType.PAINTING, type.name() + " is not a painting!");
		Preconditions.checkArgument(title.length() > 13, "That title is too long! Maximum 13 characters.");
		this.title = title;
		super.wrappedPacket.writeString(0, title);
	}
	
	@Override
	public BlockFace getDirection(){
		Preconditions.checkArgument(type == EntityType.PAINTING, type.name() + " is not a painting!");
		return direction;
	}
	
	@Override
	public void setDirection(BlockFace direction){
		Preconditions.checkArgument(type == EntityType.PAINTING, type.name() + " is not a painting!");
		this.direction = direction;
		super.wrappedPacket.writeEnum(0, blockFaceToDirection(direction));
	}
	
	@Override
	public Object getRawPacket(){
		switch(type){
		case EXPERIENCE_ORB:
			assert location != null && orbCount > 0;
			break;
		case PAINTING:
			assert title != null && !title.isEmpty() && direction != null;
		case LIGHTNING:
		default:
			assert location != null;
			break;
		}
		return super.getRawPacket();
	}
	
	@Override
	public EntityPacket clone(){
		return unwrap(getIdentifier(), super.wrappedPacket, location != null ? location.getWorld() : null);
	}
}
