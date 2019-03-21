package net.blitzcube.peapi.packet;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import net.blitzcube.peapi.api.entity.IEntityIdentifier;
import net.blitzcube.peapi.api.packet.IEntityMovePacket;
import net.blitzcube.peapi.entity.EntityIdentifier;

/**
 * Created by iso2013 on 8/1/2018.
 */
public class EntityMovePacket extends EntityPacket implements IEntityMovePacket{
	
	private boolean		onGround;
	private MoveType	type;
	private Vector		position;
	private double		pitch, yaw;
	
	EntityMovePacket(IEntityIdentifier identifier, MoveType type){
		super(identifier, type.getEmptyPacket(), true);
		this.type = type;
	}
	
	private EntityMovePacket(IEntityIdentifier identifier, WrappedPacket rawPacket, Byte newPitch, Byte newYaw, Vector newLocation, boolean onGround, boolean teleport){
		super(identifier, rawPacket, true);
		if(teleport){
			type = MoveType.TELEPORT;
		}else if(newPitch == null && newLocation != null){
			type = MoveType.REL_MOVE;
		}else if(newPitch != null && newLocation == null){
			type = MoveType.LOOK;
		}else if(newPitch != null){
			type = MoveType.LOOK_AND_REL_MOVE;
		}
		this.pitch = newPitch != null ? newPitch : 0;
		this.yaw = newYaw != null ? newYaw : 0;
		this.position = newLocation != null ? newLocation : new Vector();
		this.onGround = onGround;
	}
	
	public static EntityPacket unwrap(int entityID, WrappedPacket packet, Player p){
		List<Byte> bytes = packet.getBytes();
		List<Double> doubles = packet.getDoubles();
		List<Integer> ints = packet.getInts();
		switch(MoveType.getMoveType(packet.getRawPacket())){
		case TELEPORT:
			return new EntityMovePacket(new EntityIdentifier(entityID, p), packet, bytes.get(1), bytes.get(0), new Vector(doubles.get(0), doubles.get(1), doubles.get(2)), packet.getBooleans().get(0), true);
		case REL_MOVE:
			return new EntityMovePacket(new EntityIdentifier(entityID, p), packet, null, null, new Vector(((double)ints.get(1)) / 4096.0, ((double)ints.get(2)) / 4096.0, ((double)ints.get(3)) / 4096.0), packet.getBooleans().get(0), false);
		case LOOK_AND_REL_MOVE:
			return new EntityMovePacket(new EntityIdentifier(entityID, p), packet, bytes.get(1), bytes.get(0), new Vector(((double)ints.get(1)) / 4096.0, ((double)ints.get(2)) / 4096.0, ((double)ints.get(3)) / 4096.0), packet.getBooleans().get(0), false);
		case LOOK:
			return new EntityMovePacket(new EntityIdentifier(entityID, p), packet, bytes.get(1), bytes.get(0), null, packet.getBooleans().get(0), false);
		}
		return null;
	}
	
	private static Vector vectorFromAngles(double pitch, double yaw){
		Vector dir = new Vector();
		pitch = Math.toRadians(pitch);
		yaw = Math.toRadians(yaw);
		dir.setY(-Math.sin(pitch));
		double horizontalLength = Math.cos(pitch);
		dir.setX(-horizontalLength * Math.sin(yaw));
		dir.setX(horizontalLength * Math.cos(yaw));
		return dir;
	}
	
	private static double[] vectorToAngles(Vector v){
		double[] angles = new double[2];
		if(v == null)
			return angles;
		if(v.getX() == 0 && v.getZ() == 0){
			angles[0] = (byte)(v.getY() > 0 ? -90 : 90);
		}
		
		double theta = Math.atan2(-v.getX(), v.getZ());
		angles[1] = Math.toDegrees((theta + (Math.PI * 2) % (Math.PI * 2)));
		angles[0] = Math.toDegrees(Math.atan(-v.getY() / Math.sqrt((v.getX() * v.getX()) + (v.getY() * v.getY()))));
		return angles;
	}
	
