package org.drools.integrationtests;

import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

public class VarargsTest extends CommonTestMethodBase {
    
    @Test
    public void testStrStartsWith() throws Exception {
        KnowledgeBase kbase = readKnowledgeBase();
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        ksession.setGlobal( "invoker", new Invoker() );

        ksession.fireAllRules();
    }

    public static class Invoker {
        public void invoke(String s1, int num, String... strings) {
            if (num != strings.length) {
                throw new RuntimeException("Expected num: " + num + ", got: " + strings.length);
            }
        }
        public void invoke(String s1, int num, A... as) {
            if (num != as.length) {
                throw new RuntimeException("Expected num: " + num + ", got: " + as.length);
            }
        }
        public void invoke(int total, A... as) {
            int sum = 0;
            for (A a : as) sum += a.getValue();
            if (total != sum) {
                throw new RuntimeException("Expected total: " + total);
            }
        }
    }

    public interface A {
        int getValue();
    }

    public static class B implements A {
        private int value;
        public B() { }
        public B(int value) { this.value = value; }
        public B(String value) { this.value = Integer.parseInt(value); }
        public int getValue() { return value; }
        public boolean equals(Object other) { return other != null && other instanceof B && value == ((B)other).value; };
    }

    private KnowledgeBase readKnowledgeBase() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newInputStreamResource(getClass().getResourceAsStream("varargs.drl")),
                ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error: errors) {
                System.err.println(error);
            }
            throw new IllegalArgumentException("Could not parse knowledge." + errors.toArray());
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
    }
}
