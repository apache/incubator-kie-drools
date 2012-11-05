/*
 * Copyright 2011 JBoss Inc 
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
package org.droolsjbpm.services.impl.bpmn2;

import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.jbpm.bpmn2.xml.PropertyHandler;
import org.jbpm.process.core.context.variable.Variable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


public class ProcessGetInputHandler extends PropertyHandler implements Handler {

    
    private ProcessDescRepoHelper repo;
    
    public ProcessGetInputHandler() {
            super();
            
    }

    @Override
    public Object start(final String uri, final String localName,
                    final Attributes attrs, final ExtensibleXmlParser parser)
                    throws SAXException {
        Object result = super.start(uri, localName, attrs, parser);
        if(result instanceof Variable){
            repo.getInputs().put(((Variable)result).getName(), ((Variable)result).getType().getStringType());
        }
        
        return result;
    }

    public void setRepo(ProcessDescRepoHelper repo) {
        this.repo = repo;
    }
    
    
}