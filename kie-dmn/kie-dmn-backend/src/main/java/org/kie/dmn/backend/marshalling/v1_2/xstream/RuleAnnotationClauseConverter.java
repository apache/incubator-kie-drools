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
import org.kie.dmn.model.api.RuleAnnotationClause;
import org.kie.dmn.model.v1_2.TRuleAnnotationClause;

public class RuleAnnotationClauseConverter extends DMNModelInstrumentedBaseConverter {

    public static final String NAME = "name";

    public RuleAnnotationClauseConverter(XStream xstream) {
        super( xstream );
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        super.assignChildElement(parent, nodeName, child);
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );

        ((RuleAnnotationClause) parent).setName(reader.getAttribute(NAME));
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);

        RuleAnnotationClause e = (RuleAnnotationClause) parent;

        if (e.getName() != null) {
            writer.addAttribute(NAME, e.getName());
        }
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TRuleAnnotationClause();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(TRuleAnnotationClause.class);
    }
}
