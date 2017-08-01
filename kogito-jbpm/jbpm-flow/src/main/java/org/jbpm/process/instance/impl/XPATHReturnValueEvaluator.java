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

package org.jbpm.process.instance.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;

import org.kie.api.runtime.process.ProcessContext;

public class XPATHReturnValueEvaluator
    implements
    ReturnValueEvaluator,
    Externalizable {
    private static final long   serialVersionUID = 510l;

    private String              expression;
    private String              id;

    public XPATHReturnValueEvaluator() {
    }

    public XPATHReturnValueEvaluator(final String expression,
                                    final String id) {
        this.expression = expression;
        this.id = id;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
//        id = in.readUTF();
        expression = (String) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
//        out.writeUTF( id );
        out.writeObject(expression);
    }

    public String getDialect() {
        return this.id;
    }

    public Object evaluate(final ProcessContext context) throws Exception {        
    	XPathFactory factory = XPathFactory.newInstance();
    	XPath xpathEvaluator = factory.newXPath();
    	xpathEvaluator.setXPathFunctionResolver( 
    			new  XPathFunctionResolver() {
    				public XPathFunction resolveFunction(QName functionName, int arity)
    				{
    					String localName = functionName.getLocalPart();
    					if ("getVariable".equals(localName)) {
    						return new GetVariableData();
    					}
    					else {
                            throw new IllegalArgumentException("Unknown BPMN function: " + functionName);
    					}
    				}

    				class GetVariableData implements XPathFunction {
    					public Object evaluate(List args) throws XPathFunctionException {
    						String varname = (String) args.get(0);
    						return context.getVariable(varname);
    					}
    				}
    			}
    	);
    	xpathEvaluator.setXPathVariableResolver(new XPathVariableResolver() {
            
            public Object resolveVariable(QName variableName) {
                return context.getVariable(variableName.getLocalPart());
            }
        });
    	
    	xpathEvaluator.setNamespaceContext(new NamespaceContext() {
			private static final String DROOLS_NAMESPACE_URI = "http://www.jboss.org/drools";
			private String[] prefixes = {"drools", "bpmn2"};
			@Override
			public Iterator getPrefixes(String namespaceURI) {
				return Arrays.asList(prefixes).iterator();
			}
			
			@Override
			public String getPrefix(String namespaceURI) {
				if (DROOLS_NAMESPACE_URI.equalsIgnoreCase(namespaceURI)) {
					return "bpmn2";
				}
				return null;
			}
			
			@Override
			public String getNamespaceURI(String prefix) {
				if ("bpmn2".equalsIgnoreCase(prefix)) {
					return DROOLS_NAMESPACE_URI;
				}
				return null;
			}
		});

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return xpathEvaluator.evaluate(this.expression, builder.newDocument(), XPathConstants.BOOLEAN);
    }

    public String toString() {
        return this.expression;
    }

}
