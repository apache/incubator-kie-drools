/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests.query;


import java.util.Map;
import java.util.stream.Stream;

import org.drools.core.reteoo.ReteDumper;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.testcoverage.common.util.KieBaseUtil.getKieBaseFromKieModuleFromDrl;

public class QueryMatchingPropertiesTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }
    
    public final static String MATCH_FOO= 			
			"package org.drools.integrationtests\n" +
			"import " + QueryMatchingPropertiesTest.Foo.class.getCanonicalName() + "\n" +
			"query \"MatchFoo\"\n" + 
			"    foo : Foo();\n" +
			"end\n";

    
    public final static String MATCH_FOO_WITH_FOO_ON_ID = 			
			"package org.drools.integrationtests\n" +
			"import " + QueryMatchingPropertiesTest.Foo.class.getCanonicalName() + "\n" +
			"query \"MatchFooWithFooOnId\"\n" + 
			"    foo : Foo();\n" +
			"    foo2 : Foo(id == foo.id);\n" +
			"end\n";

    public final static String MATCH_FOO_WITH_BAR_ON_ID = 			
    		"package org.drools.integrationtests\n" +
			"import " + QueryMatchingPropertiesTest.Bar.class.getCanonicalName() + "\n" +
			"import " + QueryMatchingPropertiesTest.Foo.class.getCanonicalName() + "\n" +
			"query \"MatchFooWithBarOnId\"\n" +
			"    foo : Foo();\n" +
			"    bar : Bar(id == foo.id)\n" +
			"end\n";
    
    public final static String MATCH_FOO_WITH_SUPERFOO_ON_ID = 			
    		"package org.drools.integrationtests\n" +
			"import " + QueryMatchingPropertiesTest.Foo.class.getCanonicalName() + "\n" +
			"import " + QueryMatchingPropertiesTest.SuperFoo.class.getCanonicalName() + "\n" +
			"query \"MatchFooWithSuperFooOnId\"\n" +
			"    foo : Foo();\n" +
			"    superFoo : SuperFoo(id == foo.id)\n" +
			"end\n";


	private KieSession kieSession;

	private int counter;
	
	@AfterEach
	public void tearDown() {
		if (kieSession != null) {
			kieSession.dispose();
		}
	}
	

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void oneFooOnly(KieBaseTestConfiguration kieBaseTestConfiguration) {
        kieSession = createSession(kieBaseTestConfiguration, MATCH_FOO);

        Foo foo = foo("x");
	    kieSession.insert(foo);
	    Bar bar = bar(foo.id);
	    kieSession.insert(bar);

	    QueryResults queryResults = kieSession.getQueryResults("MatchFoo");

	    new ReteDumper().dump(kieSession);
	    
	    
	    assertThat(queryResults).hasSize(1);
    }
    

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void oneFooOneBarSameId(KieBaseTestConfiguration kieBaseTestConfiguration) {
        kieSession = createSession(kieBaseTestConfiguration, MATCH_FOO_WITH_BAR_ON_ID);

        Foo foo = foo("x");
	    kieSession.insert(foo);
	    Bar bar = bar(foo.id);
	    kieSession.insert(bar);

	    QueryResults queryResults = kieSession.getQueryResults("MatchFooWithBarOnId");

	    QueryResults queryResults2 = kieSession.getQueryResults("MatchFooWithBarOnId");

	    new ReteDumper().dump(kieSession);
	    
	    
	    assertThat(queryResults).hasSize(1);
	    assertThat(queryResults).isSameAs(queryResults);
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void oneFooOneBarDifferentId(KieBaseTestConfiguration kieBaseTestConfiguration) {
        kieSession = createSession(kieBaseTestConfiguration, MATCH_FOO_WITH_BAR_ON_ID);

        Foo foo = foo("x");
	    kieSession.insert(foo);
	    Bar bar = bar(differentIdFrom(foo.id));
	    kieSession.insert(bar);

	    QueryResults queryResults = kieSession.getQueryResults("MatchFooWithBarOnId");
	    
	    assertThat(queryResults.toList()).hasSize(0);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void oneFooOneBarSameIdChangedToDifferentId(KieBaseTestConfiguration kieBaseTestConfiguration) {
        kieSession = createSession(kieBaseTestConfiguration, MATCH_FOO_WITH_BAR_ON_ID);

        Foo foo = foo("x");
		kieSession.insert(foo);
		Bar bar = bar(foo.id);
		FactHandle handleForBar = kieSession.insert(bar);

		Bar changedBar = bar(differentIdFrom(foo.id));
		kieSession.update(handleForBar, changedBar);
		
		QueryResults queryResults = kieSession.getQueryResults("MatchFooWithBarOnId");
		
		assertThat(queryResults.toList()).hasSize(0);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void oneFoo(KieBaseTestConfiguration kieBaseTestConfiguration) {
        kieSession = createSession(kieBaseTestConfiguration, MATCH_FOO_WITH_FOO_ON_ID);

        Foo foo = foo("x");
	    kieSession.insert(foo);

	    QueryResults queryResults = kieSession.getQueryResults("MatchFooWithFooOnId");
	    
	    assertThat(queryResults.toList()).hasSize(1).containsExactlyInAnyOrder(
	    		fooFoo(foo, foo));
    }    
    
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void twoFoosSameId(KieBaseTestConfiguration kieBaseTestConfiguration) {
        kieSession = createSession(kieBaseTestConfiguration, MATCH_FOO_WITH_FOO_ON_ID);

        Foo foo = foo("x");
	    kieSession.insert(foo);
        Foo foo2 = foo(foo.id);
	    kieSession.insert(foo2);

	    QueryResults queryResults = kieSession.getQueryResults("MatchFooWithFooOnId");
	    
	    assertThat(queryResults.toList()).hasSize(4).containsExactlyInAnyOrder(
	    		fooFoo(foo, foo),
	    		fooFoo(foo2, foo),
	    		fooFoo(foo, foo2),
	    		fooFoo(foo2, foo2));
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void twoFoosDifferentId(KieBaseTestConfiguration kieBaseTestConfiguration) {
        kieSession = createSession(kieBaseTestConfiguration, MATCH_FOO_WITH_FOO_ON_ID);

        Foo foo = foo("x");
	    kieSession.insert(foo);
        Foo foo2 = foo(differentIdFrom(foo.id));
	    kieSession.insert(foo2);

	    QueryResults queryResults = kieSession.getQueryResults("MatchFooWithFooOnId");
	    
	    assertThat(queryResults.toList()).hasSize(2).containsExactlyInAnyOrder(
	    		fooFoo(foo, foo),
	    		fooFoo(foo2, foo2));
}

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void twoFoosSameIdUpdatePreservingId(KieBaseTestConfiguration kieBaseTestConfiguration) {
        kieSession = createSession(kieBaseTestConfiguration, MATCH_FOO_WITH_FOO_ON_ID);

        Foo foo = foo("x");
	    kieSession.insert(foo);
        Foo foo2 = foo(foo.id);
	    FactHandle handleForFoo = kieSession.insert(foo2);

	    kieSession.update(handleForFoo, foo2);

	    QueryResults queryResults = kieSession.getQueryResults("MatchFooWithFooOnId");
	    
	    assertThat(queryResults.toList()).hasSize(4).containsExactlyInAnyOrder(
	    		fooFoo(foo, foo),
	    		fooFoo(foo2, foo),
	    		fooFoo(foo, foo2),
	    		fooFoo(foo2, foo2));
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void twoFoosSameIdChangedToToDifferenId(KieBaseTestConfiguration kieBaseTestConfiguration) {
        kieSession = createSession(kieBaseTestConfiguration, MATCH_FOO_WITH_FOO_ON_ID);

        Foo foo = foo("x");
	    kieSession.insert(foo);
        Foo foo2 = foo(foo.id);
	    FactHandle handleForFoo2 = kieSession.insert(foo2);
	    
	    Foo changedFoo2 = foo(differentIdFrom(foo.id));
	    kieSession.update(handleForFoo2, changedFoo2);

	    QueryResults queryResults = kieSession.getQueryResults("MatchFooWithFooOnId");
	    
	    assertThat(queryResults.toList()).hasSize(2).containsExactlyInAnyOrder(
	    		fooFoo(foo, foo),
	    		fooFoo(changedFoo2, changedFoo2));
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void twoFoosDifferentIdChangedToSameId(KieBaseTestConfiguration kieBaseTestConfiguration) {
        kieSession = createSession(kieBaseTestConfiguration, MATCH_FOO_WITH_FOO_ON_ID);

        Foo foo = foo("x");
	    kieSession.insert(foo);
        Foo foo2 = foo(differentIdFrom(foo.id));
	    FactHandle handleForFoo2 = kieSession.insert(foo2);
	    
	    Foo changedFoo2 = foo(foo.id);

	    kieSession.update(handleForFoo2, changedFoo2);

	    QueryResults queryResults = kieSession.getQueryResults("MatchFooWithFooOnId");
	    
	    assertThat(queryResults.toList()).hasSize(4).containsExactlyInAnyOrder(
	    		fooFoo(foo, foo),
	    		fooFoo(changedFoo2, foo),
	    		fooFoo(foo, changedFoo2),
	    		fooFoo(changedFoo2, changedFoo2));
    }
    

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void oneFooOneSuperFooSameId(KieBaseTestConfiguration kieBaseTestConfiguration) {
        kieSession = createSession(kieBaseTestConfiguration, MATCH_FOO_WITH_SUPERFOO_ON_ID);
        
        Foo foo = foo("x");
		kieSession.insert(foo);
        SuperFoo superFoo = superFoo(foo.id);
		kieSession.insert(superFoo);

		QueryResults queryResults = kieSession.getQueryResults("MatchFooWithSuperFooOnId");

	    assertThat(queryResults.toList()).hasSize(2).containsExactlyInAnyOrder(
	    		fooSuperFoo(foo, superFoo),
	    		fooSuperFoo(superFoo, superFoo));    
	    
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void oneFooOneSuperFooDifferentId(KieBaseTestConfiguration kieBaseTestConfiguration) {
        kieSession = createSession(kieBaseTestConfiguration, MATCH_FOO_WITH_SUPERFOO_ON_ID);
        
        Foo foo = foo("x");
		kieSession.insert(foo);
        SuperFoo superFoo = superFoo(differentIdFrom(foo.id));
		kieSession.insert(superFoo);

		QueryResults queryResults = kieSession.getQueryResults("MatchFooWithSuperFooOnId");

	    assertThat(queryResults.toList()).hasSize(1).containsExactlyInAnyOrder(
	    		fooSuperFoo(superFoo, superFoo));    
	    
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void oneFooOneSuperFooDifferentIdChangedToSameId(KieBaseTestConfiguration kieBaseTestConfiguration) {
        kieSession = createSession(kieBaseTestConfiguration, MATCH_FOO_WITH_SUPERFOO_ON_ID);
        
        Foo foo = foo("x");
		kieSession.insert(foo);
        SuperFoo superFoo = superFoo(differentIdFrom(foo.id));
		FactHandle handleForSuperFoo = kieSession.insert(superFoo);
		
		SuperFoo changedSuperFoo = superFoo(foo.id);
		kieSession.update(handleForSuperFoo, changedSuperFoo);

		QueryResults queryResults = kieSession.getQueryResults("MatchFooWithSuperFooOnId");

	    assertThat(queryResults.toList()).hasSize(2).containsExactlyInAnyOrder(
	    		fooSuperFoo(foo, changedSuperFoo), 
	    		fooSuperFoo(changedSuperFoo, changedSuperFoo));    
	    
    }
    
    
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void oneFooOneSuperFooSameIdUpdateToSameId(KieBaseTestConfiguration kieBaseTestConfiguration) {
        kieSession = createSession(kieBaseTestConfiguration, MATCH_FOO_WITH_SUPERFOO_ON_ID);

        Foo foo = foo("x");
		kieSession.insert(foo);
		SuperFoo superFoo = superFoo(foo.id);
		FactHandle handleForSuperFoo = kieSession.insert(superFoo);
		
		kieSession.update(handleForSuperFoo, superFoo);
		
		QueryResults queryResults = kieSession.getQueryResults("MatchFooWithSuperFooOnId");
		
	    assertThat(queryResults.toList()).hasSize(2).containsExactlyInAnyOrder(
	    		fooSuperFoo(foo, superFoo), 
	    		fooSuperFoo(superFoo, superFoo));        
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void oneFooOneSuperFooSameIdRemoveOneFoo(KieBaseTestConfiguration kieBaseTestConfiguration) {
        kieSession = createSession(kieBaseTestConfiguration, MATCH_FOO_WITH_SUPERFOO_ON_ID);

        Foo foo = foo("x");
        FactHandle handleForFoo = kieSession.insert(foo);
		SuperFoo superFoo = superFoo(foo.id);
		kieSession.insert(superFoo);

		kieSession.delete(handleForFoo);
		
		QueryResults queryResults = kieSession.getQueryResults("MatchFooWithSuperFooOnId");
		
	    assertThat(queryResults.toList()).hasSize(1).containsExactlyInAnyOrder(
	    		fooSuperFoo(superFoo, superFoo));    
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void oneFooOneSuperFooSameIdRemoveOneSuperFoo(KieBaseTestConfiguration kieBaseTestConfiguration) {
        kieSession = createSession(kieBaseTestConfiguration, MATCH_FOO_WITH_SUPERFOO_ON_ID);

        Foo foo = foo("x");
		kieSession.insert(foo);
		SuperFoo superFoo = superFoo(foo.id);
		FactHandle handleForSuperFoo = kieSession.insert(superFoo);

		kieSession.delete(handleForSuperFoo);
		
		QueryResults queryResults = kieSession.getQueryResults("MatchFooWithSuperFooOnId");
		
	    assertThat(queryResults.toList()).hasSize(0);    
    }


    public static class Bar {

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
        
        public String toString() {
        	return "Bar [id = " + getId() + " ]";
        }

    }

    public static class Foo {

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
        
        public String toString() {
        	return "Foo [id = " + getId() + " ]";
        }

    }

    public static class SuperFoo extends Foo {

        public String toString() {
        	return "SuperFoo [id = " + getId() + " ]";
        }
    }
    
    private KieSession createSession(KieBaseTestConfiguration kieBaseTestConfiguration, String drl) {
		KieBase knowledgeBase = getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        return knowledgeBase.newKieSession();
    }

    private String differentIdFrom(String id) {
    	counter++;
    	return id + counter; 
    }
    
    private static Bar bar(String id) {
    	Bar bar = new Bar();
    	bar.setId(id);
    	return bar;
    }
    
    private static Foo foo(String id) {
    	Foo foo = new Foo();
    	foo.setId(id);
    	return foo;
    }

    private static SuperFoo superFoo(String id) {
    	SuperFoo superFoo = new SuperFoo();
    	superFoo.setId(id);
    	return superFoo;
    }
    
    private static Map<String, Object> fooFoo(Foo foo, Foo foo2) {
    	return Map.of("foo", foo, "foo2", foo2);
    }
    
    private static Map<String, Object> fooSuperFoo(Foo foo, SuperFoo superFoo) {
    	return Map.of("foo", foo, "superFoo", superFoo);
    }

    private static Map<String, Object> fooBar(Foo foo, Bar bar) {
    	return Map.of("foo", foo, "bar", bar);
    }

}
