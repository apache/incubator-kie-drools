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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.runtime.process.AbortWorkItemCommand;
import org.drools.core.command.runtime.process.CompleteWorkItemCommand;
import org.drools.core.command.runtime.process.SignalEventCommand;
import org.drools.core.command.runtime.process.StartProcessCommand;
import org.drools.core.command.runtime.rule.*;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.internal.command.Context;
import org.kie.api.runtime.ExecutionResults;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;



/**
 * <p>Java class for BatchExecutionCommand complex type.
 *
 * DO NOT ADD NEW COMMANDS TO THIS CLASS
 * WITHOUT THOROUGHLY TESTING
 * 1. THE SERIALIZATION OF THOSE COMMANDS
 * 2. THE INTEGRATION OF THOSE COMMANDS IN THE REST AND WS/SOAP IMPLEMENTATIONS!
 */
@XmlRootElement(name="batch-execution")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "batch-execution", propOrder = {"lookup", "commands"})
public class BatchExecutionCommandImpl implements BatchExecutionCommand, GenericCommand<ExecutionResults> {

    private static final long serialVersionUID = 510l;

    @XmlAttribute
    @XStreamAsAttribute
    private String lookup;

    @XmlElements({
                         @XmlElement(name = "abort-work-item", type = AbortWorkItemCommand.class),
                         @XmlElement(name = "signal-event", type = SignalEventCommand.class),
                         @XmlElement(name = "start-process", type = StartProcessCommand.class),
                         @XmlElement(name = "retract", type = DeleteCommand.class),
                         @XmlElement(name = "get-global", type = GetGlobalCommand.class),
                         @XmlElement(name = "set-global", type = SetGlobalCommand.class),
                         @XmlElement(name = "insert-elements", type = InsertElementsCommand.class),
                         @XmlElement(name = "query", type = QueryCommand.class),
                         @XmlElement(name = "insert", type = InsertObjectCommand.class),
                         @XmlElement(name = "modify", type = ModifyCommand.class),
                         @XmlElement(name = "get-object", type = GetObjectCommand.class),
                         @XmlElement(name = "fire-all-rules", type = FireAllRulesCommand.class),
                         @XmlElement(name = "fire-until-halt", type = FireUntilHaltCommand.class),
                         @XmlElement(name = "complete-work-item", type = CompleteWorkItemCommand.class),
                         @XmlElement(name = "get-objects", type = GetObjectsCommand.class),
                         @XmlElement(name = "set-focus", type = AgendaGroupSetFocusCommand.class),
                         @XmlElement(name = "clear-activation-group", type = ClearActivationGroupCommand.class),
                         @XmlElement(name = "clear-agenda", type = ClearAgendaCommand.class),
                         @XmlElement(name = "clear-agenda-group", type = ClearAgendaGroupCommand.class),
                         @XmlElement(name = "clear-ruleflow-group", type = ClearRuleFlowGroupCommand.class)
                 })
    protected List<GenericCommand<?>> commands;

    public BatchExecutionCommandImpl() {
        // JAXB constructor
    }

    public BatchExecutionCommandImpl( List<GenericCommand<?>> commands ) {
        this.commands = commands;
    }

    public BatchExecutionCommandImpl( List<GenericCommand<?>> commands, String lookup ) {
        this.commands = commands;
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
    public List<GenericCommand<?>> getCommands() {
        if ( commands == null ) {
            commands = new ArrayList<GenericCommand<?>>();
        }
        return this.commands;
    }

    public ExecutionResults execute(Context context) {
        for ( GenericCommand<?> command : commands ) {
            ((GenericCommand<?>) command).execute( context );
        }
        return null;
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
