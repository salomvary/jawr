/**
 * Copyright 2007-2009 Jordi Hernández Sellés, Ibrahim Chaehoi, Matt Ruby
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
package net.jawr.web.context;

/**
 * This class defines the Jawr context.
 * 
 * @author Ibrahim Chaehoi
 *
 */
public class JawrContext {
	
	/** The object name for the MBean of the Jawr config manager */
	private String mbeanObjectName;
	
	/**
	 * Constructor. 
	 */
	public JawrContext() {
		
	}

	/**
	 * Returns the mbeanObjectName
	 * @return the mbeanObjectName
	 */
	public String getMbeanObjectName() {
		return mbeanObjectName;
	}

	/**
	 * Sets the mbeanObjectName.
	 * @param mbeanObjectName the mbeanObjectName to set
	 */
	public void setMbeanObjectName(String mbeanObjectName) {
		this.mbeanObjectName = mbeanObjectName;
	}
	
	/**
	 * Reset the context. 
	 */
	public void reset(){
		
		this.mbeanObjectName = null;
	}
}