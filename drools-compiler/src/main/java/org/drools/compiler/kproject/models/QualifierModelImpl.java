/*
 * Copyright 2015 JBoss Inc
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

package org.drools.compiler.kproject.models;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.util.AbstractXStreamConverter;
import org.kie.api.builder.model.QualifierModel;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class QualifierModelImpl implements QualifierModel {
    private String type;
    private String value;
    private Map<String, String> arguments = new HashMap<String, String>();

    public QualifierModelImpl() { }

    public QualifierModelImpl(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public QualifierModel addArgument(String key, String value) {
        arguments.put(key, value);
        return this;
    }

    public Map<String, String> getArguments() {
        return arguments;
    }

    boolean isSimple() {
        return value == null && arguments.isEmpty();
    }

    public static class QualifierConverter extends AbstractXStreamConverter {

        public QualifierConverter() {
            super(QualifierModelImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            QualifierModelImpl qualifier = (QualifierModelImpl) value;
            writer.addAttribute("type", qualifier.getType());
            if (qualifier.getValue() != null) {
                writer.addAttribute("value", qualifier.getValue());
            } else {
                for (Map.Entry<String, String> entry : qualifier.getArguments().entrySet()) {
                    writer.startNode("arg");
                    writer.addAttribute("key", entry.getKey());
                    writer.addAttribute("value", entry.getValue());
                    writer.endNode();
                }
            }
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            final QualifierModelImpl qualifier = new QualifierModelImpl();
            qualifier.setType(reader.getAttribute("type"));
            String value = reader.getAttribute("value");

            if (value != null) {
                qualifier.setValue(value);
            } else {
                readNodes( reader, new AbstractXStreamConverter.NodeReader() {
                    public void onNode(HierarchicalStreamReader reader,
                                       String name,
                                       String value) {
                        if ( "arg".equals( name ) ) {
                            qualifier.addArgument(reader.getAttribute("key"), reader.getAttribute("value"));
                        }
                    }
                } );
            }

            return qualifier;
        }
    }
}
