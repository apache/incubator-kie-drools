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
package org.drools.xml.support.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.kie.api.builder.model.KieBaseModel;

public class KieModuleConverter extends AbstractXStreamConverter {

    public KieModuleConverter() {
        super(KieModuleModelImpl.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        KieModuleModelImpl kModule = (KieModuleModelImpl) value;
        writePropertyMap(writer, context, "configuration", kModule.getConfProps());
        for ( KieBaseModel kBaseModule : kModule.getKieBaseModels().values() ) {
            writeObject( writer, context, "kbase", kBaseModule);
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final KieModuleModelImpl kModule = new KieModuleModelImpl();

        readNodes(reader, new AbstractXStreamConverter.NodeReader() {
            public void onNode(HierarchicalStreamReader reader, String name, String value) {
                if ("kbase".equals(name)) {
                    KieBaseModelImpl kBaseModule = readObject( reader, context, KieBaseModelImpl.class );
                    kModule.getRawKieBaseModels().put( kBaseModule.getName(), kBaseModule );
                    kBaseModule.setKModule(kModule);
                } else if ("configuration".equals(name)) {
                    kModule.setConfProps( readPropertyMap(reader, context) );
                }
            }
        });

        return kModule;
    }
}