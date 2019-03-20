package net.blitzcube.peapi.entity.modifier;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.dablakbandit.core.server.packet.wrapped.WrappedObject;
import me.dablakbandit.core.utils.NMSUtils;
import net.blitzcube.peapi.api.entity.modifier.IModifiableEntity;

/**
 * Created by iso2013 on 4/20/2018.
 */
public class ModifiableEntity{
	
	private static Class<?>					classDataWatcherSerializer	= NMSUtils.getNMSClass("DataWatcherSerializer");
	private static Class<?>					classDataWatcher			= NMSUtils.getNMSClass("DataWatcher");
	private static Class<?>					classDataWatcherObject		= NMSUtils.getNMSClass("DataWatcherObject");
	private static Class<?>					classDataWatcherRegistry	= NMSUtils.getNMSClass("DataWatcherRegistry");
	private static Class<?>					classDataWatcherItem		= NMSUtils.getInnerClass(classDataWatcher, "Item");
	
	private static Method					register					= NMSUtils.getMethod(classDataWatcher, "register", classDataWatcherObject, Object.class);
	private static Method					set							= NMSUtils.getMethod(classDataWatcher, "set", classDataWatcherObject, Object.class);
	
	private static Constructor<?>			conDataWatcherItem			= classDataWatcherItem.getConstructors()[0];
	private static Constructor<?>			conDataWatcherObject		= classDataWatcherObject.getConstructors()[0];
	
	private static Map<Class<?>, Object>	serializers					= new HashMap<>();
	private static Map<Class<?>, Object>	optionalSerializers			= new HashMap<>();
	
	static{
		try{
			for(Field field : NMSUtils.getFieldsOfType(classDataWatcherRegistry, classDataWatcherSerializer)){
				Type generic = field.getGenericType();
				if(generic instanceof ParameterizedType){
					ParameterizedType type = (ParameterizedType)generic;
					Type[] args = type.getActualTypeArguments();
					Type arg = args[0];
					
					Class<?> innerClass;
					boolean optional = false;
					
					if(arg instanceof Class<?>){
						innerClass = (Class<?>)arg;
					}else if(arg instanceof ParameterizedType){
						innerClass = (Class<?>)((ParameterizedType)arg).getActualTypeArguments()[0];
						optional = true;
					}else{
						throw new IllegalStateException("Failed to find inner class of field " + field);
					}
					
					Object serializer;
					
					try{
						serializer = field.get(null);
					}catch(Exception e){
						throw new IllegalStateException("Failed to read field " + field);
					}
					
					if(serializer == null){ throw new RuntimeException("Failed to read serializer: " + field.getName()); }
					if(optional){
						optionalSerializers.put(innerClass, serializer);
					}else{
						serializers.put(innerClass, serializer);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static Object getSerializer(Class<?> type){
		return serializers.get(type);
	}
	
	public static Object getOptionalSerializer(Class<?> type){
		return optionalSerializers.get(type);
	}
	
	public static Object newDataWatcherItem(Object dataObject, Object item){
		try{
			return conDataWatcherItem.newInstance(dataObject, item);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object newDataWatcherObject(int index, Object serializer){
		try{
			return conDataWatcherObject.newInstance(index, serializer);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void setDataWatcherItem(Object watcher, Object index, Object item, boolean has){
		try{
			if(has){
				set.invoke(item, index, item);
			}else{
				register.invoke(item, index, item);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static int getIndex(WrappedObject wrapped){
		return wrapped.getWrappedObject(classDataWatcherObject).get(0).getInts().get(0);
	}
	
	public static class WatcherBased implements IModifiableEntity{
		private final WrappedObject watcher;
		
		public WatcherBased(WrappedObject watcher){
			this.watcher = watcher;
		}
		
		@Override
		public List<WrappedObject> getWatchableObjects(){
			return new ArrayList<>(watcher.getMapValues(1).values());
		}
		
		@Override
		public WrappedObject getDataWatcher(){
			return watcher;
		}
		
		@Override
		public Map<Integer, Object> getRawObjects(){
			Map<Integer, Object> map = new HashMap<>();
			for(WrappedObject wwo : getWatchableObjects())
				map.put(getIndex(wwo), wwo.getRawObject());
			return map;
		}
		
		@Override
		public Object read(int index){
			return watcher.getMapValues(1).get(index).getObjects().get(1);
		}
		
		@Override
		public void write(int index, Object newValue, Object serializer){
			Object object = newDataWatcherObject(index, serializer);
			WrappedObject item = new WrappedObject(newDataWatcherItem(object, newValue));
			setDataWatcherItem(watcher, object, item.getRawObject(), contains(index));
			item.writeBoolean(0, true);
		}
		
		@Override
		public void clear(int index){
			watcher.getObjects(Map.class).get(1).remove(index);
		}
		
		@Override
		public void clear(){
			watcher.getObjects(Map.class).get(1).clear();
		}
		
		@Override
		public boolean contains(int index){
			return watcher.getObjects(Map.class).get(1).containsKey(index);
		}
	}
	
	public static class ListBased implements IModifiableEntity{
		private final List<WrappedObject> objects;
		
		public ListBased(List<WrappedObject> objects){
			this.objects = objects != null ? objects : new ArrayList<>();
		}
		
		@Override
		public List<WrappedObject> getWatchableObjects(){
			return objects;
		}
		
		@Override
		public WrappedObject getDataWatcher(){
			return null;
		}
		
		@Override
		public Map<Integer, Object> getRawObjects(){
			Map<Integer, Object> map = new HashMap<>();
			for(WrappedObject wwo : getWatchableObjects())
				map.put(getIndex(wwo), wwo.getRawObject());
			return map;
		}
		
		@Override
		public Object read(int index){
			return objects.stream().filter(wWO -> getIndex(wWO) == index).map((wwO) -> wwO.getObjects().get(1)).findFirst().orElse(null);
		}
		
		@Override
		public void write(int index, Object newValue, Object serializer){
			clear(index);
			objects.add(new WrappedObject(newDataWatcherItem(newDataWatcherObject(index, serializer), newValue)));
		}
		
		@Override
		public void clear(int index){
			objects.removeIf(wWO -> getIndex(wWO) == index);
		}
		
		@Override
		public void clear(){
			objects.clear();
		}
		
		@Override
		public boolean contains(int index){
			return objects.stream().anyMatch(wWO -> getIndex(wWO) == index);
		}
	}
	
	public static class MapBased implements IModifiableEntity{
		private final Map<Integer, Object> rawObjects;
		
		public MapBased(Map<Integer, Object> rawObjects){
			this.rawObjects = rawObjects;
		}
		
		@Override
		public List<WrappedObject> getWatchableObjects(){
			return null;
		}
		
		@Override
		public WrappedObject getDataWatcher(){
			return null;
		}
		
		@Override
		public Map<Integer, Object> getRawObjects(){
			return rawObjects;
		}
		
		@Override
		public Object read(int index){
			return rawObjects.get(index);
		}
		
		@Override
		public void write(int index, Object newValue, Object serializer){
			rawObjects.put(index, new WrappedObject(newDataWatcherItem(newDataWatcherObject(index, serializer), newValue)));
		}
		
		@Override
		public void clear(int index){
			rawObjects.remove(index);
		}
		
		@Override
		public void clear(){
			rawObjects.clear();
		}
		
		@Override
		public boolean contains(int index){
			return rawObjects.containsKey(index);
		}
	}
}
