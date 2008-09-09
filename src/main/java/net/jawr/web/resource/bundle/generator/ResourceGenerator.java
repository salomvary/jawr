/**
 * Copyright 2007-2008 Jordi Hern�ndez Sell�s
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
package net.jawr.web.resource.bundle.generator;

import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Locale;

import javax.servlet.ServletContext;

import net.jawr.web.config.JawrConfig;

/**
 * A ResourceGenerator is acomponent that generates script or CSS dynamically, instead of reading 
 * it from the contents of a WAR file. It is used for creating resources programatically or to 
 * retrieve them from sources outsied the scope of a WAR file. 
 * 
 * @author  Jordi Hern�ndez Sell�s
 *
 */
public interface ResourceGenerator {

	/**
	 * Create a reader on a generated resource (any script not read from the war file 
	 * structure). 
	 * 
	 * @param path
	 * @param servletContext
	 * @param charset
	 * @return
	 */
	public Reader createResource(String path,JawrConfig config, ServletContext servletContext, Locale locale, Charset charset);
	
	
	/**
	 * Returns the prefix used to create mappings to this generator. 
	 * The mappings starting with this prefix+colon(:)+mapping will use this generator. 
	 * For instance, if prefix is 'jar', every mapping such as jar:someString is rendered
	 * by this generator. 
	 * @return
	 */
	public String getMappingPrefix();
}
