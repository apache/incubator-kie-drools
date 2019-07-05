/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.backend.marshalling.v1_2.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.dmndi.DMNLabel;
import org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase;


public class DMNLabelConverter extends ShapeConverter {

    private static final String TEXT = "Text";

    public DMNLabelConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void parseElements(HierarchicalStreamReader reader, UnmarshallingContext context, Object parent) {
        while ( reader.hasMoreChildren() ) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if (nodeName.equals(TEXT)) {
                ((DMNLabel)parent).setText(reader.getValue());
            } else {
              Object object = readItem(
                      reader,
                      context,
                      null );
              if( object instanceof DMNModelInstrumentedBase ) {
                  ((KieDMNModelInstrumentedBase) object).setParent((KieDMNModelInstrumentedBase) parent);
                  ((KieDMNModelInstrumentedBase) parent).addChildren((KieDMNModelInstrumentedBase) object);
              }
              assignChildElement( parent, nodeName, object );
            }
            reader.moveUp();
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);

        // no attributes.
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        DMNLabel concrete = (DMNLabel) parent;

        if (concrete.getText() != null) {
            writeChildrenNode(writer, context, concrete.getText(), TEXT);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);

        // no attributes.
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new org.kie.dmn.model.v1_2.dmndi.DMNLabel();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(org.kie.dmn.model.v1_2.dmndi.DMNLabel.class);
    }

}
