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

package org.kie.dmn.submarine.rest.quarkus;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.ResourceTypePackageRegistry;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.kie.api.io.ResourceType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.impl.DMNPackageImpl;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.internal.utils.DMNEvaluationUtils;
import org.kie.dmn.core.internal.utils.DMNEvaluationUtils.DMNEvaluationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNSubmarineQuarkus {

    private static final Logger LOG = LoggerFactory.getLogger(DMNSubmarineQuarkus.class);

    private DMNSubmarineQuarkus() {
        // intentionally private.
    }

    public static DMNRuntime createGenericDMNRuntime() {
        KnowledgeBaseImpl knowledgeBase = new KnowledgeBaseImpl("", new RuleBaseConfiguration());
        Map<String, InternalKnowledgePackage> pkgs = knowledgeBase.getPackagesMap();
        DMNCompilerImpl compilerImpl = new DMNCompilerImpl();
        try {
            List<java.nio.file.Path> files = Files.walk(Paths.get("."))
                                                  .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".dmn"))
                                                  .peek(x -> LOG.debug("Adding DMN model {} to runtime", x))
                                                  .collect(Collectors.toList());
            for (java.nio.file.Path file : files) {
                DMNModel m = compilerImpl.compile(new FileReader(file.toFile()));
                InternalKnowledgePackage pkg = pkgs.computeIfAbsent(m.getNamespace(), ns -> new KnowledgePackageImpl(ns));
                ResourceTypePackageRegistry rpkg = pkg.getResourceTypePackages();
                DMNPackageImpl dmnpkg = rpkg.computeIfAbsent(ResourceType.DMN, rtp -> new DMNPackageImpl(m.getNamespace()));
                dmnpkg.addModel(m.getName(), m);// TODO add profiles? and check dups over namespace/name
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new DMNRuntimeImpl(knowledgeBase);
    }

    public static DMNModel modelByName(DMNRuntime dmnRuntime, String modelName) {
        List<DMNModel> modelsWithName = dmnRuntime.getModels().stream().filter(m -> modelName.equals(m.getName())).collect(Collectors.toList());
        if (modelsWithName.size() == 1) {
            return modelsWithName.get(0);
        } else {
            throw new RuntimeException("Multiple model with the same name: " + modelName);
        }
    }

    public static DMNResult evaluate(DMNRuntime dmnRuntime, String modelName, Map<String, Object> dmnContext) {
        return evaluate(dmnRuntime, modelByName(dmnRuntime, modelName).getNamespace(), modelName, dmnContext);
    }

    public static DMNResult evaluate(DMNRuntime dmnRuntime, String modelNamespace, String modelName, Map<String, Object> dmnContext) {
        DMNEvaluationResult evaluationResult = DMNEvaluationUtils.evaluate(dmnRuntime,
                                                                           modelNamespace,
                                                                           modelName,
                                                                           dmnContext,
                                                                           null,
                                                                           null,
                                                                           null);
        return new DMNResult(evaluationResult.result);
    }

}
