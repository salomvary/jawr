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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.jawr.web.util.PropertyUtils;

/**
 * This interface defines the MBean which manage the Jawr configuration for a we application, so it will affect all JawrConfigManagerMBean associated
 * to Jawr Servlets.
 * 
 * @author Ibrahim Chaehoi
 */
public class JawrApplicationConfigManager implements
		JawrApplicationConfigManagerMBean {

	
	/** The message of the property, when the values are not equals for the different configuration manager */
	private static String NOT_IDENTICAL_VALUES = "Value for this property are not identical";

	/** The message when an error occured during the retrieve of the property value */
	private static String ERROR_VALUE = "An error occured while retrieving the value for this property";

	/** The configuration manager for the Javascript handler */
	private JawrConfigManagerMBean jsMBean;

	/** The configuration manager for the CSS handler */
	private JawrConfigManagerMBean cssMBean;

	/** The configuration manager for the image handler */
	private JawrConfigManagerMBean imgMBean;

	/** The set of session ID for which all requests will be executed in debug mode */
	private Set debugSessionIdSet = new HashSet();

	/**
	 * Constructor
	 */
	public JawrApplicationConfigManager() {

	}

	/**
	 * Sets the configuration manager for the Javascript handler
	 * 
	 * @param jsMBean the configuration manager to set
	 */
	public void setJsMBean(JawrConfigManagerMBean jsMBean) {
		this.jsMBean = jsMBean;
	}

	/**
	 * Sets the configuration manager for the CSS handler
	 * 
	 * @param cssMBean the configuration manager to set
	 */
	public void setCssMBean(JawrConfigManagerMBean cssMBean) {
		this.cssMBean = cssMBean;
	}

	/**
	 * Sets the configuration manager for the image handler
	 * 
	 * @param imgMBean the configuration manager to set
	 */
	public void setImgMBean(JawrConfigManagerMBean imgMBean) {
		this.imgMBean = imgMBean;
	}

	/**
	 * Returns the list of initialized configuration managers.
	 * 
	 * @return the list of initialized configuration managers.
	 */
	private List getInitializedConfigurationManagers() {

		List mBeans = new ArrayList();
		if (jsMBean != null) {
			mBeans.add(jsMBean);
		}

		if (cssMBean != null) {
			mBeans.add(cssMBean);
		}
		if (imgMBean != null) {
			mBeans.add(imgMBean);
		}

		return mBeans;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#getCharsetName()
	 */
	public String getCharsetName() {

		return getStringValue("charsetName");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#getDebugOverrideKey()
	 */
	public String getDebugOverrideKey() {

		return getStringValue("debugOverrideKey");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#isDebugModeOn()
	 */
	public String getDebugModeOn() {

		return getStringValue("debugModeOn");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#isGzipResourcesForIESixOn()
	 */
	public String getGzipResourcesForIESixOn() {

		return getStringValue("gzipResourcesForIESixOn");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#isGzipResourcesModeOn()
	 */
	public String getGzipResourcesModeOn() {

		return getStringValue("gzipResourcesModeOn");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#getContextPathOverride()
	 */
	public String getContextPathOverride() {
		return getStringValue("contextPathOverride");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#getContextPathSslOverride()
	 */
	public String getContextPathSslOverride() {

		return getStringValue("contextPathSslOverride");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#getUseContextPathOverrideInDebugMode()
	 */
	public String getUseContextPathOverrideInDebugMode() {
	
		return getStringValue("useContextPathOverrideInDebugMode");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#setCharsetName(java.lang.String)
	 */
	public void setCharsetName(String charsetName) {
	
		setStringValue("charsetName", charsetName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#setDebugModeOn(boolean)
	 */
	public void setDebugModeOn(String debugMode) {
	
		setBooleanValue("debugModeOn", debugMode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#setDebugOverrideKey(java.lang.String)
	 */
	public void setDebugOverrideKey(String debugOverrideKey) {
	
		setStringValue("debugOverrideKey", debugOverrideKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#setGzipResourcesForIESixOn(boolean)
	 */
	public void setGzipResourcesForIESixOn(String gzipResourcesForIESixOn) {
	
		setBooleanValue("gzipResourcesForIESixOn", gzipResourcesForIESixOn);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#setGzipResourcesModeOn(boolean)
	 */
	public void setGzipResourcesModeOn(String gzipResourcesModeOn) {
	
		setBooleanValue("gzipResourcesModeOn", gzipResourcesModeOn);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#setContextPathOverride(java.lang.String)
	 */
	public void setContextPathOverride(String contextPathOverride) {

		setStringValue("contextPathOverride", contextPathOverride);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#setContextPathSslOverride(java.lang.String)
	 */
	public void setContextPathSslOverride(String contextPathSslOverride) {

		setStringValue("contextPathSslOverride", contextPathSslOverride);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#setUseContextPathOverrideInDebugMode(java.lang.String)
	 */
	public void setUseContextPathOverrideInDebugMode(
			String useContextPathOverrideInDebugMode) {
		
		setBooleanValue("useContextPathOverrideInDebugMode", useContextPathOverrideInDebugMode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#refreshConfig()
	 */
	public void refreshConfig() {

		if (jsMBean != null) {
			jsMBean.refreshConfig();
		}
		if (cssMBean != null) {
			cssMBean.refreshConfig();
		}
		if (imgMBean != null) {
			imgMBean.refreshConfig();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#addDebugSessionId(java.lang.String)
	 */
	public void addDebugSessionId(String sessionId) {
		debugSessionIdSet.add(sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#removeDebugSessionId(java.lang.String)
	 */
	public void removeDebugSessionId(String sessionId) {
		debugSessionIdSet.remove(sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#removeAllDebugSessionId()
	 */
	public void removeAllDebugSessionId() {
		debugSessionIdSet.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jawr.web.config.jmx.JawrApplicationConfigManagerMBean#isDebugSessionId(java.lang.String)
	 */
	public boolean isDebugSessionId(String sessionId) {
		return debugSessionIdSet.contains(sessionId);
	}

	/**
	 * Returns the string value of the configuration managers
	 * 
	 * @param property the property to retrieve
	 * @return the string value of the configuration managers
	 */
	public String getStringValue(String property) {

		List mBeans = getInitializedConfigurationManagers();
		try {

			if (mBeans.size() == 3) {

				if (areEquals(PropertyUtils.getProperty(jsMBean, property),
						PropertyUtils.getProperty(cssMBean, property), PropertyUtils.getProperty(imgMBean, property))) {

					return PropertyUtils.getProperty(jsMBean, property);
				} else {
					return NOT_IDENTICAL_VALUES;
				}
			}

			if (mBeans.size() == 2) {
				JawrConfigManagerMBean mBean1 = (JawrConfigManagerMBean) mBeans
						.get(0);
				JawrConfigManagerMBean mBean2 = (JawrConfigManagerMBean) mBeans
						.get(1);

				if (areEquals(PropertyUtils.getProperty(mBean1, property),
						PropertyUtils.getProperty(mBean2, property))) {
					return PropertyUtils.getProperty(mBean1, property);
				} else {
					return NOT_IDENTICAL_VALUES;
				}
			}

			JawrConfigManagerMBean mBean1 = (JawrConfigManagerMBean) mBeans
					.get(0);

			return PropertyUtils.getProperty(mBean1, property);

		} catch (Exception e) {
			return ERROR_VALUE;
		}

	}

	/**
	 * Update the property with the string value in each config manager.
	 * 
	 * @param property the property to update
	 * @param value the value to set
	 */
	public void setStringValue(String property, String value) {
		try {
			if (jsMBean != null) {
				PropertyUtils.setProperty(jsMBean, property, value);
			}
			if (cssMBean != null) {
				PropertyUtils.setProperty(cssMBean, property, value);
			}
			if (imgMBean != null) {
				PropertyUtils.setProperty(imgMBean, property, value);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Exception while setting the string value", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Exception while setting the string value", e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Exception while setting the string value", e);
		}
	}

	/**
	 * Update the property with the string value in each config manager.
	 * 
	 * @param property the property to update
	 * @param value the value to set
	 */
	public void setBooleanValue(String property, String value) {
		try {
			if (jsMBean != null) {
				PropertyUtils.setProperty(jsMBean, property, Boolean.valueOf(value));
			}
			if (cssMBean != null) {
				PropertyUtils.setProperty(cssMBean, property, Boolean.valueOf(value));
			}
			if (imgMBean != null) {
				PropertyUtils.setProperty(imgMBean, property, Boolean.valueOf(value));
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Exception while setting the boolean value", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Exception while setting the boolean value", e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Exception while setting the boolean value", e);
		}
	}
	
	/**
	 * Returns true if the 2 string are equals.
	 * 
	 * @param str1 the first string
	 * @param str2 the 2nd string
	 * @return true if the 2 string are equals.
	 */
	public boolean areEquals(String str1, String str2) {

		return (str1 == null && str2 == null || str1 != null && str2 != null
				&& str1.equals(str2));
	}

	/**
	 * Returns true if the 3 string are equals.
	 * 
	 * @param str1 the first string
	 * @param str2 the 2nd string
	 * @param str3 the 3rd string
	 * @return true if the 3 string are equals.
	 */
	public boolean areEquals(String str1, String str2, String str3) {

		return (str1 == null && str2 == null && str3 == null || str1 != null
				&& str2 != null && str3 != null && str1.equals(str2)
				&& str2.equals(str3));
	}
}
