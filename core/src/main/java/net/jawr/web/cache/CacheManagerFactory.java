/**
 * 
 */
package net.jawr.web.cache;

import net.jawr.web.config.JawrConfig;
import net.jawr.web.resource.bundle.factory.util.ClassLoaderResourceUtils;

/**
 * @author ibrahim Chaehoi
 *
 */
public class CacheManagerFactory {

	private CacheManager cacheManager;
	
	public CacheManagerFactory(JawrConfig config) {
	
		cacheManager = (CacheManager) config.getContext().getAttribute("JAWR.CACHE.MANAGER");
		if(cacheManager == null){
			String cacheManagerClass = config.getProperty("jawr.cache.manager");
			cacheManager = (CacheManager) ClassLoaderResourceUtils.buildObjectInstance(cacheManagerClass, new Object[]{config});	
		}
	}
	
	public CacheManager getCacheManager(){
		return cacheManager;
	}
}
