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

package org.jbpm.process.builder;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.ReturnValueDescr;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.drools.compiler.rule.builder.dialect.java.JavaDialect;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.jbpm.process.builder.dialect.java.JavaReturnValueEvaluatorBuilder;
import org.jbpm.process.instance.impl.ReturnValueConstraintEvaluator;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.instance.node.SplitInstance;
import org.junit.Test;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieSession;

public class JavaReturnValueConstraintEvaluatorBuilderTest extends AbstractBaseTest {

    @Test
    public void testSimpleReturnValueConstraintEvaluator() throws Exception {
        final InternalKnowledgePackage pkg = new KnowledgePackageImpl( "pkg1" );

        ProcessDescr processDescr = new ProcessDescr();
        processDescr.setClassName( "Process1" );
        processDescr.setName( "Process1" );
        
        WorkflowProcessImpl process = new WorkflowProcessImpl();
        process.setName( "Process1" );
        process.setPackageName( "pkg1" );

        ReturnValueDescr descr = new ReturnValueDescr();
        descr.setText( "return value;" );

        KnowledgeBuilderImpl pkgBuilder = new KnowledgeBuilderImpl( pkg );
        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        JavaDialect javaDialect = (JavaDialect) dialectRegistry.getDialect( "java" );

        ProcessBuildContext context = new ProcessBuildContext( pkgBuilder,
                                                               pkg,
                                                               process,
                                                               processDescr,
                                                               dialectRegistry,
                                                               javaDialect );

        pkgBuilder.addPackageFromDrl( new StringReader( "package pkg1;\nglobal Boolean value;" ) );

        ReturnValueConstraintEvaluator node = new ReturnValueConstraintEvaluator();

        final JavaReturnValueEvaluatorBuilder builder = new JavaReturnValueEvaluatorBuilder();
        builder.build( context,
                       node,
                       descr,
                       null );

        ProcessDialectRegistry.getDialect(JavaDialect.ID).addProcess( context );
        javaDialect.compileAll();
        assertEquals( 0, javaDialect.getResults().size() );

        final InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( Arrays.asList(pkgBuilder.getPackages()) );
        final KieSession ksession = kbase.newKieSession();

        ksession.setGlobal( "value", true );

        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
        processInstance.setKnowledgeRuntime( (InternalKnowledgeRuntime) ksession );

        SplitInstance splitInstance = new SplitInstance();
        splitInstance.setProcessInstance( processInstance );

        assertTrue( node.evaluate( splitInstance,
                                   null,
                                   null ) );

        ksession.setGlobal( "value",
                      false );

        assertFalse( node.evaluate( splitInstance,
                                    null,
                                    null ) );
    }

}
