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

package org.drools.command.builder;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.io.Resource;
import org.drools.runtime.impl.ExecutionResultImpl;

public class KnowledgeBuilderHasErrorsCommand
    implements
    GenericCommand<Boolean> {

    private String outIdentifier;

    public KnowledgeBuilderHasErrorsCommand() {
    }
    
    public KnowledgeBuilderHasErrorsCommand(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public Boolean execute(Context context) {
        KnowledgeBuilder kbuilder = ((KnowledgeCommandContext) context).getKnowledgeBuilder();
        boolean errors = kbuilder.hasErrors();
        if ( this.outIdentifier != null ) {
            ((ExecutionResultImpl)((KnowledgeCommandContext) context).getExecutionResults()).getResults().put( this.outIdentifier, errors );
        }
        return errors;
    }

}
