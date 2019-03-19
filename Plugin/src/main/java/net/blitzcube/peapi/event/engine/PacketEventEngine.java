package net.blitzcube.peapi.event.engine;

import me.dablakbandit.core.players.CorePlayerManager;
import me.dablakbandit.core.players.CorePlayers;
import me.dablakbandit.core.players.listener.CorePlayersListener;
import me.dablakbandit.core.players.packets.PacketHandler;
import me.dablakbandit.core.players.packets.PacketInfo;
import net.blitzcube.peapi.PacketEntityAPI;
import net.blitzcube.peapi.event.engine.listeners.EntityListener;
import net.blitzcube.peapi.event.engine.listeners.GenericListener;
import net.blitzcube.peapi.event.engine.listeners.ObjectListener;

/**
 * Created by iso2013 on 2/23/2018.
 */
class PacketEventEngine extends CorePlayersListener{
	private final PacketEntityAPI		parent;
	private final PacketEventDispatcher	dispatcher;
	private ObjectListener				object;
	private GenericListener				generic;
	private EntityListener				entity;
	
	PacketEventEngine(PacketEntityAPI parent, PacketEventDispatcher dispatcher){
		this.parent = parent;
		this.dispatcher = dispatcher;
		CorePlayerManager.getInstance().enablePacketListener();
		CorePlayerManager.getInstance().addListener(this);
		generic = new GenericListener(parent, dispatcher);
		object = new ObjectListener(parent, dispatcher);
		entity = new EntityListener(parent, dispatcher);
	}
	
	void setSendForFake(boolean sendForFake){
		entity.setSendForFake(sendForFake);
		generic.setSendForFake(sendForFake);
		object.setSendForFake(sendForFake);
	}
	
	void setCollidable(boolean collidable){
		generic.setCollidable(collidable);
	}
	
	@Override
	public void addCorePlayers(CorePlayers pl){
		PacketHandler handler = pl.getInfo(PacketInfo.class).getHandler();
		handler.addListener(generic);
		handler.addListener(object);
		handler.addListener(entity);
	}
	
	@Override
	public void loadCorePlayers(CorePlayers pl){
		
	}
	
	@Override
	public void saveCorePlayers(CorePlayers pl){
		
	}
	
	@Override
	public void removeCorePlayers(CorePlayers pl){
		
	}
}
