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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportDMNResolverUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportDMNResolverUtil.class);

    private ImportDMNResolverUtil() {
        // No constructor for util class.
    }

    public static <T> Either<String, T> resolveImportDMN(Import importElement, Collection<T> dmns, Function<T, QName> idExtractor) {
        final String importNamespace = importElement.getNamespace();
        final String importName = importElement.getName();
        final String importLocationURI = importElement.getLocationURI(); // This is optional
        final String importModelName = importElement.getAdditionalAttributes().get(TImport.MODELNAME_QNAME);

        LOGGER.debug("Resolving DMN Import with namespace={} name={} locationURI={}, modelName={}",
                importNamespace, importName, importLocationURI, importModelName);

        List<T> matchingDmns = dmns.stream()
                             .filter(m -> idExtractor.apply(m).getNamespaceURI().equals(importNamespace))
                             .collect(Collectors.toList());
        if (matchingDmns.size() == 1) {
            T located = matchingDmns.get(0);
            // Check if the located DMN Model in the NS, correspond for the import `drools:modelName`. 
            if (importModelName == null || idExtractor.apply(located).getLocalPart().equals(importModelName)) {
                LOGGER.debug("DMN Import with namespace={} and importModelName={} resolved!", importNamespace, importModelName);
                return Either.ofRight(located);
            } else {
                LOGGER.error("Impossible to find the Imported DMN with {} namespace, {} name and {} modelName.",
                        importNamespace, importName, importModelName);
                return Either.ofLeft(String.format("While importing DMN for namespace: %s, name: %s, modelName: %s, located " +
                                "within namespace only %s but does not match for the actual modelName",
                                                   importNamespace, importName, importModelName,
                                                   idExtractor.apply(located)));
            }
        } else {
            List<T> usingNSandName = matchingDmns.stream()
                                            .filter(m -> idExtractor.apply(m).getLocalPart().equals(importModelName))
                    .toList();
            if (usingNSandName.size() == 1) {
                LOGGER.debug("DMN Import with namespace={} and importModelName={} resolved!", importNamespace, importModelName);
                return Either.ofRight(usingNSandName.get(0));
            } else if (usingNSandName.isEmpty()) {
                LOGGER.error("Impossible to find the Imported DMN with {} namespace, {} name and {} modelName.",
                        importNamespace, importName, importModelName);
                return Either.ofLeft(String.format("Impossible to find the Imported DMN with %s namespace %s name and %s modelName.",
                        importNamespace, importName, importModelName));
            } else {
                LOGGER.error("Found {} number of collision resolving an Imported DMN with {} namespace {} name and {} modelName.",
                        usingNSandName.size(), importNamespace, importName, importModelName);
                return Either.ofLeft(String.format("Found a collision resolving an Imported DMN with %s namespace, %s " +
                                "name and modelName %s. There are %s DMN files with the same namespace in your project. Please " +
                                "change the DMN namespaces and make them unique to fix this issue.",
                        importNamespace, importName, importModelName, usingNSandName.size()));
            }
        }
    }

    public enum ImportType {
        UNKNOWN,
        DMN,
        PMML;
    }

    public static ImportType whichImportType(Import importElement) {
        switch (importElement.getImportType()) {
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
