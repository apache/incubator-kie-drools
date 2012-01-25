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

package org.drools.fluent.impl;

import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.command.KnowledgeBaseAddKnowledgePackagesCommand;
import org.drools.command.builder.KnowledgeBuilderAddCommand;
import org.drools.fluent.CommandScript;
import org.drools.fluent.FluentKnowledgeBase;
import org.drools.io.Resource;

public class AbstractFluentKnowledgeBase<T> extends AbstractFluentTest<T> implements FluentKnowledgeBase<T> {
    
    private CommandScript cmdScript;
    
    public AbstractFluentKnowledgeBase(CommandScript cmdScript) {
        this.cmdScript = cmdScript;
    }

    public T addKnowledgePackages() {
        cmdScript.addCommand(  new KnowledgeBaseAddKnowledgePackagesCommand() );
        return (T) this;
    }

    public T addKnowledgePackages(Resource resource,
                                  ResourceType type) {
        cmdScript.addCommand( new KnowledgeBuilderAddCommand( resource,
                                                              type,
                                                              null ) );
        return (T) this;
    }

    public T addKnowledgePackages(Resource resource,
                                  ResourceType type,
                                  ResourceConfiguration configuration) {
        cmdScript.addCommand( new KnowledgeBuilderAddCommand( resource,
                                                              type,
                                                              configuration )  );
        return (T) this;
    }
    

}
