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
package net.jawr.web.config.jmx;

import java.lang.reflect.Method;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

/**
 * Utility class for JMX.
 * 
 * @author Ibrahim Chaehoi
 */
public class JmxUtils {

	/** The logger */
	private static final Logger log = Logger.getLogger(JmxUtils.class);

	/**
	 * Returns the current MBean server or create a new one if not exist.
	 * 
	 * @return the current MBean server or create a new one if not exist.
	 */
	public static MBeanServer getMBeanServer() {

		MBeanServer mbs = null;
		
		// JMX is not enable
		if(System.getProperty("com.sun.management.jmxremote") == null){
			return null;
		}
		
		if (System.getProperty("java.version").startsWith("1.4")) {

			List servers = MBeanServerFactory.findMBeanServer(null);
			if (servers.size() > 0) {
				if (log.isDebugEnabled()){
					log.debug("Retrieving the JMX MBeanServer.");
				}
				
				mbs = (MBeanServer) servers.get(0);
			} else {
				if (log.isDebugEnabled()){
					log.debug("Creating the JMX MBeanServer.");
				}
				
				mbs = MBeanServerFactory.createMBeanServer();
				
			}
		} else {

			try {
				Class managementFactoryClass = JmxUtils.class.getClassLoader().loadClass("java.lang.management.ManagementFactory");
				Method getPlatformMBeanServerMethod = managementFactoryClass.getMethod("getPlatformMBeanServer", new Class[] {});
				mbs = (MBeanServer) getPlatformMBeanServerMethod.invoke(null, null);
			} catch (Exception e) {
				log.error("Enable to get the JMX MBeanServer.");
			}
		}

		return mbs;
	}
	
	/**
	 * Returns the object name for the Jawr configuration Manager MBean
	 * @param servletContext the servelt context
	 * @param resourceType the resource type
	 * @return the object name for the Jawr configuration Manager MBean
	 * @throws Exception if an exception occurs
	 */
	public static String getMBeanObjectName(ServletContext servletContext, String resourceType) throws Exception {
		String contextPath = null;
		
		// Get the context path
		if(servletContext.getMajorVersion() > 2 || servletContext.getMajorVersion() == 2 && servletContext.getMinorVersion() >= 5){
			Method getServletContextPathMethod = servletContext.getClass().getMethod("getContextPath", new Class[] {});
			contextPath = (String) getServletContextPathMethod.invoke(servletContext, null);
		}else{
			contextPath = servletContext.getInitParameter("contextPath");
		}
		
		return getMBeanObjectName(contextPath, resourceType);
	}

	/**
	 * Returns the object name for the Jawr configuration Manager MBean
	 * @param contextPath the context path
	 * @param resourceType  the resource type
	 * @return the object name for the Jawr configuration Manager MBean
	 */
	public static String getMBeanObjectName(String contextPath, String resourceType) {
		String objectNameStr = null;
		if(contextPath == null){
			log.warn("No context path defined for this web application. You will face issues, if you are deploying mutiple web app, without defining the context.\n" +
					"If you are using a server with Servlet API inferior than 2.5, please use the initParameter 'contextPath' to define your context path in the Jawr servlet.");
			
			contextPath = "default";
		}
		
		if(contextPath.startsWith("/")){
			contextPath = contextPath.substring(1);
		}
		objectNameStr = "net.jawr.web.jmx:type=JawrConfigManager,webappContext="+contextPath+",name="+resourceType+"MBean";
		
		return objectNameStr;
	}
}
