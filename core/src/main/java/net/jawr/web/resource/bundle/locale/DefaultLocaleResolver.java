package net.jawr.web.resource.bundle.locale;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

public class DefaultLocaleResolver implements LocaleResolver {

	public String resolveLocaleCode(HttpServletRequest request) {
		if(request.getLocale() != Locale.getDefault())
			return request.getLocale().toString();
		else return null;
	}

}
