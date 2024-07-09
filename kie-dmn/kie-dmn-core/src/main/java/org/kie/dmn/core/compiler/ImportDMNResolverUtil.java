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
import org.kie.dmn.model.api.Definitions;
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
        final String importerDMNNamespace = ((Definitions) importElement.getParent()).getNamespace();
        final String importerDMNName = ((Definitions) importElement.getParent()).getName();
        final String importNamespace = importElement.getNamespace();
        final String importName = importElement.getName();
        final String importLocationURI = importElement.getLocationURI(); // This is optional
        final String importModelName = importElement.getAdditionalAttributes().get(TImport.MODELNAME_QNAME);

        LOGGER.debug("Resolving an Import in DMN Model with name={} and namespace={}. " +
                        "Importing a DMN model with namespace={} name={} locationURI={}, modelName={}",
                importerDMNNamespace, importerDMNName, importNamespace, importName, importLocationURI, importModelName);

        List<T> matchingDMNList = dmns.stream()
                .filter(m -> idExtractor.apply(m).getNamespaceURI().equals(importNamespace))
                .collect(Collectors.toList());
        if (matchingDMNList.size() == 1) {
            T located = matchingDMNList.get(0);
            // Check if the located DMN Model in the NS, correspond for the import `drools:modelName`. 
            if (importModelName == null || idExtractor.apply(located).getLocalPart().equals(importModelName)) {
                LOGGER.debug("DMN Model with name={} and namespace={} successfully imported a DMN " +
                                "with namespace={} name={} locationURI={}, modelName={}",
                        importerDMNNamespace, importerDMNName, importNamespace, importName, importLocationURI, importModelName);
                return Either.ofRight(located);
            } else {
                LOGGER.error("DMN Model with name={} and namespace={} can't import a DMN with namespace={}, name={}, modelName={}, " +
                                "located within namespace only {} but does not match for the actual modelName",
                        importerDMNNamespace, importerDMNName, importNamespace, importName, importModelName, idExtractor.apply(located));
                return Either.ofLeft(String.format(
                        "DMN Model with name=%s and namespace=%s can't import a DMN with namespace=%s, name=%s, modelName=%s, " +
                                "located within namespace only %s but does not match for the actual modelName",
                        importerDMNNamespace, importerDMNName, importNamespace, importName, importModelName, idExtractor.apply(located)));
            }
        } else {
            List<T> usingNSandName = matchingDMNList.stream()
                    .filter(dmn -> idExtractor.apply(dmn).getLocalPart().equals(importModelName))
                    .toList();
            if (usingNSandName.size() == 1) {
                LOGGER.debug("DMN Model with name={} and namespace={} successfully imported a DMN " +
                                "with namespace={} name={} locationURI={}, modelName={}",
                        importerDMNNamespace, importerDMNName, importNamespace, importName, importLocationURI, importModelName);
                return Either.ofRight(usingNSandName.get(0));
            } else if (usingNSandName.isEmpty()) {
                LOGGER.error("DMN Model with name={} and namespace={} failed to import a DMN with namespace={} name={} locationURI={}, modelName={}.",
                        importerDMNNamespace, importerDMNName, importNamespace, importName, importLocationURI, importModelName);
                return Either.ofLeft(String.format(
                        "DMN Model with name=%s and namespace=%s failed to import a DMN with namespace=%s name=%s locationURI=%s, modelName=%s. ",
                        importerDMNNamespace, importerDMNName, importNamespace, importName, importLocationURI, importModelName));
            } else {
                LOGGER.error("DMN Model with name={} and namespace={} detected a collision ({} elements) trying to import a DMN with namespace={} name={} locationURI={}, modelName={}",
                        importerDMNNamespace, importerDMNName, usingNSandName.size(), importNamespace, importName, importLocationURI, importModelName);
                return Either.ofLeft(String.format(
                        "DMN Model with name=%s and namespace=%s detected a collision trying to import a DMN with %s namespace, " +
                                "%s name and modelName %s. There are %s DMN files with the same namespace in your project. " +
                                "Please change the DMN namespaces and make them unique to fix this issue.",
                        importerDMNNamespace, importerDMNName, importNamespace, importName, importModelName, usingNSandName.size()));
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
