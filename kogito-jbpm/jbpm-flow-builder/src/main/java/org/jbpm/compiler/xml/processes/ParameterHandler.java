/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.compiler.xml.processes;

import java.util.HashSet;

import org.jbpm.process.core.ParameterDefinition;
import org.jbpm.process.core.TypeObject;
import org.jbpm.process.core.Work;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.impl.ParameterDefinitionImpl;
import org.drools.core.xml.BaseAbstractHandler;
import org.drools.core.xml.ExtensibleXmlParser;
import org.drools.core.xml.Handler;
import org.jbpm.process.core.ValueObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ParameterHandler extends BaseAbstractHandler implements Handler {
	
    public ParameterHandler() {
        if ((this.validParents == null) && (this.validPeers == null)) {
            this.validParents = new HashSet<Class<?>>();
            this.validParents.add(Work.class);
            this.validPeers = new HashSet<Class<?>>();         
            this.validPeers.add(null);            
            this.allowNesting = false;
        }
    }
    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder(localName, attrs);
        final String name = attrs.getValue("name");
        emptyAttributeCheck(localName, "name", name, parser);
        Work work = (Work) parser.getParent();
        ParameterDefinition parameterDefinition = new ParameterDefinitionImpl();
        parameterDefinition.setName(name);
        work.addParameterDefinition(parameterDefinition);
        return new ParameterWrapper(parameterDefinition, work);
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return null;
    }
    
    public Class<?> generateNodeFor() {
        return ParameterWrapper.class;
    }
    
    public class ParameterWrapper implements TypeObject, ValueObject {
    	private Work work;
    	private ParameterDefinition parameterDefinition;
    	public ParameterWrapper(ParameterDefinition parameterDefinition, Work work) {
    		this.work = work;
    		this.parameterDefinition = parameterDefinition;
    	}
		public DataType getType() {
			return parameterDefinition.getType();
		}
		public void setType(DataType type) {
			parameterDefinition.setType(type);
		}
		public Object getValue() {
			return work.getParameter(parameterDefinition.getName());
		}
		public void setValue(Object value) {
			work.setParameter(parameterDefinition.getName(), value);
		}
    }

}
