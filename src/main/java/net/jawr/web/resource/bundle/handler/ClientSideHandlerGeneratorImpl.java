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
package net.jawr.web.resource.bundle.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.jawr.web.config.JawrConfig;
import net.jawr.web.resource.bundle.JoinableResourceBundle;
import net.jawr.web.resource.bundle.factory.util.ClassLoaderResourceUtils;
import net.jawr.web.resource.bundle.factory.util.PathNormalizer;
import net.jawr.web.resource.bundle.generator.JavascriptStringUtil;


import org.apache.log4j.Logger;

/**
 * 
 * @author Jordi Hernández Sellés
 */
public class ClientSideHandlerGeneratorImpl implements
		ClientSideHandlerGenerator {
	private static final Logger log = Logger.getLogger(ClientSideHandlerGeneratorImpl.class.getName());
	
	/**
	 * Global bundles, to include in every page
	 */
	private List globalBundles;
	
	/**
	 * Bundles to include upon request
	 */
	private List contextBundles;
	
	
	private JawrConfig config;

	public ClientSideHandlerGeneratorImpl(List globalBundles,
			List contextBundles, JawrConfig config) {
		super();
		this.globalBundles = globalBundles;
		this.contextBundles = contextBundles;
		this.config = config;
	}


	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.handler.ClientSideHandlerGenerator#getClientSideHandler(javax.servlet.http.HttpServletRequest)
	 */
	public StringBuffer getClientSideHandler(HttpServletRequest request) {
		StringBuffer sb = loadScriptTemplate(SCRIPT_TEMPLATE);// TODO cache this
		String locale = this.config.getLocaleResolver().resolveLocaleCode(request);
		sb.append("JAWR.app_context_path='").append(request.getContextPath()).append("';");
		
		sb.append("JAWR.loader.mapping='").append(getPathPrefix(request)).append("';");
		sb.append("JAWR.loader.jsbundles = [");
		addAllBundles(globalBundles,locale,sb);
		if(!globalBundles.isEmpty() && !contextBundles.isEmpty()){
			sb.append(",");
		}
		addAllBundles(contextBundles,locale,sb);
		sb.append("]\n");
		
		if(this.config.isDebugModeOn()) {
			sb.append(loadScriptTemplate(DEBUG_SCRIPT_TEMPLATE)).append("\n");
		}
		
		// The global bundles are already sorted and filtered by the bundleshandler
		for(Iterator it = globalBundles.iterator();it.hasNext();){
			JoinableResourceBundle bundle = (JoinableResourceBundle) it.next();
			sb.append("JAWR.loader.insert(")
				.append(JavascriptStringUtil.quote(bundle.getName()))
				.append(");\n");
		}
		return sb;
	}
	
	private String getPathPrefix(HttpServletRequest request) {
		if(null != this.config.getContextPathOverride()) {
			return this.config.getContextPathOverride();
		}
		String mapping = null == this.config.getServletMapping() ? "" : this.config.getServletMapping();
		String path = PathNormalizer.joinPaths(request.getContextPath(), mapping);
		path = path.endsWith("/") ? path : path +'/';
		return PathNormalizer.joinPaths(request.getContextPath(), mapping);
	}
	
	/**
	 * Adds a javascript Resourcebundle representation for each member of a
	 * List containing JoinableResourceBundles
	 * @param bundles
	 * @param variantKey
	 * @param buf
	 */
	private void addAllBundles(List bundles, String variantKey, StringBuffer buf){
		for(Iterator it = bundles.iterator();it.hasNext();){
			JoinableResourceBundle bundle = (JoinableResourceBundle) it.next();
			appendBundle(bundle,variantKey,buf);
		}
	}
	
	private void appendBundle(JoinableResourceBundle bundle,String variantKey, StringBuffer buf){
		buf.append("new JAWR.ResourceBundle(")
			.append(JavascriptStringUtil.quote(bundle.getName()))
			.append(",").append(JavascriptStringUtil.quote(bundle.getURLPrefix(variantKey)))
			.append(",[");
		for(Iterator it = bundle.getItemPathList(variantKey).iterator();it.hasNext();){
			String path = (String) it.next();
			if(this.config.getGeneratorRegistry().isPathGenerated(path)) {
				path = PathNormalizer.createGenerationPath(path);
			}
			if("".equals(this.config.getContextPathOverride()) && path.startsWith("/"))
				path = path.substring(1);
			buf.append(JavascriptStringUtil.quote(path));
			if(it.hasNext())
				buf.append(",");
		}
		buf.append("]");
		buf.append(")");
		
	}
	
	/**
	 * Loads a template containing the functions which convert properties into methods. 
	 * @return
	 */
	private StringBuffer loadScriptTemplate(String path) {
		StringWriter sw = new StringWriter();
		try {
			InputStream is = ClassLoaderResourceUtils.getResourceAsStream(path,this);
			int i;
			while((i = is.read()) != -1) {
				sw.write(i);
			}
		} catch (IOException e) {
			log.fatal("a serious error occurred when initializing ClientSideHandlerGeneratorImpl");
			throw new RuntimeException("Classloading issues prevent loading the loader template to be loaded. ");
		}
		
		return sw.getBuffer();
	}
}
