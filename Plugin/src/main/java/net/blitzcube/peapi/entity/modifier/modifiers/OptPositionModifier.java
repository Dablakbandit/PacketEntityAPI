package net.blitzcube.peapi.entity.modifier.modifiers;

import java.util.Optional;

import org.bukkit.util.Vector;

import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.modifier.IModifiableEntity;
import net.blitzcube.peapi.entity.modifier.ModifiableEntity;

/**
 * Created by iso2013 on 4/20/2018.
 */
public class OptPositionModifier extends OptModifier<Vector>{
	
	private static Class<?>	classBlockPosition		= NMSUtils.getNMSClass("BlockPosition");
	private static Object	serializer				= ModifiableEntity.getOptionalSerializer(classBlockPosition);
	private static Class<?>	classBaseBlockPosition	= NMSUtils.getNMSClass("BaseBlockPosition");
	
	public OptPositionModifier(int index, String label, Optional<Vector> def){
		super(Vector.class, index, label, def);
	}
	
	public Class getOptionalType(){
		return classBlockPosition;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Optional<Vector> getValue(IModifiableEntity target){
		Object val = target.read(super.index);
		if(val == null)
			return null;
		if(!(val instanceof Optional))
			throw new IllegalStateException("Read inappropriate type from modifiable entity!");
		Optional bp = (Optional)val;
		if(!bp.isPresent())
			return Optional.empty();
		return Optional.of(PositionModifier.toVector(bp.get()));
	}
	
	@Override
	public void setValue(IModifiableEntity target, Optional<Vector> newValue){
		if(newValue != null){
			if(newValue.isPresent()){
				Vector v = newValue.get();
				target.write(super.index, Optional.of(PositionModifier.toBlockPosition(v)), serializer);
			}else{
				target.write(super.index, Optional.empty(), serializer);
			}
		}else
			super.unsetValue(target);
	}
}
