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
package org.kie.dmn.backend.marshalling.v1_4.xstream;

import org.kie.dmn.model.api.ChildExpression;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Filter;
import org.kie.dmn.model.v1_4.TChildExpression;
import org.kie.dmn.model.v1_4.TFilter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class FilterConverter extends ExpressionConverter  {

    public static final String IN = "in";
    public static final String MATCH = "match";

    public FilterConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Filter filter = (Filter) parent;

        if (IN.equals(nodeName)) {
            filter.setIn((ChildExpression) child);
        } else if (MATCH.equals(nodeName)) {
            filter.setMatch((ChildExpression) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        Filter filter = (Filter) parent;
        writeChildrenNode(writer, context, filter.getIn(), IN);
        writeChildrenNode(writer, context, filter.getMatch(), MATCH);

    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TFilter();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TFilter.class);
    }
    
    protected void parseElements(HierarchicalStreamReader reader, UnmarshallingContext context, Object parent) {
        mvDownConvertAnotherMvUpAssignChildElement(reader, context, parent, IN, TChildExpression.class);
        mvDownConvertAnotherMvUpAssignChildElement(reader, context, parent, MATCH, TChildExpression.class);
    }

}
