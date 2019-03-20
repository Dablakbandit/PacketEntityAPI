package net.blitzcube.peapi.entity.modifier.modifiers;

import java.lang.reflect.Constructor;
import java.util.List;

import org.bukkit.util.Vector;

import me.dablakbandit.core.server.packet.wrapped.WrappedObject;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.modifier.IModifiableEntity;

/**
 * Created by iso2013 on 4/18/2018.
 */
public class PositionModifier extends GenericModifier<Vector>{
	private static Class<?>		classBlockPosition		= NMSUtils.getNMSClass("BlockPosition");
	private static Class<?>		classBaseBlockPosition	= NMSUtils.getNMSClass("BaseBlockPosition");
	
	private static Constructor	conBlockPosition		= NMSUtils.getConstructor(classBlockPosition, int.class, int.class, int.class);
	
	public static Vector toVector(Object blockPosition){
		WrappedObject wrapped = new WrappedObject(blockPosition);
		List<Integer> ints = wrapped.getObjects(Integer.class, classBaseBlockPosition);
		return new Vector(ints.get(0), ints.get(1), ints.get(2));
	}
	
	public static Object toBlockPosition(Vector vector){
		try{
			return conBlockPosition.newInstance(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public PositionModifier(int index, String label, Vector def){
		super(classBlockPosition, index, label, def);
	}
	
	@Override
	public Vector getValue(IModifiableEntity target){
		Object bp = target.read(super.index);
		if(bp == null)
			return null;
		return toVector(bp);
	}
	
	@Override
	public void setValue(IModifiableEntity target, Vector newValue){
		if(newValue != null){
			target.write(super.index, toBlockPosition(newValue), serializer);
		}else
			super.unsetValue(target);
	}
}
