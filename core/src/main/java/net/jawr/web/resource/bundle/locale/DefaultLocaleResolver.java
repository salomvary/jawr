package net.jawr.web.resource.bundle.locale;

import java.util.Locale;

import javax.servlet.ServletRequest;

public class DefaultLocaleResolver implements LocaleResolver {

	public String resolveLocaleCode(ServletRequest request) {
		if(request.getLocale() != Locale.getDefault())
			return request.getLocale().toString();
		else return null;
	}

}
