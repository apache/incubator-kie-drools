package org.drools.compiler;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.definition.process.NodeContainer;
import org.drools.definition.process.Process;
import org.drools.definition.process.WorkflowProcess;
import org.drools.io.InternalResource;
import org.drools.io.Resource;
import org.drools.lang.descr.ActionDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.process.builder.ProcessNodeBuilder;
import org.drools.process.builder.ProcessNodeBuilderRegistry;
import org.drools.process.core.Context;
import org.drools.process.core.ContextContainer;
import org.drools.process.core.context.exception.ActionExceptionHandler;
import org.drools.process.core.context.exception.ExceptionHandler;
import org.drools.process.core.context.exception.ExceptionScope;
import org.drools.process.core.impl.ProcessImpl;
import org.drools.process.core.validation.ProcessValidationError;
import org.drools.process.core.validation.ProcessValidator;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.ruleflow.core.validation.RuleFlowProcessValidator;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.workflow.core.node.ConstraintTrigger;
import org.drools.workflow.core.node.MilestoneNode;
import org.drools.workflow.core.node.Split;
import org.drools.workflow.core.node.StartNode;
import org.drools.workflow.core.node.StateNode;
import org.drools.workflow.core.node.Trigger;
import org.drools.xml.XmlProcessReader;
import org.drools.xml.processes.RuleFlowMigrator;

