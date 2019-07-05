/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import org.kie.dmn.model.api.DMNExternalLink;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_2.TDMNExternalLink;

public class DMNExternalLinkConverter extends DMNModelInstrumentedBaseConverter {

    static final String LINK_DESCRIPTION = "linkDescription";
    static final String URL = "url";

    public DMNExternalLinkConverter(final XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TDMNExternalLink();
    }

    @Override
    public boolean canConvert(final Class aClass) {
        return aClass.equals(DMNExternalLink.class) || aClass.equals(TDMNExternalLink.class);
    }

    @Override
    protected void assignChildElement(final Object parent, final String nodeName, final Object child) {
        super.assignChildElement(parent, nodeName, child);
    }

    @Override
    protected void assignAttributes(final HierarchicalStreamReader reader, final Object parent) {
        super.assignAttributes(reader, parent);
        final DMNExternalLink external = (DMNExternalLink) parent;
        external.setLinkDescription(reader.getAttribute(LINK_DESCRIPTION));
        external.setURL(reader.getAttribute(URL));
    }

    @Override
    protected void writeChildren(final HierarchicalStreamWriter writer, final MarshallingContext context, final Object parent) {
        super.writeChildren(writer, context, parent);
    }

    @Override
    protected void writeAttributes(final HierarchicalStreamWriter writer, final Object parent) {
        super.writeAttributes(writer, parent);
        final DMNExternalLink external = (DMNExternalLink) parent;
        writer.addAttribute(LINK_DESCRIPTION, external.getLinkDescription());
        writer.addAttribute(URL, external.getURL());
    }
}
