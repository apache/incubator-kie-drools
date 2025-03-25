/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.compiler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.xml.namespace.QName;

import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.ast.*;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.pmml.DMNImportPMMLInfo;
import org.kie.dmn.core.pmml.EfestoPMMLUtils;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.util.Either;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.api.NamespaceConsts;
import org.kie.dmn.model.v1_1.TImport;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DMNImportsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DMNImportsUtil.class);

    private DMNImportsUtil() {
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
                importerDMNName, importerDMNNamespace, importNamespace, importName, importLocationURI, importModelName);

        if (dmns.isEmpty()) {
            return Either.ofLeft("Impossible to resolve an import against an empty DMN collection");
        }

        List<T> matchingDMNList = dmns.stream()
                .filter(m -> idExtractor.apply(m).getNamespaceURI().equals(importNamespace))
                .toList();
        if (matchingDMNList.size() == 1) {
            T located = matchingDMNList.get(0);
            // Check if the located DMN Model in the NS, correspond for the import `drools:modelName`. 
            if (importModelName == null || idExtractor.apply(located).getLocalPart().equals(importModelName)) {
                LOGGER.debug("DMN Model with name={} and namespace={} successfully imported a DMN " +
                                "with namespace={} name={} locationURI={}, modelName={}",
                        importerDMNName, importerDMNNamespace, importNamespace, importName, importLocationURI, importModelName);
                return Either.ofRight(located);
            } else {
                LOGGER.error("DMN Model with name={} and namespace={} can't import a DMN with namespace={}, name={}, modelName={}, " +
                                "located within namespace only {} but does not match for the actual modelName",
                        importerDMNName, importerDMNNamespace, importNamespace, importName, importModelName, idExtractor.apply(located));
                return Either.ofLeft(String.format(
                        "DMN Model with name=%s and namespace=%s can't import a DMN with namespace=%s, name=%s, modelName=%s, " +
                                "located within namespace only %s but does not match for the actual modelName",
                        importerDMNName, importerDMNNamespace, importNamespace, importName, importModelName, idExtractor.apply(located)));
            }
        } else {
            List<T> usingNSandName = matchingDMNList.stream()
                    .filter(dmn -> idExtractor.apply(dmn).getLocalPart().equals(importModelName))
                    .toList();
            if (usingNSandName.size() == 1) {
                LOGGER.debug("DMN Model with name={} and namespace={} successfully imported a DMN " +
                                "with namespace={} name={} locationURI={}, modelName={}",
                        importerDMNName, importerDMNNamespace, importNamespace, importName, importLocationURI, importModelName);
                return Either.ofRight(usingNSandName.get(0));
            } else if (usingNSandName.isEmpty()) {
                LOGGER.error("DMN Model with name={} and namespace={} failed to import a DMN with namespace={} name={} locationURI={}, modelName={}.",
                        importerDMNName, importerDMNNamespace, importNamespace, importName, importLocationURI, importModelName);
                return Either.ofLeft(String.format(
                        "DMN Model with name=%s and namespace=%s failed to import a DMN with namespace=%s name=%s locationURI=%s, modelName=%s. ",
                        importerDMNName, importerDMNNamespace, importNamespace, importName, importLocationURI, importModelName));
            } else {
                LOGGER.error("DMN Model with name={} and namespace={} detected a collision ({} elements) trying to import a DMN with namespace={} name={} locationURI={}, modelName={}",
                        importerDMNName, importerDMNNamespace, usingNSandName.size(), importNamespace, importName, importLocationURI, importModelName);
                return Either.ofLeft(String.format(
                        "DMN Model with name=%s and namespace=%s detected a collision trying to import a DMN with %s namespace, " +
                                "%s name and modelName %s. There are %s DMN files with the same namespace in your project. " +
                                "Please change the DMN namespaces and make them unique to fix this issue.",
                        importerDMNName, importerDMNNamespace, importNamespace, importName, importModelName, usingNSandName.size()));
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

    /**
     * Resolves a DMN import by searching for the corresponding DMNModel based on the provided Import object.
     * If the import is resolved successfully, it is checked and merged with the current DMN model.
     * @param i : The Import object representing the DMN import to be resolved.
     * @param dmnModels : A collection of existing DMNModel objects that may be used to resolve the import.
     * @param model : Instance of the DMNModelImpl into which the resolved import will be incorporated.
     * @param toMerge : A list that will hold DMN models to be merged with the current model.
     */
    static void resolveDMNImportType(Import i, Collection<DMNModel> dmnModels, DMNModelImpl model, List<DMNModel> toMerge) {
        Either<String, DMNModel> resolvedResult = DMNImportsUtil.resolveImportDMN(i, dmnModels, (DMNModel m) -> new QName(m.getNamespace(), m.getName()));
        DMNModel located = resolvedResult.cata(msg -> {
            MsgUtil.reportMessage(LOGGER,
                    DMNMessage.Severity.ERROR,
                    i,
                    model,
                    null,
                    null,
                    Msg.IMPORT_NOT_FOUND_FOR_NODE,
                    msg,
                    i);
            return null;
        }, Function.identity());
        checkLocatedDMNModel(i, located, model, toMerge);
    }

    /**
     * This method is used to checks if a DMNModel is located and processes it by setting the import alias or merging it with the original model.
     * @param i : represents an import object, and it has a name (i.getName()). If the name is available, it will be used as the import alias.
     * @param located : it is a DMN model that has been locate.
     * @param model : it is the target DMN model where the import or merge is being applied model.
     * @param toMerge : This is a list that will hold DMN models that are to be merged later, instead of being directly imported.
     */
    static void checkLocatedDMNModel(Import i, DMNModel located, DMNModelImpl model, List<DMNModel> toMerge) {
        if (located != null) {
            String importAlias = Optional.ofNullable(i.getName()).orElse(located.getName());
            // incubator-kie-issues#852: The idea is to not treat the anonymous models as import, but to "merge" them
            //  with original one,
            // because otherwise we would have to deal with clashing name aliases, or similar issues
            if (importAlias != null && !importAlias.isEmpty()) {
                model.setImportAliasForNS(importAlias, located.getNamespace(), located.getName());
                importFromModel(model, located, importAlias);
            } else {
                toMerge.add(located);
            }
        }
    }

    /**
     * The method is using for handling imports related to PMML models, allowing the DMN model to incorporate external PMML-based resources.
     * @param model : Represents a DMN model that requires the PMML import resolution.
     * @param i : Object that specifies the import details for the PMML resource.
     * @param relativeResolver : A function that resolves relative paths to resources.
     * @param dmnCompilerConfig : Instance of the DMNCompilerConfigurationImpl that holds configuration details, including the root class loader.
     */
    static void resolvePMMLImportType(DMNModelImpl model, Import i, Function<String, Reader> relativeResolver, DMNCompilerConfigurationImpl dmnCompilerConfig) {
        ModelLocalUriId relativeResource = EfestoPMMLUtils.resolveRelativeResource(model, i, i, relativeResolver);
        resolvePMMLImportType(model, i, relativeResource, dmnCompilerConfig);
    }

    /**
     * Resolves the PMML import type for a given DMN model and import, consuming and processing the PMML import information.
     * If an error occurs while reading the PMML resource, the exception is handled and passed to the error consumer for appropriate processing.
     * @param model : represents a DMN model where the PMML import information will be added.
     * @param i : The Import object that specifies the PMML import details.
     * @param pmmlModelLocalUriId : The ModelLocalUriId pointing at the PMML data.
     * @param dmnCompilerConfig : The DMNCompilerConfigurationImpl providing configuration details for the DMN compiler.
     * @throws IOException If an error occurs while reading the PMML resource input stream.
     */
    static void resolvePMMLImportType(DMNModelImpl model, Import i, ModelLocalUriId pmmlModelLocalUriId, DMNCompilerConfigurationImpl dmnCompilerConfig) {
        String pmmlSource = EfestoPMMLUtils.getPmmlSource(pmmlModelLocalUriId);
        try (InputStream pmmlInputStream = new ByteArrayInputStream(pmmlSource.getBytes(StandardCharsets.UTF_8))) {
            DMNImportPMMLInfo.from(pmmlInputStream, dmnCompilerConfig, model, i).consume(new DMNCompilerImpl.PMMLImportErrConsumer(model, i),
                    model::addPMMLImportInfo);
        } catch (IOException e) {
            new DMNCompilerImpl.PMMLImportErrConsumer(model, i).accept(e);
        }
    }

    /**
     * Logs an error message when an unsupported or unknown import type is encountered during DMN model compilation.
     * @param model : Instance of the DMNModelImpl that represents the DMN model where the error occurred.
     * @param importType : The type of the import that caused the error (e.g., DMN, PMML, or unknown type).
     */
    static void logErrorMessage(DMNModelImpl model, String importType) {
        MsgUtil.reportMessage(LOGGER,
                DMNMessage.Severity.ERROR,
                null,
                model,
                null,
                null,
                Msg.IMPORT_TYPE_UNKNOWN,
                importType);
    }

    /**
     * This method ensures that all necessary components from the source DMN model are integrated into the target model,
     * including the import chain and various types of nodes (inputs, items, decisions, etc.).
     * @param model : The DMNModelImpl instance into which elements from the source model will be imported.
     * @param m : DMN model from which elements will be imported.
     * @param iAlias : The alias to be used for the import chain when adding the elements from the source model.
     */
    static void importFromModel(DMNModelImpl model, DMNModel m, String iAlias) {
        model.addImportChainChild(((DMNModelImpl) m).getImportChain(), iAlias);
        for (ItemDefNode idn : m.getItemDefinitions()) {
            model.getTypeRegistry().registerType(idn.getType());
        }
        for (InputDataNode idn : m.getInputs()) {
            model.addInput(idn);
        }
        for (BusinessKnowledgeModelNode bkm : m.getBusinessKnowledgeModels()) {
            model.addBusinessKnowledgeModel(bkm);
        }
        for (DecisionNode dn : m.getDecisions()) {
            model.addDecision(dn);
        }
        for (DecisionServiceNode dsn : m.getDecisionServices()) {
            model.addDecisionService(dsn);
        }
    }
}
