package org.drools.rule.builder.dialect.mvel;

import java.io.StringReader;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.base.mvel.MVELReturnValueEvaluator;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.ReturnValueDescr;
import org.drools.rule.Package;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;
import org.drools.workflow.instance.impl.ReturnValueConstraintEvaluator;
import org.drools.workflow.instance.node.SplitInstance;

public class MVELReturnValueConstraintEvaluatorBuilderTest extends TestCase {

    public void setUp() {
    }

    public void testSimpleReturnValueConstraintEvaluator() throws Exception {
        final Package pkg = new Package( "pkg1" );

        ReturnValueDescr descr = new ReturnValueDescr();
        descr.setText( "return value" );

        PackageBuilder pkgBuilder = new PackageBuilder( pkg );
        final PackageBuilderConfiguration conf = pkgBuilder.getPackageBuilderConfiguration();
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

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkgBuilder.getPackage() );
        final InternalWorkingMemory wm = (InternalWorkingMemory) ruleBase.newStatefulSession();

        wm.setGlobal( "value", true );        

        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
        processInstance.setWorkingMemory( wm );

        SplitInstance splitInstance = new SplitInstance();
        splitInstance.setProcessInstance( processInstance );
        
        ( (MVELReturnValueEvaluator) node.getReturnValueEvaluator()).compile( Thread.currentThread().getContextClassLoader() );

        assertTrue( node.evaluate( splitInstance,
                                   null,
                                   null ) );
        
        wm.setGlobal( "value", false );     
        
        assertFalse( node.evaluate( splitInstance,
                                   null,
                                   null ) );        
    }

}
