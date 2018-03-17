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
package org.jbpm.compiler;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.BaseKnowledgeBuilderResultImpl;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.compiler.DuplicateProcess;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.ParserError;
import org.drools.compiler.compiler.ProcessLoadError;
import org.drools.compiler.lang.descr.ActionDescr;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.drools.compiler.rule.builder.dialect.java.JavaDialect;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.jbpm.compiler.xml.ProcessSemanticModule;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.compiler.xml.processes.RuleFlowMigrator;
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
import org.jbpm.process.core.validation.ProcessValidatorRegistry;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.core.node.CompositeNode;
import org.jbpm.workflow.core.node.ConstraintTrigger;
import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.StateNode;
import org.jbpm.workflow.core.node.Trigger;
import org.kie.api.definition.process.Connection;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.FactoryConfigurationError;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A ProcessBuilder can be used to build processes based on XML files
 * containing a process definition.
 */
public class ProcessBuilderImpl implements org.drools.compiler.compiler.ProcessBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ProcessBuilderImpl.class);

    private KnowledgeBuilderImpl knowledgeBuilder;
    private final List<BaseKnowledgeBuilderResultImpl> errors = new ArrayList<BaseKnowledgeBuilderResultImpl>();

    public ProcessBuilderImpl(KnowledgeBuilderImpl packageBuilder) {
        this.knowledgeBuilder = packageBuilder;
        configurePackageBuilder(packageBuilder);
    }

    public void configurePackageBuilder(KnowledgeBuilder packageBuilder) {
        KnowledgeBuilderConfigurationImpl conf = ((KnowledgeBuilderImpl) packageBuilder).getBuilderConfiguration();
        if (conf.getSemanticModules().getSemanticModule(ProcessSemanticModule.URI) == null) {
        	conf.addSemanticModule(new ProcessSemanticModule());
        }
    }

    public List<BaseKnowledgeBuilderResultImpl> getErrors() {
        return errors;
    }

    public void buildProcess(final Process process, Resource resource) {
        if ( resource != null ) {
            ((org.jbpm.process.core.Process) process).setResource(resource);
        }
        boolean hasErrors = false;
        ProcessValidator validator = ProcessValidatorRegistry.getInstance().getValidator(process, resource);
        if (validator == null) {
            logger.warn("Could not find validator for process {}.", ((Process)process).getType());
            logger.warn("Continuing without validation of the process {} [{}]", process.getName(), process.getId());
        } else {
            ProcessValidationError[] errors = validator.validateProcess( (WorkflowProcess) process );
            if ( errors.length != 0 ) {
                hasErrors = true;
                for ( int i = 0; i < errors.length; i++ ) {
                    this.errors.add( new ParserError( resource,
                                                      errors[i].toString(),
                                                      -1,
                                                      -1 ) );
                }
            }
        }
        if ( !hasErrors ) {

            // generate and add rule for process
            String rules = "package " + process.getPackageName() + "\n";
            // NPE for validator
            if (validator != null && validator.compilationSupported()) {
            	rules = generateRules( process );
            }
            try {
                knowledgeBuilder.addPackageFromDrl( new StringReader( rules ), resource );
            } catch ( IOException e ) {
                // should never occur
                logger.error("IOException during addPackageFromDRL", e);
            } catch ( DroolsParserException e ) {
                // should never occur
                logger.error("DroolsParserException during addPackageFromDRL", e);
            }

            PackageRegistry pkgRegistry = this.knowledgeBuilder.getPackageRegistry(process.getPackageName());
			if (pkgRegistry != null) {
				InternalKnowledgePackage p = pkgRegistry.getPackage();

				if (p != null) {
				    if( validator != null ) {
				        // NPE for validator
				        if (validator.compilationSupported()) {
				            ProcessDescr processDescr = new ProcessDescr();
				            processDescr.setName(process.getPackageName() + "." + process.getName());
				            processDescr.setResource( resource );
				            processDescr.setProcessId( process.getId() );
				            DialectCompiletimeRegistry dialectRegistry = pkgRegistry.getDialectCompiletimeRegistry();
				            Dialect dialect = dialectRegistry.getDialect( "java" );
				            dialect.init(processDescr);

				            ProcessBuildContext buildContext = new ProcessBuildContext(
				                    this.knowledgeBuilder,
				                    p,
				                    process,
				                    processDescr,
				                    dialectRegistry,
				                    dialect);

				            buildContexts( ( ContextContainer ) process, buildContext );
				            if (process instanceof WorkflowProcess) {
				                buildNodes( (WorkflowProcess) process, buildContext );
				            }
				        }
				        Process duplicateProcess = p.getRuleFlows().get(process.getId());
				        if (duplicateProcess != null) {
				            Resource duplicatedResource = duplicateProcess.getResource();
				            if (resource == null || duplicatedResource == null || duplicatedResource.getSourcePath() == null ||
				                    duplicatedResource.getSourcePath().equals(resource.getSourcePath())) {
				                this.errors.add(new DuplicateProcess(process,
				                        this.knowledgeBuilder.getBuilderConfiguration()));
				            } else {
				                this.errors.add( new ParserError( resource,
				                        "Process with same id already exists: " + process.getId(),
				                        -1,
				                        -1 ) );
				            }
				        }
				        p.addProcess( process );
				        // NPE for validator
				        if (validator.compilationSupported()) {
				            pkgRegistry.compileAll();
				            pkgRegistry.getDialectRuntimeRegistry().onBeforeExecute();
				        }
				    }
				}
	        } else {
				// invalid package registry..there is an issue with the package
				// name of the process
				throw new RuntimeException("invalid package name");
			}
        }
    }

    public void buildContexts(ContextContainer contextContainer, ProcessBuildContext buildContext) {
    	List<Context> exceptionScopes = contextContainer.getContexts(ExceptionScope.EXCEPTION_SCOPE);
    	if (exceptionScopes != null) {
    		for (Context context: exceptionScopes) {
    		    // TODO: OCRAM: add compensation scope to process builder????
    			ExceptionScope exceptionScope = (ExceptionScope) context;
    			for (ExceptionHandler exceptionHandler: exceptionScope.getExceptionHandlers().values()) {
    				if (exceptionHandler instanceof ActionExceptionHandler) {
    					DroolsConsequenceAction action = (DroolsConsequenceAction)
    						((ActionExceptionHandler) exceptionHandler).getAction();
    					ActionDescr actionDescr = new ActionDescr();
    			        actionDescr.setText( action.getConsequence() );
    			        actionDescr.setResource(buildContext.getProcessDescr().getResource());

    			        ProcessDialect dialect = ProcessDialectRegistry.getDialect( action.getDialect() );
    			        dialect.getActionBuilder().build( buildContext, action, actionDescr, (ProcessImpl) buildContext.getProcess() );
    				}
    			}
    		}
    	}
    }

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

            if ("true".equals(System.getProperty("jbpm.enable.multi.con"))) {
            	builder = ProcessNodeBuilderRegistry.INSTANCE.getNodeBuilder( NodeImpl.class );
            	if (builder != null) {
            		builder.build(process, processDescr, context, node);
            	}
            }
        }
    }

    public List<Process> addProcessFromXml(final Resource resource) throws IOException {
    	Reader reader = resource.getReader();
        KnowledgeBuilderConfigurationImpl configuration = knowledgeBuilder.getBuilderConfiguration();
        XmlProcessReader xmlReader = new XmlProcessReader( configuration.getSemanticModules(), knowledgeBuilder.getRootClassLoader() );

        List<Process> processes = null;

        try {
            String portRuleFlow = System.getProperty( "drools.ruleflow.port", "false" );
            Reader portedReader = null;
            if ( portRuleFlow.equalsIgnoreCase( "true" ) ) {
                portedReader = portToCurrentVersion( reader );
            } else {
                portedReader = reader;
            }
            processes = xmlReader.read(portedReader);
            if (processes != null) {
                // it is possible an xml file could not be parsed, so we need to
                // stop null pointers
                for (Process process : processes) {
                    buildProcess(process, resource);

                    xmlReader.getProcessBuildData().onBuildComplete(process);
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
            reader.close();
        }

        return processes;
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
            Set<String> imports = ruleFlow.getImports();
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
                        if ( constraint != null && "rule".equals( constraint.getType() ) ) {
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
                            builder.append( createStartConstraintRule( process, startNode.getNodeContainer(),
                                                                       (ConstraintTrigger) trigger ) );
                        }
                    }
                }
            } else if ( nodes[i] instanceof NodeContainer ) {
                generateRules( ((NodeContainer) nodes[i]).getNodes(), process, builder);
                if ( nodes[i] instanceof DynamicNode && "rule".equals(((DynamicNode) nodes[i]).getLanguage())) {
                    DynamicNode dynamicNode = (DynamicNode) nodes[i];
                    if (dynamicNode.getCompletionExpression() != null) {
                        builder.append( createAdHocCompletionRule( process, dynamicNode ) );
                    }
                    if (dynamicNode.getActivationExpression() != null && !dynamicNode.getActivationExpression().isEmpty()) {
                        builder.append( createAdHocActivationRule( process, dynamicNode ) );
                    }
                }
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
        		connection.getToType() + "\"  @Propagation(EAGER) \n" +
        	"      ruleflow-group \"DROOLS_SYSTEM\" \n" +
        	"    when \n" +
        	"      " + constraint + "\n" +
        	"    then \n" +
            "end \n\n";
    }

    private String createMilestoneRule(Process process,
                                       MilestoneNode milestone) {
        return
        	"rule \"RuleFlow-Milestone-" + process.getId() + "-" + milestone.getUniqueId() + "\" @Propagation(EAGER) \n" +
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
	        		key.getNodeId() + "-" + key.getToType() + "\" @Propagation(EAGER) \n" +
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
                    attachedTo + "\" @Propagation(EAGER) \n" +
                "      ruleflow-group \"DROOLS_SYSTEM\" \n" +
                "    when \n" +
                "      " + condition + "\n" +
                "    then \n" +
                "end \n\n";
        }
    }

    private String createEventSubprocessStateRule(Process process, CompositeNode compositeNode,
            ConstraintTrigger trigger) {
        String condition = trigger.getConstraint();
        if (condition == null
                || condition.trim().length() == 0) {
            return "";
        } else {
            return
                "rule \"RuleFlowStateEventSubProcess-Event-" + process.getId() + "-" + compositeNode.getUniqueId() + "\" @Propagation(EAGER) \n" +
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

    private String createStartConstraintRule(Process process, NodeContainer nodeContainer,
                                             ConstraintTrigger trigger) {
        if (nodeContainer instanceof EventSubProcessNode) {
            return createEventSubprocessStateRule(process, (EventSubProcessNode) nodeContainer, trigger);
        }

        String result =
        	"rule \"RuleFlow-Start-" + process.getId() + "\" @Propagation(EAGER) \n" +
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
            result += "        ((org.jbpm.process.instance.ProcessRuntimeImpl)((org.drools.core.common.InternalWorkingMemory)kcontext.getKnowledgeRuntime()).getProcessRuntime()).startProcess(\"" + process.getId() + "\", params, \"conditional\");\n" + "end\n\n";
        } else {
            result += "        ((org.jbpm.process.instance.ProcessRuntimeImpl)((org.drools.core.common.InternalWorkingMemory)kcontext.getKnowledgeRuntime()).getProcessRuntime()).startProcess(\"" + process.getId() + "\", null, \"conditional\");\n" + "end\n\n";
        }
        return result;
    }
    
    private String createAdHocCompletionRule(Process process, DynamicNode dynamicNode) {
        return
        "rule \"RuleFlow-AdHocComplete-" + process.getId() + "-" + dynamicNode.getUniqueId() + "\" @Propagation(EAGER) \n" +
        "      ruleflow-group \"DROOLS_SYSTEM\" \n" +
        "    when \n" +
        "      " + dynamicNode.getCompletionExpression() + "\n" +
        "    then \n" +
        "end \n\n";
    }
    
    private String createAdHocActivationRule(Process process, DynamicNode dynamicNode) {
        return
        "rule \"RuleFlow-AdHocActivate-" + process.getId() + "-" + dynamicNode.getUniqueId() + "\" @Propagation(EAGER) \n" +
        "      ruleflow-group \"DROOLS_SYSTEM\" \n" +
        "    when \n" +
        "      " + dynamicNode.getActivationExpression() + "\n" +
        "    then \n" +
        "end \n\n";
    }

}
