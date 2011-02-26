package net.jawr.web.cache;

public interface CacheManager {

	public void put(String key, Object obj);
	
	public Object get(String key);
	
	public Object remove(String key);
	
	public void clear();
	
}
