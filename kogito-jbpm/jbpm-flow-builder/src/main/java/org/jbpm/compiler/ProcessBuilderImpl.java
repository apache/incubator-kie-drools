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
package org.jbpm.compiler;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;

import org.drools.RuntimeDroolsException;
import org.drools.compiler.Dialect;
import org.drools.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.PackageRegistry;
import org.drools.compiler.ParserError;
import org.drools.compiler.ProcessBuilder;
import org.drools.compiler.ProcessLoadError;
import org.drools.compiler.ReturnValueDescr;
import org.kie.definition.process.Connection;
import org.kie.definition.process.Node;
import org.kie.definition.process.NodeContainer;
import org.kie.definition.process.Process;
import org.kie.definition.process.WorkflowProcess;
import org.kie.io.Resource;
import org.drools.io.internal.InternalResource;
import org.drools.lang.descr.ActionDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.rule.builder.dialect.java.JavaDialect;
import org.jbpm.compiler.xml.ProcessSemanticModule;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.compiler.xml.processes.RuleFlowMigrator;
import org.jbpm.process.builder.MultiConditionalSequenceFlowNodeBuilder;
import org.jbpm.process.builder.ProcessBuildContext;
import org.jbpm.process.builder.ProcessNodeBuilder;
import org.jbpm.process.builder.ProcessNodeBuilderRegistry;
import org.jbpm.process.builder.dialect.ProcessDialect;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.exception.ActionExceptionHandler;
import org.jbpm.process.core.context.exception.ExceptionHandler;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.core.impl.ProcessImpl;
import org.jbpm.process.core.validation.ProcessValidationError;
import org.jbpm.process.core.validation.ProcessValidator;
import org.jbpm.process.instance.impl.ReturnValueConstraintEvaluator;
import org.jbpm.process.instance.impl.RuleConstraintEvaluator;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.validation.RuleFlowProcessValidator;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.impl.ConstraintImpl;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.core.node.ConstraintTrigger;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.StateNode;
import org.jbpm.workflow.core.node.Trigger;

/**
 * A ProcessBuilder can be used to build processes based on XML files
 * containing a process definition.
 */
public class ProcessBuilderImpl implements ProcessBuilder {

    private PackageBuilder                packageBuilder;
    private final List<DroolsError>       errors                         = new ArrayList<DroolsError>();
    private Map<String, ProcessValidator> processValidators              = new HashMap<String, ProcessValidator>();

    public ProcessBuilderImpl(PackageBuilder packageBuilder) {
        this.packageBuilder = packageBuilder;
        configurePackageBuilder(packageBuilder);
        this.processValidators.put( RuleFlowProcess.RULEFLOW_TYPE,
                                    RuleFlowProcessValidator.getInstance() );
    }
    
    public void configurePackageBuilder(PackageBuilder packageBuilder) {
        PackageBuilderConfiguration conf = packageBuilder.getPackageBuilderConfiguration();
        if (conf.getSemanticModules().getSemanticModule(ProcessSemanticModule.URI) == null) {
        	conf.addSemanticModule(new ProcessSemanticModule());
        }
    }

    public List<DroolsError> getErrors() {
        return errors;
    }

