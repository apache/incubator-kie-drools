/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.command.builder;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.command.Context;

public class KnowledgeBuilderGetErrorsCommand
    implements
    GenericCommand<KnowledgeBuilderErrors> {

    private String outIdentifier;

    public KnowledgeBuilderGetErrorsCommand() {
    }
    
    public KnowledgeBuilderGetErrorsCommand(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public KnowledgeBuilderErrors execute(Context context) {
        KnowledgeBuilder kbuilder = ((KnowledgeCommandContext) context).getKnowledgeBuilder();
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( this.outIdentifier != null ) {
            ((ExecutionResultImpl)((KnowledgeCommandContext) context).getExecutionResults()).getResults().put( this.outIdentifier, errors );
        }
        return errors;
    }

}
