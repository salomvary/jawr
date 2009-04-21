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
package net.jawr.web;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.jawr.web.resource.bundle.JoinableResourceBundle;
import net.jawr.web.resource.bundle.handler.ResourceBundlesHandler;
import net.jawr.web.resource.bundle.renderer.BasicBundleRenderer;
import net.jawr.web.resource.bundle.renderer.BundleRenderer;
import net.jawr.web.servlet.JawrServlet;
import net.jawr.web.servlet.mock.MockServletConfig;
import net.jawr.web.servlet.mock.MockServletContext;
import net.jawr.web.servlet.mock.MockServletRequest;
import net.jawr.web.servlet.mock.MockServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The bundle processor will manage the bundle processing at build time.
 * 
 * @author Ibrahim Chaehoi
 */
public class BundleProcessor {

	/** The name of the param-value tag */
	private static final String WEB_XML_FILE_PATH = "WEB-INF/web.xml";

	/** The name of the param-value tag */
	private static final String PARAM_NAME_TAG_NAME = "param-name";

	/** The name of the param-value tag */
	private static final String PARAM_VALUE_TAG_NAME = "param-value";

	/** The name of the param-value tag */
	private static final String SERVLET_TAG_NAME = "servlet";

	/** The name of the param-value tag */
	private static final String SERVLET_CLASS_TAG_NAME = "servlet-class";

	/** The name of the param-value tag */
	private static final String SERVLET_NAME_TAG_NAME = "servlet-name";

	/** The name of the param-value tag */
	private static final String INIT_PARAM_TAG_NAME = "init-param";

	/**
	 * Launch the bundle processing
	 * 
	 * @param baseDirPath the base directory path
	 * @param tmpDirPath the temp directory path
	 * @param destDirPath the destination directory path
	 * @throws Exception if an exception occurs
	 */
	public void process(String baseDirPath, String tmpDirPath, String destDirPath) throws Exception {

		process(baseDirPath, tmpDirPath, destDirPath, new ArrayList());
	}

	/**
	 * Launch the bundle processing
	 * 
	 * @param baseDirPath the base directory path
	 * @param tmpDirPath the temp directory path
	 * @param destDirPath the destination directory path
	 * @param servletNames the list of the name of servlets to initialized
	 * @throws Exception if an exception occurs
	 */
	public void process(String baseDirPath, String tmpDirPath, String destDirPath, List servletNames) throws Exception {

		// Retrieve the parameters from baseDir+"/WEB-INF/web.xml"
		File webXml = new File(baseDirPath, WEB_XML_FILE_PATH);
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = docBuilder.parse(webXml);
		NodeList servletNodes = doc.getElementsByTagName(SERVLET_TAG_NAME);
		String servletName = null;
		Class servletClass = null;

		ServletContext servletContext = new MockServletContext(baseDirPath, tmpDirPath);

		for (int i = 0; i < servletNodes.getLength(); i++) {

			MockServletConfig config = new MockServletConfig(servletContext);

			Node servletNode = servletNodes.item(i);
			Map initParameters = new HashMap();
			boolean servletMustBeInitialized = false;
			NodeList childNodes = servletNode.getChildNodes();
			for (int j = 0; j < childNodes.getLength(); j++) {
				Node servletChildNode = childNodes.item(j);
				if (servletChildNode.getNodeName().equals(SERVLET_NAME_TAG_NAME)) {
					servletName = servletChildNode.getTextContent();
					config.setServletName(servletName);

					// If the servlet name is part of the list of servlet to initialized
					// Set the flag accordingly
					if (servletNames.contains(servletName)) {
						servletMustBeInitialized = true;
					}

				} else if (servletChildNode.getNodeName().equals(SERVLET_CLASS_TAG_NAME)) {

					String servletClassName = servletChildNode.getTextContent();
					servletClass = getClass().getClassLoader().loadClass(servletClassName);

					// If the servlet is a Jawr Servlet it must be initialized
					if (JawrServlet.class.isAssignableFrom(servletClass)) {
						servletMustBeInitialized = true;
					}
				} else if (servletChildNode.getNodeName().equals(INIT_PARAM_TAG_NAME)) {
					initializeInitParams(servletChildNode, initParameters);
				}
			}

			if (servletMustBeInitialized) {

				// Remove config listener parameters
				initParameters.remove("jawr.config.reload.interval");
				config.setInitParameters(initParameters);
				process(servletClass, config, destDirPath);
			}
		}
	}

