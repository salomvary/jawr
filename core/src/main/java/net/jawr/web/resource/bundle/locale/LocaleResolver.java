package net.jawr.web.resource.bundle.locale;

import javax.servlet.ServletRequest;

public interface LocaleResolver {
	
	public abstract String resolveLocaleCode(ServletRequest request); 

}
