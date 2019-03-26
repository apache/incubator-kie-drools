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

package org.drools.core.command.runtime;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.command.runtime.process.AbortWorkItemCommand;
import org.drools.core.command.runtime.process.CompleteWorkItemCommand;
import org.drools.core.command.runtime.process.SignalEventCommand;
import org.drools.core.command.runtime.process.StartProcessCommand;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.command.runtime.rule.InsertElementsCommand;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.drools.core.command.runtime.rule.QueryCommand;
import org.drools.core.fluent.impl.Batch;
import org.kie.api.command.Command;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;


/**
 * <p>Java class for BatchExecutionCommand complex type.
 *
 * DO NOT ADD NEW COMMANDS TO THIS CLASS
 * WITHOUT THOROUGHLY TESTING
 * 1. THE SERIALIZATION OF THOSE COMMANDS
 * 2. THE INTEGRATION OF THOSE COMMANDS IN THE REST AND WS/SOAP IMPLEMENTATIONS!
 */
public class BatchExecutionCommandImpl implements Batch, ExecutableCommand<ExecutionResults> {

    private static final long serialVersionUID = 510l;

    private String lookup;

   protected List<Command> commands;

    public BatchExecutionCommandImpl() {
        // JAXB constructor
    }

    public BatchExecutionCommandImpl( List<? extends Command> commands ) {
        this( commands, null );
    }

    public BatchExecutionCommandImpl( List<? extends Command> commands, String lookup ) {
        this.commands = (List<Command>) commands;
        this.lookup = lookup;
    }

    /**
     * Gets the value of the abortWorkItemOrCompleteWorkItemOrFireAllRules property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abortWorkItemOrCompleteWorkItemOrFireAllRules property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCommand().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SetGlobalCommand }
     * {@link CompleteWorkItemCommand }
     * {@link AbortWorkItemCommand }
     * {@link SignalEventCommand }
     * {@link FireAllRulesCommand }
     * {@link StartProcessCommand }
     * {@link GetGlobalCommand }
     * {@link InsertElementsCommand }
     * {@link QueryCommand }
     * {@link InsertObjectCommand }
     */
    public List<Command> getCommands() {
        return this.commands;
    }

    public BatchExecutionCommandImpl addCommand( Command cmd ) {
        if ( commands == null ) {
            commands = new ArrayList<Command>();
        }
        this.commands.add(cmd);
        return this;
    }

    public ExecutionResults execute(Context context) {
        for ( Command command : commands ) {
            ((ExecutableCommand<?>) command).execute( context );
        }
        return null;
    }

    public long getDistance() {
        return 0L;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public String getLookup() {
        return lookup;
    }

    public String toString() {
        return "BatchExecutionCommandImpl{" +
               "lookup='" + lookup + '\'' +
               ", commands=" + commands +
               '}';
    }
}
