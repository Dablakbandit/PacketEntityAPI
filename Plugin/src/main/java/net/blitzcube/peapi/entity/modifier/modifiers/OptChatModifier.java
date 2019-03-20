package net.blitzcube.peapi.entity.modifier.modifiers;

import java.util.Optional;

import me.dablakbandit.core.json.JSONObject;
import me.dablakbandit.core.utils.NMSUtils;
import me.dablakbandit.core.utils.jsonformatter.JSONFormatter;
import net.blitzcube.peapi.api.entity.modifier.IModifiableEntity;
import net.blitzcube.peapi.entity.modifier.ModifiableEntity;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

/**
 * Created by iso2013 on 8/20/2018.
 */
public class OptChatModifier extends OptModifier<BaseComponent[]>{
	
	private static Class<?>				classIChatBaseComponent	= NMSUtils.getNMSClass("IChatBaseComponent");
	private static Object				serializer				= ModifiableEntity.getOptionalSerializer(classIChatBaseComponent);
	private final PseudoStringModifier	pseudoStringModifier	= new PseudoStringModifier(this);
	
	public OptChatModifier(int index, String label, Optional<BaseComponent[]> def){
		super(null, index, label, def);
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "deprecation" })
	public Optional<BaseComponent[]> getValue(IModifiableEntity target){
		Object val = target.read(super.index);
		if(val == null)
			return null;
		if(!(val instanceof Optional))
			throw new IllegalStateException("Read inappropriate type from modifiable entity!");
		Optional bp = (Optional)val;
		return bp.map(wrappedChatComponent -> {
			try{
				return ChatModifier.deSerialize(wrappedChatComponent);
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		});
	}
	
	@Override
	public void setValue(IModifiableEntity target, Optional<BaseComponent[]> newValue){
		if(newValue != null){
			if(newValue.isPresent()){
				BaseComponent[] v = newValue.get();
				JSONFormatter jf = new JSONFormatter();
				try{
					jf.append(new JSONObject(ComponentSerializer.toString(v)));
					
					target.write(index, jf.toSerialized(), serializer);
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
	public Class<BaseComponent[]> getFieldType(){
		return BaseComponent[].class;
	}
	
	public PseudoStringModifier asPseudoStringModifier(){
		return pseudoStringModifier;
	}
}
