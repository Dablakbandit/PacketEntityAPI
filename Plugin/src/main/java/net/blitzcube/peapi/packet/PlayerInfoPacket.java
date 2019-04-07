/*
 * Copyright (c) 2019 Ashley Thew
 */

package net.blitzcube.peapi.packet;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.stream.Collectors;

import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.packet.IPlayerInfoPacket;

public class PlayerInfoPacket extends EntityPacket implements IPlayerInfoPacket{
	
	private static Class<?>		classPacketPlayOutPlayerInfo	= NMSUtils.getNMSClass("PacketPlayOutPlayerInfo");
	private static Constructor	conPacketPlayOutPlayerInfo		= NMSUtils.getConstructor(classPacketPlayOutPlayerInfo);
	private static Class<?>		classPlayerInfoData				= NMSUtils.getInnerClass(classPacketPlayOutPlayerInfo, "PlayerInfoData");
	private static Class<?>		enumPlayerInfoAction			= NMSUtils.getInnerClass(classPacketPlayOutPlayerInfo, "EnumPlayerInfoAction");
	
	public static WrappedPacket getEmptyPacket(){
		try{
			return new WrappedPacket(conPacketPlayOutPlayerInfo.newInstance());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	protected PlayerInfoAction action;
	
	PlayerInfoPacket(WrappedPacket wrappedPacket, PlayerInfoAction action){
		super(null, wrappedPacket, true);
		setAction(action);
	}
	
	public PlayerInfoAction getAction(){
		return action;
	}
	
	public void setAction(PlayerInfoAction action){
		this.action = action;
		wrappedPacket.writeEnum(0, NMSUtils.getEnum(action.name(), enumPlayerInfoAction));
	}
	
	@Override
	public EntityPacket clone(){
		PlayerInfoPacket info = new PlayerInfoPacket(getEmptyPacket(), getAction());
		info.setInfo(getInfo());
		return info;
	}
	
	@Override
	public List<PlayerInfoData> getInfo(){
		return wrappedPacket.getLists().get(0).stream().map(object -> new PlayerInfoData(object)).collect(Collectors.toList());
	}
	
	@Override
	public void setInfo(List<PlayerInfoData> info){
		wrappedPacket.writeList(0, info.stream().map(object -> object.getData()).collect(Collectors.toList()));
	}
	
	public void addInfo(PlayerInfoData data){
		List<PlayerInfoData> list = getInfo();
		list.add(data);
		setInfo(list);
	}
}
