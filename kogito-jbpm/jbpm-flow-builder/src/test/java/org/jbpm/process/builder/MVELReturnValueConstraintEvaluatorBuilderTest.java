package org.jbpm.process.builder;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.drools.common.InternalKnowledgeRuntime;
import org.drools.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.ReturnValueDescr;
import org.kie.definition.KnowledgePackage;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
import org.kie.runtime.StatefulKnowledgeSession;
import org.jbpm.JbpmTestCase;
import org.jbpm.process.builder.dialect.mvel.MVELReturnValueEvaluatorBuilder;
import org.jbpm.process.instance.impl.MVELReturnValueEvaluator;
import org.jbpm.process.instance.impl.ReturnValueConstraintEvaluator;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.instance.node.SplitInstance;

public class MVELReturnValueConstraintEvaluatorBuilderTest extends JbpmTestCase {

    public void setUp() {
    }

    public void testSimpleReturnValueConstraintEvaluator() throws Exception {
        final Package pkg = new Package( "pkg1" );

        ReturnValueDescr descr = new ReturnValueDescr();
        descr.setText( "return value" );

        PackageBuilder pkgBuilder = new PackageBuilder( pkg );
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
        packages.add( new KnowledgePackageImp(pkgBuilder.getPackage()) );
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
