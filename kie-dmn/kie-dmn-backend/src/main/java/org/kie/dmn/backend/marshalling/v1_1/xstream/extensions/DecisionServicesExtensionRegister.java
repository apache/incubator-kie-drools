/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.backend.marshalling.v1_1.xstream.extensions;

import javax.xml.namespace.QName;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;
import org.kie.dmn.api.marshalling.DMNExtensionRegister;
import org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.extensions.DecisionServices;

public class DecisionServicesExtensionRegister implements DMNExtensionRegister {

    @Override
    public void registerExtensionConverters(XStream xstream) {
        xstream.alias("decisionServices", DecisionServices.class);
        xstream.registerConverter(new DecisionServicesConverter(xstream));
    }

    @Override
    public void beforeMarshal(Object o, QNameMap qmap) {
        qmap.registerMapping(new QName(KieDMNModelInstrumentedBase.URI_KIE, "decisionServices", "drools"), "decisionServices");
    }

}
