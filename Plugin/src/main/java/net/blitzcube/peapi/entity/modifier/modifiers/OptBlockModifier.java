package net.blitzcube.peapi.entity.modifier.modifiers;

import java.lang.reflect.Method;
import java.util.Optional;

import org.bukkit.material.MaterialData;

import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.modifier.IModifiableEntity;
import net.blitzcube.peapi.entity.modifier.ModifiableEntity;

/**
 * Created by iso2013 on 4/20/2018.
 */
public class OptBlockModifier extends OptModifier<MaterialData>{
	private static Class<?>	classIBlockData			= NMSUtils.getNMSClass("IBlockData");
	private static Object	serializer				= ModifiableEntity.getOptionalSerializer(classIBlockData);
	
	private static Class<?>	classCraftMagicNumbers	= NMSUtils.getOBCClass("util.CraftMagicNumbers");
	
	private static Method	methodGetMaterial		= NMSUtils.getMethod(classCraftMagicNumbers, "getMaterial", classIBlockData);
	private static Method	methodGetBlock			= NMSUtils.getMethod(classCraftMagicNumbers, "getBlock", MaterialData.class);
	
	public OptBlockModifier(int index, String label, Optional<MaterialData> def){
		super(MaterialData.class, index, label, def);
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "deprecation" })
	public Optional<MaterialData> getValue(IModifiableEntity target){
		Object val = target.read(super.index);
		if(val == null)
			return null;
		if(!(val instanceof Optional))
			throw new IllegalStateException("Read inappropriate type from modifiable entity!");
		Optional<Object> bp = (Optional<Object>)val;
		if(!bp.isPresent())
			return Optional.empty();
		try{
			return Optional.of((MaterialData)methodGetMaterial.invoke(null, bp.get()));
		}catch(Exception e){
			e.printStackTrace();
		}
		return Optional.empty();
	}
	
	@Override
	public void setValue(IModifiableEntity target, Optional<MaterialData> newValue){
		if(newValue != null){
			if(newValue.isPresent()){
				MaterialData v = newValue.get();
				Optional optional = Optional.empty();
				try{
					Object blockdata = methodGetBlock.invoke(null, v);
					optional = Optional.of(blockdata);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				target.write(index, optional, serializer);
			}else{
				target.write(super.index, Optional.empty(), serializer);
			}
		}else
			super.unsetValue(target);
	}
}
