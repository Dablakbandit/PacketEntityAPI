package net.blitzcube.peapi.entity.modifier.modifiers;

import java.util.Optional;

import me.dablakbandit.core.utils.NMSUtils;
import me.dablakbandit.core.utils.jsonformatter.JSONFormatter;
import net.blitzcube.peapi.api.entity.modifier.IModifiableEntity;
import net.blitzcube.peapi.entity.modifier.ModifiableEntity;

/**
 * Created by iso2013 on 8/20/2018.
 */
public class OptChatModifier extends OptModifier<JSONFormatter>{
	
	private static Class<?>				classIChatBaseComponent	= NMSUtils.getNMSClass("IChatBaseComponent");
	private static Object				serializer				= ModifiableEntity.getOptionalSerializer(classIChatBaseComponent);
	private final PseudoStringModifier	pseudoStringModifier	= new PseudoStringModifier(this);
	
	public OptChatModifier(int index, String label, Optional<JSONFormatter> def){
		super(null, index, label, def);
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "deprecation" })
	public Optional<JSONFormatter> getValue(IModifiableEntity target){
		Object val = target.read(super.index);
		if(val == null)
			return null;
		if(!(val instanceof Optional))
			throw new IllegalStateException("Read inappropriate type from modifiable entity!");
		Optional bp = (Optional)val;
		return bp.map(wrappedChatComponent -> {
			try{
				return JSONFormatter.fromSerialized(wrappedChatComponent);
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		});
	}
	
	@Override
	public void setValue(IModifiableEntity target, Optional<JSONFormatter> newValue){
		if(newValue != null){
			if(newValue.isPresent()){
				JSONFormatter v = newValue.get();
				try{
					target.write(index, Optional.of(v.toSerialized()), serializer);
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				target.write(super.index, Optional.empty(), serializer);
			}
		}else
			super.unsetValue(target);
	}
	
	@Override
	public Class<JSONFormatter> getFieldType(){
		return JSONFormatter.class;
	}
	
	public PseudoStringModifier asPseudoStringModifier(){
		return pseudoStringModifier;
	}
}
