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
import org.kie.api.builder.model.QualifierModel;
import org.kie.api.builder.model.WorkItemHandlerModel;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class WorkItemHandlerModelImpl implements WorkItemHandlerModel {

    private KieSessionModelImpl kSession;

    private String name;
    private String type;
    private QualifierModel qualifier;

    public WorkItemHandlerModelImpl() { }

    public WorkItemHandlerModelImpl(KieSessionModelImpl kSession, String name, String type) {
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

    public static class WorkItemHandelerConverter extends AbstractXStreamConverter {

        public WorkItemHandelerConverter() {
            super(WorkItemHandlerModelImpl.class);
        }

        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            WorkItemHandlerModelImpl wih = (WorkItemHandlerModelImpl) value;
            writer.addAttribute("name", wih.getName());
            writer.addAttribute("type", wih.getType());
            /* TODO make qualifiers working properly before readd them to the xml
            QualifierModelImpl qualifier = (QualifierModelImpl)wih.getQualifierModel();
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
            final WorkItemHandlerModelImpl wih = new WorkItemHandlerModelImpl();
            wih.setName(reader.getAttribute("name"));
            wih.setType(reader.getAttribute("type"));
            /* TODO make qualifiers working properly before readd them to the xml
            String qualifierType = reader.getAttribute("qualifier");
            if (qualifierType != null) {
                wih.newQualifierModel(qualifierType);
            }

            readNodes( reader, new AbstractXStreamConverter.NodeReader() {
                public void onNode(HierarchicalStreamReader reader,
                                   String name,
                                   String value) {
                    if ( "qualifier".equals( name ) ) {
                        QualifierModelImpl qualifier = readObject(reader, context, QualifierModelImpl.class);
                        wih.setQualifierModel(qualifier);
                    }
                }
            } );
            */
            return wih;
        }
    }
}
