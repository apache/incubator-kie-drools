package org.drools.compiler.integrationtests;

import java.util.HashSet;
import java.util.Set;

import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.definition.type.PropertyReactive;
import org.kie.runtime.StatefulKnowledgeSession;

public class VarargsTest extends CommonTestMethodBase {
    
    @Test
    public void testStrStartsWith() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase("varargs.drl");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        ksession.setGlobal( "invoker", new Invoker() );

        ksession.fireAllRules();
    }

    @Test
    public void testVarargs() throws Exception {
        KnowledgeBase kbase = loadKnowledgeBase("varargs2.drl");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        MySet mySet = new MySet( "one", "two" );
        ksession.insert(mySet);
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

    @PropertyReactive
    public static class MySet {
        Set<String> set = new HashSet<String>();

        public MySet( String... strings ){
            add( strings );
        }

        public void add( String... strings ){
            for( String s: strings ){
                set.add( s );
            }
        }

        public boolean contains( String s ){
            return set.contains( s );
        }

        public String toString(){
            return set.toString();
        }
    }
}
