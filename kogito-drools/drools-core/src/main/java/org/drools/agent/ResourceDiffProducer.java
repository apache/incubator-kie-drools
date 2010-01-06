/*
 *  Copyright 2009 esteban.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.drools.agent;

import java.util.Set;
import org.drools.definition.KnowledgeDefinition;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.agent.impl.ResourceDiffResult;

/**
 *
 * @author esteban
 */
public interface ResourceDiffProducer {
    ResourceDiffResult diff(Set<KnowledgeDefinition> originalDefinitions, KnowledgePackageImp newPkg, KnowledgePackageImp currentPkg );
}
