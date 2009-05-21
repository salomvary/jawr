/**
 * Copyright 2007-2009 Jordi Hern�ndez Sell�s, Ibrahim Chaehoi
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
package net.jawr.web;

/**
 * The constant value for Jawr.
 * 
 * @author Jordi Hern�ndez Sell�s
 * @author Ibrahim Chaehoi
 */
public class JawrConstant {

	/** The init parameter servlet for the resource type */
	public static final String TYPE_INIT_PARAMETER = "type";

	/** The image type */
	public static final String IMG_TYPE = "img";
	
	/** The js type */
	public static final String JS_TYPE = "js";
	
	/** The css type */
	public static final String CSS_TYPE = "css";
	
	/** The classpath resource prefix */
	public static final String CLASSPATH_RESOURCE_PREFIX = "jar:";

	/** The cache buster separator */
	public static final String CACHE_BUSTER_PREFIX = "cb";
	
	/** The cache buster separator */
	public static final String CLASSPATH_CACHE_BUSTER_PREFIX = "cpCb";
	
	/** The Jawr application config manager attribute */
	public static final String JAWR_APPLICATION_CONFIG_MANAGER = "net.jawr.web.jmx.JAWR_APPLICATION_CONFIG_MANAGER";
	
	/** The javascript servlet context attribute name */
	public static final String JS_CONTEXT_ATTRIBUTE  = "net.jawr.web.resource.bundle.JS_CONTEXT_ATTRIBUTE";

	/** The css servlet context attribute name */
	public static final String CSS_CONTEXT_ATTRIBUTE = "net.jawr.web.resource.bundle.CSS_CONTEXT_ATTRIBUTE";
	
	/** The image servlet context attribute name*/
	public static final String IMG_CONTEXT_ATTRIBUTE = "net.jawr.web.resource.bundle.IMG_CONTEXT_ATTRIBUTE";
	
}
