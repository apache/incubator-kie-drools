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
package org.kie.dmn.core.compiler;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.kie.dmn.feel.util.Either;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.api.NamespaceConsts;
import org.kie.dmn.model.v1_1.TImport;

public class ImportDMNResolverUtil {

    private ImportDMNResolverUtil() {
        // No constructor for util class.
    }

    public static <T> Either<String, T> resolveImportDMN(Import _import, Collection<T> all, Function<T, QName> idExtractor) {
        final String iNamespace = _import.getNamespace();
        final String iName = _import.getName();
        final String iModelName = _import.getAdditionalAttributes().get(TImport.MODELNAME_QNAME);
        List<T> allInNS = all.stream()
                             .filter(m -> idExtractor.apply(m).getNamespaceURI().equals(iNamespace))
                             .collect(Collectors.toList());
        if (allInNS.size() == 1) {
            T located = allInNS.get(0);
            // Check if the located DMN Model in the NS, correspond for the import `drools:modelName`. 
            if (iModelName == null || idExtractor.apply(located).getLocalPart().equals(iModelName)) {
                return Either.ofRight(located);
            } else {
                return Either.ofLeft(String.format("While importing DMN for namespace: %s, name: %s, modelName: %s, located within namespace only %s but does not match for the actual name",
                                                   iNamespace, iName, iModelName,
                                                   idExtractor.apply(located)));
            }
        } else {
            List<T> usingNSandName = allInNS.stream()
                                            .filter(m -> idExtractor.apply(m).getLocalPart().equals(iModelName))
                                            .collect(Collectors.toList());
            if (usingNSandName.size() == 1) {
                return Either.ofRight(usingNSandName.get(0));
            } else if (usingNSandName.size() == 0) {
                return Either.ofLeft(String.format("Could not locate required dependency while importing DMN for namespace: %s, name: %s, modelName: %s.",
                                                   iNamespace, iName, iModelName));
            } else {
                return Either.ofLeft(String.format("While importing DMN for namespace: %s, name: %s, modelName: %s, could not locate required dependency within: %s.",
                                                   iNamespace, iName, iModelName,
                                                   allInNS.stream().map(idExtractor).collect(Collectors.toList())));
            }
        }
    }

    public static enum ImportType {
        UNKNOWN,
        DMN,
        PMML;
    }

    public static ImportType whichImportType(Import _import) {
        switch (_import.getImportType()) {
            case org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase.URI_DMN:
            case "http://www.omg.org/spec/DMN1-2Alpha/20160929/MODEL":
            case org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_DMN:
            case org.kie.dmn.model.v1_3.KieDMNModelInstrumentedBase.URI_DMN:
            case org.kie.dmn.model.v1_4.KieDMNModelInstrumentedBase.URI_DMN:
            case org.kie.dmn.model.v1_5.KieDMNModelInstrumentedBase.URI_DMN:
                return ImportType.DMN;
            case NamespaceConsts.PMML_3_0:
            case NamespaceConsts.PMML_3_1:
            case NamespaceConsts.PMML_3_2:
            case NamespaceConsts.PMML_4_0:
            case NamespaceConsts.PMML_4_1:
            case NamespaceConsts.PMML_4_2:
            case NamespaceConsts.PMML_4_3:
                return ImportType.PMML;
            default:
                return ImportType.UNKNOWN;
        }
    }
}
