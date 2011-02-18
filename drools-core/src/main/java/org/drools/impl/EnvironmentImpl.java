/*
 * Copyright 2010 JBoss Inc
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

package org.drools.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.runtime.Environment;
import org.drools.runtime.Globals;

public class EnvironmentImpl implements Environment {

	private Map<String, Object> environment = new HashMap<String, Object>();
	
    private Environment delegate;
    
    public void setDelegate(Environment delegate) {
        this.delegate = delegate;
    }       
	
	public Object get(String identifier) {
	    Object object = environment.get(identifier);
	    if ( object == null && delegate != null ) {
	        object = this.delegate.get( identifier );
	    }
		return object;
	}

	public void set(String name, Object object) {
		environment.put(name, object);
	}

}
