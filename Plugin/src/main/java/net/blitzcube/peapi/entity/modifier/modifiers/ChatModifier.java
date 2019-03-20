package net.blitzcube.peapi.entity.modifier.modifiers;

import java.lang.reflect.Method;

import me.dablakbandit.core.json.JSONObject;
import me.dablakbandit.core.utils.NMSUtils;
import me.dablakbandit.core.utils.jsonformatter.JSONFormatter;
import net.blitzcube.peapi.api.entity.modifier.IModifiableEntity;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

/**
 * Created by iso2013 on 4/18/2018.
 */
public class ChatModifier extends GenericModifier<BaseComponent[]>{
	
	private static Class<?> classIChatBaseComponent = NMSUtils.getNMSClass("IChatBaseComponent");
	
	public ChatModifier(int index, String label, String def){
		super(classIChatBaseComponent, index, label, ComponentSerializer.parse(def));
	}
	
	private static Class<?>	classChatSerializer		= NMSUtils.getNMSClass("ChatSerializer", "IChatBaseComponent");
	
	private static Method	methodChatSerializerA	= NMSUtils.getMethod(classChatSerializer, "a", classIChatBaseComponent);
	
	public static String deSerialize(Object ichat) throws Exception{
		return (String)methodChatSerializerA.invoke(null, ichat);
	}
	
	@Override
	public BaseComponent[] getValue(IModifiableEntity target){
		try{
			return ComponentSerializer.parse(deSerialize(target.read(super.index)));
		}catch(Exception e){
			e.printStackTrace();
		}
		return new BaseComponent[0];
	}
	
	@Override
	public void setValue(IModifiableEntity target, BaseComponent[] newValue){
		if(newValue != null){
			try{
				JSONFormatter jf = new JSONFormatter();
				jf.append(new JSONObject(ComponentSerializer.toString(newValue)));
				target.write(super.index, jf.toSerialized(), serializer);
			}catch(Exception e){
				e.printStackTrace();
			}
		}else
			super.unsetValue(target);
	}
}
