/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.kproject.models;

import org.drools.core.util.AbstractXStreamConverter;
import org.kie.api.builder.model.ChannelModel;
import org.kie.api.builder.model.QualifierModel;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ChannelModelImpl implements ChannelModel {

    private KieSessionModelImpl kSession;

    private String name;
    private String type;
    private QualifierModel qualifier;

    public ChannelModelImpl() { }

    public ChannelModelImpl(KieSessionModelImpl kSession, String name, String type) {
        this.kSession = kSession;
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = type;
    }

    public QualifierModel getQualifierModel() {
        return qualifier;
    }

    private void setQualifierModel(QualifierModel qualifier) {
        this.qualifier = qualifier;
    }

    public QualifierModel newQualifierModel(String type) {
        QualifierModelImpl qualifier = new QualifierModelImpl(type);
        this.qualifier = qualifier;
        return qualifier;
    }

    public KieSessionModelImpl getKSession() {
        return kSession;
    }

    public void setKSession(KieSessionModelImpl kSession) {
        this.kSession = kSession;
    }

    public static class ChannelConverter extends AbstractXStreamConverter {

        public ChannelConverter() {
            super(ChannelModelImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            ChannelModelImpl channel = (ChannelModelImpl) value;
            writer.addAttribute("name", channel.getName());
            writer.addAttribute("type", channel.getType());
            /* TODO make qualifiers working properly before readd them to the xml
            QualifierModelImpl qualifier = (QualifierModelImpl)channel.getQualifierModel();
            if (qualifier != null) {
                if (qualifier.isSimple()) {
                    writer.addAttribute("qualifier", qualifier.getType());
                } else {
                    writeObject(writer, context, "qualifier", qualifier);
                }
            }
            */
        }

        public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
            final ChannelModelImpl channel = new ChannelModelImpl();
            channel.setName(reader.getAttribute("name"));
            channel.setType(reader.getAttribute("type"));
            /* TODO make qualifiers working properly before readd them to the xml
            String qualifierType = reader.getAttribute("qualifier");
            if (qualifierType != null) {
                channel.newQualifierModel(qualifierType);
            }

            readNodes( reader, new AbstractXStreamConverter.NodeReader() {
                public void onNode(HierarchicalStreamReader reader,
                                   String name,
                                   String value) {
                    if ( "qualifier".equals( name ) ) {
                        QualifierModelImpl qualifier = readObject(reader, context, QualifierModelImpl.class);
                        channel.setQualifierModel(qualifier);
                    }
                }
            } );
            */
            return channel;
        }
    }
}
