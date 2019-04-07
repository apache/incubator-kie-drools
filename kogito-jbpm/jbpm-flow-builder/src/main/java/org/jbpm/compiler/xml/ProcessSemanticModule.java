/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.compiler.xml;

import org.drools.core.xml.DefaultSemanticModule;
import org.drools.core.xml.SemanticModule;
import org.jbpm.compiler.xml.processes.ActionNodeHandler;
import org.jbpm.compiler.xml.processes.CompositeNodeHandler;
import org.jbpm.compiler.xml.processes.ConnectionHandler;
import org.jbpm.compiler.xml.processes.ConstraintHandler;
import org.jbpm.compiler.xml.processes.DynamicNodeHandler;
import org.jbpm.compiler.xml.processes.EndNodeHandler;
import org.jbpm.compiler.xml.processes.EventFilterHandler;
import org.jbpm.compiler.xml.processes.EventNodeHandler;
import org.jbpm.compiler.xml.processes.ExceptionHandlerHandler;
import org.jbpm.compiler.xml.processes.FaultNodeHandler;
import org.jbpm.compiler.xml.processes.ForEachNodeHandler;
import org.jbpm.compiler.xml.processes.FunctionImportHandler;
import org.jbpm.compiler.xml.processes.GlobalHandler;
import org.jbpm.compiler.xml.processes.HumanTaskNodeHandler;
import org.jbpm.compiler.xml.processes.ImportHandler;
import org.jbpm.compiler.xml.processes.InPortHandler;
import org.jbpm.compiler.xml.processes.JoinNodeHandler;
import org.jbpm.compiler.xml.processes.MappingHandler;
import org.jbpm.compiler.xml.processes.MetaDataHandler;
import org.jbpm.compiler.xml.processes.MilestoneNodeHandler;
import org.jbpm.compiler.xml.processes.OutPortHandler;
import org.jbpm.compiler.xml.processes.ParameterHandler;
import org.jbpm.compiler.xml.processes.ProcessHandler;
import org.jbpm.compiler.xml.processes.RuleSetNodeHandler;
import org.jbpm.compiler.xml.processes.SplitNodeHandler;
import org.jbpm.compiler.xml.processes.StartNodeHandler;
import org.jbpm.compiler.xml.processes.StateNodeHandler;
import org.jbpm.compiler.xml.processes.SubProcessNodeHandler;
import org.jbpm.compiler.xml.processes.SwimlaneHandler;
import org.jbpm.compiler.xml.processes.TimerHandler;
import org.jbpm.compiler.xml.processes.TimerNodeHandler;
import org.jbpm.compiler.xml.processes.TriggerHandler;
import org.jbpm.compiler.xml.processes.TypeHandler;
import org.jbpm.compiler.xml.processes.ValueHandler;
import org.jbpm.compiler.xml.processes.VariableHandler;
import org.jbpm.compiler.xml.processes.WorkHandler;
import org.jbpm.compiler.xml.processes.WorkItemNodeHandler;

public class ProcessSemanticModule extends DefaultSemanticModule implements SemanticModule {
	
	public static final String URI = "http://drools.org/drools-5.0/process";
	
    public ProcessSemanticModule() {
        super ( URI );

        addHandler( "process",
                           new ProcessHandler() );
        addHandler( "start",
                           new StartNodeHandler() );
        addHandler( "end",
                           new EndNodeHandler() );
        addHandler( "actionNode",
                           new ActionNodeHandler() );
        addHandler( "ruleSet",
                           new RuleSetNodeHandler() );
        addHandler( "subProcess",
                           new SubProcessNodeHandler() );
        addHandler( "workItem",
                           new WorkItemNodeHandler() );
        addHandler( "split",
                           new SplitNodeHandler() );
        addHandler( "join",
                           new JoinNodeHandler() );
        addHandler( "milestone",
                           new MilestoneNodeHandler() );
        addHandler( "timerNode",
                           new TimerNodeHandler() );
        addHandler( "humanTask",
                           new HumanTaskNodeHandler() );
        addHandler( "forEach",
                           new ForEachNodeHandler() );
        addHandler( "composite",
                           new CompositeNodeHandler() );
        addHandler( "connection",
                           new ConnectionHandler() );
        addHandler( "import",
                           new ImportHandler() );
        addHandler( "functionImport",
                           new FunctionImportHandler() );
        addHandler( "global",
                           new GlobalHandler() );        
        addHandler( "variable",
                           new VariableHandler() );        
        addHandler( "swimlane",
                           new SwimlaneHandler() );        
        addHandler( "type",
                           new TypeHandler() );        
        addHandler( "value",
                           new ValueHandler() );        
        addHandler( "work",
                           new WorkHandler() );        
        addHandler( "parameter",
                           new ParameterHandler() );        
        addHandler( "mapping",
                           new MappingHandler() );        
        addHandler( "constraint",
                           new ConstraintHandler() );        
        addHandler( "in-port",
                           new InPortHandler() );        
        addHandler( "out-port",
                           new OutPortHandler() );        
        addHandler( "eventNode",
                		   new EventNodeHandler() );        
        addHandler( "eventFilter",
                		   new EventFilterHandler() );        
        addHandler( "fault",
     		   			   new FaultNodeHandler() );        
        addHandler( "exceptionHandler",
	   			   		   new ExceptionHandlerHandler() );        
        addHandler( "timer",
                		   new TimerHandler() );
        addHandler( "trigger",
     		               new TriggerHandler() );
        addHandler( "state",
     		               new StateNodeHandler() );        
        addHandler( "dynamic",
 		                   new DynamicNodeHandler() );        
        addHandler( "metaData",
                           new MetaDataHandler() );        
    }
}
