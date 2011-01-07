/*
  Copyright 2010 Intalio Inc

  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
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

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathVariableResolver;

import org.drools.runtime.process.ProcessContext;

public class XPATHReturnValueEvaluator
    implements
    ReturnValueEvaluator,
    Externalizable {
    private static final long   serialVersionUID = 510l;

    private String              xpath;
    private String              id;

    public XPATHReturnValueEvaluator() {
    }

    public XPATHReturnValueEvaluator(final String xpath,
                                    final String id) {
        this.xpath = xpath;
        this.id = id;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        id = in.readUTF();
        xpath = (String) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF( id );
        out.writeObject(xpath);
    }

    public String getDialect() {
        return this.id;
    }

    public Object evaluate(final ProcessContext context) throws Exception {        
    	XPathFactory factory = XPathFactory.newInstance();
    	XPath xpath = factory.newXPath();
    	xpath.setXPathVariableResolver( 
    			new  XPathVariableResolver() {
    				public Object resolveVariable(QName var)
    				{
    					if (var == null) {
    						throw new NullPointerException("The variable name cannot be null");
    					}

    					if (context.getVariable(var.getLocalPart()) != null) {
    						return context.getVariable(var.getLocalPart());
    					}
    					else {
    						return null;
    					}
    				}

    			}
    	);
        
        XPathExpression expr 
         = xpath.compile(this.xpath);

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return expr.evaluate(builder.newDocument(), XPathConstants.BOOLEAN);
    }

    public String toString() {
        return this.xpath;
    }    

}
