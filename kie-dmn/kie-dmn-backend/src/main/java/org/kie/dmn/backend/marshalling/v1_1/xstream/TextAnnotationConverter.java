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

package org.kie.dmn.backend.marshalling.v1_1.xstream;

import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.TextAnnotation;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class TextAnnotationConverter extends ArtifactConverter {
    public static final String TEXT = "text";
    public static final String TEXT_FORMAT = "textFormat";
    
    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        TextAnnotation ta = (TextAnnotation) parent;
        
        if (TEXT.equals(nodeName)) {
            ta.setText((String) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        TextAnnotation ta = (TextAnnotation) parent;
        
        String textFormat = reader.getAttribute(TEXT_FORMAT);
        
        ta.setTextFormat(textFormat);
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        TextAnnotation ta = (TextAnnotation) parent;
        
        if (ta.getText() != null) writeChildrenNode(writer, context, ta.getText(), TEXT);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        TextAnnotation ta = (TextAnnotation) parent;
        
        if (ta.getTextFormat() != null) writer.addAttribute(TEXT_FORMAT, ta.getTextFormat()); 
    }

    public TextAnnotationConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TextAnnotation();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( TextAnnotation.class );
    }

}
