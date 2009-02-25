package org.drools.rule.builder.dialect.mvel;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.DefaultKnowledgeHelper;
import org.drools.base.mvel.MVELAction;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.PackageRegistry;
import org.drools.lang.descr.ActionDescr;
import org.drools.rule.Package;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.spi.Action;
import org.drools.spi.KnowledgeHelper;
import org.drools.workflow.core.DroolsAction;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
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
        
        PackageRegistry pkgReg = pkgBuilder.getPackageRegistry( pkg.getName() );
        MVELDialect mvelDialect = ( MVELDialect ) pkgReg.getDialectCompiletimeRegistry().getDialect( "mvel" );

        PackageBuildContext context = new PackageBuildContext();
        context.init( pkgBuilder, pkg, null, pkgReg.getDialectCompiletimeRegistry(), mvelDialect, null);
        
        pkgBuilder.addPackageFromDrl( new StringReader("package pkg1;\nglobal java.util.List list;\n") );        
        
        ActionNode actionNode = new ActionNode();
        DroolsAction action = new DroolsConsequenceAction("java", null);
        actionNode.setAction(action);
        
        final MVELActionBuilder builder = new MVELActionBuilder();
        builder.build( context,
                       action,
                       actionDescr,
                       actionNode );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( pkgBuilder.getPackage() );
        final WorkingMemory wm = ruleBase.newStatefulSession();

        List list = new  ArrayList();
        wm.setGlobal( "list", list );        
        
        KnowledgeHelper knowledgeHelper = new DefaultKnowledgeHelper();
        ((MVELAction) actionNode.getAction().getMetaData("Action")).compile( Thread.currentThread().getContextClassLoader() );
        ((Action)actionNode.getAction().getMetaData("Action")).execute( knowledgeHelper, wm, null );
        
        assertEquals("hello world", list.get(0) );
    }    

}

