/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.codegen.common.GeneratedFile;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;

public class RuleUnitQueryEventCodegen {

    private final KogitoBuildContext context;
    private final Iterable<QueryGenerator> ruleUnitGenerators;

    public RuleUnitQueryEventCodegen(KogitoBuildContext context, Iterable<QueryGenerator> ruleUnitGenerators) {
        this.context = context;
        this.ruleUnitGenerators = ruleUnitGenerators;
    }

    Collection<GeneratedFile> generate() {
        List<GeneratedFile> generatedFiles = new ArrayList<>();
        if (context.getAddonsConfig().useEventDrivenRules()) {
            for (QueryGenerator queryGenerator : ruleUnitGenerators) {
                generatedFiles.add(new QueryEventDrivenExecutorGenerator(queryGenerator).generate());
            }
        }
        return generatedFiles;
    }

}
