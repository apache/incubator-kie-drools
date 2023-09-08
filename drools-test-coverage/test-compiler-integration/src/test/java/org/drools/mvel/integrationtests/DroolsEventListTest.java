/**
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
package org.drools.mvel.integrationtests;

import java.util.Collection;
import java.util.Comparator;

import ca.odell.glazedlists.SortedList;
import org.drools.mvel.compiler.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.Row;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class DroolsEventListTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DroolsEventListTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testOpenQuery() throws Exception {
        String str = "";
        str += "package org.kie.test  \n";
        str += "import " + Cheese.class.getCanonicalName() + "\n";
        str += "query cheeses(String $type1, String $type2) \n";
        str += "    stilton : Cheese(type == $type1, $price : price) \n";
        str += "    cheddar : Cheese(type == $type2, price == stilton.price) \n";
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
        FactHandle s2Fh = ksession.insert( stilton2 );
        FactHandle s3Fh = ksession.insert( stilton3 );
        FactHandle c1Fh = ksession.insert( cheddar1 );
        FactHandle c2Fh = ksession.insert( cheddar2 );
        FactHandle c3Fh = ksession.insert( cheddar3 );
                      
        DroolsEventList list = new DroolsEventList();
        // Open the LiveQuery
        LiveQuery query = ksession.openLiveQuery( "cheeses", new Object[] { "cheddar", "stilton" } , list );
        
        SortedList<Row> sorted = new SortedList<Row>( list, new Comparator<Row>() {

            public int compare(Row r1,
                               Row r2) {
                Cheese c1 = ( Cheese ) r1.get( "stilton" );
                Cheese c2 = ( Cheese ) r2.get( "stilton" );
                return c1.getPrice() - c2.getPrice();
            }
        });


        assertThat(sorted.size()).isEqualTo(3);
        assertThat(((Cheese) sorted.get(0).get("stilton")).getPrice()).isEqualTo(1);
        assertThat(((Cheese) sorted.get(1).get("stilton")).getPrice()).isEqualTo(2);
        assertThat(((Cheese) sorted.get(2).get("stilton")).getPrice()).isEqualTo(3);

        // alter the price to remove the last row
        stilton3.setPrice( 4 );
        ksession.update(  s3Fh, stilton3 );
        ksession.fireAllRules();

        assertThat(sorted.size()).isEqualTo(2);
        assertThat(((Cheese) sorted.get(0).get("stilton")).getPrice()).isEqualTo(1);
        assertThat(((Cheese) sorted.get(1).get("stilton")).getPrice()).isEqualTo(2);

        // alter the price to put the last row back in
        stilton3.setPrice( 3 );
        ksession.update(  s3Fh, stilton3 );
        ksession.fireAllRules();

        assertThat(sorted.size()).isEqualTo(3);
        assertThat(((Cheese) sorted.get(0).get("stilton")).getPrice()).isEqualTo(1);
        assertThat(((Cheese) sorted.get(1).get("stilton")).getPrice()).isEqualTo(2);
        assertThat(((Cheese) sorted.get(2).get("stilton")).getPrice()).isEqualTo(3);
        
        // alter the price to remove the middle row
        stilton2.setPrice( 4 );
        ksession.update(  s2Fh, stilton2 );
        ksession.fireAllRules();

        assertThat(sorted.size()).isEqualTo(2);
        assertThat(((Cheese) sorted.get(0).get("stilton")).getPrice()).isEqualTo(1);
        assertThat(((Cheese) sorted.get(1).get("stilton")).getPrice()).isEqualTo(3);
        
        // alter the price to add the previous middle rows to the end
        cheddar2.setPrice( 4 );
        ksession.update(  c2Fh, cheddar2 );
        ksession.fireAllRules();

        assertThat(sorted.size()).isEqualTo(3);
        assertThat(((Cheese) sorted.get(0).get("stilton")).getPrice()).isEqualTo(1);
        assertThat(((Cheese) sorted.get(1).get("stilton")).getPrice()).isEqualTo(3);
        assertThat(((Cheese) sorted.get(2).get("stilton")).getPrice()).isEqualTo(4);
            
        // Check a standard retract
        ksession.retract( s1Fh );
        ksession.fireAllRules();

        assertThat(sorted.size()).isEqualTo(2);
        assertThat(((Cheese) sorted.get(0).get("stilton")).getPrice()).isEqualTo(3);
        assertThat(((Cheese) sorted.get(1).get("stilton")).getPrice()).isEqualTo(4);

        // Close the query, we should get removed events for each row
        query.close();

        assertThat(sorted.size()).isEqualTo(0);
       
    }

}
