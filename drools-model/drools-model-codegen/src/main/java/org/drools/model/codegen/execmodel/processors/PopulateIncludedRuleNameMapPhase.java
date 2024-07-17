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
package org.drools.model.codegen.execmodel.processors;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.model.Model;
import org.drools.modelcompiler.CanonicalKieModule;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.internal.builder.KnowledgeBuilderResult;

public class PopulateIncludedRuleNameMapPhase implements CompilationPhase {

    private final Map<KieBaseModel, InternalKieModule> includeModules;
    private final Map<String, Set<String>> includedRuleNameMap;

    public PopulateIncludedRuleNameMapPhase(Map<KieBaseModel, InternalKieModule> includeModules, Map<String, Set<String>> includedRuleNameMap) {
        this.includeModules = includeModules;
        this.includedRuleNameMap = includedRuleNameMap;
    }

    @Override
    public void process() {
        for (Map.Entry<KieBaseModel, InternalKieModule> entry : includeModules.entrySet()) {
            KieBaseModel kieBaseModel = entry.getKey();
            InternalKieModule includeModule = entry.getValue();
            if ((includeModule instanceof CanonicalKieModule canonicalKieModule) && canonicalKieModule.hasModelFile()) {
                Collection<Model> includeModels = canonicalKieModule.getModelForKBase((KieBaseModelImpl)kieBaseModel);
                for (Model includeModel : includeModels) {
                    includeModel.getRules().forEach(rule -> includedRuleNameMap.computeIfAbsent(includeModel.getPackageName(), k -> new HashSet<>()).add(rule.getName()));
                }
            }
        }
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return Collections.emptyList();
    }
}
