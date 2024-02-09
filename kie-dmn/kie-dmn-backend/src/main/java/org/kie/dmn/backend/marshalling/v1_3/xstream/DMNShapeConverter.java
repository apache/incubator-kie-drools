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
package org.kie.dmn.backend.marshalling.v1_3.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.dmndi.DMNDecisionServiceDividerLine;
import org.kie.dmn.model.api.dmndi.DMNLabel;
import org.kie.dmn.model.v1_3.dmndi.DMNShape;

public class DMNShapeConverter extends ShapeConverter {

    private static final String FILL_COLOR = "FillColor";
    private static final String STROKE_COLOR = "StrokeColor";
    private static final String FONT_COLOR = "FontColor";
    
    private static final String FONT_FAMILY = "fontFamily";
    private static final String FONT_SIZE = "fontSize";
    private static final String FONT_ITALIC = "fontItalic";
    private static final String FONT_BOLD = "fontBold";
    private static final String FONT_UNDERLINE = "fontUnderline";
    private static final String FONT_STRIKE_THROUGH = "fontStrikeThrough";
    private static final String LABEL_HORIZONTAL_ALIGNMENT = "labelHorizontalAlignement";
    private static final String LABEL_VERTICAL_ALIGNMENT = "labelVerticalAlignment";


    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        DMNShape style = (DMNShape) parent;
        
        if (child instanceof DMNLabel) {
            style.setDMNLabel((DMNLabel) child);
        } else if (child instanceof DMNDecisionServiceDividerLine) {
            style.setDMNDecisionServiceDividerLine((DMNDecisionServiceDividerLine) child);
        } else {
            super.assignChildElement(style, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        DMNShape style = (DMNShape) parent;

        style.setDmnElementRef(MarshallingUtils.parseQNameString(reader.getAttribute("dmnElementRef")));
        
        String isListedInputData = reader.getAttribute("isListedInputData");
        String isCollapsed = reader.getAttribute("isCollapsed");
         
        if (isListedInputData != null) {
            style.setIsListedInputData(Boolean.valueOf(isListedInputData));
        }
        if (isCollapsed != null) {
            style.setIsCollapsed(Boolean.valueOf(isCollapsed));
        }
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        DMNShape style = (DMNShape) parent;
        
        if (style.getDMNLabel() != null) {
            writeChildrenNode(writer, context, style.getDMNLabel(), "DMNLabel");
        }
        if (style.getDMNDecisionServiceDividerLine() != null) {
            writeChildrenNode(writer, context, style.getDMNDecisionServiceDividerLine(), "DMNDecisionServiceDividerLine");
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        DMNShape style = (DMNShape) parent;

        writer.addAttribute("dmnElementRef", MarshallingUtils.formatQName(style.getDmnElementRef(), style));

        if (style.isIsListedInputData() != null) {
            writer.addAttribute("isListedInputData", style.isIsListedInputData().toString());
        }
        writer.addAttribute("isCollapsed", Boolean.valueOf(style.isIsCollapsed()).toString());
    }

    public DMNShapeConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new org.kie.dmn.model.v1_3.dmndi.DMNShape();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(org.kie.dmn.model.v1_3.dmndi.DMNShape.class);
    }

}
