package org.drools.compiler.xml;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class TestXmlDsl {

    @Test
    public void testSimpleDsl() throws Exception {
        Properties properties = new Properties();
        properties.put( "semanticModules", "mydsl.conf" );

        PackageBuilderConfiguration conf = new PackageBuilderConfiguration( properties );

        PackageBuilder builder = new PackageBuilder( conf );
        builder.addProcessFromXml( new InputStreamReader( TestXmlDsl.class.getResourceAsStream( "XmlDslTest.xml" ) ) );

        System.out.println( builder.getErrors() );

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        StatefulSession session = ruleBase.newStatefulSession();
        List list = new ArrayList();
        session.setGlobal( "list", list );

        session.startProcess( "process name" );

        assertEquals( 2, list.size() );
        assertEquals( "action node was here", list.get(0) );
        assertEquals( "dsl was here", list.get(1) );
    }
}
