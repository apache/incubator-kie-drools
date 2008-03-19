package org.drools.rule.builder.dialect.mvel;

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
import org.drools.rule.Package;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.spi.Action;
import org.drools.workflow.core.node.ActionNode;

public class MVELDecisionBuilderTest extends TestCase {

    public void setUp() {
    }
    
    public void testSimpleAction() throws Exception {
        final Package pkg = new Package( "pkg1" );

        ActionDescr actionDescr = new ActionDescr();
        actionDescr.setText( "list.add( 'hello world' )" );       

        PackageBuilder pkgBuilder = new PackageBuilder( pkg );
        final PackageBuilderConfiguration conf = pkgBuilder.getPackageBuilderConfiguration();
        MVELDialect mvelDialect = ( MVELDialect ) pkgBuilder.getDialectRegistry().getDialect( "mvel" );

        PackageBuildContext context = new PackageBuildContext();
        context.init( conf, pkg, null, pkgBuilder.getDialectRegistry(), mvelDialect, null);
        
        pkgBuilder.addPackageFromDrl( new StringReader("package pkg1;\nglobal java.util.List list;\n") );        
        
        ActionNode actionNode = new ActionNode();
        
        final MVELActionBuilder builder = new MVELActionBuilder();
        builder.build( context,
                       actionNode,
                       actionDescr );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkgBuilder.getPackage() );
        final WorkingMemory wm = ruleBase.newStatefulSession();

        List list = new  ArrayList();
        wm.setGlobal( "list", list );        
        
        ((Action)actionNode.getAction()).execute( wm );
        
        assertEquals("hello world", list.get(0) );
    }    

}

