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
package org.kie.dmn.backend.marshalling.v1_4.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.v1_4.TImport;

public class ImportConverter extends NamedElementConverter {
    public static final String NAMESPACE = "namespace";
    public static final String LOCATION_URI = "locationURI"; 
    public static final String IMPORT_TYPE = "importType";  
    
    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        super.assignChildElement(parent, nodeName, child);
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        Import i = (Import) parent;
        
        String namespace = reader.getAttribute(NAMESPACE);
        String locationUri = reader.getAttribute(LOCATION_URI);
        String importType = reader.getAttribute(IMPORT_TYPE);
        
        i.setNamespace(namespace);
        i.setLocationURI(locationUri);
        i.setImportType(importType);
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        Import i = (Import) parent;
        
        if (i.getNamespace() != null) writer.addAttribute(NAMESPACE, i.getNamespace());
        if (i.getLocationURI() != null) writer.addAttribute(LOCATION_URI, i.getLocationURI());
        if (i.getImportType() != null) writer.addAttribute(IMPORT_TYPE, i.getImportType());
    }

    public ImportConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TImport();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TImport.class);
    }

}
