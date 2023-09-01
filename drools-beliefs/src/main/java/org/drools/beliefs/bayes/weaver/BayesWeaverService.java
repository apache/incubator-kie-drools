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
package org.drools.beliefs.bayes.weaver;

import org.drools.beliefs.bayes.assembler.BayesPackage;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.kie.api.definition.KiePackage;
import org.kie.api.internal.weaver.KieWeaverService;
import org.kie.api.io.ResourceType;

public class BayesWeaverService implements KieWeaverService<BayesPackage> {

    @Override
    public ResourceType getResourceType() {
        return ResourceType.BAYES;
    }

    @Override
    public void merge(KiePackage kiePkg, BayesPackage bayesPkg) {
        InternalKnowledgePackage internalPkg = (InternalKnowledgePackage) kiePkg;
        BayesPackage registeredPkg = internalPkg.getResourceTypePackages()
                .computeIfAbsent(ResourceType.BAYES, rt -> new BayesPackage(kiePkg.getName()));

        for (String name : bayesPkg.listJunctionTrees()) {
            registeredPkg.addJunctionTree(name, bayesPkg.getJunctionTree(name));
        }
    }

    @Override
    public void weave(KiePackage kiePkg, BayesPackage rtPkg) {
        System.out.println("Hello World ");
    }
}
