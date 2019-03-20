package net.blitzcube.peapi.entity.modifier.modifiers;

import java.lang.reflect.Method;

import org.bukkit.Particle;

import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.modifier.IModifiableEntity;
import net.blitzcube.peapi.entity.modifier.ModifiableEntity;

/**
 * Created by iso2013 on 8/20/2018.
 */
public class ParticleModifier extends GenericModifier<Particle>{
	
	private static Class<?>	classCraftParticle	= NMSUtils.getOBCClass("CraftParticle");
	private static Class<?>	classParticleParam	= NMSUtils.getNMSClass("ParticleParam");
	private static Object	serializer			= ModifiableEntity.getSerializer(classParticleParam);
	
	public static Method	methodToBukkit		= NMSUtils.getMethod(classCraftParticle, "toBukkit", classParticleParam);
	public static Method	methodToNMS			= NMSUtils.getMethod(classCraftParticle, "toNMS", Particle.class);
	
	public ParticleModifier(int index, String label, int def){
		super(null, index, label, Particle.values()[def]);
	}
	
	private Particle fromNMS(Object nms){
		if(nms == null)
			return null;
		Particle t;
		try{
			t = (Particle)methodToBukkit.invoke(null, nms);
		}catch(Exception e){
			throw new IllegalArgumentException("Cannot decode invalid particle type " + nms + "!");
		}
		return t;
	}
	
	@Override
	public Particle getValue(IModifiableEntity target){
		return fromNMS(target.read(super.index));
	}
	
	@Override
	public void setValue(IModifiableEntity target, Particle newValue){
		if(newValue != null){
			Object wrapped = toNMS(newValue);
			if(wrapped == null){ throw new IllegalArgumentException("Cannot encode invalid particle type " + newValue.name() + "!"); }
			target.write(super.index, wrapped, serializer);
		}else
			super.unsetValue(target);
	}
	
	private Object toNMS(Particle particle){
		try{
			return methodToNMS.invoke(null, particle);
		}catch(Exception e){
			return null;
		}
	}
	
	@Override
	public Class<Particle> getFieldType(){
		return Particle.class;
	}
}
