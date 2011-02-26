/**
 * 
 */
package net.jawr.web.resource.bundle.processor;

import java.util.List;

/**
 * @author 820085
 *
 */
public interface GlobalProcessor<T> {

	/**
	 * Process the bundles for a type of resources.
	 *  
	 * @param ctx the processing context
	 * @param bundles the list of bundles to process.
	 */
	public void processBundles(T ctx, List bundles);
}
