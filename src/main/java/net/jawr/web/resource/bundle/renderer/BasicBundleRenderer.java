/**
 *    Copyright 2008 Andreas Andreou
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package net.jawr.web.resource.bundle.renderer;

import net.jawr.web.resource.bundle.handler.ResourceBundlesHandler;

/**
 * This class defines the basic Bundle renderer.
 * 
 * @author andyhot
 */
public class BasicBundleRenderer extends AbstractBundleLinkRenderer {

	/**
	 * Constructor
	 * @param bundler the resource bundles handler
	 * @param useRandomParam the flag indicating if we should use teh random parameter
     */
	public BasicBundleRenderer(ResourceBundlesHandler bundler) {
		this(bundler, false);
	}
	
	/**
	 * Constructor
	 * @param bundler the resource bundles handler
	 * @param useRandomParam the flag indicating if we should use teh random parameter
     */
	public BasicBundleRenderer(ResourceBundlesHandler bundler, boolean useRandomParam) {
		super(bundler, useRandomParam);
	}

	/* (non-Javadoc)
	 * @see net.jawr.web.resource.bundle.renderer.AbstractBundleLinkRenderer#renderLink(java.lang.String)
	 */
	protected String renderLink(String fullPath) {
		return fullPath;
	}

}
