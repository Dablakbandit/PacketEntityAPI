/*
 * Copyright (c) 2019 Ashley Thew
 */

package net.blitzcube.peapi.api.packet;

import java.lang.reflect.Constructor;
import java.util.List;

import org.bukkit.GameMode;

import com.mojang.authlib.GameProfile;

import me.dablakbandit.core.server.packet.wrapped.WrappedObject;
import me.dablakbandit.core.utils.NMSUtils;
import me.dablakbandit.core.utils.jsonformatter.JSONFormatter;

public interface IPlayerInfoPacket extends IEntityPacket{
	
	List<PlayerInfoData> getInfo();
	
	void setInfo(List<PlayerInfoData> info);
	
	void addInfo(PlayerInfoData data);
	
	PlayerInfoAction getAction();
	
	void setAction(PlayerInfoAction action);
	
	class PlayerInfoData{
		
		private static Class<?>		classPlayerInfoData		= NMSUtils.getNMSClass("PlayerInfoData", "PacketPlayOutPlayerInfo");
		private static Class<?>		enumGameMode			= NMSUtils.getNMSClass("EnumGamemode");
		private static Class<?>		classIChatBaseComponent	= NMSUtils.getNMSClass("IChatBaseComponent");
		
		private static Constructor	conPlayerInfoData		= classPlayerInfoData.getDeclaredConstructors()[0];
		
		private WrappedObject		data;
		
		public PlayerInfoData(GameProfile profile, int ping, GameMode gameMode, String name){
			try{
				this.data = new WrappedObject(conPlayerInfoData.newInstance(null, profile, ping, NMSUtils.getEnum(gameMode.name(), enumGameMode), new JSONFormatter().append(name).toSerialized()));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		public PlayerInfoData(WrappedObject data){
			this.data = data;
		}
		
		public WrappedObject getData(){
			return data;
		}
		
		public GameProfile getGameProfile(){
			return data.getObjects(GameProfile.class).get(0);
		}
		
		public void setGameProfile(GameProfile profile){
			data.write(0, profile, GameProfile.class);
		}
		
		public int getPing(){
			return data.getInts().get(0);
		}
		
		public void setPing(int i){
			data.writeInt(0, i);
		}
		
		public GameMode getGameMode(){
			return GameMode.valueOf(data.getEnums().get(0).name());
		}
		
		public void setGameMode(GameMode gameMode){
			data.writeEnum(0, NMSUtils.getEnum(gameMode.name(), enumGameMode));
		}
		
		public JSONFormatter getName(){
			return JSONFormatter.fromSerialized(data.getObjects(classIChatBaseComponent).get(0));
		}
		
		public void setName(JSONFormatter jf){
			data.write(0, jf.toSerialized(), classIChatBaseComponent);
		}
		
	}
	
	enum PlayerInfoAction{
		ADD_PLAYER, UPDATE_GAME_MODE, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, REMOVE_PLAYER;
	}
}
