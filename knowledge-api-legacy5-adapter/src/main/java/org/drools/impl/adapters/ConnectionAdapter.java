/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.impl.adapters;

import java.util.Map;

import org.kie.api.definition.process.Connection;

public class ConnectionAdapter implements org.drools.definition.process.Connection {

	public Connection delegate;
	
	public ConnectionAdapter(Connection delegate) {
		this.delegate = delegate;
	}

	public org.drools.definition.process.Node getFrom() {
		return new NodeAdapter(delegate.getFrom());
	}

	public org.drools.definition.process.Node getTo() {
		return new NodeAdapter(delegate.getTo());
	}

	public String getFromType() {
		return delegate.getFromType();
	}

	public String getToType() {
		return delegate.getToType();
	}

	public Map<String, Object> getMetaData() {
		return delegate.getMetaData();
	}

	public Object getMetaData(String name) {
		return delegate.getMetaData().get(name);
	}

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ConnectionAdapter && delegate.equals(((ConnectionAdapter)obj).delegate);
    }
}
