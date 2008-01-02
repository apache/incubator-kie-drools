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
import java.util.Map.Entry;

import org.drools.lang.descr.ActionDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.process.builder.ActionNodeBuilder;
import org.drools.process.builder.ProcessNodeBuilder;
import org.drools.process.builder.ProcessNodeBuilderRegistry;
import org.drools.process.builder.SplitNodeBuilder;
import org.drools.rule.Package;
import org.drools.rule.ReturnValueConstraint;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.ruleflow.common.core.Process;
import org.drools.ruleflow.core.ActionNode;
import org.drools.ruleflow.core.Connection;
import org.drools.ruleflow.core.Constraint;
import org.drools.ruleflow.core.MilestoneNode;
import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.ruleflow.core.RuleFlowProcessValidationError;
import org.drools.ruleflow.core.RuleFlowProcessValidator;
import org.drools.ruleflow.core.Split;
import org.drools.ruleflow.core.impl.ActionNodeImpl;
import org.drools.ruleflow.core.impl.ConstraintImpl;
import org.drools.ruleflow.core.impl.DroolsConsequenceAction;
import org.drools.ruleflow.core.impl.ReturnValueConstraintEvaluator;
import org.drools.ruleflow.core.impl.RuleFlowConstraintEvaluator;
import org.drools.ruleflow.core.impl.RuleFlowProcessImpl;
import org.drools.ruleflow.core.impl.RuleFlowProcessValidatorImpl;
import org.drools.ruleflow.core.impl.SplitImpl;
import org.drools.spi.ReturnValueEvaluator;

import com.thoughtworks.xstream.XStream;

/**
 * A ProcessBuilder can be used to build processes based on XML files
 * containing a process definition.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class ProcessBuilder {

    private PackageBuilder packageBuilder;
    private final List     errors = new ArrayList();

    public ProcessBuilder(PackageBuilder packageBuilder) {
        this.packageBuilder = packageBuilder;
    }

    public List getErrors() {
        return errors;
    }

    public void buildProcess(final Process process) {
        if ( process instanceof RuleFlowProcess ) {
            RuleFlowProcessValidator validator = RuleFlowProcessValidatorImpl.getInstance();
            RuleFlowProcessValidationError[] errors = validator.validateProcess( (RuleFlowProcess) process );
            if ( errors.length != 0 ) {
                for ( int i = 0; i < errors.length; i++ ) {
                    this.errors.add( new ParserError( errors[i].toString(),
                                                      -1,
                                                      -1 ) );
                }
            } else {
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
                this.packageBuilder.getPackage().addRuleFlow( process );

                Package pkg = this.packageBuilder.getPackage();
                if ( pkg != null ) {
                    // we can only do this is this.pkg != null, as otherwise the dialects won't be properly initialised
                    // as the dialects are initialised when the pkg is  first created
                    this.packageBuilder.getDialectRegistry().compileAll();

                    // some of the rules and functions may have been redefined
                    if ( pkg.getPackageCompilationData().isDirty() ) {
                        pkg.getPackageCompilationData().reload();
                    }
                }
            }
        }
    }

    

    public void buildNodes(Process process) {
        ProcessNodeBuilderRegistry nodeBuilderRegistry = packageBuilder.getPackageBuilderConfiguration().getProcessNodeBuilderRegistry();

        RuleFlowProcess rfp = (RuleFlowProcess) process;

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

        for ( Node node : rfp.getNodes() ) {
            ProcessNodeBuilder builder = nodeBuilderRegistry.getNodeBuilder( node );
            if ( builder != null ) {
                // only build if there is a registered builder for this node type
                builder.build( process,
                               processDescr,
                               context,
                               node );
            }
        }

        if ( !context.getErrors().isEmpty() ) {
            this.errors.addAll( context.getErrors() );
        }

        for ( Iterator it = this.packageBuilder.getDialectRegistry().iterator(); it.hasNext(); ) {
            dialect = (Dialect) it.next();
            dialect.addProcess( context );
        }

    }

    public void addProcessFromFile(final Reader reader) throws Exception {
        final XStream stream = new XStream();
        stream.setMode( XStream.ID_REFERENCES );
        final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        final ClassLoader newLoader = this.getClass().getClassLoader();
        try {
            Thread.currentThread().setContextClassLoader( newLoader );
            final RuleFlowProcess process = (RuleFlowProcess) stream.fromXML( reader );
            buildProcess( process );
        } finally {
            Thread.currentThread().setContextClassLoader( oldLoader );
        }
        reader.close();
    }

    private String generateRules(final Process process) {
        StringBuilder builder = new StringBuilder();

        if ( process instanceof RuleFlowProcessImpl ) {
            RuleFlowProcessImpl ruleFlow = (RuleFlowProcessImpl) process;
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
                        for ( Iterator iterator = split.getOutgoingConnections().iterator(); iterator.hasNext(); ) {
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
