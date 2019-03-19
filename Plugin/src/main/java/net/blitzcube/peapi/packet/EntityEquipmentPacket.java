package net.blitzcube.peapi.packet;

import java.lang.reflect.Constructor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.IEntityIdentifier;
import net.blitzcube.peapi.api.packet.IEntityEquipmentPacket;
import net.blitzcube.peapi.entity.EntityIdentifier;

/**
 * Created by iso2013 on 4/21/2018.
 */
public class EntityEquipmentPacket extends EntityPacket implements IEntityEquipmentPacket{
	
	private static Class<?>			classPacketPlayOutEntityEquipment	= NMSUtils.getClass("PacketPlayOutEntityEquipment");
	private static Constructor<?>	conPacketPlayOutEntityEquipment		= NMSUtils.getConstructor(classPacketPlayOutEntityEquipment);
	
	public static WrappedPacket getEmptyPacket(){
		try{
			return new WrappedPacket(conPacketPlayOutEntityEquipment.newInstance());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private EquipmentSlot	slot;
	private ItemStack		item;
	
	private static Class<?>	classEnumItemSlot	= NMSUtils.getClass("EnumItemSlot");
	
	EntityEquipmentPacket(IEntityIdentifier identifier, WrappedPacket packet){
		super(identifier, packet, true);
	}
	
	private EntityEquipmentPacket(IEntityIdentifier identifier, WrappedPacket rawPacket, EquipmentSlot slot, ItemStack item){
		super(identifier, rawPacket, false);
		this.slot = slot;
		this.item = item;
	}
	
	public static EntityPacket unwrap(int entityID, WrappedPacket packet, Player p){
		return new EntityEquipmentPacket(new EntityIdentifier(entityID, p), packet, fromItemSlot(packet.getEnums().get(0)), packet.getItemStacks().get(0));
	}
	
	private static EquipmentSlot fromItemSlot(Object itemslot){
		String slot = ((Enum<?>)itemslot).name();
		switch(slot){
		case "MAINHAND":
			return EquipmentSlot.HAND;
		case "OFFHAND":
			return EquipmentSlot.OFF_HAND;
		case "FEET":
		case "LEGS":
		case "CHEST":
		case "HEAD":
			return EquipmentSlot.valueOf(slot);
		}
		return null;
	}
	
	private static Enum<?> fromEquipmentSlot(EquipmentSlot i){
		String find = null;
		switch(i){
		case HAND:
			find = "MAINHAND";
			break;
		case OFF_HAND:
			find = "OFFHAND";
			break;
		case FEET:
		case LEGS:
		case CHEST:
		case HEAD:
			find = i.name();
			break;
		}
		return NMSUtils.getEnum(find, classEnumItemSlot);
	}
	
	@Override
	public EquipmentSlot getSlot(){
		return slot;
	}
	
	@Override
	public void setSlot(EquipmentSlot slot){
		this.slot = slot;
		wrappedPacket.writeEnum(0, fromEquipmentSlot(slot));
	}
	
	@Override
	public ItemStack getItem(){
		return item;
	}
	
	@Override
	public void setItem(ItemStack item){
		this.item = item;
		wrappedPacket.writeItemStack(0, item);
	}
	
	@Override
	public Object getRawPacket(){
		return super.getRawPacket();
	}
	
	@Override
	public EntityPacket clone(){
		EntityEquipmentPacket p = new EntityEquipmentPacket(getIdentifier(), getEmptyPacket());
		p.setSlot(slot);
		p.setItem(item);
		return p;
	}
}
