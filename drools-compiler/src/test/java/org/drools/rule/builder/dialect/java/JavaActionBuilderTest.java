package org.drools.rule.builder.dialect.java;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.PackageRegistry;
import org.drools.lang.descr.ActionDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.rule.Package;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.spi.Action;
import org.drools.spi.KnowledgeHelper;
import org.drools.workflow.core.DroolsAction;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.workflow.core.node.ActionNode;

public class JavaActionBuilderTest extends TestCase {

    public void setUp() {
    }
    
    public void testSimpleAction() throws Exception {
        final Package pkg = new Package( "pkg1" );

        ActionDescr actionDescr = new ActionDescr();
        actionDescr.setText( "list.add( \"hello world\" );" );       

        PackageBuilder pkgBuilder = new PackageBuilder( pkg );
        final PackageBuilderConfiguration conf = pkgBuilder.getPackageBuilderConfiguration();
        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        JavaDialect javaDialect = ( JavaDialect ) dialectRegistry.getDialect( "java" );

        ProcessDescr processDescr = new ProcessDescr();
        processDescr.setClassName( "Process1" );
        processDescr.setName( "Process1" );
        
        WorkflowProcessImpl process = new WorkflowProcessImpl();
        process.setName( "Process1" );
        process.setPackageName( "pkg1" );

        ProcessBuildContext context = new ProcessBuildContext(conf, pkgBuilder.getPackage(), null, processDescr, dialectRegistry, javaDialect);
        
        context.init( conf, pkg, null, dialectRegistry, javaDialect, null);
        
        pkgBuilder.addPackageFromDrl( new StringReader("package pkg1;\nglobal java.util.List list;\n") );        
        
        ActionNode actionNode = new ActionNode();
        DroolsAction action = new DroolsConsequenceAction("java", null);
        actionNode.setAction(action);
        
        javaDialect.getActionBuilder().build( context, action, actionDescr );
        javaDialect.addProcess( context );
        javaDialect.compileAll();            
        assertEquals( 0, javaDialect.getResults().size() );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkgBuilder.getPackage() );
        final WorkingMemory wm = ruleBase.newStatefulSession();

        List list = new  ArrayList();
        wm.setGlobal( "list", list );        
        
        KnowledgeHelper knowledgeHelper = new DefaultKnowledgeHelper();
        ((Action) actionNode.getAction().getMetaData("Action")).execute( knowledgeHelper, wm, null );
       
        assertEquals("hello world", list.get(0) );
    }    
    

}

