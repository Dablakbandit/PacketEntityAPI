package net.blitzcube.peapi.entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import me.dablakbandit.core.players.CorePlayerManager;
import me.dablakbandit.core.players.packets.PacketHandler;
import me.dablakbandit.core.players.packets.PacketInfo;
import me.dablakbandit.core.server.packet.wrapped.WrappedPacket;
import net.blitzcube.peapi.api.event.IEntityPacketContext;
import net.blitzcube.peapi.api.packet.IEntityPacket;

/**
 * Created by iso2013 on 4/23/2018.
 */
public class EntityPacketContext implements IEntityPacketContext{
	private final Player	target;
	private TaskChain		chain;
	
	public EntityPacketContext(TaskChainFactory chainFactory, Player target){
		this.chain = chainFactory.newChain();
		this.target = target;
	}
	
	@Override
	public IEntityPacketContext queueDispatch(IEntityPacket... packets){
		for(IEntityPacket p : packets){
			WrappedPacket c = p.getWrappedPacket();
			if(c != null && c.isPlayIn()){
				chain = chain.sync(() -> safeReceive(c));
			}else{
				chain = chain.sync(() -> safeSend(c));
			}
		}
		return this;
	}
	
	@Override
	public IEntityPacketContext queueDispatch(Set<IEntityPacket> packets){
		for(IEntityPacket p : packets){
			WrappedPacket c = p.getWrappedPacket();
			if(c != null && c.isPlayIn()){
				chain = chain.sync(() -> safeReceive(c));
			}else{
				chain = chain.sync(() -> safeSend(c));
			}
		}
		return this;
	}
	
	@Override
	public IEntityPacketContext queueDispatch(IEntityPacket[] packets, int[] delays){
		Preconditions.checkArgument(packets.length >= delays.length, "Too many delays have " + "been specified!");
		for(int i = 0; i < packets.length; i++){
			IEntityPacket p = packets[i];
			int delay = i < delays.length ? delays[i] : 0;
			if(delay < 0)
				delay = 0;
			WrappedPacket c = p.getWrappedPacket();
			if(c != null && c.isPlayIn()){
				chain = chain.sync(() -> safeReceive(c));
			}else{
				chain = chain.sync(() -> safeSend(c));
			}
			if(delay > 0)
				chain = chain.delay(delay);
		}
		return this;
	}
	
	@Override
	public IEntityPacketContext queueDispatch(Collection<IEntityPacket> packets, int[] delays){
		Preconditions.checkArgument(packets.size() >= delays.length, "Too many delays have " + "been specified!");
		Iterator<IEntityPacket> it = packets.iterator();
		int i = 0;
		while(it.hasNext()){
			IEntityPacket p = it.next();
			int delay = i < delays.length ? delays[i] : 0;
			if(delay < 0)
				delay = 0;
			WrappedPacket c = p.getWrappedPacket();
			if(c != null && c.isPlayIn()){
				chain = chain.sync(() -> safeReceive(c));
			}else{
				chain = chain.sync(() -> safeSend(c));
			}
			if(delay > 0)
				chain = chain.delay(delay);
			i++;
		}
		return this;
	}
	
	@Override
	public IEntityPacketContext queueDispatch(IEntityPacket[] packets, int delay){
		int[] delays = new int[packets.length];
		delays[0] = delay;
		return queueDispatch(packets, delays);
	}
	
	@Override
	public IEntityPacketContext queueDispatch(Set<IEntityPacket> packets, int delay){
		int[] delays = new int[packets.size()];
		delays[0] = delay;
		return queueDispatch(packets, delays);
	}
	
	@Override
	public IEntityPacketContext queueDispatch(IEntityPacket packet, int delay){
		WrappedPacket c = packet.getWrappedPacket();
		if(c != null && c.isPlayIn()){
			chain = chain.sync(() -> safeReceive(c));
		}else{
			chain = chain.sync(() -> safeSend(c));
		}
		if(delay > 0)
			chain = chain.delay(delay);
		return this;
	}
	
	@Override
	public IEntityPacketContext queueDispatch(IEntityPacket packet){
		WrappedPacket c = packet.getWrappedPacket();
		if(c != null && c.isPlayIn()){
			chain = chain.sync(() -> safeReceive(c));
		}else{
			chain = chain.sync(() -> safeSend(c));
		}
		return this;
	}
	
	@Override
	public void execute(){
		chain.execute();
	}
	
	private void safeSend(WrappedPacket packet){
		if(packet == null)
			return;
		try{
			PacketHandler handler = CorePlayerManager.getInstance().getPlayers().get(target).getInfo(PacketInfo.class).getHandler();
			handler.bypassWrite(packet.getRawPacket(), true);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void safeReceive(WrappedPacket packet){
		if(packet == null)
			return;
		try{
			PacketHandler handler = CorePlayerManager.getInstance().getPlayers().get(target).getInfo(PacketInfo.class).getHandler();
			handler.bypassRead(packet.getRawPacket(), true);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
