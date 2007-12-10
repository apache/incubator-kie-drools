package org.drools.xml.processes;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;
import org.drools.ruleflow.common.core.Process;
import org.drools.xml.XmlProcessReader;

public class ActionNodeTest extends TestCase {
    public void testSingleActionNode() throws Exception {        
        XmlProcessReader reader = new XmlProcessReader( );
        Process process = reader.read( new InputStreamReader( ActionNodeTest.class.getResourceAsStream( "ActionNodeTest.xml" ) ) );
        
        PackageBuilder builder = new PackageBuilder();
        builder.addProcess( process );
        
        System.out.println( builder.getErrors() );
        
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        
        StatefulSession session = ruleBase.newStatefulSession();
        List list = new ArrayList();
        session.setGlobal( "list", list );
        
        session.startProcess( "process name" );
        
        assertEquals( 1, list.size() );
        assertEquals( "action node was here", list.get(0) );
        
    }
}
