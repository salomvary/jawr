/**
 * Copyright 2007-2008 Jordi Hernández Sellés
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
package net.jawr.web.resource.bundle.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import net.jawr.web.resource.bundle.factory.util.ClassLoaderResourceUtils;

import org.apache.log4j.Logger;


/**
 * Creates a script which holds the data from a message bundle(s). The script is such that properties can be accessed as functions
 * (i.e.: alert(com.mycompany.mymessage()); ). 
 * 
 * @author Jordi Hernández Sellés
 */
public class MessageBundleScriptCreator {
	private static final Logger log = Logger.getLogger(MessageBundleScriptCreator.class.getName());
	private static final String SCRIPT_TEMPLATE = "/net/jawr/web/resource/bundle/message/messages.js";
	private static StringBuffer template;
	private String configParam;
	private Properties props;
	
	
	public MessageBundleScriptCreator(String configParam) {
		super();
		if(null == template)
			template = loadScriptTemplate();
		
		props = new Properties();
		this.configParam = configParam;
	}
	
	/**
	 * Loads a template containing the functions which convert properties into methods. 
	 * @return
	 */
	private StringBuffer loadScriptTemplate() {
		StringWriter sw = new StringWriter();
		try {
			InputStream is = ClassLoaderResourceUtils.getResourceAsStream(SCRIPT_TEMPLATE,this);
			int i;
			while((i = is.read()) != -1) {
				sw.write(i);
			}
		} catch (IOException e) {
			log.fatal("a serious error occurred when initializing MessageBundleScriptCreator");
			throw new RuntimeException("Classloading issues prevent loading the message template to be loaded. ");
		}
		
		return sw.getBuffer();
	}


	/**
	 * Loads the message resource bundles specified and uses a BundleStringJasonifier to generate the properties. 
	 * @return
	 */
	public Reader createScript(){
		Locale locale = null;
		if(configParam.indexOf('@') != -1){
			String localeKey = configParam.substring(configParam.indexOf('@')+1);
			configParam = configParam.substring(0,configParam.indexOf('@'));
			locale = new Locale(localeKey);
		}
		String[] names = configParam.split("\\|");
		for(int x = 0;x < names.length; x++) {
			ResourceBundle bundle;
			
			if(null != locale)
				bundle = ResourceBundle.getBundle(names[x],locale);
			else bundle = ResourceBundle.getBundle(names[x]);
			
			Enumeration keys = bundle.getKeys();
			while(keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				props.put(key, bundle.getString(key));
			}
		}
		BundleStringJasonifier bsj = new BundleStringJasonifier(props);
		String script = template.toString();
		String messages = bsj.serializeBundles().toString();
		script = script.replaceFirst("@namespace", "messages");
		script = script.replaceFirst("@messages", messages);
		
		return new StringReader(script);
	}
	
	
}
