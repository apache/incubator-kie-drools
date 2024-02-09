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
import org.kie.dmn.model.api.ChildExpression;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.v1_5.TChildExpression;

public class ChildExpressionConverter extends DMNModelInstrumentedBaseConverter {

    public static final String ID = "id";

    public ChildExpressionConverter(XStream xstream) {
        super( xstream );
    }
    
    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
    	ChildExpression i = (ChildExpression) parent;
        
        if (child instanceof Expression) {
            i.setExpression((Expression) child);
        } else {
        	super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );
        String id = reader.getAttribute( ID );

        if (id != null) {
            ((ChildExpression) parent).setId(id);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        ChildExpression e = (ChildExpression) parent;
        
        if (e.getId() != null) {
            writer.addAttribute(ID, e.getId());
        }
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        ChildExpression i = (ChildExpression) parent;
        
        writeChildrenNode(writer, context, i.getExpression(), MarshallingUtils.defineExpressionNodeName(xstream, i.getExpression()));
    }

	@Override
	protected DMNModelInstrumentedBase createModelObject() {
		return new TChildExpression();
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(TChildExpression.class);
	}

}
