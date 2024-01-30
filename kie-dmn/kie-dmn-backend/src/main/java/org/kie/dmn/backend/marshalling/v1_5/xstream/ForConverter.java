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
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.ChildExpression;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.For;
import org.kie.dmn.model.v1_5.TChildExpression;
import org.kie.dmn.model.v1_5.TFor;
import org.kie.dmn.model.v1_5.TTypedChildExpression;

public class ForConverter extends IteratorConverter {

	public static final String RETURN = "return";

    public ForConverter(XStream xstream) {
        super( xstream );
    }
    
    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
    	For i = (For) parent;
        
        if (RETURN.equals(nodeName) && child instanceof ChildExpression) {
            i.setReturn((ChildExpression) child);
        } else {
        	super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        For i = (For) parent;
        
        writeChildrenNode(writer, context, i.getReturn(), RETURN);
    }

	@Override
	protected DMNModelInstrumentedBase createModelObject() {
		return new TFor();
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(TFor.class);
	}
	
    protected void parseElements(HierarchicalStreamReader reader, UnmarshallingContext context, Object parent) {
        mvDownConvertAnotherMvUpAssignChildElement(reader, context, parent, IN, TTypedChildExpression.class);
        mvDownConvertAnotherMvUpAssignChildElement(reader, context, parent, RETURN, TChildExpression.class);
    }

}
