package net.blitzcube.peapi.entity.modifier.modifiers;

import me.dablakbandit.core.json.JSONObject;
import me.dablakbandit.core.nbt.CompoundTag;
import me.dablakbandit.core.utils.ItemUtils;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.modifier.IModifiableEntity;

/**
 * Created by iso2013 on 8/16/2018.
 */
public class NbtCompoundModifier extends GenericModifier<JSONObject>{
	
	private static Class<?> classNBTTagCompound = NMSUtils.getNMSClass("NBTTagCompound");
	
	public NbtCompoundModifier(int index, String label, JSONObject def){
		super(classNBTTagCompound, index, label, def);
	}
	
	@Override
	public JSONObject getValue(IModifiableEntity target){
		try{
			Object nbt = target.read(super.index);
			JSONObject jo = new JSONObject();
			ItemUtils.getInstance().convertCompoundTagToJSON(nbt, jo);
			return jo;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void setValue(IModifiableEntity target, JSONObject newValue){
		try{
			target.write(super.index, ItemUtils.getInstance().convertJSONToCompoundTag(newValue), serializer);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public Class<CompoundTag> getFieldType(){
		return CompoundTag.class;
	}
}