	@Override
	public Vector getNewDirection(){
		return vectorFromAngles(pitch, yaw);
	}
	
	@Override
	public void setNewDirection(Vector direction){
		double[] angles = vectorToAngles(direction);
		setPitchYaw(angles[0], angles[1]);
	}
	
	@Override
	public Vector getNewPosition(){
		return position;
	}
	
	@Override
	public void setNewPosition(Vector position, boolean teleport){
		this.position = position;
		if(position == null)
			position = new Vector();
		switch(type){
		case REL_MOVE:
		case LOOK_AND_REL_MOVE:
			wrappedPacket.writeInt(1, (int)(position.getX() * 4096));
			wrappedPacket.writeInt(2, (int)(position.getY() * 4096));
			wrappedPacket.writeInt(3, (int)(position.getZ() * 4096));
			break;
		case LOOK:
			if(teleport){
				setType(MoveType.TELEPORT);
			}else{
				setType(MoveType.LOOK_AND_REL_MOVE);
				wrappedPacket.writeInt(1, (int)(position.getX() * 4096));
				wrappedPacket.writeInt(2, (int)(position.getY() * 4096));
				wrappedPacket.writeInt(3, (int)(position.getZ() * 4096));
				break;
			}
		case TELEPORT:
			wrappedPacket.writeDouble(0, position.getX());
			wrappedPacket.writeDouble(1, position.getY());
			wrappedPacket.writeDouble(2, position.getZ());
			break;
		}
	}
	
	private void setType(MoveType newType){
		if(newType == type)
			return;
		type = newType;
		wrappedPacket = new WrappedPacket(type.getEmptyPacket());
		// super.rawPacket.getModifier().writeDefaults();
		setNewPosition(position, newType == MoveType.TELEPORT);
		setPitchYaw(pitch, yaw);
		setOnGround(onGround);
	}
	
	@Override
	public MoveType getMoveType(){
		return type;
	}
	
	@Override
	public boolean isOnGround(){
		return onGround;
	}
	
	@Override
	public void setOnGround(boolean onGround){
		this.onGround = onGround;
		wrappedPacket.writeBoolean(0, onGround);
	}
	
	@Override
	public double getPitch(){
		return pitch;
	}
	
	@Override
	public double getYaw(){
		return yaw;
	}
	
	@Override
	public void setPitchYaw(double pitch, double yaw){
		switch(type){
		case REL_MOVE:
			setType(MoveType.LOOK_AND_REL_MOVE);
		case LOOK_AND_REL_MOVE:
		case LOOK:
		case TELEPORT:
			wrappedPacket.writeByte(1, (byte)pitch);
			wrappedPacket.writeByte(0, (byte)yaw);
			break;
		}
		this.pitch = pitch;
		this.yaw = yaw;
	}
	
	@Override
	public Location getLocation(Location currentLocation){
		if(type != MoveType.TELEPORT && position != null){
			return new Location(currentLocation.getWorld(), currentLocation.getX() + position.getX(), currentLocation.getY() + position.getY(), currentLocation.getZ() + position.getZ(), (float)yaw, (float)pitch);
		}else if(position == null){
			return new Location(currentLocation.getWorld(), currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), (float)yaw, (float)pitch);
		}else{
			return new Location(currentLocation.getWorld(), position.getX(), position.getY(), position.getZ(), (float)yaw, (float)pitch);
		}
	}
	
	@Override
	public void setLocation(Location newLocation, Location currentLocation){
		if(newLocation.distanceSquared(currentLocation) > 64){
			setNewPosition(newLocation.toVector(), true);
		}else{
			setNewPosition(newLocation.toVector().subtract(currentLocation.toVector()), false);
		}
		setNewDirection(newLocation.getDirection());
	}
	
	@Override
	public Object getRawPacket(){
		return super.getRawPacket();
	}
	
	@Override
	public EntityPacket clone(){
		EntityMovePacket p = new EntityMovePacket(getIdentifier(), type);
		p.setNewPosition(position, type == MoveType.TELEPORT);
		p.setPitchYaw(pitch, yaw);
		p.setOnGround(onGround);
		return p;
	}
}
