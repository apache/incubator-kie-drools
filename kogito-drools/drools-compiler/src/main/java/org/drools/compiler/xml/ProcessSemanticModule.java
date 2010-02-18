package org.drools.compiler.xml;

import org.drools.compiler.xml.processes.ActionNodeHandler;
import org.drools.compiler.xml.processes.CompositeNodeHandler;
import org.drools.compiler.xml.processes.ConnectionHandler;
import org.drools.compiler.xml.processes.ConstraintHandler;
import org.drools.compiler.xml.processes.DynamicNodeHandler;
import org.drools.compiler.xml.processes.EndNodeHandler;
import org.drools.compiler.xml.processes.EventFilterHandler;
import org.drools.compiler.xml.processes.EventNodeHandler;
import org.drools.compiler.xml.processes.ExceptionHandlerHandler;
import org.drools.compiler.xml.processes.FaultNodeHandler;
import org.drools.compiler.xml.processes.ForEachNodeHandler;
import org.drools.compiler.xml.processes.FunctionImportHandler;
import org.drools.compiler.xml.processes.GlobalHandler;
import org.drools.compiler.xml.processes.HumanTaskNodeHandler;
import org.drools.compiler.xml.processes.ImportHandler;
import org.drools.compiler.xml.processes.InPortHandler;
import org.drools.compiler.xml.processes.JoinNodeHandler;
import org.drools.compiler.xml.processes.MappingHandler;
import org.drools.compiler.xml.processes.MilestoneNodeHandler;
import org.drools.compiler.xml.processes.OutPortHandler;
import org.drools.compiler.xml.processes.ParameterHandler;
import org.drools.compiler.xml.processes.ProcessHandler;
import org.drools.compiler.xml.processes.RuleSetNodeHandler;
import org.drools.compiler.xml.processes.SplitNodeHandler;
import org.drools.compiler.xml.processes.StartNodeHandler;
import org.drools.compiler.xml.processes.StateNodeHandler;
import org.drools.compiler.xml.processes.SubProcessNodeHandler;
import org.drools.compiler.xml.processes.SwimlaneHandler;
import org.drools.compiler.xml.processes.TimerHandler;
import org.drools.compiler.xml.processes.TimerNodeHandler;
import org.drools.compiler.xml.processes.TriggerHandler;
import org.drools.compiler.xml.processes.TypeHandler;
import org.drools.compiler.xml.processes.ValueHandler;
import org.drools.compiler.xml.processes.VariableHandler;
import org.drools.compiler.xml.processes.WorkHandler;
import org.drools.compiler.xml.processes.WorkItemNodeHandler;
import org.drools.xml.DefaultSemanticModule;
import org.drools.xml.SemanticModule;

public class ProcessSemanticModule extends DefaultSemanticModule implements SemanticModule {    
    public ProcessSemanticModule() {
        super ( "http://drools.org/drools-5.0/process" );

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
    }
}
