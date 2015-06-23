/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.process.builder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.ReturnValueDescr;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.jbpm.process.builder.dialect.mvel.MVELReturnValueEvaluatorBuilder;
import org.jbpm.process.instance.impl.MVELReturnValueEvaluator;
import org.jbpm.process.instance.impl.ReturnValueConstraintEvaluator;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.instance.node.SplitInstance;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class MVELReturnValueConstraintEvaluatorBuilderTest extends AbstractBaseTest {

    @Test
    public void testSimpleReturnValueConstraintEvaluator() throws Exception {
        final InternalKnowledgePackage pkg = new KnowledgePackageImpl( "pkg1" );

        ReturnValueDescr descr = new ReturnValueDescr();
        descr.setText( "return value" );

        KnowledgeBuilderImpl pkgBuilder = new KnowledgeBuilderImpl( pkg );
        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        MVELDialect mvelDialect = (MVELDialect) dialectRegistry.getDialect( "mvel" );

        PackageBuildContext context = new PackageBuildContext();
        context.init( pkgBuilder,
                      pkg,
                      null,
                      dialectRegistry,
                      mvelDialect,
                      null );

        pkgBuilder.addPackageFromDrl( new StringReader( "package pkg1;\nglobal Boolean value;" ) );

        ReturnValueConstraintEvaluator node = new ReturnValueConstraintEvaluator();

        final MVELReturnValueEvaluatorBuilder builder = new MVELReturnValueEvaluatorBuilder();
        builder.build( context,
                       node,
                       descr,
                       null );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        List<KnowledgePackage> packages = new ArrayList<KnowledgePackage>();
        packages.add( pkgBuilder.getPackage() );
        kbase.addKnowledgePackages( packages );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.setGlobal( "value", true );        

        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
        processInstance.setKnowledgeRuntime( (InternalKnowledgeRuntime) ksession );

        SplitInstance splitInstance = new SplitInstance();
        splitInstance.setProcessInstance( processInstance );
        
        MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkgBuilder.getPackage().getDialectRuntimeRegistry().getDialectData( "mvel");        
        
        ( (MVELReturnValueEvaluator) node.getReturnValueEvaluator()).compile( data );

        assertTrue( node.evaluate( splitInstance,
                                   null,
                                   null ) );
        
        ksession.setGlobal( "value", false );     
        
        assertFalse( node.evaluate( splitInstance,
                                   null,
                                   null ) );        
    }

}
