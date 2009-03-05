/**
 * Copyright 2009 Ibrahim Chaehoi
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
package net.jawr.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jawr.web.config.JawrConfig;
import net.jawr.web.resource.bundle.factory.util.ClassLoaderResourceUtils;
import net.jawr.web.resource.bundle.factory.util.ConfigPropertiesSource;
import net.jawr.web.resource.bundle.factory.util.PropsFilePropertiesSource;
import net.jawr.web.resource.bundle.factory.util.ServletContextAware;

import org.apache.log4j.Logger;

/**
 * This servlet is responsible of providing images, which are define in the classpath.
 * This image could be linked to a CSS file also loaded from the classpath. 
 * 
 * @author Ibrahim CHAEHOI
 */
public class ClassPathImageServlet extends HttpServlet {

	// ~---------- Variables ----------

	/** The logger */
	private static final Logger log = Logger.getLogger(JawrServlet.class);
	
    /** The image MIME map, associating the image extension to their MIME type */
	private static Map<String, String> imgMimeMap = new HashMap<String, String>(20);
    static {
        imgMimeMap.put("gif", "image/gif");
        imgMimeMap.put("jpg", "image/jpeg");
        imgMimeMap.put("jpe", "image/jpeg");
        imgMimeMap.put("jpeg", "image/jpeg");
        imgMimeMap.put("png", "image/png");
        imgMimeMap.put("ief", "image/ief");
        imgMimeMap.put("tiff", "image/tiff");
        imgMimeMap.put("tif", "image/tiff");
        imgMimeMap.put("ras", "image/x-cmu-raster");
        imgMimeMap.put("pnm", "image/x-portable-anymap");
        imgMimeMap.put("pbm", "image/x-portable-bitmap");
        imgMimeMap.put("pgm", "image/x-portable-graymap");
        imgMimeMap.put("ppm", "image/x-portable-pixmap");
        imgMimeMap.put("rgb", "image/x-rgb");
        imgMimeMap.put("xbm", "image/x-xbitmap");
        imgMimeMap.put("xpm", "image/x-xpixmap");
        imgMimeMap.put("xwd", "image/x-xwindowdump");
        
        /* Add XML related MIMEs */
        imgMimeMap.put("svg", "image/svg+xml");
        imgMimeMap.put("svgz", "image/svg+xml");
        imgMimeMap.put("wbmp", "image/vnd.wap.wbmp");
        
    }
    
    /** The servlet path configure in the JAWR properties */
    private String servletPath;
    
    // ~---------- Methods ----------

    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException {
		try {
			
			ServletConfig config = getServletConfig();
			String configLocation = config.getInitParameter("configLocation");
			String configPropsSourceClass = config.getInitParameter("configPropertiesSourceClass");
			if(null == configLocation && null == configPropsSourceClass) 
				throw new ServletException("Neither configLocation nor configPropertiesSourceClass init params were set."
											+" You must set at least the configLocation param. Please check your web.xml file");
			
			// Initialize the config properties source that will provide with all configuration options. 
			ConfigPropertiesSource propsSrc;
			
			// Load a custom class to set config properties
			if(null != configPropsSourceClass) {
				propsSrc = (ConfigPropertiesSource) ClassLoaderResourceUtils.buildObjectInstance(configPropsSourceClass);			
				if(propsSrc instanceof ServletContextAware){
					((ServletContextAware)propsSrc).setServletContext(getServletContext());
				}
			}
			else {
				// Default config properties source, reads from a .properties file in the classpath. 
				propsSrc = new PropsFilePropertiesSource();
			}

			// If a custom properties source is a subclass of PropsFilePropertiesSource, we hand it the configLocation param. 
			// This affects the standard one as well. 
			if(propsSrc instanceof PropsFilePropertiesSource)
				((PropsFilePropertiesSource) propsSrc).setConfigLocation(configLocation);
			
			
			// Read properties from properties source
			JawrConfig jawrConfig = new JawrConfig(propsSrc.getConfigProperties());
			servletPath = jawrConfig.getCssImageServletPath();
			
			if(servletPath == null){
				throw new ServletException("The property '"+JawrConfig.JAWR_CSS_IMG_CLASSPATH_SERVLET_PATH+"' is not defined in the configuration source.");
			}
			
		}catch (ServletException e) {
			log.fatal("Jawr servlet with name" +  getServletConfig().getServletName() +" failed to initialize properly. ");
			log.fatal("Cause:");
			log.fatal(e.getMessage(),e);
			throw e;
		}catch (RuntimeException e) {
			log.fatal("Jawr servlet with name" +  getServletConfig().getServletName() +" failed to initialize properly. ");
			log.fatal("Cause: ");
			log.fatal(e.getMessage(),e);
			throw new ServletException(e);
		}
	}

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

    	
		String requestUri = request.getRequestURI();
		int servletPathIdx = requestUri.indexOf(servletPath);
		
		// If the servlet path is not found return a 404 error
		if(servletPathIdx == -1){
			
			log.error("The configured CSS image servlet path '"+servletPath+"' is not found in the request URI : " + requestUri);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		// Retrieve the file path requested
		String fileName = requestUri.substring(servletPathIdx+servletPath.length());
		
		int suffixIdx = requestUri.lastIndexOf(".");
		if(suffixIdx == -1){
			
			log.error("No extension found for the request URI : " + requestUri);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		// Retrieve the extension
		String extension = requestUri.substring(suffixIdx+1).toLowerCase();
		String contentType = imgMimeMap.get(extension);
		if(contentType == null){

			log.error("No image extension match the extension '"+extension+"' for the request URI : " + requestUri);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		// Read the file from the classpath and send it in the outputStream
		int length = 0;
		try {
			OutputStream os = response.getOutputStream();
			InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
			int count;
			byte buf[] = new byte[4096];
			while ((count = is.read(buf)) > -1){
				os.write(buf, 0, count);
				length += count;
			}
			is.close();
			os.close();
		} catch (Exception ex) {
			
			log.error("Unable to load the image for the request URI : " + requestUri, ex);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}

		// Set the content length, and the content type based on the file extension
		response.setContentType(contentType);
		response.setContentLength(length);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
