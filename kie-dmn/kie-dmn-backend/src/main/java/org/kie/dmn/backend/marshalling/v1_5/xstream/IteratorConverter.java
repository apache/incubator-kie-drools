/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.backend.marshalling.v1_5.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.Iterator;
import org.kie.dmn.model.api.TypedChildExpression;

public abstract class IteratorConverter extends ExpressionConverter {

	public static final String IN = "in";
    public static final String ITERATOR_VARIABLE = "iteratorVariable";

    public IteratorConverter(XStream xstream) {
        super( xstream );
    }
    
    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
    	Iterator i = (Iterator) parent;
        
        if (IN.equals(nodeName) && child instanceof TypedChildExpression) {
            i.setIn((TypedChildExpression) child);
        } else {
        	super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );
        String iteratorVariable = reader.getAttribute( ITERATOR_VARIABLE );

        if (iteratorVariable != null) {
            ((Iterator) parent).setIteratorVariable(iteratorVariable);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        Iterator e = (Iterator) parent;
        
        if (e.getId() != null) {
            writer.addAttribute(ITERATOR_VARIABLE, e.getIteratorVariable());
        }
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        Iterator i = (Iterator) parent;
        
        writeChildrenNode(writer, context, i.getIn(), IN);
    }

}
