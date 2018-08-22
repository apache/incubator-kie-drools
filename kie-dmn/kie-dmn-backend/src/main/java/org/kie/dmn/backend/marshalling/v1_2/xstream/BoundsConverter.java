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
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.dmndi.Bounds;

public class BoundsConverter extends DMNModelInstrumentedBaseConverter {


    private static final String HEIGHT = "height";
    private static final String WIDTH = "width";
    private static final String Y = "y";
    private static final String X = "x";

    public BoundsConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        super.assignChildElement(parent, nodeName, child);
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        Bounds abs = (Bounds) parent;

        abs.setX(Double.valueOf(reader.getAttribute(X)));
        abs.setY(Double.valueOf(reader.getAttribute(Y)));
        abs.setWidth(Double.valueOf(reader.getAttribute(WIDTH)));
        abs.setHeight(Double.valueOf(reader.getAttribute(HEIGHT)));
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);

        Bounds abs = (Bounds) parent;

        writer.addAttribute(X, FormatUtils.manageDouble(abs.getX()));
        writer.addAttribute(Y, FormatUtils.manageDouble(abs.getY()));
        writer.addAttribute(WIDTH, FormatUtils.manageDouble(abs.getWidth()));
        writer.addAttribute(HEIGHT, FormatUtils.manageDouble(abs.getHeight()));
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new org.kie.dmn.model.v1_2.dmndi.Bounds();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(org.kie.dmn.model.v1_2.dmndi.Bounds.class);
    }

}
