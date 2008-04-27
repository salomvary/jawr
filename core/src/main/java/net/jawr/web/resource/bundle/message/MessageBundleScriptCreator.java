package net.jawr.web.resource.bundle.message;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;


public class MessageBundleScriptCreator {
	private static final Logger log = Logger.getLogger(MessageBundleScriptCreator.class.getName());
	private static final String SCRIPT_TEMPLATE = "/net/jawr/web/resource/bundle/message/messages.js";
	private static StringBuffer template;
	private String configParam;
	private Properties props;
	
	
	public MessageBundleScriptCreator(String configParam) {
		super();
		if(null == template)
			template = get();
		
		props = new Properties();
		this.configParam = configParam;
	}
	
	private StringBuffer get() {
		StringWriter sw = new StringWriter();
		try {
			// TODO ensure this is loaded within any server
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(SCRIPT_TEMPLATE);
			int i;
			while((i = is.read()) != -1) {
				sw.write(i);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sw.getBuffer();
	}


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
