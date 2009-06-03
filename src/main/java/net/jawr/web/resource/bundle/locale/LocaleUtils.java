/**
 * Copyright 2007-2009 Jordi Hernández Sellés, Ibrahim Chaehoi
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
package net.jawr.web.resource.bundle.locale;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import net.jawr.web.resource.FileNameUtils;
import net.jawr.web.resource.bundle.factory.util.ClassLoaderResourceUtils;

/**
 * Utility class for locale.
 * 
 * are allowed within the url.   
 *  
 * @author Jordi Hernández Sellés
 * @author Ibrahim Chaehoi
 */
public class LocaleUtils {
	
	/** The message resource bundle suffix */
	private static final String MSG_RESOURCE_BUNDLE_SUFFIX = ".properties";

	/**
	 * Returns the localized bundle name
	 * @param bundleName
	 * @param localeKey
	 * @return the localized bundle name
	 */
	public static String getLocalizedBundleName(String bundleName, String localeKey) {
		
		String newName = bundleName.substring(0, bundleName.lastIndexOf('.'));
		newName += '_' + localeKey;
		newName += bundleName.substring(bundleName.lastIndexOf('.'));
		
		return newName;
	}

	/**
	 * Returns the list of available locale suffixes for a message resource bundle
	 * @param messageBundlePath the resource bundle path
	 * @return the list of available locale suffixes for a message resource bundle
	 */
	public static List getAvailableLocaleSuffixes(String messageBundlePath) {
		
		List availableLocaleSuffixes = new ArrayList();
		Locale[] availableLocales = Locale.getAvailableLocales();
		int idx = FileNameUtils.indexOfExtension(messageBundlePath);
		if(idx != -1){
			messageBundlePath = messageBundlePath.substring(0, idx);
		}
		
		Control msgRsBundleControl = ResourceBundle.Control.getControl(Control.FORMAT_DEFAULT);
		
		addSuffixIfAvailable(messageBundlePath, availableLocaleSuffixes, msgRsBundleControl, Locale.ROOT);
		
		for (int i = 0; i < availableLocales.length; i++) {
			Locale locale = availableLocales[i];
			addSuffixIfAvailable(messageBundlePath, availableLocaleSuffixes, msgRsBundleControl, locale);
		}
		
		return availableLocaleSuffixes;
	}

	/**
	 * Add the locale suffix if the message resource bundle file exists. 
	 * @param messageBundlePath the message resource bundle path
	 * @param availableLocaleSuffixes the list of available locale suffix to update
	 * @param msgRsBundleControl the message resource control
	 * @param locale the locale to check.
	 */
	private static void addSuffixIfAvailable(String messageBundlePath, List availableLocaleSuffixes, Control msgRsBundleControl, Locale locale) {
		String localMsgResourcePath = msgRsBundleControl.toBundleName(messageBundlePath, locale)+MSG_RESOURCE_BUNDLE_SUFFIX;
		URL resourceUrl = null; 
		try {
			resourceUrl = ClassLoaderResourceUtils.getResourceURL(localMsgResourcePath, LocaleUtils.class);
		} catch (Exception e) {
			// Nothing to do
		}
		
		if(resourceUrl != null){
			
			String suffix = localMsgResourcePath.substring(messageBundlePath.length());
			if(suffix.length() > 0){
				
				if(suffix.length() == MSG_RESOURCE_BUNDLE_SUFFIX.length()){
					suffix = "";
				}else{
					// remove the "_" before the suffix "_en_US" => "en_US"
					suffix = suffix.substring(1, suffix.length()-MSG_RESOURCE_BUNDLE_SUFFIX.length());
				}
			}
			availableLocaleSuffixes.add(suffix);
		}
	}
}
