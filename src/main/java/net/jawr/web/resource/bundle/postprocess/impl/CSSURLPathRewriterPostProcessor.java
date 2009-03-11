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
package net.jawr.web.resource.bundle.postprocess.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.jawr.web.config.JawrConfig;
import net.jawr.web.exception.ResourceNotFoundException;
import net.jawr.web.resource.bundle.factory.util.ClassLoaderResourceUtils;
import net.jawr.web.resource.bundle.factory.util.RegexUtil;
import net.jawr.web.resource.bundle.generator.GeneratorRegistry;
import net.jawr.web.resource.bundle.postprocess.AbstractChainedResourceBundlePostProcessor;
import net.jawr.web.resource.bundle.postprocess.BundleProcessingStatus;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Single file postprocessor used to rewrite CSS URLs according to the new relative locations of the references when
 * added to a bundle. Since the path changes, the URLs must be rewritten accordingly.  
 * URLs in css files are expected to be according to the css spec (see http://www.w3.org/TR/REC-CSS2/syndata.html#value-def-uri). 
 * Thus, single double, or no quotes enclosing the url are allowed (and remain as they are after rewriting). Escaped parens and quotes 
 * are allowed within the url.   
 *  
 * @author Jordi Hern�ndez Sell�s
 * @author Ibrahim Chaehoi
 */
public class CSSURLPathRewriterPostProcessor extends
		AbstractChainedResourceBundlePostProcessor {
	
	/** The logger */
	private static Log logger = LogFactory.getLog(CSSURLPathRewriterPostProcessor.class);
	
	/** The prefix for CSS defines in the classpath  */
	public static final String CLASSPATH_CSS_PREFIX = GeneratorRegistry.CLASSPATH_CSS_BUNDLE_PREFIX + GeneratorRegistry.PREFIX_SEPARATOR;

	/** The URL separator */
	private static final String URL_SEPARATOR = "/";

	/** The cache buster separator */
	private static final String CACHE_BUSTER_PREFIX = "-cb";

	/** The extension separator */
	private static final String EXTENSION_SEPARATOR = ".";

	/** The URL separator for JAR element */
	private static final String URL_JAR_ELT_PREFIX = "!";

	/** The URL file prefix */
	private static final String URL_FILE_PREFIX = "file:/";

	/** The url pattern */
	private static final Pattern urlPattern = Pattern.compile(	"url\\(\\s*" // 'url(' and any number of whitespaces 
																+ "((\\\\\\))|[^)])*" // any sequence of characters, except an unescaped ')'
																+ "\\s*\\)",  // Any number of whitespaces, then ')'
																Pattern.CASE_INSENSITIVE); // works with 'URL('
	
	/** The back reference regexp */
	private static final String backRefRegex = "(\\.\\./)";
	
	/** The back reference regexp pattern */
	private static final Pattern backRefRegexPattern = Pattern.compile(backRefRegex);
	
	/** The URL separator pattern */
	private static final Pattern URLSeparatorPattern = Pattern.compile(URL_SEPARATOR);
	
	/** The cache buster patter */
	//private static Pattern cacheBusterPattern = Pattern.compile("(.*)\\.(.*)$");

	/** The cache buster replace pattern */
	//private static final String CACHE_BUSTER_REPLACE_PATTERN = "$1.$2";
	
	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.postprocess.impl.AbstractChainedResourceBundlePostProcessor#doPostProcessBundle(net.jawr.web.resource.bundle.postprocess.BundleProcessingStatus, java.lang.StringBuffer)
	 */
	protected StringBuffer doPostProcessBundle(BundleProcessingStatus status,
			StringBuffer bundleData) throws IOException {
		
		String data = bundleData.toString();
		
		// Initial backrefs (number of backtracking paths needed, i.e. '../').
		int initialBackRefs = 1;
		
		JawrConfig jawrConfig = status.getJawrConfig();
		if(! "".equals(jawrConfig.getServletMapping())){
			StringTokenizer tk = new StringTokenizer(jawrConfig.getServletMapping(),URL_SEPARATOR);
			initialBackRefs += tk.countTokens();
		}
		
		String bundleName = status.getCurrentBundle().getName();
		initialBackRefs += numberOfForwardReferences(bundleName);
		List resourceBackRefs = getPathNamesInResource(status.getLastPathAdded());
		Matcher matcher = urlPattern.matcher(data);
		StringBuffer sb = new StringBuffer();
		while(matcher.find()) {
			List backRefs = new ArrayList();
			backRefs.addAll(resourceBackRefs);
			
			String url = getUrlPath(matcher.group(), initialBackRefs, backRefs, status);
			
			matcher.appendReplacement(sb, RegexUtil.adaptReplacementToMatcher(url));
		}
		matcher.appendTail(sb);
		return sb;
	}
	

	/**
	 * Transform a matched url so it points to the proper relative path with respect to the given path.  
	 * @param match the matched URL
	 * @param initialBackRefs the initial backward references
	 * @param resourceBackRefs the list of resources backward references
	 * @param status the bundle processing status
	 * @return the image URL path
	 */
	private String getUrlPath(String match, int initialBackRefs, List resourceBackRefs, BundleProcessingStatus status) {

		JawrConfig jawrConfig = status.getJawrConfig();
		String imagePathOverride = jawrConfig.getCssImagePathOverride();
		boolean useClassPathCssImgServlet = jawrConfig.isUseClasspathCssImageServlet();
		String classPathImgServletPath = jawrConfig.getCssImageServletPath();
		
		String url = match.substring(match.indexOf('(')+1,match.lastIndexOf(')'))
					.trim();

		// To keep quotes as they are, first they are checked and removed. 
		String quoteStr = "";
		if( url.startsWith("'") || url.startsWith("\"")) {
			quoteStr = url.charAt(0)+"";
			url = url.substring(1,url.length()-1);
		}
		
		// Check if the URL is absolute, if it is return it as is. 
		int firstSlash = url.indexOf('/');
		if(0 == firstSlash || (firstSlash != -1 && url.charAt(++firstSlash) == '/')){
			StringBuffer sb = new StringBuffer("url(");
			sb.append(quoteStr).append(url).append(quoteStr).append(")");
			return sb.toString();
		}
		
		int backRefsInURL = 0;
		// Remove leading slash
		if(url.startsWith("../")) {
			Matcher backUrls = backRefRegexPattern.matcher(url);
			while(backUrls.find())
				backRefsInURL++;
			url = url.replaceAll(backRefRegex, "");
		}
		else if(url.startsWith(URL_SEPARATOR))
			url = url.substring(1,url.length());
		else if(url.startsWith("./"))
			url = url.substring(2,url.length());
		
		// append the override url here if need be
		if(imagePathOverride != null){
			StringBuffer sb = new StringBuffer("url(");
			sb.append(quoteStr).append(imagePathOverride).append(url).append(quoteStr).append(")");
			return sb.toString();
		}
		
		// Adjust the forward pathnames according to the backrefs in the url
		if(backRefsInURL > 0 && resourceBackRefs.size() > 0) {
			int size = resourceBackRefs.size();
			int counter = 0;
			while(size > 0) {
				counter++;
				if(counter >= 50)
					throw new RuntimeException("Infinite loop on url: " + url + " :size:" + size);
				size--;
				backRefsInURL--;
				resourceBackRefs.remove(size);
				if(backRefsInURL == 0)
					break;
			}
		}		
		
		boolean classpathCss = !resourceBackRefs.isEmpty()
		&& ((String) resourceBackRefs.get(0))
				.startsWith(CLASSPATH_CSS_PREFIX) && useClassPathCssImgServlet;
		
		if (classpathCss) {
			String root = (String) resourceBackRefs.get(0);
			resourceBackRefs.set(0, root.replace(CLASSPATH_CSS_PREFIX, ""));
			
			// If we are in Debug mode, the Jawr CSS generator will be used.
			// As the path of this generator is define as root level,
			// we remove the initial backreference.
			if(jawrConfig.isDebugModeOn()){
				initialBackRefs = 0;
			}
			
			
		}
		
		// Start rendering the result, starting by the initial quote, if any. 
		StringBuffer urlPrefix = new StringBuffer("url(").append(quoteStr);
		
		// Add the back references as needed
		for (int i = 0; i < initialBackRefs; i++) {
			urlPrefix.append("../");
		}
		
		if (classpathCss) {
			// If path starts with "/", remove it
			String servletPath = classPathImgServletPath;
			if(servletPath.startsWith(URL_SEPARATOR)){
				servletPath = servletPath.substring(1);
			}
			
			// Add classpath image servlet path in the URL
			urlPrefix.append(servletPath);
		}
		
		StringBuffer urlBuffer = new StringBuffer();
		for(Iterator it = resourceBackRefs.iterator();it.hasNext(); ) {
			urlBuffer.append(it.next()).append(URL_SEPARATOR);			
		}
		urlBuffer.append(url);
		
		url = addCacheBuster(status, urlBuffer.toString()); 
		
		return urlPrefix.append(url).append(quoteStr).append(")").toString();
	}
	
	
	/**
	 * Adds the cache buster to the CSS image
	 * @param status the bundle processing status
	 * @param url the URL
	 * @return the url of the CSS image with a cache buster
	 * @throws ResourceNotFoundException if the resource is not found
	 */
	private String addCacheBuster(BundleProcessingStatus status, String url) {
		
		String newUrl = status.getImageMapping(url);
		 
		if(newUrl != null){
			return newUrl;
		}
		
		newUrl = url;
		
		URL internalCssImgUrl = null;
		try {
			internalCssImgUrl = ClassLoaderResourceUtils.getResourceURL(url, this);
		} catch (ResourceNotFoundException e) {
			logger.debug(e);
		}
		if(internalCssImgUrl != null){
			String filePath = internalCssImgUrl.getFile();
			filePath = filePath.substring(URL_FILE_PREFIX.length());
			int jarDefEndIdx = filePath.lastIndexOf(URL_JAR_ELT_PREFIX);
			filePath = filePath.substring(0, jarDefEndIdx);
			File file = new File(filePath);
			
			long lastModified = -1;
			if(file.exists()){
				lastModified = file.lastModified();
			}else{
				if(logger.isInfoEnabled()){
					logger.info("Impossible to find the jar : '"+filePath+"'.\n" +
							"Define the lastModified date as the current date. This value will be change at the next bundle processing.");
				}
				
				lastModified = (new Date()).getTime();
			}
			
			// Add the cache buster extension
			int extensionIdx = url.lastIndexOf(EXTENSION_SEPARATOR);
			newUrl = url.substring(0, extensionIdx)+CACHE_BUSTER_PREFIX+lastModified+url.substring(extensionIdx);
			
			// Set the result in a map, so we will not search it the next time
			status.setImageMapping(url, newUrl);
		}
		return newUrl;
	}


	/**
	 * Gets the path names within a resource URL, excluding the resource file name. 
	 * @param resourceURL the resource URL
	 * @return the list of path names within a resource URL, excluding the resource file name. 
	 */
	private List getPathNamesInResource(String resourceURL) {
		List names = new ArrayList();
		String[] namesArray = resourceURL.split(URL_SEPARATOR);
		
		// Add all but the last pathname
		for (int i = 0; i < namesArray.length -1 ; i++) {
			if(!"".equals(namesArray[i]))
				names.add(namesArray[i]);
		}
		
		return names;
	}
	
	/**
	 * Find the number of occurrences of / within a url. 
	 * @param url the url
	 * @return the number of occurrences of / within a url. 
	 */
	private int numberOfForwardReferences(String url) {
		Matcher backUrls = URLSeparatorPattern.matcher(url);
		int numMatches = 0;
		while(backUrls.find())
			numMatches++;
		
		if(url.startsWith(URL_SEPARATOR))
			numMatches--;
		
		return numMatches;
	}
	
}
