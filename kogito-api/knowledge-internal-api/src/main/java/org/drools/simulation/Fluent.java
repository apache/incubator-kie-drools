/*
 * Copyright 2011 JBoss Inc
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

package org.drools.simulation;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public interface Fluent {       
        Object getValue(); // returns the last commands returned value
        Fluent set(String name);   // assigns the last commands return vlaue to a variable
        
    
        Fluent newPath(String name);
        Fluent createStep(long distance);

        Fluent createKnowledgeBuilder();

        Fluent hasErrors();        
        Fluent getErrors();

        Fluent add(Resource resource,
                   ResourceType type);

        Fluent add(Resource resource,
                                   ResourceType type,
                                   ResourceConfiguration configuration); 
        
        Fluent newKnowledgeBase();        
        
        Fluent fireAllRules();
        
        Fluent insert(Object object);
    
}
