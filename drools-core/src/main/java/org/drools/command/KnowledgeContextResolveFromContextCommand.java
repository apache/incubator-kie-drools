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

package org.drools.command;

import org.drools.WorkingMemoryEntryPoint;
import org.drools.command.impl.FixedKnowledgeCommandContext;
import org.drools.command.impl.GenericCommand;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeBuilder;
import org.kie.command.Command;
import org.kie.command.Context;
import org.kie.runtime.StatefulKnowledgeSession;

public class KnowledgeContextResolveFromContextCommand
    implements
    GenericCommand {

    private String  kbaseIdentifier;
    private String  kbuilderIdentifier;
    private String  statefulKsessionName;
    private String  kresults;
    private String  workingMemoryEntryPointName;
    private Command command;

    public KnowledgeContextResolveFromContextCommand(Command command,
                                                     String kbuilderIdentifier,
                                                     String kbaseIdentifier,
                                                     String statefulKsessionName,
                                                     String kresults) {
        this.command = command;
        this.kbuilderIdentifier = kbuilderIdentifier;
        this.kbaseIdentifier = kbaseIdentifier;
        this.statefulKsessionName = statefulKsessionName;
        this.kresults = kresults;
    }
     public KnowledgeContextResolveFromContextCommand(Command command,
                                                     String kbuilderIdentifier,
                                                     String kbaseIdentifier,
                                                     String statefulKsessionName,
                                                     String workingMemoryEntryPointName,
                                                     String kresults) {
        this(command, kbuilderIdentifier, kbaseIdentifier, statefulKsessionName, kresults);
        this.workingMemoryEntryPointName = workingMemoryEntryPointName;
    }

    public Object execute(Context context) {
        FixedKnowledgeCommandContext kcContext = new FixedKnowledgeCommandContext( context,
                                                                         (KnowledgeBuilder) context.get( this.kbuilderIdentifier ),
                                                                         (KnowledgeBase) context.get( this.kbaseIdentifier ),
                                                                         (StatefulKnowledgeSession) context.get( this.statefulKsessionName ),
                                                                         (WorkingMemoryEntryPoint) context.get(this.workingMemoryEntryPointName),
                                                                         (ExecutionResultImpl) context.get( this.kresults ) );
        return ((GenericCommand) command).execute( kcContext );
    }

    public Command getCommand() {
        return this.command;
    }

}
