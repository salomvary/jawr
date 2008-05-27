/**
 * Copyright 2008 Jordi Hernández Sellés
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
package net.jawr.web.resource.bundle.generator.dwr;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;

import net.jawr.web.resource.bundle.factory.util.ClassLoaderResourceUtils;
import net.jawr.web.resource.bundle.generator.ResourceGenerator;

import org.directwebremoting.Container;
import org.directwebremoting.extend.CreatorManager;
import org.directwebremoting.extend.EnginePrivate;
import org.directwebremoting.extend.Remoter;
import org.directwebremoting.impl.ContainerUtil;

/**
 * Generator that creates resources from DWR beans. 
 * 
 * @author Jordi Hernández Sellés
 * 
 */
public class DWRBeanGenerator implements ResourceGenerator {

	private static final String INFRASTRUCTURE_KEY = "_util";
	private static final String ALL_INTERFACES_KEY = "_**";
	private static final String ENGINE_PATH = "org/directwebremoting/engine.js";
	private static final String UTIL_PATH = "org/directwebremoting/util.js";
	private static final String AUTH_PATH = "org/directwebremoting/auth.js";
	private static final String ACTIONUTIL_PATH = "org/directwebremoting/webwork/DWRActionUtil.js";
	private static final String JS_PATH_REF = "'+jawr_dwr_path+'";
	private static String ENGINE_INIT;
	ServletContext servletContext;
	
	static {
		ENGINE_INIT = EnginePrivate.getEngineInitScript();
	}
	
	
	public DWRBeanGenerator(ServletContext servletContext) {
		super();
		this.servletContext = servletContext;		
	}



	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.generator.ResourceGenerator#createResource(java.lang.String, java.nio.charset.Charset)
	 */
	public Reader createResource(String path, Charset charset) {
		StringBuffer data = null;
		if(INFRASTRUCTURE_KEY.equals(path)) {
			data = getInfrastructureScript();
		}
		else if(ALL_INTERFACES_KEY.equals(path)) {
			data = new StringBuffer(ENGINE_INIT);
			data.append(getAllPublishedInterfaces(JS_PATH_REF));
		}
		else {
			data = new StringBuffer(ENGINE_INIT);
			StringTokenizer tk = new StringTokenizer(path,"|");
			while(tk.hasMoreTokens()) {
				data.append(getInterfaceScript(tk.nextToken(),JS_PATH_REF));
			}
		}
		
		return new StringReader(data.toString());
	}
	
	
	/**
	 * Fetches and joins all the infrastructure scripts used by DWR. 
	 * @return
	 */
	private StringBuffer getInfrastructureScript() {
		StringBuffer sb = new StringBuffer();
		sb.append(readDWRScript(ENGINE_PATH));
		sb.append(readDWRScript(UTIL_PATH));
		sb.append(readDWRScript(AUTH_PATH));
		return sb;
	}
	
	/**
	 * Read a DWR utils script from the classpath. 
	 * @param classpath
	 * @return
	 */
	private StringBuffer readDWRScript(String classpath) {
		StringBuffer sb = null;
		try {
			InputStream is = ClassLoaderResourceUtils.getResourceAsStream(classpath, this);
			ReadableByteChannel chan = Channels.newChannel(is);
			Reader r = Channels.newReader(chan,"utf-8");
			StringWriter sw = new StringWriter();
			int i = 0;
			while((i = r.read()) != -1) {
				sw.write(i);
			}
			sb = sw.getBuffer();
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return sb;
	}
	
	/**
	 * Returns a script with a specified DWR interface
	 * @param basePath
	 * @return
	 */
	private StringBuffer getInterfaceScript(String scriptName, String basePath) {
		StringBuffer sb = new StringBuffer(ENGINE_INIT);

		// List all containers to find all DWR interfaces
		List containers = ContainerUtil.getAllPublishedContainers(servletContext);
		boolean found = false;
		for(Iterator it = containers.iterator();it.hasNext() && !found;) {
			Container container = (Container) it.next();
			
			// The creatormanager holds the list of beans
			CreatorManager ctManager = (CreatorManager) container.getBean(CreatorManager.class.getName());
			if( null != ctManager ) {
				// The remoter builds interface scripts. 
				Remoter remoter = (Remoter) container.getBean(Remoter.class.getName());
				try {
					String script = remoter.generateInterfaceScript(scriptName, basePath);
					found = true;
					// Must remove the engine init script to avoid unneeded duplication
					script = removeEngineInit(script);
					sb.append(script);
				}
				catch(SecurityException ex){ }
			}
		}
		return found ? sb : null;
	}
	
	/**
	 * Returns a script with all the DWR interfaces available in the servletcontext
	 * @param basePath
	 * @return
	 */
	private StringBuffer getAllPublishedInterfaces(String basePath) {
		
		StringBuffer sb = new StringBuffer();

		// List all containers to find all DWR interfaces
		List containers = ContainerUtil.getAllPublishedContainers(servletContext);
		for(Iterator it = containers.iterator();it.hasNext();) {
			Container container = (Container) it.next();
			
			// The creatormanager holds the list of beans
			CreatorManager ctManager = (CreatorManager) container.getBean(CreatorManager.class.getName());
			if(null != ctManager) {
				// The remoter builds interface scripts. 
				Remoter remoter = (Remoter) container.getBean(Remoter.class.getName());
				
				Collection c = ctManager.getCreatorNames();
				for(Iterator names = c.iterator();names.hasNext();) {
					String script = remoter.generateInterfaceScript((String)names.next(), basePath);
					// Must remove the engine init script to avoid unneeded duplication
					script = removeEngineInit(script);
					sb.append(script);
				}
			}
		}
		return sb;
	}
	
	/**
	 * Removes the engine init script so that it is not repeated unnecesarily. 
	 * @param script
	 * @return
	 */
	private String removeEngineInit(String script) {
		int start = script.indexOf(ENGINE_INIT);
		int end = start + ENGINE_INIT.length();
		StringBuffer rets = new StringBuffer();
		
		if(start > 0) {
			rets.append(script.substring(0, start)).append("\n");
		}
		rets.append(script.substring(end));
		
		
		return rets.toString();
	}
	

}
