/**
 * Copyright 2009-2011 Ibrahim Chaehoi
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
package net.jawr.web.resource.handler.reader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletContext;

import net.jawr.web.JawrConstant;
import net.jawr.web.config.JawrConfig;
import net.jawr.web.exception.ResourceNotFoundException;
import net.jawr.web.resource.bundle.generator.GeneratorRegistry;
import net.jawr.web.resource.bundle.generator.PrefixedResourceGenerator;
import net.jawr.web.resource.handler.reader.grails.GrailsServletContextResourceReader;
import net.jawr.web.util.StringUtils;

import org.apache.log4j.Logger;

/**
 * This class defines the manager for resource reader.
 * 
 * @author Ibrahim Chaehoi
 */
public class ServletContextResourceReaderHandler implements ResourceReaderHandler {

	/** The logger */
	private static final Logger LOGGER = Logger.getLogger(ServletContextResourceReaderHandler.class);
	
	/** The servlet context */
	private ServletContext servletContext;
	
	/** The working directory */
	private String workingDirectory;
	
	/** The generator registry */
	private GeneratorRegistry generatorRegistry;
	
	/** The list of resource readers */
	private List<TextResourceReader> resourceReaders = new ArrayList<TextResourceReader>();
	
	/** The list of stream resource readers */
	private List<StreamResourceReader> streamResourceReaders = new ArrayList<StreamResourceReader>();
	
	/** The list of resource info providers */
	private List<ResourceBrowser> resourceInfoProviders = new ArrayList<ResourceBrowser>();
	
	/**
	 * Constructor
	 * @param servletContext the servlet context
	 * @param jawrConfig the Jawr config
	 * @param generatorRegistry the generator registry
	 * @throws IOException if an IOException occurs.
	 */
	public ServletContextResourceReaderHandler(ServletContext servletContext,
			JawrConfig jawrConfig, GeneratorRegistry generatorRegistry) throws IOException {
		
		String tempWorkingDirectory = ((File) servletContext.getAttribute(JawrConstant.SERVLET_CONTEXT_TEMPDIR)).getCanonicalPath();
		if(jawrConfig.getUseBundleMapping() && StringUtils.isNotEmpty(jawrConfig.getJawrWorkingDirectory())){
			tempWorkingDirectory = jawrConfig.getJawrWorkingDirectory();
		}
		
		this.servletContext = servletContext;
		this.generatorRegistry = generatorRegistry;
		this.generatorRegistry.setResourceReaderHandler(this);
		if (tempWorkingDirectory.startsWith(JawrConstant.FILE_URI_PREFIX)) {
			tempWorkingDirectory = tempWorkingDirectory.substring(JawrConstant.FILE_URI_PREFIX.length());
		} 
		this.workingDirectory = tempWorkingDirectory;
		
		ServletContextResourceReader rd = null;
		
		// In grails apps, the generator uses a special implementation
		if(null == servletContext.getAttribute(JawrConstant.GRAILS_WAR_DEPLOYED)){
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("Using standard servlet context resource reader.");
			}
		
			rd = new ServletContextResourceReader(servletContext, jawrConfig);
		}else{
			
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("Using grails context resource reader.");
			}
		
