/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.util;

import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.kie.dmn.core.compiler.DMNTypeRegistryV12;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;

public class NamespaceUtil {

    private NamespaceUtil() {
    }

    /**
     * Given a typeRef in the form of prefix:localname or importalias.localname, resolves namespace and localname appropriately.
     * <br>Example: <code>feel:string</code> would be resolved as <code>http://www.omg.org/spec/FEEL/20140401, string</code>.
     * <br>Example: <code>myimport.tPerson</code> assuming an external model namespace as "http://drools.org" would be resolved as <code>http://drools.org, tPerson</code>.
     * @param localElement the local element is used to determine the namespace from the prefix if present, as in the form prefix:localname
     * @param importAliases the map of import aliases is used to determine the namespace, as in the form importalias.localname
     * @param typeRef the typeRef to be resolved.
     * @return
     */
    public static QName getNamespaceAndName(DMNModelInstrumentedBase localElement, Map<String, QName> importAliases, QName typeRef, String modelNamespace) {
        if (localElement instanceof org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase) {
            if (!typeRef.getPrefix().equals(XMLConstants.DEFAULT_NS_PREFIX)) {
                return new QName(localElement.getNamespaceURI(typeRef.getPrefix()), typeRef.getLocalPart());
            } else {
                for (Map.Entry<String, QName> alias : importAliases.entrySet()) {
                    String prefix = alias.getKey() + ".";
                    if (typeRef.getLocalPart().startsWith(prefix)) {
                        return new QName(alias.getValue().getNamespaceURI(), typeRef.getLocalPart().replace(prefix, ""));
                    }
                }
                return new QName(localElement.getNamespaceURI(typeRef.getPrefix()), typeRef.getLocalPart());
            }
        } else { // DMN v1.2 onwards:
            for (BuiltInType bi : DMNTypeRegistryV12.ITEMDEF_TYPEREF_FEEL_BUILTIN) {
                for (String biName : bi.getNames()) {
                    if (biName.equals(typeRef.getLocalPart())) {
                        return new QName(localElement.getURIFEEL(), typeRef.getLocalPart());
                    }
                }
            }
            for (Map.Entry<String, QName> alias : importAliases.entrySet()) {
                String prefix = alias.getKey() + ".";
                if (typeRef.getLocalPart().startsWith(prefix)) {
                    return new QName(alias.getValue().getNamespaceURI(), typeRef.getLocalPart().replace(prefix, ""));
                }
            }
            for (String nsKey : localElement.recurseNsKeys()) {
                String prefix = nsKey + ".";
                if (typeRef.getLocalPart().startsWith(prefix)) {
                    return new QName(localElement.getNamespaceURI(nsKey), typeRef.getLocalPart().replace(prefix, ""));
                }
            }
            return new QName(modelNamespace, typeRef.getLocalPart());
        }
    }
}