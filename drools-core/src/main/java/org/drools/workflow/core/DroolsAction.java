/**
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

package org.drools.workflow.core;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import org.drools.spi.CompiledInvoker;
import org.drools.spi.Wireable;

public class DroolsAction implements Externalizable, Wireable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
    private Map<String, Object> metaData = new HashMap<String, Object>();

    public void wire(Object object) {
        setMetaData( "Action",
                     object );
    }
    
    public void setMetaData(String name, Object value) {
        this.metaData.put(name, value);
    }
    
    public Object getMetaData(String name) {
        return this.metaData.get(name);
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    @SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.name = (String) in.readObject();
        this.metaData = (Map<String, Object>) in.readObject();
        Object action = in.readObject();
        setMetaData( "Action", action );
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( name );
        Object action = this.metaData.remove( "Action" );
        out.writeObject( this.metaData );
        if ( action instanceof CompiledInvoker ) {
            out.writeObject(  null );
        } else {
            out.writeObject(action);   
        } 
        setMetaData( "Action", action );
    }
    
}
