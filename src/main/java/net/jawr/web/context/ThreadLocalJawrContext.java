/**
 * Copyright 2007-2009 Jordi Hernández Sellés, Ibrahim Chaehoi, Matt Ruby
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package net.jawr.web.context;

/**
 * This class defines the context for Jawr, it holds the context in a ThreadLocal object.
 * 
 * @author Ibrahim Chaehoi
 */
public class ThreadLocalJawrContext {

	/**
	 * debugOverride will allow us to override production mode on a request by request basis.
	 * ThreadLocal is used to hold the overridden status throughout a given request.
	 */
	private static ThreadLocal jawrContext = new ThreadLocal();
	
	/**
	 * The debugOverride will be automatially set to false
	 */
	private ThreadLocalJawrContext() {
		
		jawrContext.set(new JawrContext());
	}
	
	/**
	 * Returns the mbean object name
	 * @return the mbean object name
	 */
	public static String getMbeanObjectName() {
		
		return ((JawrContext) jawrContext.get()).getMbeanObjectName();
	}

	/**
	 * Sets the mbean object name
	 * @param mbeanObjectName the mbean object name
	 */
	public static void setMbeanObjectName(String mbeanObjectName) {

		JawrContext ctx = (JawrContext) jawrContext.get();
		if(ctx == null){
			ctx =  new JawrContext();
		}
		ctx.setMbeanObjectName(mbeanObjectName);
		jawrContext.set(ctx);
	}
	
	/**
	 * Sets the mbean object name
	 * @param mbeanObjectName the mbean object name
	 */
	public static void reset() {

		JawrContext ctx = (JawrContext) jawrContext.get();
		ctx.reset();
	}
}
