package org.jbpm.process.builder;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.drools.common.InternalKnowledgeRuntime;
import org.drools.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.ReturnValueDescr;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.lang.descr.ProcessDescr;
import org.drools.rule.Package;
import org.drools.rule.builder.dialect.java.JavaDialect;
import org.jbpm.JbpmTestCase;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.jbpm.process.builder.dialect.java.JavaReturnValueEvaluatorBuilder;
import org.jbpm.process.instance.impl.ReturnValueConstraintEvaluator;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.instance.node.SplitInstance;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.definition.KnowledgePackage;
import org.kie.runtime.StatefulKnowledgeSession;

public class JavaReturnValueConstraintEvaluatorBuilderTest extends JbpmTestCase {

    public void testSimpleReturnValueConstraintEvaluator() throws Exception {
        final Package pkg = new Package( "pkg1" );

        ProcessDescr processDescr = new ProcessDescr();
        processDescr.setClassName( "Process1" );
        processDescr.setName( "Process1" );
        
        WorkflowProcessImpl process = new WorkflowProcessImpl();
        process.setName( "Process1" );
        process.setPackageName( "pkg1" );

        ReturnValueDescr descr = new ReturnValueDescr();
        descr.setText( "return value;" );

        PackageBuilder pkgBuilder = new PackageBuilder( pkg );
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

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        List<KnowledgePackage> packages = new ArrayList<KnowledgePackage>();
        packages.add( new KnowledgePackageImp(pkgBuilder.getPackage()) );
        kbase.addKnowledgePackages( packages );
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

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
