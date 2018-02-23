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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.v1_1.DMNElementReference;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.DecisionService;

public class DecisionServiceConverter extends NamedElementConverter {

    public static final String OUTPUT_DECISION = "outputDecision";

    public static final String ENCAPSULATED_DECISION = "encapsulatedDecision";

    public static final String INPUT_DECISION = "inputDecision";

    public static final String INPUT_DATA = "inputData";

    public DecisionServiceConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new DecisionService();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(DecisionService.class);
    }

    @Override
    protected void parseElements(HierarchicalStreamReader reader, UnmarshallingContext context, Object parent) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            Object object = null;
            String nodeName = reader.getNodeName();
            if (nodeName.equals(INPUT_DATA)) {
                // Patch because the tag name inputData is used in both decision services and as a DRG Element
                DMNElementReference ref = new DMNElementReference();
                ref.setHref(reader.getAttribute("href"));
                object = ref;
            } else {
                // Default behaviour
                object = readItem(reader, context, null);
            }
            if (object instanceof DMNModelInstrumentedBase) {
                ((DMNModelInstrumentedBase) object).setParent((DMNModelInstrumentedBase) parent);
                ((DMNModelInstrumentedBase) parent).addChildren((DMNModelInstrumentedBase) object);
            }
            reader.moveUp();
            assignChildElement(parent, nodeName, object);
        }
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        DecisionService decisionService = (DecisionService) parent;
        switch (nodeName) {
        case OUTPUT_DECISION:
            decisionService.getOutputDecision().add((DMNElementReference) child);
            break;
        case ENCAPSULATED_DECISION:
            decisionService.getEncapsulatedDecision().add((DMNElementReference) child);
            break;
        case INPUT_DECISION:
            decisionService.getInputDecision().add((DMNElementReference) child);
            break;
        case INPUT_DATA:
            decisionService.getInputData().add((DMNElementReference) child);
            break;
        }
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        DecisionService decisionService = (DecisionService) parent;

        for (DMNElementReference ref : decisionService.getOutputDecision()) {
            writeChildrenNode(writer, context, ref, OUTPUT_DECISION);
        }
        for (DMNElementReference ref : decisionService.getEncapsulatedDecision()) {
            writeChildrenNode(writer, context, ref, ENCAPSULATED_DECISION);
        }
        for (DMNElementReference ref : decisionService.getInputDecision()) {
            writeChildrenNode(writer, context, ref, INPUT_DECISION);
        }
        for (DMNElementReference ref : decisionService.getInputData()) {
            writeChildrenNode(writer, context, ref, INPUT_DATA);
        }

    }

}
