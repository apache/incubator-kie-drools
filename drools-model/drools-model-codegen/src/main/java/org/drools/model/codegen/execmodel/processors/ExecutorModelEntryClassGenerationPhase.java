/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.codegen.execmodel.processors;

import java.util.Collection;

import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.BuildResultCollectorImpl;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.model.codegen.execmodel.PackageModel;
import org.kie.api.io.Resource;
import org.kie.internal.builder.InternalMessage;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResultSeverity;

public class ExecutorModelEntryClassGenerationPhase<T> implements CompilationPhase {
    private final PackageModel pkgModel;
    private final BuildResultCollector results;


    public ExecutorModelEntryClassGenerationPhase(
            PackageModel pkgModel) {
        this.pkgModel = pkgModel;
        this.results = new BuildResultCollectorImpl();
    }

    @Override
    public void process() {
        final String executorModelEntryClass = pkgModel.getName() + "." + pkgModel.getRulesFileName();
        if (pkgModel.getRuleUnits() != null && !pkgModel.getRuleUnits().isEmpty()) {
            pkgModel.getRuleUnits().forEach(ruleUnitDescription -> {
                String executorModelRuleUnitEntryClass = executorModelEntryClass + "_" + ruleUnitDescription.getSimpleName();
                results.addBuilderResult(new ExecutorModelEntryClassResult(executorModelRuleUnitEntryClass));
            });
        } else {
            results.addBuilderResult(new ExecutorModelEntryClassResult(executorModelEntryClass));
        }
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return results.getAllResults();
    }

    public static class ExecutorModelEntryClassResult implements KnowledgeBuilderResult {

        private final String message;

        public ExecutorModelEntryClassResult(String executorModelEntryClass) {
            this.message = executorModelEntryClass;
        }

        @Override
        public ResultSeverity getSeverity() {
            return ResultSeverity.INFO;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public int[] getLines() {
            return new int[0];
        }

        @Override
        public Resource getResource() {
            return null;
        }

        @Override
        public InternalMessage asMessage(long id) {
            return null;
        }
    }
}
