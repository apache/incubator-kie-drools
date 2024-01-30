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
import com.thoughtworks.xstream.converters.Converter;
import org.kie.dmn.backend.marshalling.v1x.ConverterDefinesExpressionNodeName;
import org.kie.dmn.model.api.Conditional;
import org.kie.dmn.model.api.Context;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.Every;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.Filter;
import org.kie.dmn.model.api.For;
import org.kie.dmn.model.api.FunctionDefinition;
import org.kie.dmn.model.api.Invocation;
import org.kie.dmn.model.api.List;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.Relation;
import org.kie.dmn.model.api.Some;
import org.kie.dmn.model.api.dmndi.DMNEdge;
import org.kie.dmn.model.api.dmndi.DMNShape;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MarshallingUtils {

    private final static Pattern QNAME_PAT = Pattern.compile("(\\{([^\\}]*)\\})?(([^:]*):)?(.*)");

    public static QName parseQNameString(String qns) {
        if (qns != null) {
            Matcher m = QNAME_PAT.matcher(qns);
            if (m.matches()) {
                if (m.group(4) != null) {
                    return new QName(m.group(2), m.group(5), m.group(4));
                } else {
                    return new QName(m.group(2), m.group(5));
                }
            } else {
                return new QName(qns);
            }
        } else {
            return null;
        }
    }

    public static String formatQName(QName qname, DMNModelInstrumentedBase parent) {
        if (!XMLConstants.DEFAULT_NS_PREFIX.equals(qname.getPrefix())) {
            String nsForPrefix = parent.getNamespaceURI(qname.getPrefix());
            if (parent.getURIFEEL().equals(nsForPrefix)) {
                return qname.getLocalPart(); // DMN v1.2 feel comes without a prefix.
            } else if (parent instanceof DMNShape || parent instanceof DMNEdge) {
                return qname.getPrefix() + ":" + qname.getLocalPart();
            } else {
                return qname.getPrefix() + "." + qname.getLocalPart(); // DMN v1.2 namespace typeRef lookup is done with dot.
            }
        } else {
            return qname.toString();
        }
    }
    
    public static String defineExpressionNodeName(XStream xstream, Expression e) {
        Converter converter = xstream.getConverterLookup().lookupConverterForType(e.getClass());
        if (converter instanceof ConverterDefinesExpressionNodeName) {
            ConverterDefinesExpressionNodeName defines = (ConverterDefinesExpressionNodeName) converter;
            return defines.defineExpressionNodeName(e);
        } else {
            return defineExpressionNodeName(e);
        }
    }

    private static String defineExpressionNodeName(Expression e) {
        String nodeName = "expression";
        if (e instanceof Context) {
            nodeName = "context";
        } else if (e instanceof DecisionTable) {
            nodeName = "decisionTable";
        } else if (e instanceof FunctionDefinition) {
            nodeName = "functionDefinition";
        } else if (e instanceof Invocation) {
            nodeName = "invocation";
        } else if (e instanceof LiteralExpression) {
            nodeName = "literalExpression";
        } else if (e instanceof Relation) {
            nodeName = "relation";
        } else if (e instanceof List) {
            nodeName = "list";
        } else if (e instanceof For) {
        	nodeName = "for";
        } else if (e instanceof Every) {
        	nodeName = "every";
        } else if (e instanceof Some) {
        	nodeName = "some";
        } else if (e instanceof Conditional) {
        	nodeName = "conditional";
        } else if (e instanceof Filter) {
        	nodeName = "filter";
        }
        return nodeName;
    }

    private MarshallingUtils() {
        // Constructing instances is not allowed for this class
    }
}
