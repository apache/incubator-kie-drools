/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.beliefs.bayes.runtime;

import org.drools.beliefs.bayes.BayesInstance;
import org.drools.beliefs.bayes.JunctionTree;
import org.drools.beliefs.bayes.assembler.BayesPackage;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.ResourceTypePackageRegistry;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;

public class BayesRuntimeImpl implements BayesRuntime {

    private KieBase kieBase;

    public BayesRuntimeImpl(KieBase kieBase) {
        this.kieBase = kieBase;
    }

    public BayesInstance createInstance(Class cls) {
        InternalKnowledgePackage kpkg = (InternalKnowledgePackage) kieBase.getKiePackage(cls.getPackage().getName());
        ResourceTypePackageRegistry map = kpkg.getResourceTypePackages();
        BayesPackage bayesPkg = (BayesPackage) map.get(ResourceType.BAYES);
        JunctionTree jtree = bayesPkg.getJunctionTree(cls.getSimpleName());

        return new BayesInstance(jtree, cls);
    }
}
