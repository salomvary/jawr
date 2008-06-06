/**
 * Copyright 2008  Jordi Hernández Sellés
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
package net.jawr.web.resource.bundle.generator.validator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;

import net.jawr.web.resource.bundle.factory.util.ClassLoaderResourceUtils;
import net.jawr.web.resource.bundle.generator.ResourceGenerator;

import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorResources;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


/**
 * Generates validation javascript using the apache commons validator. 
 * 
 * @author Jordi Hernández Sellés
 *
 */
public class CommonsValidatorGenerator implements ResourceGenerator {
	private static final Logger log = Logger.getLogger(CommonsValidatorGenerator.class.getName());

	private ValidatorResources validatorResources;
	
	
	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.generator.ResourceGenerator#createResource(java.lang.String, javax.servlet.ServletContext, java.nio.charset.Charset)
	 */
	public Reader createResource(String path, ServletContext servletContext,Charset charset) {
		if(null == this.validatorResources)
			createValidatorResources(path, servletContext);
		
		StringBuffer sb = new StringBuffer();

        Iterator actions = validatorResources.getValidatorActions().values().iterator();

        while (actions.hasNext()) {
            ValidatorAction va = (ValidatorAction) actions.next();

            if (va != null) {
                String javascript = va.getJavascript();

                if ((javascript != null) && (javascript.length() > 0)) {
                    sb.append(javascript).append("\n");
                }
            }
        }

        return new StringReader(sb.toString() );
	}
	
	private void createValidatorResources(String path, ServletContext servletContext) {
		
		StringTokenizer st = new StringTokenizer(path, "|");
        List urlList = new ArrayList();
        InputStream[] inputStreams = new InputStream[st.countTokens()];
        int pos = 0;
        try {
            while (st.hasMoreTokens()) {
                String validatorRules = st.nextToken().trim();

                if (log.isInfoEnabled()) {
                    log.info("Loading validation rules file from '"
                        + validatorRules + "'");
                }
                inputStreams[pos] = ClassLoaderResourceUtils.getResourceAsStream(validatorRules, this);
                pos++;
            }

            int urlSize = urlList.size();
            URL[] urlArray = new URL[urlSize];

            for (int urlIndex = 0; urlIndex < urlSize; urlIndex++) {
                urlArray[urlIndex] = (URL) urlList.get(urlIndex);
            }

            this.validatorResources = new ValidatorResources(inputStreams);
        } catch (SAXException sex) {
            throw new RuntimeException(sex);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
		} catch (IOException e) {
            throw new RuntimeException(e);
		}
		
	}
	
	
}