	/**
	 * Initialize the init parameters define in the servlet config
	 * 
	 * @param initParameters the map of initialization parameters
	 */
	private void initializeInitParams(Node initParamNode, Map initParameters) {

		String paramName = null;
		String paramValue = null;

		NodeList childNodes = initParamNode.getChildNodes();
		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = childNodes.item(j);
			String nodeName = childNode.getNodeName();
			if (nodeName.equals(PARAM_NAME_TAG_NAME)) {
				paramName = childNode.getTextContent();
			} else if (nodeName.equals(PARAM_VALUE_TAG_NAME)) {
				paramValue = childNode.getTextContent();
			}
		}

		initParameters.put(paramName, paramValue);
	}

	/**
	 * Launch the bundle processing
	 * 
	 * @param servletClass the servlet class
	 * @param servletConfig the servlet config
	 * @param destDirPath the destination directory path
	 * @throws ServletException if a servlet exception occurs.
	 */
	public void process(Class servletClass, ServletConfig servletConfig, String destDirPath) throws Exception {

		HttpServlet servlet = (HttpServlet) servletClass.newInstance();
		servlet.init(servletConfig);
		String type = servletConfig.getInitParameter("type");
		String mapping = servletConfig.getInitParameter("mapping");
		ResourceBundlesHandler bundleHandler = null;
		ServletContext servletContext = servletConfig.getServletContext();

		// Retrieve the bundle Handler
		if (type == null || type.equals("js")) {

			bundleHandler = (ResourceBundlesHandler) servletContext.getAttribute(ResourceBundlesHandler.JS_CONTEXT_ATTRIBUTE);
		} else if (type.equals("css")) {
			bundleHandler = (ResourceBundlesHandler) servletContext.getAttribute(ResourceBundlesHandler.CSS_CONTEXT_ATTRIBUTE);
		}

		if (bundleHandler != null) {

			File destDir = new File(destDirPath);
			// Create the destination directory if it doesn't exists
			if (!destDir.exists()) {
				destDir.mkdirs();
			}

			createBundles(servlet, bundleHandler, destDirPath, mapping);
		}
	}

	/**
	 * Creates the bundles in the destination directory
	 * 
	 * @param servlet the servlet
	 * @param bundleHandler the bundles handler
	 * @param destDirPath the destination directory path
	 * @param mapping the mapping of the servlet
	 * @throws IOException if an IO exception occurs
	 * @throws ServletException if a servlet exception occurs
	 */
	private void createBundles(HttpServlet servlet, ResourceBundlesHandler bundleHandler, String destDirPath, String mapping) throws IOException,
			ServletException {
		List bundles = bundleHandler.getContextBundles();

		// How to configure useGzip for IE6 as it depends on the browser,
		// which is not defined here
		boolean useGzip = bundleHandler.getConfig().isGzipResourcesModeOn();
		Iterator bundleIterator = bundles.iterator();
		MockServletResponse response = new MockServletResponse();
		MockServletRequest request = new MockServletRequest();

		// For the list of bundle defines, create the file associated
		while (bundleIterator.hasNext()) {
			JoinableResourceBundle bundle = (JoinableResourceBundle) bundleIterator.next();
			String path = createLinkToBundle(bundleHandler, bundle.getName(), useGzip);

			request.setRequestPath(path);
			if (mapping != null && mapping.length() > 0) {
				int idx = path.indexOf(mapping);
				if (idx > -1) {
					path = path.substring(idx + mapping.length());
				}

				if (!path.startsWith("/")) {
					path = "/" + path;
				}
				request.setPathInfo(path);
			}

			// Create the parent directory of the destination file
			File destFile = new File(destDirPath, path);
			if (!destFile.exists()) {
				destFile.getParentFile().mkdirs();
			}

			// Set the response mock to write in the destination file
			response.setFile(destFile);
			servlet.service(request, response);
			response.close();
		}
	}

	/**
	 * Returns the link to the bundle
	 * 
	 * @param handler the resource bundles handler
	 * @param path the path
	 * @return the link to the bundle
	 * @throws IOException if an IO exception occurs
	 */
	private String createLinkToBundle(ResourceBundlesHandler handler, String path, boolean useGzip) throws IOException {

		BundleRenderer bundleRenderer = new BasicBundleRenderer(handler);
		StringWriter sw = new StringWriter();
		bundleRenderer.renderBundleLinks(path, "", "", new HashSet(), useGzip, sw);
		return sw.toString();
	}
}
