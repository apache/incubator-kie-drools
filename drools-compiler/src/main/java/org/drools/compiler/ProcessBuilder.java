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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.lang.descr.ProcessDescr;
import org.drools.process.builder.ProcessNodeBuilder;
import org.drools.process.builder.ProcessNodeBuilderRegistry;
import org.drools.process.core.Process;
import org.drools.process.core.validation.ProcessValidationError;
import org.drools.process.core.validation.ProcessValidator;
import org.drools.rule.Package;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.ruleflow.core.validation.RuleFlowProcessValidator;
import org.drools.workflow.core.Connection;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.WorkflowProcess;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.workflow.core.node.MilestoneNode;
import org.drools.workflow.core.node.Split;
import org.drools.xml.XmlProcessReader;

/**
 * A ProcessBuilder can be used to build processes based on XML files
 * containing a process definition.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class ProcessBuilder {

    private PackageBuilder packageBuilder;
    private final List     errors = new ArrayList();
    private Map<String, ProcessValidator> processValidators = new HashMap<String, ProcessValidator>();;

    public ProcessBuilder(PackageBuilder packageBuilder) {
        this.packageBuilder = packageBuilder;
        this.processValidators.put(RuleFlowProcess.RULEFLOW_TYPE, RuleFlowProcessValidator.getInstance());
    }

    public List getErrors() {
        return errors;
    }

    public void buildProcess(final Process process) {
        boolean hasErrors = false;
        ProcessValidator validator = processValidators.get(process.getType());
        if (validator == null) {
            System.out.println("Could not find validator for process " + process.getType() + ".");
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
            buildNodes( process );
            this.packageBuilder.getPackage().addProcess( process );

            Package pkg = this.packageBuilder.getPackage();
            if ( pkg != null ) {
                // we can only do this is this.pkg != null, as otherwise the dialects won't be properly initialised
                // as the dialects are initialised when the pkg is  first created
                this.packageBuilder.getDialectRegistry().compileAll();
                
                pkg.getDialectDatas().reloadDirty();
            }
        }
    }

    

    public void buildNodes(Process process) {
        ProcessNodeBuilderRegistry nodeBuilderRegistry = packageBuilder.getPackageBuilderConfiguration().getProcessNodeBuilderRegistry();

        WorkflowProcess rfp = (WorkflowProcess) process;

        ProcessDescr processDescr = new ProcessDescr();
        processDescr.setName( rfp.getPackageName() );

        Dialect dialect = this.packageBuilder.getDialectRegistry().getDialect( "java" );
        dialect.init( processDescr );

        ProcessBuildContext context = new ProcessBuildContext( this.packageBuilder.getPackageBuilderConfiguration(),
                                                               this.packageBuilder.getPackage(),
                                                               process,
                                                               processDescr,
                                                               this.packageBuilder.getDialectRegistry(),
                                                               dialect );

        processNodes(rfp.getNodes(), process, processDescr, context, nodeBuilderRegistry);

        if ( !context.getErrors().isEmpty() ) {
            this.errors.addAll( context.getErrors() );
        }

        for ( Iterator it = this.packageBuilder.getDialectRegistry().iterator(); it.hasNext(); ) {
            dialect = (Dialect) it.next();
            dialect.addProcess( context );
        }

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
            if (node instanceof NodeContainer) {
                processNodes(((NodeContainer) node).getNodes(), process, processDescr, context, nodeBuilderRegistry);
            }
        }
    }

    public void addProcessFromFile(final Reader reader) throws Exception {
        PackageBuilderConfiguration configuration = new PackageBuilderConfiguration();
        XmlProcessReader xmlReader = new XmlProcessReader( configuration.getSemanticModules() );
        
        final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        final ClassLoader newLoader = this.getClass().getClassLoader();
        try {
            Thread.currentThread().setContextClassLoader( newLoader );
            Process process = xmlReader.read(reader);
            buildProcess( process );
        } finally {
            Thread.currentThread().setContextClassLoader( oldLoader );
        }
        reader.close();
    }

    private String generateRules(final Process process) {
        StringBuilder builder = new StringBuilder();

        if ( process instanceof WorkflowProcessImpl ) {
            WorkflowProcessImpl ruleFlow = (WorkflowProcessImpl) process;
            builder.append( "package " + ruleFlow.getPackageName() + "\n" );
            List imports = ruleFlow.getImports();
            if ( imports != null ) {
                for ( Iterator iterator = imports.iterator(); iterator.hasNext(); ) {
                    builder.append( "import " + iterator.next() + ";\n" );
                }
            }
            Map globals = ruleFlow.getGlobals();
            if ( globals != null ) {
                for ( Iterator iterator = globals.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    builder.append( "global " + entry.getValue() + " " + entry.getKey() + ";\n" );
                }
            }

            Node[] nodes = ruleFlow.getNodes();
            for ( int i = 0; i < nodes.length; i++ ) {
                if ( nodes[i] instanceof Split ) {
                    Split split = (Split) nodes[i];
                    if ( split.getType() == Split.TYPE_XOR || split.getType() == Split.TYPE_OR ) {
                        for ( Iterator iterator = split.getDefaultOutgoingConnections().iterator(); iterator.hasNext(); ) {
                            Connection connection = (Connection) iterator.next();
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
                }
            }
        }
        return builder.toString();
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
}
