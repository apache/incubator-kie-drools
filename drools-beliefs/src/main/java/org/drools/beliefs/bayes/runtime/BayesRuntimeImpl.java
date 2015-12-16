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
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.kie.internal.io.ResourceTypePackage;

import org.kie.api.io.ResourceType;

import java.util.HashMap;
import java.util.Map;

public class BayesRuntimeImpl implements BayesRuntime {
    private InternalKnowledgeRuntime runtime;

    //private Map<String, BayesInstance> instances;

    public BayesRuntimeImpl(InternalKnowledgeRuntime runtime) {
        this.runtime = runtime;
        //this.instances = new HashMap<String, BayesInstance>();
    }

//    @Override
//    public BayesInstance createBayesFact(Class cls) {
//        // using the two-tone pattern, to ensure only one is created
//        BayesInstance instance = instances.get( cls.getName() );
//        if ( instance == null ) {
//            instance = createInstance(cls);
//        }
//
//        return instance;
//    }

    public  BayesInstance createInstance(Class cls) {
        // synchronised using the two-tone pattern, to ensure only one is created
//        BayesInstance instance = instances.get( cls.getName() );
//        if ( instance != null ) {
//            return instance;
//        }


        InternalKnowledgePackage kpkg = (InternalKnowledgePackage) runtime.getKieBase().getKiePackage( cls.getPackage().getName() );
        Map<ResourceType, ResourceTypePackage> map = kpkg.getResourceTypePackages();
        BayesPackage bayesPkg  = (BayesPackage) map.get( ResourceType.BAYES );
        JunctionTree jtree =  bayesPkg.getJunctionTree(cls.getSimpleName());

        BayesInstance instance = new BayesInstance( jtree, cls );
//        instances.put( cls.getName() , instance );

        return instance;
    }
}
