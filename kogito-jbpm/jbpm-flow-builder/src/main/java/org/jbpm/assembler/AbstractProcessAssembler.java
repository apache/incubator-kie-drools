/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.assembler;

import java.util.List;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.ProcessLoadError;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.jbpm.compiler.ProcessBuilderImpl;
import org.kie.api.definition.process.Process;
import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilderResult;

public abstract class AbstractProcessAssembler implements KieAssemblerService {

    @Override
    public void addResourceAfterRules(
            Object kbuilder,
            Resource resource,
            ResourceType type,
            ResourceConfiguration configuration) {

        KnowledgeBuilderImpl kb = (KnowledgeBuilderImpl) kbuilder;
        ProcessBuilderImpl processBuilder = (ProcessBuilderImpl) kb.getProcessBuilder();
        configurePackageBuilder(kb);

        try {
            List<Process> processes = processBuilder.addProcessFromXml(resource);
            List<KnowledgeBuilderResult> errors = processBuilder.getErrors();
            if (errors.isEmpty()) {

                for (Process process : processes) {
                    onProcessAdded(process, kb);
                }
            } else {
                errors.forEach(kb::addBuilderResult);
                errors.clear();
            }
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            kb.addBuilderResult(new ProcessLoadError(resource, "Unable to load process.", e));
        }

        // propagate dialect errors to process building errors
        for (PackageRegistry pkg : kb.getPackageRegistry().values()) {
            // addResults() method, contrary to what could be expectations,
            // *adds* the result to the *given* list
            List<KnowledgeBuilderResult> es = pkg.getDialectCompiletimeRegistry().addResults(null);
            es.forEach(kb::addBuilderResult);
        }
    }

    protected abstract void configurePackageBuilder(KnowledgeBuilderImpl kb);

    protected void onProcessAdded(Process process, Object kbuilder) {
        KnowledgeBuilderImpl kb = (KnowledgeBuilderImpl) kbuilder;
        InternalKnowledgeBase kBase = kb.getKnowledgeBase();
        if (kBase != null && process != null) {
            kBase.addProcess(process);
        }
    }
}
