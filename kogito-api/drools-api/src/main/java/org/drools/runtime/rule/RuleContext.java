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

package org.drools.runtime.rule;

import org.drools.definition.rule.Rule;
import org.drools.runtime.KnowledgeContext;

public interface RuleContext extends KnowledgeContext {
    
    /**
     * Returns the active Rule for the current context
     *  
     * @return
     */
    Rule getRule();

    /**
     * Returns the current Activation for the current context
     * 
     * @return
     */
    Activation getActivation();
    
    /**
     * Logically inserts a fact into the KnowledgeSession, justified by the current
     * rule context.
     * 
     * @param object the fact to insert into the knowledge session
     */
    void insertLogical(Object object);
    
}
