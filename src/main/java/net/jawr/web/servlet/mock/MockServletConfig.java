/**
 * 
 */
package net.jawr.web.servlet.mock;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.collections.iterators.IteratorEnumeration;

/**
 * @author ibrahim
 *
 */
public class MockServletConfig implements ServletConfig {

	/** The servlet context */
	private ServletContext context;
	
	/** The initialization parameters */
	private Map initParameters = new HashMap();
	
	private String servletName;
	
	/**
	 * Constructor 
	 */
	public MockServletConfig(ServletContext context){
		
		this.context = context;
	}
	
	/**
	 * Constructor 
	 */
	public MockServletConfig(String servletName, ServletContext context, Map initParameters){
		
		this.servletName = servletName;
		this.context = context;
		this.initParameters = initParameters;
	}
	
	/**
	 * @param servletName the servletName to set
	 */
	public void setServletName(String servletName) {
		this.servletName = servletName;
	}

	/**
	 * Sets the initialization parameters map
	 * @param initParameters the initParameters to set
	 */
	public void setInitParameters(Map initParameters) {
		this.initParameters = initParameters;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getInitParameter(java.lang.String)
	 */
	public String getInitParameter(String name) {
		
		return (String) initParameters.get(name);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getInitParameterNames()
	 */
	public Enumeration getInitParameterNames() {
		
		return new IteratorEnumeration(initParameters.keySet().iterator());
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getServletContext()
	 */
	public ServletContext getServletContext() {
		return context;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getServletName()
	 */
	public String getServletName() {
		return servletName;
	}

}
