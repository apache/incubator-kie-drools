package org.jbpm.process.builder;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.compiler.lang.descr.ActionDescr;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.drools.rule.Package;
import org.drools.compiler.rule.builder.dialect.java.JavaDialect;
import org.drools.core.spi.ProcessContext;
import org.jbpm.process.builder.dialect.ProcessDialect;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.core.node.ActionNode;

public class JavaActionBuilderTest extends TestCase {

    public void testSimpleAction() throws Exception {
        final Package pkg = new Package( "pkg1" );

        ActionDescr actionDescr = new ActionDescr();
        actionDescr.setText( "list.add( \"hello world\" );" );       

        PackageBuilder pkgBuilder = new PackageBuilder( pkg );
        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        JavaDialect javaDialect = ( JavaDialect ) dialectRegistry.getDialect( "java" );

        ProcessDescr processDescr = new ProcessDescr();
        processDescr.setClassName( "Process1" );
        processDescr.setName( "Process1" );
        
        WorkflowProcessImpl process = new WorkflowProcessImpl();
        process.setName( "Process1" );
        process.setPackageName( "pkg1" );

        ProcessBuildContext context = new ProcessBuildContext(pkgBuilder, pkgBuilder.getPackage(), null, processDescr, dialectRegistry, javaDialect);
        
        context.init( pkgBuilder, pkg, null, dialectRegistry, javaDialect, null);
        
        pkgBuilder.addPackageFromDrl( new StringReader("package pkg1;\nglobal java.util.List list;\n") );        
        
        ActionNode actionNode = new ActionNode();
        DroolsAction action = new DroolsConsequenceAction("java", null);
        actionNode.setAction(action);
        
        ProcessDialect dialect = ProcessDialectRegistry.getDialect( "java" );            
        dialect.getActionBuilder().build( context, action, actionDescr, actionNode );
        dialect.addProcess( context );
        javaDialect.compileAll();            
        assertEquals( 0, javaDialect.getResults().size() );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkgBuilder.getPackage() );
        final WorkingMemory wm = ruleBase.newStatefulSession();

        List<String> list = new ArrayList<String>();
        wm.setGlobal( "list", list );        
        
        ProcessContext processContext = new ProcessContext( ((InternalWorkingMemory) wm).getKnowledgeRuntime() );
        ((Action) actionNode.getAction().getMetaData("Action")).execute( processContext );
       
        assertEquals("hello world", list.get(0) );
    }    
    

}

