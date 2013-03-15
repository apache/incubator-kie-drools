package org.jbpm.process.builder;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemory;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.compiler.lang.descr.ActionDescr;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.drools.spi.ProcessContext;
import org.jbpm.process.builder.dialect.mvel.MVELActionBuilder;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.process.instance.impl.MVELAction;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;

public class MVELActionBuilderTest extends TestCase {


    public void testSimpleAction() throws Exception {
        final Package pkg = new Package( "pkg1" );

        ActionDescr actionDescr = new ActionDescr();
        actionDescr.setText( "list.add( 'hello world' )" );       

        PackageBuilder pkgBuilder = new PackageBuilder( pkg );
        DialectCompiletimeRegistry dialectRegistry = pkgBuilder.getPackageRegistry( pkg.getName() ).getDialectCompiletimeRegistry();
        MVELDialect mvelDialect = ( MVELDialect ) dialectRegistry.getDialect( "mvel" );

        PackageBuildContext context = new PackageBuildContext();
        context.init( pkgBuilder, pkg, null, dialectRegistry, mvelDialect, null);
        
        pkgBuilder.addPackageFromDrl( new StringReader("package pkg1;\nglobal java.util.List list;\n") );        
        
        ActionNode actionNode = new ActionNode();
        DroolsAction action = new DroolsConsequenceAction("mvel", null);
        actionNode.setAction(action);
        
        final MVELActionBuilder builder = new MVELActionBuilder();
        builder.build( context,
                       action,
                       actionDescr,
                       actionNode );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkgBuilder.getPackage() );
        final WorkingMemory wm = ruleBase.newStatefulSession();

        List<String> list = new  ArrayList<String>();
        wm.setGlobal( "list", list );     
        
        MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkgBuilder.getPackage().getDialectRuntimeRegistry().getDialectData( "mvel");
        
        ((MVELAction) actionNode.getAction().getMetaData("Action")).compile( data );
        
        ProcessContext processContext = new ProcessContext( ((InternalWorkingMemory) wm).getKnowledgeRuntime() );
        ((Action) actionNode.getAction().getMetaData("Action")).execute( processContext );
        
        assertEquals("hello world", list.get(0) );
    }    

}

