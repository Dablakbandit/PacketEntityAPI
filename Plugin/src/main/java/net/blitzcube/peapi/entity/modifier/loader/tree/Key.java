package net.blitzcube.peapi.entity.modifier.loader.tree;

import java.util.List;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;

import me.dablakbandit.core.json.JSONObject;
import net.blitzcube.peapi.entity.modifier.modifiers.*;

/**
 * Created by iso2013 on 4/20/2018.
 */
public class Key extends Node.Attribute{
	private final int			index;
	private final String		type;
	private final String		label;
	private final JsonElement	def;
	
	Key(int index, String type, String label, JsonElement def){
		this.index = index;
		this.type = type;
		this.label = label;
		this.def = def;
	}
	
	@Override
	public List<GenericModifier> asGenericModifier(){
		switch(this.type){
		case "Boolean":
			return ImmutableList.of(new GenericModifier<>(Boolean.class, index, label, def.getAsBoolean()));
		case "Byte":
			return ImmutableList.of(new GenericModifier<>(Byte.class, index, label, def.getAsByte()));
		case "Float":
			return ImmutableList.of(new GenericModifier<>(Float.class, index, label, def.getAsFloat()));
		case "Integer":
			return ImmutableList.of(new GenericModifier<>(Integer.class, index, label, def.getAsInt()));
		case "NBTCompound":
			try{
				return ImmutableList.of(new NbtCompoundModifier(index, label, def.isJsonNull() ? null : new JSONObject(def.getAsString())));
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		case "String":
			return ImmutableList.of(new GenericModifier<>(String.class, index, label, def.getAsString()));
		case "Block":
			return ImmutableList.of(new PositionModifier(index, label, null));
		case "Chat":
			return ImmutableList.of(new ChatModifier(index, label, def.getAsString()));
		case "Direction":
			return ImmutableList.of(new DirectionModifier(index, label, DirectionModifier.getValue(def.getAsInt())));
		case "Color":
			return ImmutableList.of(new ColorModifier(index, label, def.getAsInt()));
		case "EulerAngle":
			return ImmutableList.of(new EulerAngleModifier(index, label, stringToEulerAngle(def.getAsString())));
		case "ItemStack":
			return ImmutableList.of(new ItemModifier(index, label, new ItemStack(Material.AIR)));
		case "Optional[Block]":
			return ImmutableList.of(new OptPositionModifier(index, label, Optional.empty()));
		case "Optional[BlockData]":
			return ImmutableList.of(new OptBlockModifier(index, label, Optional.empty()));
		case "Optional[Chat]":
			return ImmutableList.of(new OptChatModifier(index, label, Optional.empty()));
		case "Optional[UUID]":
			return ImmutableList.of(new OptUUIDModifier(index, label, Optional.empty()));
		case "Particle":
			return ImmutableList.of(new ParticleModifier(index, label, def.getAsInt()));
		default:
			throw new IllegalStateException("Could not find a structured type for " + type + "!");
		}
	}
	
	private EulerAngle stringToEulerAngle(String input){
		input = input.replace("(", "").replace(")", "");
		String[] values = input.split(",");
		return new EulerAngle(Double.valueOf(values[0]), Double.valueOf(values[1]), Double.valueOf(values[2]));
	}
}
