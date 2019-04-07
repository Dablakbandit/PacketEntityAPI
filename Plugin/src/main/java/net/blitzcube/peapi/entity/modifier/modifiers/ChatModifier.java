package net.blitzcube.peapi.entity.modifier.modifiers;

import me.dablakbandit.core.utils.NMSUtils;
import me.dablakbandit.core.utils.jsonformatter.JSONFormatter;
import net.blitzcube.peapi.api.entity.modifier.IModifiableEntity;

/**
 * Created by iso2013 on 4/18/2018.
 */
public class ChatModifier extends GenericModifier<JSONFormatter>{
	
	private static Class<?> classIChatBaseComponent = NMSUtils.getNMSClass("IChatBaseComponent");
	
	public ChatModifier(int index, String label, String def){
		super(classIChatBaseComponent, index, label, new JSONFormatter().append(def));
	}
	
	@Override
	public JSONFormatter getValue(IModifiableEntity target){
		try{
			return JSONFormatter.fromSerialized(target.read(super.index));
		}catch(Exception e){
			e.printStackTrace();
		}
		return new JSONFormatter();
	}
	
	@Override
	public void setValue(IModifiableEntity target, JSONFormatter newValue){
		if(newValue != null){
			try{
				target.write(super.index, newValue.toSerialized(), serializer);
			}catch(Exception e){
				e.printStackTrace();
			}
		}else
			super.unsetValue(target);
	}
}
