package org.drools.kproject;

import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.definition.type.FactType;
import org.kie.runtime.StatefulKnowledgeSession;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URL;
import java.net.URLClassLoader;

import static junit.framework.Assert.assertEquals;

public class KProjectTest {

    @Test @Ignore
    public void testKJar() throws Exception {
        // Simulate having the jar in the classpath
        URLClassLoader urlClassLoader = new URLClassLoader( new URL[] { new java.io.File("drools-maven-plugin-example/target/classes/test.jar").toURI().toURL() } );
        Thread.currentThread().setContextClassLoader( urlClassLoader );

        StatefulKnowledgeSession ksession = KnowledgeBaseFactory.getStatefulKnowlegeSession("KBase1.session");
        useKSession(ksession);
    }

    private void useKSession(StatefulKnowledgeSession ksession) throws InstantiationException, IllegalAccessException {
        KnowledgeBase kbase = ksession.getKnowledgeBase();
        FactType aType = kbase.getFactType( "org.drools.test", "FactA" );
        Object a = aType.newInstance();
        FactType bType = kbase.getFactType( "org.drools.test", "FactB" );
        Object b = bType.newInstance();
        aType.set( a, "fieldB", b );
        bType.set( b, "fieldA", a );

        ksession.insert( a );
        ksession.insert( b );

        int rules = ksession.fireAllRules();
        assertEquals( 1, rules );
    }
}
