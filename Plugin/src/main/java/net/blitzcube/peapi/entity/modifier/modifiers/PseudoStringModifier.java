package net.blitzcube.peapi.entity.modifier.modifiers;

import java.util.Optional;

import me.dablakbandit.core.utils.jsonformatter.JSONFormatter;
import net.blitzcube.peapi.api.entity.modifier.IModifiableEntity;

/**
 * Created by iso2013 on 8/20/2018.
 */
public class PseudoStringModifier extends GenericModifier<String>{
	private final OptChatModifier internal;
	
	public PseudoStringModifier(OptChatModifier internal){
		super(String.class, internal.index, internal.label, fromComponentOptional(internal.def));
		this.internal = internal;
	}
	
	private static String fromComponentOptional(Optional<JSONFormatter> components){
		return components.get().toNormalString();
	}
	
	@Override
	public String getValue(IModifiableEntity target){
		return fromComponentOptional(internal.getValue(target));
	}
	
	@Override
	public void setValue(IModifiableEntity target, String newValue){
		internal.setValue(target, Optional.of(new JSONFormatter().append(newValue)));
	}
}
