package net.blitzcube.peapi.entity.modifier.modifiers;

import org.bukkit.inventory.ItemStack;

import me.dablakbandit.core.utils.ItemUtils;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.modifier.IModifiableEntity;

/**
 * Created by iso2013 on 4/18/2018.
 */
public class ItemModifier extends GenericModifier<ItemStack>{
	
	private static Class<?> classItemStack = NMSUtils.getNMSClass("ItemStack");
	
	public ItemModifier(int index, String label, ItemStack def){
		super(classItemStack, index, label, def);
	}
	
	@Override
	public ItemStack getValue(IModifiableEntity target){
		try{
			return ItemUtils.getInstance().asBukkitCopy(target.read(super.index));
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void setValue(IModifiableEntity target, ItemStack newValue){
		try{
			target.write(super.index, ItemUtils.getInstance().getNMSCopy(newValue), serializer);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
