/*
 * Copyright 2010 JBoss Inc
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

import java.util.Set;
import org.drools.definition.KnowledgeDefinition;
import org.drools.definition.KnowledgePackage;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
public class ResourceDiffResult {
    private final KnowledgePackage pkg;

    /**
     * The definitions present in package's old version but not in the
     * new one.
     */
    private final Set<KnowledgeDefinition> removedDefinitions;

    /**
     * The definitions that were not modified in the new version of the
     * package.
     */
    private final Set<KnowledgeDefinition> unmodifiedDefinitions;

    public ResourceDiffResult(KnowledgePackage pkg, Set<KnowledgeDefinition> unmodifiedDefinitions, Set<KnowledgeDefinition> removedDefinitions) {
        this.pkg = pkg;
        this.unmodifiedDefinitions = unmodifiedDefinitions;
        this.removedDefinitions = removedDefinitions;
    }

    public KnowledgePackage getPkg() {
        return pkg;
    }

    public Set<KnowledgeDefinition> getUnmodifiedDefinitions() {
        return unmodifiedDefinitions;
    }

    public Set<KnowledgeDefinition> getRemovedDefinitions() {
        return removedDefinitions;
    }

}
