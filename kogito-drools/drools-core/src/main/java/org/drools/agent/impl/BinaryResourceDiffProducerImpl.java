/*
 * Copyright 2009 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.agent.impl;

import java.util.HashSet;
import java.util.Set;
import org.drools.definition.KnowledgeDefinition;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.agent.ResourceDiffProducer;
import org.drools.rule.Rule;
import org.drools.util.ReflectiveVisitor;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
public class BinaryResourceDiffProducerImpl extends ReflectiveVisitor implements ResourceDiffProducer {

    private KnowledgePackageImp newPkg;

    public ResourceDiffResult diff(Set<KnowledgeDefinition> originalDefinitions, KnowledgePackageImp pkg) {

        this.newPkg = pkg;
        
        Set<KnowledgeDefinition> removed = new HashSet<KnowledgeDefinition>();


        for (KnowledgeDefinition knowledgeDefinition : originalDefinitions) {
            this.visit(knowledgeDefinition);
        }


        //return the whole new package as new
        return new ResourceDiffResult(pkg, originalDefinitions);
    }

    
    public void visitRule(final Rule rule){
        //ok, so I got an old rule: is it modified in the new pkg? is it even present on it?
        //newPkg.

    }



}