/**
 * A ProcessBuilder can be used to build processes based on XML files
 * containing a process definition.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class ProcessBuilder {

    private PackageBuilder                packageBuilder;
    private final List<DroolsError>       errors                         = new ArrayList<DroolsError>();
    private Map<String, ProcessValidator> processValidators              = new HashMap<String, ProcessValidator>();
    private static final String           XSL_FROM_4_TO_5                = "/org/drools/xml/processes/RuleFlowFrom4To5.xsl";
    private static final String           PROCESS_ELEMENT_WITH_NAMESPACE = "<process xmlns=\"http://drools.org/drools-5.0/process\"\n" + "    xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                                                                           + "    xs:schemaLocation=\"http://drools.org/drools-5.0/process drools-processes-5.0.xsd\"\n";

    public ProcessBuilder(PackageBuilder packageBuilder) {
        this.packageBuilder = packageBuilder;
        this.processValidators.put( RuleFlowProcess.RULEFLOW_TYPE,
                                    RuleFlowProcessValidator.getInstance() );
    }

    public List<DroolsError> getErrors() {
        return errors;
    }

    public void buildProcess(final Process process, Resource resource) {
        if ( resource != null && ((InternalResource)resource).hasURL() ) {
            ((org.drools.process.core.Process) process).setResource( resource );
        }
        boolean hasErrors = false;
        ProcessValidator validator = processValidators.get(((Process)process).getType());
        if (validator == null) {
            System.out.println("Could not find validator for process " + ((Process)process).getType() + ".");
            System.out.println("Continuing without validation of the process " + process.getName() + "[" + process.getId() + "]");
        } else {
            ProcessValidationError[] errors = validator.validateProcess( (WorkflowProcess) process );
            if ( errors.length != 0 ) {
                hasErrors = true;
                for ( int i = 0; i < errors.length; i++ ) {
                    this.errors.add( new ParserError( errors[i].toString(),
                                                      -1,
                                                      -1 ) );
                }
            }
        }
        if ( !hasErrors ) {
            // generate and add rule for process
            String rules = generateRules( process );
            try {
                packageBuilder.addPackageFromDrl( new StringReader( rules ) );
            } catch ( IOException e ) {
                // should never occur
                e.printStackTrace( System.err );
            } catch ( DroolsParserException e ) {
                // should never occur
                e.printStackTrace( System.err );
            }
            
            PackageRegistry pkgRegistry = this.packageBuilder.getPackageRegistry(process.getPackageName());
            org.drools.rule.Package p = pkgRegistry.getPackage();
            
            if (p != null) {
            
	            ProcessDescr processDescr = new ProcessDescr();
	            processDescr.setName(process.getPackageName());
	            processDescr.setResource( resource );
	            DialectCompiletimeRegistry dialectRegistry = pkgRegistry.getDialectCompiletimeRegistry();           
	            Dialect dialect = dialectRegistry.getDialect( "java" );
	            dialect.init(processDescr);
	
	            ProcessBuildContext buildContext = new ProcessBuildContext(
	        		this.packageBuilder,
	                p,
	                process,
	                processDescr,
	                dialectRegistry,
	                dialect);
	
	            buildContexts( ( ContextContainer ) process, buildContext );
	            if (process instanceof WorkflowProcess) {
	            	buildNodes( (WorkflowProcess) process, buildContext );
	            }
	            p.addProcess( process );
	
	            pkgRegistry.compileAll();                
	            pkgRegistry.getDialectRuntimeRegistry().onBeforeExecute();
            }
        }
    }

    public void buildContexts(ContextContainer contextContainer, ProcessBuildContext buildContext) {
    	List<Context> exceptionScopes = contextContainer.getContexts(ExceptionScope.EXCEPTION_SCOPE);
    	if (exceptionScopes != null) {
    		for (Context context: exceptionScopes) {
    			ExceptionScope exceptionScope = (ExceptionScope) context;
    			for (ExceptionHandler exceptionHandler: exceptionScope.getExceptionHandlers().values()) {
    				if (exceptionHandler instanceof ActionExceptionHandler) {
    					DroolsConsequenceAction action = (DroolsConsequenceAction) 
    						((ActionExceptionHandler) exceptionHandler).getAction();
    					ActionDescr actionDescr = new ActionDescr();
    			        actionDescr.setText( action.getConsequence() );   
    			        Dialect dialect = buildContext.getDialectRegistry().getDialect( action.getDialect() );            
    			        dialect.getActionBuilder().build( buildContext, action, actionDescr, (ProcessImpl) buildContext.getProcess() );
    				}
    			}
    		}
    	}
    }
    
    @SuppressWarnings("unchecked")
	public void buildNodes(WorkflowProcess process, ProcessBuildContext context) {
    	ProcessNodeBuilderRegistry nodeBuilderRegistry = packageBuilder.getPackageBuilderConfiguration().getProcessNodeBuilderRegistry();
        processNodes(process.getNodes(), process, context.getProcessDescr(), context, nodeBuilderRegistry);
        if ( !context.getErrors().isEmpty() ) {
            this.errors.addAll( context.getErrors() );
        }
        context.getDialectRegistry().addProcess( context );
    }
    
    private void processNodes(
            Node[] nodes, Process process, ProcessDescr processDescr, 
            ProcessBuildContext context, ProcessNodeBuilderRegistry nodeBuilderRegistry) {
        for ( Node node : nodes ) {
            ProcessNodeBuilder builder = nodeBuilderRegistry.getNodeBuilder( node );
            if ( builder != null ) {
                // only build if there is a registered builder for this node type
                builder.build( process,
                               processDescr,
                               context,
                               node );
            }
            if ( node instanceof NodeContainer ) {
                processNodes( ((NodeContainer) node).getNodes(),
                              process,
                              processDescr,
                              context,
                              nodeBuilderRegistry );
            }
            if ( node instanceof ContextContainer ) {
                buildContexts( (ContextContainer) node,
                               context );
            }
        }
    }

    public void addProcessFromFile(final Reader reader, final Resource resource) throws Exception {
        PackageBuilderConfiguration configuration = packageBuilder.getPackageBuilderConfiguration();
        XmlProcessReader xmlReader = new XmlProcessReader( configuration.getSemanticModules() );
        
        final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        final ClassLoader newLoader = this.getClass().getClassLoader();
        try {
            Thread.currentThread().setContextClassLoader( newLoader );
            String portRuleFlow = System.getProperty( "drools.ruleflow.port", "false" );
            Reader portedReader = null;
            if ( portRuleFlow.equalsIgnoreCase( "true" ) ) {
                portedReader = portToCurrentVersion( reader );
            } else {
                portedReader = reader;
            }
            Process process = xmlReader.read(portedReader);
            if ( process != null ) {
                // it is possible an xml file could not be parsed, so we need to stop null pointers
                buildProcess( process, resource );
            } else {
                // @TODO could we maybe add something a bit more informative about what is wrong with the XML ?
                this.errors.add( new RuleFlowLoadError( "unable to parse xml", null ) );
            }
        } finally {
            Thread.currentThread().setContextClassLoader( oldLoader );
        }
        reader.close();
    }

    
  
    /*************************************************************************
     * Converts a drools version 4 .rf or .rfm ruleflow to a version 5 .rf.
     * Version 5 .rf ruleflows are allowed, but are not migrated.
     * @param reader containing any drools 4 .rf or .rfm ruleflow, or a 
     * version 5 .rf
     * @return reader containing the input reader in the latest (5) .rf format
     * @throws Exception
     ************************************************************************/
    private Reader portToCurrentVersion(final Reader reader) throws Exception {
        //Migrate v4 ruleflows to v5
        String xml = RuleFlowMigrator.convertReaderToString( reader );
        
        if ( RuleFlowMigrator.needToMigrateRFM(xml) ) {
            // Not a current version RFM convert it.
            xml = RuleFlowMigrator.portRFMToCurrentVersion(xml);
        }
        else if ( RuleFlowMigrator.needToMigrateRF(xml) ) {
            // Not a current version RF convert it.
            xml = RuleFlowMigrator.portRFMToCurrentVersion(xml);}
        //
        // Note that we have also return any input v5 ruleflow as 
        // a StringReader since the act of checking it using 
        // convertReaderToString will have read the reader making it
        // appear empty if read later. As reset is not guaranteed on
        // all Reader implementation, it is safest to convert the v5 
        // ruleflow string representation to a StringReader as well.
        //
        return new StringReader( xml );
    }


    private String generateRules(final Process process) {
        StringBuffer builder = new StringBuffer();

        if ( process instanceof WorkflowProcessImpl ) {
            WorkflowProcessImpl ruleFlow = (WorkflowProcessImpl) process;
            builder.append( "package " + ruleFlow.getPackageName() + "\n" );
            List<String> imports = ruleFlow.getImports();
            if ( imports != null ) {
                for ( String importString: imports ) {
                    builder.append( "import " + importString + ";\n" );
                }
            }
            List<String> functionImports = ruleFlow.getFunctionImports();
            if ( functionImports != null ) {
                for ( String importString: functionImports ) {
                    builder.append( "import function " + importString + ";\n" );
                }
            }
            Map<String, String> globals = ruleFlow.getGlobals();
            if ( globals != null ) {
                for ( Map.Entry<String, String> entry: globals.entrySet()) {
                    builder.append( "global " + entry.getValue() + " " + entry.getKey() + ";\n" );
                }
            }

            Node[] nodes = ruleFlow.getNodes();
            generateRules(nodes, process, builder);
        }
        return builder.toString();
    }
 
    private void generateRules(Node[] nodes, Process process, StringBuffer builder) {
        for ( int i = 0; i < nodes.length; i++ ) {
            if ( nodes[i] instanceof Split ) {
                Split split = (Split) nodes[i];
                if ( split.getType() == Split.TYPE_XOR || split.getType() == Split.TYPE_OR ) {
                    for ( Connection connection : split.getDefaultOutgoingConnections() ) {
                        Constraint constraint = split.getConstraint( connection );
                        if ( "rule".equals( constraint.getType() ) ) {
                            builder.append( createSplitRule( process,
                                                             connection,
                                                             split.getConstraint( connection ).getConstraint() ) );
                        }
                    }
                }
            } else if ( nodes[i] instanceof MilestoneNode ) {
                MilestoneNode milestone = (MilestoneNode) nodes[i];
                builder.append( createMilestoneRule( process,
                                                     milestone ) );
            } else if ( nodes[i] instanceof StateNode ) {
                StateNode state = (StateNode) nodes[i];
                builder.append( createAllStateRules(process, state) );
            } else if ( nodes[i] instanceof StartNode ) {
                StartNode startNode = (StartNode) nodes[i];
                List<Trigger> triggers = startNode.getTriggers();
                if ( triggers != null ) {
                    for ( Trigger trigger : triggers ) {
                        if ( trigger instanceof ConstraintTrigger ) {
                            builder.append( createStartConstraintRule( process,
                                                                       (ConstraintTrigger) trigger ) );
                        }
                    }
                }
            } else if ( nodes[i] instanceof NodeContainer ) {
                generateRules( ((NodeContainer) nodes[i]).getNodes(), process, builder);
            }
        }
    }

    private String createSplitRule(Process process,
                                   Connection connection,
                                   String constraint) {
        return "rule \"RuleFlow-Split-" + process.getId() + "-" + connection.getFrom().getId() + "-" + connection.getTo().getId() + "\" \n" + "      ruleflow-group \"DROOLS_SYSTEM\" \n" + "    when \n" + "      " + constraint + "\n" + "    then \n"
               + "end \n\n";
    }

    private String createMilestoneRule(Process process,
                                       MilestoneNode milestone) {
        return "rule \"RuleFlow-Milestone-" + process.getId() + "-" + milestone.getId() + "\" \n" + "      ruleflow-group \"DROOLS_SYSTEM\" \n" + "    when \n" + "      " + milestone.getConstraint() + "\n" + "    then \n" + "end \n\n";
    }
    private String createStateRule(Process process,
                                       StateNode state, String key) {
        return "rule \"RuleFlowStateNode-" + process.getId() + "-" + state.getId() + "-" + key + "\" \n" + "      ruleflow-group \"DROOLS_SYSTEM\" \n" + "    when \n" + "      " + state.getConstraints().get(key).getConstraint() + "\n" + "    then \n" + "end \n\n";
    }
    private String createAllStateRules(Process process, StateNode state){
        String result = "";
        for(String key : state.getConstraints().keySet()){
            result  += createStateRule(process, state, key);
        }
        return result;

    }

    private String createStartConstraintRule(Process process,
                                             ConstraintTrigger trigger) {
        String result = "rule \"RuleFlow-Start-" + process.getId() + "\" \n" + "    when\n" + "        " + trigger.getConstraint() + "\n" + "    then\n";
        Map<String, String> inMappings = trigger.getInMappings();
        if ( inMappings != null && !inMappings.isEmpty() ) {
            result += "        java.util.Map params = new java.util.HashMap();\n";
            for ( Map.Entry<String, String> entry : inMappings.entrySet() ) {
                result += "        params.put(\"" + entry.getKey() + "\", " + entry.getValue() + ");\n";
            }
            result += "        drools.getWorkingMemory().startProcess(\"" + process.getId() + "\", params);\n" + "end\n\n";
        } else {
            result += "        drools.getWorkingMemory().startProcess(\"" + process.getId() + "\");\n" + "end\n\n";
        }
        return result;
    }

}
