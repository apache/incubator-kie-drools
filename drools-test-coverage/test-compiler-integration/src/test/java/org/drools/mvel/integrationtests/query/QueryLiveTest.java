package org.drools.mvel.integrationtests.query;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.drools.mvel.compiler.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.ViewChangedEventListener;

public class QueryLiveTest {
	

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testOpenQuery(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        String str = "";
        str += "package org.drools.mvel.compiler.test  \n";
        str += "import org.drools.mvel.compiler.Cheese \n";
        str += "query cheeses(String $type1, String $type2) \n";
        str += "    stilton : Cheese(type == $type1, $sprice : price) \n";
        str += "    cheddar : Cheese(type == $type2, $cprice : price == stilton.price) \n";
        str += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Cheese stilton1 = new Cheese( "stilton",
                                      1 );
        Cheese cheddar1 = new Cheese( "cheddar",
                                      1 );
        Cheese stilton2 = new Cheese( "stilton",
                                      2 );
        Cheese cheddar2 = new Cheese( "cheddar",
                                      2 );
        Cheese stilton3 = new Cheese( "stilton",
                                      3 );
        Cheese cheddar3 = new Cheese( "cheddar",
                                      3 );

        FactHandle s1Fh = ksession.insert( stilton1 );
        ksession.insert( stilton2 );
        ksession.insert( stilton3 );
        ksession.insert( cheddar1 );
        ksession.insert( cheddar2 );
        FactHandle c3Fh = ksession.insert( cheddar3 );

        final List<Object[]> updated = new ArrayList<Object[]>();
        final List<Object[]> removed = new ArrayList<Object[]>();
        final List<Object[]> added = new ArrayList<Object[]>();

        ViewChangedEventListener listener = new ViewChangedEventListener() {
            public void rowUpdated( Row row ) {
                Object[] array = new Object[6];
                array[0] = row.get( "stilton" );
                array[1] = row.get( "cheddar" );
                array[2] = row.get( "$sprice" );
                array[3] = row.get( "$cprice" );
                array[4] = row.get( "$type1" );
                array[5] = row.get( "$type2" );
                updated.add( array );
            }

            public void rowDeleted( Row row ) {
                Object[] array = new Object[6];
                array[0] = row.get( "stilton" );
                array[1] = row.get( "cheddar" );
                array[2] = row.get( "$sprice" );
                array[3] = row.get( "$cprice" );
                array[4] = row.get( "$type1" );
                array[5] = row.get( "$type2" );
                removed.add( array );
            }

            public void rowInserted( Row row ) {
                Object[] array = new Object[6];
                array[0] = row.get( "stilton" );
                array[1] = row.get( "cheddar" );
                array[2] = row.get( "$sprice" );
                array[3] = row.get( "$cprice" );
                array[4] = row.get( "$type1" );
                array[5] = row.get( "$type2" );
                added.add( array );
            }
        };

        // Open the LiveQuery
        LiveQuery query = ksession.openLiveQuery( "cheeses",
                                                  new Object[]{"stilton", "cheddar"},
                                                  listener );

        ksession.fireAllRules();

        // Assert that on opening we have three rows added
        assertThat(added).hasSize(3);
        assertThat(removed).hasSize(0);
        assertThat(updated).hasSize(0);

        // Assert that the identifiers where retrievable
        assertThat(added.get(2)[0]).isSameAs(stilton1);
        assertThat(added.get(2)[1]).isSameAs(cheddar1);
        assertThat(added.get(2)[2]).isEqualTo(1);
        assertThat(added.get(2)[3]).isEqualTo(1);
        assertThat(added.get(2)[4]).isEqualTo("stilton");
        assertThat(added.get(2)[5]).isEqualTo("cheddar");

        // And that we have correct values from those rows
        assertThat(added.get(0)[3]).isEqualTo(3);
        assertThat(added.get(1)[3]).isEqualTo(2);
        assertThat(added.get(2)[3]).isEqualTo(1);

        // Do an update that causes a match to become untrue, thus triggering a removed
        cheddar3.setPrice( 4 );
        ksession.update( c3Fh,
                         cheddar3 );
        ksession.fireAllRules();

        assertThat(added).hasSize(3);
        assertThat(removed).hasSize(1);
        assertThat(updated).hasSize(0);

        assertThat(removed.get(0)[3]).isEqualTo(4);

        // Now make that partial true again, and thus another added
        cheddar3.setPrice( 3 );
        ksession.update( c3Fh,
                         cheddar3 );
        ksession.fireAllRules();

        assertThat(added).hasSize(4);
        assertThat(removed).hasSize(1);
        assertThat(updated).hasSize(0);

        assertThat(added.get(3)[3]).isEqualTo(3);

        // check a standard update
        cheddar3.setOldPrice( 0 );
        ksession.update( c3Fh,
                         cheddar3 );
        ksession.fireAllRules();

        assertThat(added).hasSize(4);
        assertThat(removed).hasSize(1);
        assertThat(updated).hasSize(1);

        assertThat(updated.get(0)[3]).isEqualTo(3);

        // Check a standard retract
        ksession.retract( s1Fh );
        ksession.fireAllRules();

        assertThat(added).hasSize(4);
        assertThat(removed).hasSize(2);
        assertThat(updated).hasSize(1);

        assertThat(removed.get(1)[3]).isEqualTo(1);

        // Close the query, we should get removed events for each row
        query.close();

        ksession.fireAllRules();

        assertThat(added).hasSize(4);
        assertThat(removed).hasSize(4);
        assertThat(updated).hasSize(1);

        assertThat(removed.get(3)[3]).isEqualTo(2);
        assertThat(removed.get(2)[3]).isEqualTo(3);

        // Check that updates no longer have any impact.
        ksession.update( c3Fh,
                         cheddar3 );
        assertThat(added).hasSize(4);
        assertThat(removed).hasSize(4);
        assertThat(updated).hasSize(1);
    }


    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testOpenQueryNoParams(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        // RHDM-717
        String str = "";
        str += "package org.drools.mvel.compiler.test  \n";
        str += "import org.drools.mvel.compiler.Cheese \n";
        str += "query cheeses \n";
        str += "    stilton : Cheese(type == 'stilton') \n";
        str += "    cheddar : Cheese(type == 'cheddar', price == stilton.price) \n";
        str += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Cheese stilton1 = new Cheese( "stilton", 1 );
        Cheese cheddar1 = new Cheese( "cheddar", 1 );
        Cheese stilton2 = new Cheese( "stilton", 2 );
        Cheese cheddar2 = new Cheese( "cheddar", 2 );
        Cheese stilton3 = new Cheese( "stilton", 3 );
        Cheese cheddar3 = new Cheese( "cheddar", 3 );

        FactHandle s1Fh = ksession.insert( stilton1 );
        ksession.insert( stilton2 );
        ksession.insert( stilton3 );
        ksession.insert( cheddar1 );
        ksession.insert( cheddar2 );
        FactHandle c3Fh = ksession.insert( cheddar3 );

        final List<Object[]> updated = new ArrayList<Object[]>();
        final List<Object[]> removed = new ArrayList<Object[]>();
        final List<Object[]> added = new ArrayList<Object[]>();

        ViewChangedEventListener listener = new ViewChangedEventListener() {
            public void rowUpdated( Row row ) {
                Object[] array = new Object[2];
                array[0] = row.get( "stilton" );
                array[1] = row.get( "cheddar" );
                updated.add( array );
            }

            public void rowDeleted( Row row ) {
                Object[] array = new Object[2];
                array[0] = row.get( "stilton" );
                array[1] = row.get( "cheddar" );
                removed.add( array );
            }

            public void rowInserted( Row row ) {
                Object[] array = new Object[2];
                array[0] = row.get( "stilton" );
                array[1] = row.get( "cheddar" );
                added.add( array );
            }
        };

        // Open the LiveQuery
        LiveQuery query = ksession.openLiveQuery( "cheeses",null, listener );

        ksession.fireAllRules();

        // Assert that on opening we have three rows added
        assertThat(added).hasSize(3);
        assertThat(removed).hasSize(0);
        assertThat(updated).hasSize(0);

        // Do an update that causes a match to become untrue, thus triggering a removed
        cheddar3.setPrice( 4 );
        ksession.update( c3Fh, cheddar3 );
        ksession.fireAllRules();

        assertThat(added).hasSize(3);
        assertThat(removed).hasSize(1);
        assertThat(updated).hasSize(0);

        // Now make that partial true again, and thus another added
        cheddar3.setPrice( 3 );
        ksession.update( c3Fh, cheddar3 );
        ksession.fireAllRules();

        assertThat(added).hasSize(4);
        assertThat(removed).hasSize(1);
        assertThat(updated).hasSize(0);

        // check a standard update
        cheddar3.setOldPrice( 0 );
        ksession.update( c3Fh, cheddar3 );
        ksession.fireAllRules();

        assertThat(added).hasSize(4);
        assertThat(removed).hasSize(1);
        assertThat(updated).hasSize(1);

        // Check a standard retract
        ksession.retract( s1Fh );
        ksession.fireAllRules();

        assertThat(added).hasSize(4);
        assertThat(removed).hasSize(2);
        assertThat(updated).hasSize(1);

        // Close the query, we should get removed events for each row
        query.close();

        ksession.fireAllRules();

        assertThat(added).hasSize(4);
        assertThat(removed).hasSize(4);
        assertThat(updated).hasSize(1);

        // Check that updates no longer have any impact.
        ksession.update( c3Fh, cheddar3 );
        assertThat(added).hasSize(4);
        assertThat(removed).hasSize(4);
        assertThat(updated).hasSize(1);
    }

}
