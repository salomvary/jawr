/**
 * 
 */
package net.jawr.web;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.jawr.web.servlet.JawrServlet;
import net.jawr.web.servlet.mock.MockServletConfig;
import net.jawr.web.servlet.mock.MockServletContext;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The bundle processor will manage the bundle processing at build time.
 * 
 * @author Ibrahim Chaehoi
 */
public class BundleProcessor {

	/**
	 * Launch the bundle processing
	 * @param baseDirPath the base directory path
	 * @param tmpDirPath the temp directory path
	 * @throws Exception if an exception occurs
	 */
	public void process(String baseDirPath, String tmpDirPath) throws Exception {
	
		process(baseDirPath, tmpDirPath, new ArrayList());
	}
	
	/**
	 * Launch the bundle processing
	 * @param baseDirPath the base directory path
	 * @param tmpDirPath the temp directory path
	 * @param servletNames the list of the name of servlets to initialized 
	 * @throws Exception if an exception occurs
	 */
	public void process(String baseDirPath, String tmpDirPath, List servletNames) throws Exception {
		
		
		// Retrieve the parameters from baseDir+"/WEB-INF/web.xml"
		File webXml = new File(baseDirPath, "WEB-INF/web.xml");
		DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = docBuilder.parse(webXml);
		NodeList servletNodes = doc.getElementsByTagName("servlet");
		String servletName = null;
		Class servletClass = null;
		
		ServletContext servletContext = new MockServletContext(baseDirPath, tmpDirPath);
		
		for(int i = 0; i < servletNodes.getLength(); i++){
			
			MockServletConfig config = new MockServletConfig(servletContext);
			
			Node servletNode = servletNodes.item(i);
			Map initParameters = new HashMap();
			boolean servletMustBeInitialized = false;
			NodeList childNodes = servletNode.getChildNodes();
			for(int j = 0; j < childNodes.getLength(); j++){
				Node servletChildNode =  childNodes.item(j);
				if(servletChildNode.getNodeName().equals("servlet-name")){
					servletName = servletChildNode.getTextContent();
					config.setServletName(servletName);

					// If the servlet name is part of the list of servlet to initialized
					// Set the flag accordingly
					if(servletNames.contains(servletName)){
						servletMustBeInitialized = true;
					}
					
				}else if(servletChildNode.getNodeName().equals("servlet-class")){
					
					String servletClassName = servletChildNode.getTextContent();
					servletClass = getClass().getClassLoader().loadClass(servletClassName);
					
					// If the servlet is a Jawr Servlet it must be initialized
					if(JawrServlet.class.isAssignableFrom(servletClass)){
						servletMustBeInitialized = true;
					}
				}else if(servletChildNode.getNodeName().equals("init-param")){
					initializeInitParams(servletChildNode, initParameters);
				}
			}
			
			if(servletMustBeInitialized){
				
				// Remove config listener parameters 
				initParameters.remove("jawr.config.reload.interval");
				config.setInitParameters(initParameters);
				process(servletClass, config);
			}
		}
	}

	/**
	 * Initialize the init parameters define in the servlet config
	 * @param initParameters the map of initialization parameters
	 */
	private void initializeInitParams(Node initParamNode, Map initParameters) {
	
		String paramName = null;
		String paramValue = null;
		
		NodeList childNodes = initParamNode.getChildNodes();
		for(int j = 0; j < childNodes.getLength(); j++){
			Node childNode =  childNodes.item(j);
			String nodeName = childNode.getNodeName();
			if(nodeName.equals("param-name")){
				paramName = childNode.getTextContent();
			}else if(nodeName.equals("param-value")){
				paramValue = childNode.getTextContent();
			}
		}
		
		initParameters.put(paramName, paramValue);
	}

	/**
	 * Launch the bundle processing
	 * @param servletClass the servlet class
	 * @param servletConfig the servlet config
	 * @throws ServletException if a servlet exception occurs.
	 */
	public void process(Class servletClass, ServletConfig servletConfig) throws Exception{
		
		HttpServlet servlet = (HttpServlet) servletClass.newInstance();
		servlet.init(servletConfig);
	}
}
