package net.blitzcube.peapi.entity.modifier.modifiers;

import java.lang.reflect.Constructor;
import java.util.List;

import org.bukkit.util.EulerAngle;

import me.dablakbandit.core.server.packet.wrapped.WrappedObject;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.modifier.IModifiableEntity;

/**
 * Created by iso2013 on 4/18/2018.
 */
public class EulerAngleModifier extends GenericModifier<EulerAngle>{
	
	private static Class<?>		classVector3f	= NMSUtils.getNMSClass("Vector3f");
	private static Constructor	conVector3f		= NMSUtils.getConstructor(classVector3f, float.class, float.class, float.class);
	
	public EulerAngleModifier(int index, String label, EulerAngle def){
		super(classVector3f, index, label, def);
	}
	
	@Override
	public EulerAngle getValue(IModifiableEntity target){
		Object vector = target.read(super.index);
		if(vector == null)
			return null;
		WrappedObject object = new WrappedObject(vector);
		List<Float> floats = object.getFloats();
		return new EulerAngle(floats.get(0), floats.get(1), floats.get(2));
	}
	
	@Override
	public void setValue(IModifiableEntity target, EulerAngle newValue){
		if(newValue != null){
			try{
				target.write(super.index, conVector3f.newInstance((float)newValue.getX(), (float)newValue.getY(), (float)newValue.getZ()), serializer);
			}catch(Exception e){
				e.printStackTrace();
			}
		}else
			super.unsetValue(target);
	}
}
