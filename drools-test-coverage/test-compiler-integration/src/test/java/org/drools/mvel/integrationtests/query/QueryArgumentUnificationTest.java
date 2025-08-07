package org.drools.mvel.integrationtests.query;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.drools.mvel.compiler.Address;
import org.drools.mvel.compiler.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.Variable;

public class QueryArgumentUnificationTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueriesWithVariableUnification(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        String str = "";
        str += "package org.drools.mvel.compiler.test  \n";
        str += "import org.drools.mvel.compiler.Person \n";
        str += "query peeps( String $name, String $likes, int $age ) \n";
        str += "    $p : Person( $name := name, $likes := likes, $age := age ) \n";
        str += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Person p1 = new Person("darth", "stilton", 100);
        Person p2 = new Person("yoda", "stilton", 300);
        Person p3 = new Person("luke", "brie", 300);
        Person p4 = new Person("bobba", "cheddar", 300);

        ksession.insert( p1 );
        ksession.insert( p2 );
        ksession.insert( p3 );
        ksession.insert( p4 );

        QueryResults results = ksession.getQueryResults("peeps", Variable.v, Variable.v, Variable.v);
        assertThat(results).hasSize(4);
        assertThat(results).extracting(r -> ((Person) r.get( "$p" )).getName()).containsExactlyInAnyOrder("luke", "yoda", "bobba","darth");

        results = ksession.getQueryResults("peeps", Variable.v, Variable.v, 300);
        assertThat(results).hasSize(3);
        assertThat(results).extracting(r -> ((Person) r.get( "$p" )).getName()).containsExactlyInAnyOrder("luke", "yoda", "bobba");

        results = ksession.getQueryResults("peeps", Variable.v, "stilton", 300);
        assertThat(results).hasSize(1);
        assertThat(results).extracting(r -> ((Person) r.get( "$p" )).getName()).containsExactlyInAnyOrder("yoda");
        
        results = ksession.getQueryResults("peeps", Variable.v, "stilton", Variable.v);
        assertThat(results).hasSize(2);
        assertThat(results).extracting(r -> ((Person) r.get( "$p" )).getName()).containsExactlyInAnyOrder("yoda", "darth");

        results = ksession.getQueryResults("peeps", "darth", Variable.v, Variable.v);
        assertThat(results).hasSize(1);
        assertThat(results).extracting(r -> ((Person) r.get( "$p" )).getName()).containsExactlyInAnyOrder("darth");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueriesWithVariableUnificationOnPatterns(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        String str = "";
        str += "package org.drools.mvel.compiler.test  \n";
        str += "import org.drools.mvel.compiler.Person \n";
        str += "query peeps( Person $p, String $name, String $likes, int $age ) \n";
        str += "    $p := Person( $name := name, $likes := likes, $age := age ) \n";
        str += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Person p1 = new Person( "darth", "stilton", 100 );
        Person p2 = new Person( "yoda", "stilton", 300 );
        Person p3 = new Person( "luke", "brie", 300 );
        Person p4 = new Person( "bobba", "cheddar", 300 );

        ksession.insert( p1 );
        ksession.insert( p2 );
        ksession.insert( p3 );
        ksession.insert( p4 );

        QueryResults results = ksession.getQueryResults("peeps", Variable.v, Variable.v, Variable.v, Variable.v);
        assertThat(results).hasSize(4);
        assertThat(results).extracting(r -> ((Person) r.get( "$p" )).getName()).containsExactlyInAnyOrder("luke", "yoda", "bobba","darth");

        results = ksession.getQueryResults("peeps", p1, Variable.v, Variable.v, Variable.v);
        
        assertThat(results).hasSize(1);
        assertThat(results).extracting(r -> ((Person) r.get( "$p" )).getName()).containsExactlyInAnyOrder("darth");
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testQueriesWithVariableUnificationOnNestedFields(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        String str = "";
        str += "package org.drools.mvel.compiler.test  \n";
        str += "import org.drools.mvel.compiler.Person \n";
        str += "query peeps( String $name, String $likes, String $street) \n";
        str += "    $p : Person( $name := name, $likes := likes, $street := address.street ) \n";
        str += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Person p1 = new Person( "darth", "stilton", 100 );
        p1.setAddress( new Address( "s1" ) );

        Person p2 = new Person( "yoda", "stilton", 300 );
        p2.setAddress( new Address( "s2" ) );

        ksession.insert( p1 );
        ksession.insert( p2 );

        QueryResults results = ksession.getQueryResults("peeps", Variable.v, Variable.v, Variable.v);
        assertThat(results).hasSize(2);
        assertThat(results).extracting(r -> ((Person) r.get( "$p" )).getName()).containsExactlyInAnyOrder("yoda", "darth");

        results = ksession.getQueryResults("peeps", Variable.v, Variable.v, "s1");
        assertThat(results).hasSize(1);
        assertThat(results).extracting(r -> ((Person) r.get( "$p" )).getName()).containsExactlyInAnyOrder("darth");
    }

}
