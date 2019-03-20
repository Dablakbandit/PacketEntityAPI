package net.blitzcube.peapi.entity.modifier.modifiers;

import org.bukkit.block.BlockFace;

import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.modifier.IModifiableEntity;

/**
 * Created by iso2013 on 4/20/2018.
 */
public class DirectionModifier extends GenericModifier<BlockFace>{
	
	private static Class<?> enumDirection = NMSUtils.getNMSClass("EnumDirection");
	
	public DirectionModifier(int index, String label, Enum def){
		super(enumDirection, index, label, fromWrapped(def));
	}
	
	public static BlockFace fromWrapped(Enum wrapped){
		if(wrapped == null)
			return null;
		return BlockFace.valueOf(wrapped.name());
	}
	
	public static Enum getValue(int i){
		return NMSUtils.getEnum(i, enumDirection);
	}
	
	@Override
	public BlockFace getValue(IModifiableEntity target){
		return fromWrapped((Enum)target.read(super.index));
	}
	
	@Override
	public void setValue(IModifiableEntity target, BlockFace newValue){
		if(newValue != null){
			Enum direction = NMSUtils.getEnum(newValue.name(), enumDirection);
			target.write(super.index, direction, serializer);
		}else
			super.unsetValue(target);
	}
	
	@Override
	public Class<?> getFieldType(){
		return BlockFace.class;
	}
}
