package net.blitzcube.peapi.entity.modifier.modifiers;

import net.blitzcube.peapi.api.entity.modifier.IModifiableEntity;

/**
 * Created by iso2013 on 4/20/2018.
 */
public class BitmaskModifier extends GenericModifier<Boolean>{
	private final byte	def;
	private final byte	mask;
	
	public BitmaskModifier(int index, byte mask, String label, Byte def){
		super(Byte.class, index, label, false);
		this.def = def;
		this.mask = mask;
	}
	
	public void clear(IModifiableEntity target){
		target.clear(super.index);
	}
	
	@Override
	public Boolean getValue(IModifiableEntity target){
		return (getOrDef(target) & mask) > 0;
	}
	
	@Override
	public void setValue(IModifiableEntity target, Boolean newValue){
		target.write(super.index, (byte)((getOrDef(target) & ~mask) | (newValue ? mask : 0)), serializer);
	}
	
	private byte getOrDef(IModifiableEntity target){
		Object val = target.read(super.index);
		if(val != null && !val.getClass().equals(Byte.class))
			throw new IllegalStateException("Read inappropriate type from modifiable entity!");
		return val != null ? (Byte)val : def;
	}
	
	@Override
	public void unsetValue(IModifiableEntity target){
		super.unsetValue(target, true);
	}
}
