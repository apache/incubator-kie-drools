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

import org.drools.core.util.AbstractXStreamConverter;
import org.kie.api.builder.model.ListenerModel;
import org.kie.api.builder.model.QualifierModel;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ListenerModelImpl implements ListenerModel {

    private KieSessionModelImpl kSession;
    private String type;
    private ListenerModel.Kind kind;
    private QualifierModel qualifier;

    public ListenerModelImpl() { }

    public ListenerModelImpl(KieSessionModelImpl kSession, String type, ListenerModel.Kind kind) {
        this.kSession = kSession;
        this.type = type;
        this.kind = kind;
    }

    public String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = type;
    }

    public ListenerModel.Kind getKind() {
        return kind;
    }

    void setKind(ListenerModel.Kind kind) {
        this.kind = kind;
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

    public static class ListenerConverter extends AbstractXStreamConverter {

        public ListenerConverter() {
            super(ListenerModelImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            ListenerModelImpl listener = (ListenerModelImpl) value;
            writer.addAttribute("type", listener.getType());
            /* TODO make qualifiers working properly before readd them to the xml
            QualifierModelImpl qualifier = (QualifierModelImpl)listener.getQualifierModel();
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
            final ListenerModelImpl listener = new ListenerModelImpl();
            listener.setType(reader.getAttribute("type"));
            /* TODO make qualifiers working properly before readd them to the xml
            String qualifierType = reader.getAttribute("qualifier");
            if (qualifierType != null) {
                listener.newQualifierModel(qualifierType);
            }

            readNodes( reader, new AbstractXStreamConverter.NodeReader() {
                public void onNode(HierarchicalStreamReader reader,
                                   String name,
                                   String value) {
                    if ( "qualifier".equals( name ) ) {
                        QualifierModelImpl qualifier = readObject(reader, context, QualifierModelImpl.class);
                        listener.setQualifierModel(qualifier);
                    }
                }
            } );
            */
            return listener;
        }
    }
}
