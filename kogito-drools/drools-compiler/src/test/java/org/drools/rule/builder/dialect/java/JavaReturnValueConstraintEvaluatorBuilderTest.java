package org.drools.rule.builder.dialect.java;

import java.io.StringReader;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.ReturnValueDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.rule.Package;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.workflow.instance.impl.ReturnValueConstraintEvaluator;
import org.drools.workflow.instance.node.SplitInstance;

public class JavaReturnValueConstraintEvaluatorBuilderTest extends TestCase {

    public void setUp() {
    }

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
        final PackageBuilderConfiguration conf = pkgBuilder.getPackageBuilderConfiguration();
        JavaDialect javaDialect = (JavaDialect) pkgBuilder.getDialectRegistry().getDialect( "java" );

        ProcessBuildContext context = new ProcessBuildContext( conf,
                                                               pkg,
                                                               process,
                                                               processDescr,
                                                               pkgBuilder.getDialectRegistry(),
                                                               javaDialect );

        pkgBuilder.addPackageFromDrl( new StringReader( "package pkg1;\nglobal Boolean value;" ) );

        ReturnValueConstraintEvaluator node = new ReturnValueConstraintEvaluator();

        final JavaReturnValueEvaluatorBuilder builder = new JavaReturnValueEvaluatorBuilder();
        builder.build( context,
                       node,
                       descr );

        javaDialect.addProcess( context );
        javaDialect.compileAll();
        assertEquals( 0, javaDialect.getResults().size() );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkgBuilder.getPackage() );
        final InternalWorkingMemory wm = (InternalWorkingMemory) ruleBase.newStatefulSession();

        wm.setGlobal( "value",
                      true );

        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
        processInstance.setWorkingMemory( wm );

        SplitInstance splitInstance = new SplitInstance();
        splitInstance.setProcessInstance( processInstance );

        assertTrue( node.evaluate( splitInstance,
                                   null,
                                   null ) );

        wm.setGlobal( "value",
                      false );

        assertFalse( node.evaluate( splitInstance,
                                    null,
                                    null ) );
    }

}