			rd = new GrailsServletContextResourceReader(servletContext, jawrConfig);
		}
		addResourceReaderToEnd(rd);
		
		// Add FileSystemResourceReader if needed
		String baseContextDir = jawrConfig.getProperty(JawrConstant.JAWR_BASECONTEXT_DIRECTORY);
		if(StringUtils.isNotEmpty(baseContextDir)){
			ResourceReader fileRd = new FileSystemResourceReader(jawrConfig);
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("The base directory context is set to "+baseContextDir);
			}
			
			boolean baseContextDirHighPriority = Boolean.valueOf(jawrConfig.getProperty(JawrConstant.JAWR_BASECONTEXT_DIRECTORY_HIGH_PRIORITY));
			if(baseContextDirHighPriority){
				addResourceReaderToStart(fileRd);
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("Jawr will search in priority in the base directory context before searching in the war content.");
				}
			}else{
				addResourceReaderToEnd(fileRd);
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("Jawr will search in priority in the war content before searching in the base directory context.");
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see net.jawr.web.resource.handler.ResourceReaderHandler#getWorkingDirectory()
	 */
	public String getWorkingDirectory() {
		return this.workingDirectory;
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.handler.WorkingDirectoryLocationAware#setWorkingDirectory(java.lang.String)
	 */
	public void setWorkingDirectory(String workingDir) {
		this.workingDirectory = workingDir;
	}
	
	/**
	 * Initialize the reader
	 * @param obj the reader to initialize
	 */
	private void initReader(Object obj){
		if(obj instanceof WorkingDirectoryLocationAware){
			((WorkingDirectoryLocationAware) obj).setWorkingDirectory(workingDirectory);
		}
		if(obj instanceof ServletContextAware){
			((ServletContextAware) obj).setServletContext(servletContext);
		}
		
		if(obj instanceof ResourceBrowser){
			resourceInfoProviders.add((ResourceBrowser)obj);
		}
	}
	
	/**
	 * Adds the resource reader to the list of available resource readers.  
	 * @param rd the resource reader
	 */
	public void addResourceReaderToEnd(ResourceReader rd) {
		
		if(rd instanceof TextResourceReader){
			resourceReaders.add((TextResourceReader) rd);
		}
		
		if(rd instanceof StreamResourceReader){
			streamResourceReaders.add((StreamResourceReader) rd);
		}
		
		initReader(rd);
	}
	
	/**
	 * Adds the resource reader to the list of available resource readers at the specified position.  
	 * @param rd
	 */
	public void addResourceReaderToStart(ResourceReader rd) {
		if(rd instanceof TextResourceReader){
			resourceReaders.add(0, (TextResourceReader) rd);
		}
		if(rd instanceof StreamResourceReader){
			streamResourceReaders.add(0, (StreamResourceReader) rd);
		}
		
		initReader(rd);
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.handler.reader.ResourceReaderHandler#getResource(java.lang.String)
	 */
	public Reader getResource(String resourceName) throws ResourceNotFoundException{
		
		return getResource(resourceName, false);
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.handler.ResourceReader#getResource(java.lang.String, boolean)
	 */
	public Reader getResource(String resourceName, boolean processingBundle) throws ResourceNotFoundException {

		Reader rd = null;
		
		for (Iterator<TextResourceReader> iterator = resourceReaders.iterator(); iterator.hasNext();) {
			TextResourceReader rsReader = iterator.next();
			if (!(rsReader instanceof PrefixedResourceGenerator) 
					|| (resourceName.startsWith(((PrefixedResourceGenerator)rsReader).getMappingPrefix()+GeneratorRegistry.PREFIX_SEPARATOR))){
					
				rd = rsReader.getResource(resourceName, processingBundle);
				if(rd != null){
					break;
				}
			}
		}
		
		if(rd == null){
			throw new ResourceNotFoundException(resourceName);
		}
		
		return rd;
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.handler.ResourceReader#getResourceAsStream(java.lang.String)
	 */
	public InputStream getResourceAsStream(String resourceName) throws ResourceNotFoundException {

		return getResourceAsStream(resourceName, false);
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.handler.stream.StreamResourceReader#getResourceAsStream(java.lang.String, boolean)
	 */
	public InputStream getResourceAsStream(String resourceName,
			boolean processingBundle) throws ResourceNotFoundException {
		
		generatorRegistry.loadGeneratorIfNeeded(resourceName);
		
		InputStream is = null;
		for (Iterator<StreamResourceReader> iterator = streamResourceReaders.iterator(); iterator.hasNext();) {
			
			StreamResourceReader rsReader = iterator.next();
			if (!(rsReader instanceof PrefixedResourceGenerator) 
					|| (resourceName.startsWith(((PrefixedResourceGenerator)rsReader).getMappingPrefix()+GeneratorRegistry.PREFIX_SEPARATOR))){
			
				is = rsReader.getResourceAsStream(resourceName);
				if(is != null){
					break;
				}
			}
		}
		
		if(is == null){
			throw new ResourceNotFoundException(resourceName);
		}
		
		return is;
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.handler.reader.ResourceBrowser#getResourceNames(java.lang.String)
	 */
	public Set<String> getResourceNames(String dirName) {
		Set<String> resourceNames = new TreeSet<String>();
		for (Iterator<ResourceBrowser> iterator = resourceInfoProviders.iterator(); iterator.hasNext();) {
			ResourceBrowser rsBrowser = iterator.next();
			if(generatorRegistry.isPathGenerated(dirName)){
				if (rsBrowser instanceof PrefixedResourceGenerator) {
					PrefixedResourceGenerator rsGeneratorBrowser = (PrefixedResourceGenerator) rsBrowser;
					String prefix = rsGeneratorBrowser.getMappingPrefix()+GeneratorRegistry.PREFIX_SEPARATOR;
					if(dirName.startsWith(prefix)){
						resourceNames.addAll(rsBrowser.getResourceNames(dirName));
						break;
					}
				}
			}else{
				if (!(rsBrowser instanceof PrefixedResourceGenerator)) {
					resourceNames.addAll(rsBrowser.getResourceNames(dirName));
						break;
				}
			}
		}
		
		return resourceNames;
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.handler.reader.ResourceBrowser#isDirectory(java.lang.String)
	 */
	public boolean isDirectory(String resourceName) {
		boolean result = false;
		for (Iterator<ResourceBrowser> iterator = resourceInfoProviders.iterator(); iterator.hasNext() && !result;) {
			ResourceBrowser rsBrowser = iterator.next();
			if(generatorRegistry.isPathGenerated(resourceName)){
				if (rsBrowser instanceof PrefixedResourceGenerator) {
					PrefixedResourceGenerator rsGeneratorBrowser = (PrefixedResourceGenerator) rsBrowser;
					String prefix = rsGeneratorBrowser.getMappingPrefix()+GeneratorRegistry.PREFIX_SEPARATOR;
					if(resourceName.startsWith(prefix)){
						result = rsBrowser.isDirectory(resourceName);
					}
				}
			}else{
				if(!(rsBrowser instanceof PrefixedResourceGenerator)){
					result = rsBrowser.isDirectory(resourceName);
				}
			}
		}
		return result;
	}
	
}
