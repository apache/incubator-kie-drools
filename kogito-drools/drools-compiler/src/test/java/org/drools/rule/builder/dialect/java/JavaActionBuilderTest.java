package org.drools.rule.builder.dialect.java;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.lang.descr.ActionDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.rule.Package;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.spi.Action;
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
        JavaDialect javaDialect = ( JavaDialect ) pkgBuilder.getDialectRegistry().getDialect( "java" );

        ProcessDescr processDescr = new ProcessDescr();
        processDescr.setClassName( "Process1" );
        processDescr.setName( "Process1" );
        
        WorkflowProcessImpl process = new WorkflowProcessImpl();
        process.setName( "Process1" );
        process.setPackageName( "pkg1" );

        ProcessBuildContext context = new ProcessBuildContext(conf, pkgBuilder.getPackage(), null, processDescr, pkgBuilder.getDialectRegistry(), javaDialect);
        
        context.init( conf, pkg, null, pkgBuilder.getDialectRegistry(), javaDialect, null);
        
        pkgBuilder.addPackageFromDrl( new StringReader("package pkg1;\nglobal java.util.List list;\n") );        
        
        ActionNode actionNode = new ActionNode();
        
        javaDialect.getActionBuilder().build( context, actionNode, actionDescr );
        javaDialect.addProcess( context );
        javaDialect.compileAll();            
        assertEquals( 0, javaDialect.getResults().size() );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkgBuilder.getPackage() );
        final WorkingMemory wm = ruleBase.newStatefulSession();

        List list = new  ArrayList();
        wm.setGlobal( "list", list );        
        
        ((Action)actionNode.getAction()).execute( wm );
       
        assertEquals("hello world", list.get(0) );
    }    
    

}

