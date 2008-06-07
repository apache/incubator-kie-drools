/*
 * Copyright 2006 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.lang.dsl;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * An ANTLR-driven implementation for the DSL Mapping Entry interface
 * 
 * @author mattgeis
 */
public class AntlrDSLMappingEntry extends AbstractDSLMappingEntry implements DSLMappingEntry {
	private boolean headMatchGroupAdded = false;
	private boolean tailMatchGroupAdded = false;
	
	public AntlrDSLMappingEntry() {
		this(DSLMappingEntry.ANY, DSLMappingEntry.EMPTY_METADATA, null, null);
	}

	public AntlrDSLMappingEntry(final Section section, final MetaData metadata,
			final String key, final String value) {
		this.section = section;
		this.metadata = metadata;
		this.setMappingKey(key);
		this.setMappingValue(value);
	}

	
	/**
	 * @param key
	 *            the key to set
	 */
	public void setMappingKey(String key) {
		//the "key" in this case is already mostly formed into 
		//a pattern by ANTLR, and just requires a bit of post-processing.
		if (key != null) {
			key = key.trim();
		}
		this.key = key;

		if (key != null) {
			int substr = 0;
			// escape '$' to avoid errors
			//final String escapedKey = key.replaceAll("\\$", "\\\\\\$");
			// retrieving variables list and creating key pattern
			final StringBuffer buf = new StringBuffer();

			if (!key.startsWith("^")) {
				// making it start with a space char or a line start
				buf.append("(\\W|^)").append(key);
				redistributeVariables();
				headMatchGroupAdded = true;
			}


			// if pattern ends with a pure variable whose pattern could create
			// a greedy match, append a line end to avoid multiple line matching
			if (buf.toString().endsWith("(.*?)")) {
				buf.append("$");
			} else {
				buf.append("(\\W|$)");
				tailMatchGroupAdded = true;
			}

			// setting the key pattern and making it space insensitive
			String pat = buf.toString();
			//first, look to see if it's 
			if (key.substring(substr).trim().startsWith("-")
					&& (!key.substring(substr).trim().startsWith("-\\s*"))) {
				pat = pat.substring(0, pat.indexOf('-') + 1) + "\\s*"
						+ pat.substring(pat.indexOf('-') + 1).trim();
			}
			//may not need to do this at all
			//pat = pat.replaceAll("\\s+", "\\\\s+");
			this.keyPattern = Pattern.compile(pat, Pattern.DOTALL
					| Pattern.MULTILINE);

		} else {
			this.keyPattern = null;
		}
		// update value mapping
		//this.setMappingValue(this.value);
	}
	
	/**
	 * The keys for this map are integers, starting at 1.  However,
	 * in certain cases we insert a matching group at the start of the
	 * pattern, which means that 1 should become 2, 2 become 3, etc.
	 */
	private void redistributeVariables(){
		for (Iterator it = variables.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			Integer i = (Integer)entry.getValue();
			variables.put(entry.getKey(), new Integer(i.intValue() + 1));
		}
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setMappingValue(final String value) {
		StringBuffer valuePatternBuffer = new StringBuffer();
		StringBuffer valueBuffer = new StringBuffer();
		if(headMatchGroupAdded){
			valuePatternBuffer.append("$1");
			valueBuffer.append("$1");
		}
		valuePatternBuffer.append(value);
		valueBuffer.append(value);
		if(tailMatchGroupAdded){
			int maxGroupIndex = 0;
			if(!variables.isEmpty()){
				Integer tailMatchGroupIndex = (Integer) Collections.max(variables.values());
				maxGroupIndex = tailMatchGroupIndex.intValue();
			}else if(headMatchGroupAdded){
				//if empty, but head group matched, set max group to 1
				maxGroupIndex++;
			}
			maxGroupIndex++;
			valuePatternBuffer.append("$" + maxGroupIndex);
			valueBuffer.append("$" + maxGroupIndex);
		}
		this.valuePattern = valuePatternBuffer.toString();
		this.value = valueBuffer.toString();
		
	}
}