    public void buildProcess(final Process process, Resource resource) {
        if ( resource != null && ((InternalResource)resource).hasURL() ) {
            ((org.jbpm.process.core.Process) process).setResource( resource );
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
//            System.out.println(rules);
            try {
                packageBuilder.addPackageFromDrl( new StringReader( rules ), resource );
            } catch ( IOException e ) {
                // should never occur
                e.printStackTrace( System.err );
            } catch ( DroolsParserException e ) {
                // should never occur
                e.printStackTrace( System.err );
            }
            
            PackageRegistry pkgRegistry = this.packageBuilder.getPackageRegistry(process.getPackageName());
			if (pkgRegistry != null) {
				org.drools.rule.Package p = pkgRegistry.getPackage();
            
	            if (p != null) {
	            
		            ProcessDescr processDescr = new ProcessDescr();
		            processDescr.setName(process.getPackageName() + "." + process.getName());
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
	        } else {
				// invalid package registry..there is an issue with the package
				// name of the process
				throw new RuntimeDroolsException("invalid package name");
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
    			        ProcessDialect dialect = ProcessDialectRegistry.getDialect( action.getDialect() );            
    			        dialect.getActionBuilder().build( buildContext, action, actionDescr, (ProcessImpl) buildContext.getProcess() );
    				}
    			}
    		}
    	}
    }
    
    @SuppressWarnings("unchecked")
	public void buildNodes(WorkflowProcess process, ProcessBuildContext context) {
        processNodes(process.getNodes(), process, context.getProcessDescr(), context);
        if ( !context.getErrors().isEmpty() ) {
            this.errors.addAll( context.getErrors() );
        }
        ProcessDialectRegistry.getDialect(JavaDialect.ID).addProcess( context );
    }
    
    private void processNodes(
            Node[] nodes, Process process, ProcessDescr processDescr, 
            ProcessBuildContext context) {
        for ( Node node : nodes ) {
            ProcessNodeBuilder builder = ProcessNodeBuilderRegistry.INSTANCE.getNodeBuilder( node );
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
                              context );
            }
            if ( node instanceof ContextContainer ) {
                buildContexts( (ContextContainer) node,
                               context );
            }
            
            if (System.getProperty("jbpm.enable.multi.con") != null) {
            	builder = ProcessNodeBuilderRegistry.INSTANCE.getNodeBuilder( NodeImpl.class );
            	if (builder != null) {
            		builder.build(process, processDescr, context, node);
            	}
            }
        }
    }

    public List<DroolsError> addProcessFromXml(final Resource resource) throws IOException {
    	Reader reader = resource.getReader();
        PackageBuilderConfiguration configuration = packageBuilder.getPackageBuilderConfiguration();
        XmlProcessReader xmlReader = new XmlProcessReader( configuration.getSemanticModules(), packageBuilder.getRootClassLoader() );
        
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
            List<Process> processes = xmlReader.read(portedReader);
            if (processes != null) {
                // it is possible an xml file could not be parsed, so we need to
                // stop null pointers
                for (Process process : processes) {
                    buildProcess(process, resource);
                }
            } else {
                // @TODO could we maybe add something a bit more informative about what is wrong with the XML ?
                this.errors.add( new ProcessLoadError( resource, "unable to parse xml", null ) );
            }
        } catch ( FactoryConfigurationError e1 ) {
            this.errors.add( new ProcessLoadError( resource, "FactoryConfigurationError ", e1.getException()) );
        } catch ( Exception e2 ) {
        	e2.printStackTrace();
            this.errors.add( new ProcessLoadError( resource, "unable to parse xml", e2 ) );
        } finally {
            Thread.currentThread().setContextClassLoader( oldLoader );
        }
        reader.close();
        return this.errors;
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
                builder.append( createStateRules(process, state) );
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
            } else if ( nodes[i] instanceof EventNode ) {
                EventNode state = (EventNode) nodes[i];
                builder.append( createEventStateRule(process, state) );
            }
        }
    }

    private String createSplitRule(Process process,
                                   Connection connection,
                                   String constraint) {
        return 
        	"rule \"RuleFlow-Split-" + process.getId() + "-" +
        		((org.jbpm.workflow.core.Node) connection.getFrom()).getUniqueId() + "-" + 
        		((org.jbpm.workflow.core.Node) connection.getTo()).getUniqueId() + "-" +
        		connection.getToType() + "\" \n" +
        	"      ruleflow-group \"DROOLS_SYSTEM\" \n" + 
        	"    when \n" + 
        	"      " + constraint + "\n" + 
        	"    then \n" +
            "end \n\n";
    }

    private String createMilestoneRule(Process process,
                                       MilestoneNode milestone) {
        return 
        	"rule \"RuleFlow-Milestone-" + process.getId() + "-" + milestone.getUniqueId() + "\" \n" + 
        	"      ruleflow-group \"DROOLS_SYSTEM\" \n" + 
        	"    when \n" + 
        	"      " + milestone.getConstraint() + "\n" + 
        	"    then \n" + 
        	"end \n\n";
    }
    
    private String createStateRule(Process process, StateNode state, ConnectionRef key, Constraint constraint) {
    	if (constraint.getConstraint() == null
    			|| constraint.getConstraint().trim().length() == 0) {
    		return "";
    	} else {
	        return 
	        	"rule \"RuleFlowStateNode-" + process.getId() + "-" + state.getUniqueId() + "-" + 
	        		key.getNodeId() + "-" + key.getToType() + "\" \n" + 
	    		"      ruleflow-group \"DROOLS_SYSTEM\" \n" + 
	    		"    when \n" + 
	    		"      " + state.getConstraints().get(key).getConstraint() + "\n" + 
	    		"    then \n" + 
	    		"end \n\n";
    	}
	}
    
    private String createEventStateRule(Process process, EventNode event) {
        String condition = (String) event.getMetaData("Condition");
        String attachedTo = (String) event.getMetaData("AttachedTo");
        if (condition == null
                || condition.trim().length() == 0) {
            return "";
        } else {
            return 
                "rule \"RuleFlowStateEvent-" + process.getId() + "-" + event.getUniqueId() + "-" + 
                    attachedTo + "\" \n" + 
                "      ruleflow-group \"DROOLS_SYSTEM\" \n" + 
                "    when \n" + 
                "      " + condition + "\n" + 
                "    then \n" +
                "end \n\n";
        }
    }
    
    private String createStateRules(Process process, StateNode state) {
        String result = "";
        for (Map.Entry<ConnectionRef, Constraint> entry: state.getConstraints().entrySet()) {
    		result += createStateRule(process, state, entry.getKey(), entry.getValue());
        }
        return result;
    }

    private String createStartConstraintRule(Process process,
                                             ConstraintTrigger trigger) {
        String result = 
        	"rule \"RuleFlow-Start-" + process.getId() + "\" \n" + 
        	(trigger.getHeader() == null ? "" : "        " + trigger.getHeader() + " \n") + 
        	"    when\n" + 
        	"        " + trigger.getConstraint() + "\n" + 
        	"    then\n";
        Map<String, String> inMappings = trigger.getInMappings();
        if ( inMappings != null && !inMappings.isEmpty() ) {
            result += "        java.util.Map params = new java.util.HashMap();\n";
            for ( Map.Entry<String, String> entry : inMappings.entrySet() ) {
                result += "        params.put(\"" + entry.getKey() + "\", " + entry.getValue() + ");\n";
            }
            result += "        kcontext.getKnowledgeRuntime().startProcess(\"" + process.getId() + "\", params);\n" + "end\n\n";
        } else {
            result += "        kcontext.getKnowledgeRuntime().startProcess(\"" + process.getId() + "\");\n" + "end\n\n";
        }
        return result;
    }

}
