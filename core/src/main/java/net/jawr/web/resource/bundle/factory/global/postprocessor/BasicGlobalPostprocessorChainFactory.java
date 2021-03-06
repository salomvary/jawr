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
package net.jawr.web.resource.bundle.factory.global.postprocessor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import net.jawr.web.JawrConstant;
import net.jawr.web.resource.bundle.factory.util.ClassLoaderResourceUtils;
import net.jawr.web.resource.bundle.global.preprocessor.css.smartsprites.CssSmartSpritesGlobalPreprocessor;
import net.jawr.web.resource.bundle.global.processor.AbstractChainedGlobalProcessor;
import net.jawr.web.resource.bundle.global.processor.ChainedGlobalProcessor;
import net.jawr.web.resource.bundle.global.processor.CustomGlobalProcessorChainedWrapper;
import net.jawr.web.resource.bundle.global.processor.EmptyGlobalProcessor;
import net.jawr.web.resource.bundle.global.processor.GlobalProcessor;

/**
 * This class defines the global preprocessor factory.
 * 
 * @author Ibrahim Chaehoi
 * 
 */
public class BasicGlobalPostprocessorChainFactory implements
		GlobalPostprocessorChainFactory {

	/** The user-defined preprocessors */
	private Map<String, ChainedGlobalProcessor> customPreprocessors = new HashMap<String, ChainedGlobalProcessor>();

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.jawr.web.resource.bundle.factory.global.preprocessor.
	 * GlobalPreprocessorChainFactory#setCustomGlobalPreprocessors(java.util.Map)
	 */
	public void setCustomGlobalPreprocessors(Map<String, String> keysClassNames) {
		
		for(Iterator<Entry<String, String>> it = keysClassNames.entrySet().iterator(); it.hasNext();){
			
			Entry<String, String> entry = it.next();
			GlobalProcessor customGlobalPreprocessor = 
				(GlobalProcessor) ClassLoaderResourceUtils.buildObjectInstance((String) entry.getValue());
			
			String key = (String) entry.getKey();			
			customPreprocessors.put(key, new CustomGlobalProcessorChainedWrapper(key, customGlobalPreprocessor));
		}		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.factory.processor.ProcessorChainFactory#
	 * buildDefaultProcessorChain()
	 */
	public GlobalProcessor buildDefaultProcessorChain() {

		return new EmptyGlobalProcessor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.factory.processor.ProcessorChainFactory#
	 * buildProcessorChain(java.lang.String)
	 */
	public GlobalProcessor buildProcessorChain(String processorKeys) {

		if (null == processorKeys)
			return null;
		else if (JawrConstant.EMPTY_GLOBAL_PREPROCESSOR_ID
				.equals(processorKeys))
			return new EmptyGlobalProcessor();

		StringTokenizer tk = new StringTokenizer(processorKeys, ",");

		AbstractChainedGlobalProcessor chain = null;
		while (tk.hasMoreTokens())
			chain = addOrCreateChain(chain, tk.nextToken());

		return chain;
	}

	/**
	 * Creates an AbstractChainedGlobalPreprocessor. If the supplied
	 * chain is null, the new chain is returned. Otherwise it is added to the
	 * existing chain.
	 * 
	 * @param chain
	 *            the chained post processor
	 * @param key
	 *            the id of the post processor
	 * @return the chained post processor, with the new post processor.
	 */
	private AbstractChainedGlobalProcessor addOrCreateChain(
			AbstractChainedGlobalProcessor chain, String key) {

		AbstractChainedGlobalProcessor toAdd;

		if (customPreprocessors.get(key) == null) {
			toAdd = buildProcessorByKey(key);
		} else{
			toAdd = (AbstractChainedGlobalProcessor) customPreprocessors
				.get(key);
		}
		
		AbstractChainedGlobalProcessor newChainResult = null;
		if (chain == null) {
			newChainResult = toAdd;
		}else{
			chain.addNextProcessor(toAdd);
			newChainResult = chain;
		}

		return newChainResult;
	}

	/**
	 * Build the global preprocessor from the ID given in parameter
	 * 
	 * @param key the ID of the preprocessor
	 * @return a global preprocessor
	 */
	private AbstractChainedGlobalProcessor buildProcessorByKey(String key) {

		AbstractChainedGlobalProcessor processor = null;

		if (key.equals(JawrConstant.GLOBAL_CSS_SMARTSPRITES_PREPROCESSOR_ID)) {
			processor = new CssSmartSpritesGlobalPreprocessor();
		}

		return processor;
	}

}
