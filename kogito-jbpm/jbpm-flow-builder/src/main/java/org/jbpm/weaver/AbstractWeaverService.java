/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.weaver;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.ProcessPackage;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.internal.weaver.KieWeaverService;

public abstract class AbstractWeaverService implements KieWeaverService<ProcessPackage> {

    @Override
    public void merge(KieBase kieBase, KiePackage kiePkg, ProcessPackage processPkg) {
        ProcessPackage existing =
                ProcessPackage.getOrCreate(((InternalKnowledgePackage) kiePkg).getResourceTypePackages());
        existing.getRuleFlows().putAll(processPkg.getRuleFlows());
    }

    @Override
    public void weave(KieBase kieBase, KiePackage kiePackage, ProcessPackage processPackage) {

    }

}
