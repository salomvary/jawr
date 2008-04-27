package net.jawr.web.resource.bundle.message;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

public class BundleStringJasonifier {
	private Map keyMap;
	private Properties bundleValues;
	
	private static final String FUNC = "p(";
	
	

	public BundleStringJasonifier(Properties bundleValues) {
		super();
		this.bundleValues = bundleValues;
		this.keyMap = new HashMap();
		Enumeration keys = this.bundleValues.keys();
		while(keys.hasMoreElements())
			processKey((String)keys.nextElement());
	}
	
	public StringBuffer serializeBundles() {
		StringBuffer sb = new StringBuffer("{");
		
		for(Iterator it = keyMap.keySet().iterator(); it.hasNext();) {
			String currentKey = (String) it.next();
			handleKey(sb,keyMap,currentKey,currentKey,!it.hasNext());
		}
		
		return sb.append("}");
	}
	
	private void handleKey( final StringBuffer sb, Map currentLeaf, String currentKey, String fullKey, boolean isLeafLast) {
		
		Map newLeaf = (Map) currentLeaf.get(currentKey);
		
		if(bundleValues.containsKey(fullKey)) {
			addValuedKey(sb,currentKey,fullKey);
			if(!newLeaf.isEmpty()) {
				sb.append(",({");
				for(Iterator it = newLeaf.keySet().iterator(); it.hasNext();) {
					String newKey = (String) it.next();
					handleKey(sb,newLeaf,newKey,fullKey + "." + newKey,!it.hasNext());
				}
				sb.append("}))");
			}
			else {
				sb.append(")");
			}
		}
		else if(!newLeaf.isEmpty()) {
			sb.append(quote(currentKey))
			  .append(":{");
			for(Iterator it = newLeaf.keySet().iterator(); it.hasNext();) {
				String newKey = (String) it.next();
				handleKey(sb,newLeaf,newKey,fullKey + "." + newKey,!it.hasNext());
			}
			sb.append("}");

		}
		if(!isLeafLast)
			sb.append(",");
	}
	
	private void addValuedKey(final StringBuffer sb, String key, String fullKey) {
		sb	.append(quote(key))
		.append(":")
		.append(FUNC)
		.append(quote(bundleValues.get(fullKey).toString()));
	}

	private void processKey(String key) {
		StringTokenizer tk = new StringTokenizer(key,".");
		Map currentMap = this.keyMap;
		while(tk.hasMoreTokens()) {
			String token = tk.nextToken();
			if(!currentMap.containsKey(token))
				currentMap.put(token, new HashMap());
			currentMap = (Map) currentMap.get(token);
		}
	}
	
	/**
	 * From JSONObject.java, from which the copyright notice is the following: 
	 * 
	 * Copyright (c) 2002 JSON.org
	 * Permission is hereby granted, free of charge, to any person obtaining a copy
	 * of this software and associated documentation files (the "Software"), to deal
	 * in the Software without restriction, including without limitation the rights
	 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	 * copies of the Software, and to permit persons to whom the Software is
	 * furnished to do so, subject to the following conditions:
	 *
	 *
	 * The above copyright notice and this permission notice shall be included in all
	 * copies or substantial portions of the Software.
	 * 
	 * Produce a string in double quotes with backslash sequences in all the
     * right places. A backslash will be inserted within </, allowing JSON
     * text to be delivered in HTML. In JSON text, a string cannot contain a
     * control character or an unescaped quote or backslash.
     * @param string A String
     * @return  A String correctly formatted for insertion in a JSON text.
	 */
	private String quote(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        char         b;
        char         c = 0;
        int          i;
        int          len = string.length();
        StringBuffer sb = new StringBuffer(len + 4);
        String       t;

        sb.append('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                sb.append('\\');
                sb.append(c);
                break;
            case '/':
                if (b == '<') {
                    sb.append('\\');
                }
                sb.append(c);
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
                sb.append("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
                               (c >= '\u2000' && c < '\u2100')) {
                    t = "000" + Integer.toHexString(c);
                    sb.append("\\u" + t.substring(t.length() - 4));
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }
}
