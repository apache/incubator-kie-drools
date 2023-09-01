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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.common.InternalFactHandle;
import org.drools.mvel.compiler.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class UnlinkingTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public UnlinkingTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void multipleJoinsUsingSameOTN() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_LRUnlinking.drl");

        final KieSession wmOne = kbase.newKieSession();
        final KieSession wmTwo = kbase.newKieSession();

        final List<Person> listOne = new ArrayList<Person>();
        final List<Person> listTwo = new ArrayList<Person>();

        wmOne.setGlobal( "results",
                         listOne );
        wmTwo.setGlobal( "results",
                         listTwo );

        Person name = new Person();
        Person likes = new Person();
        Person age = new Person();
        Person hair = new Person();
        Person happy = new Person();
        Person match = new Person();

        name.setName( "Ana" );
        likes.setLikes( "Chocolate" );
        age.setAge( 30 );
        hair.setHair( "brown" );
        happy.setHappy( true );

        match.setName( "Leo" );
        match.setLikes( "Chocolate" );
        match.setAge( 30 );
        match.setHair( "brown" );
        match.setHappy( true );
        
        // WM One - first round of inserts
        wmOne.insert( name );
        wmOne.insert( likes );
        wmOne.insert( age );

        wmOne.fireAllRules();

        assertThat(listOne.size()).as("Should not have fired").isEqualTo(0);

        // WM Two - first round o inserts
        wmTwo.insert( name );
        wmTwo.insert( likes );
        wmTwo.insert( age );

        wmTwo.fireAllRules();

        assertThat(listTwo.size()).as("Should not have fired").isEqualTo(0);
        
        wmOne.insert( hair );
        wmOne.insert( happy );
        InternalFactHandle matchHandle = (InternalFactHandle) wmOne.insert( match );
        
        wmOne.fireAllRules();

        assertThat(listOne.size() > 0).as("Should have fired").isTrue();

        assertThat(listOne.get(0)).as("Should have inserted the match Person").isEqualTo(matchHandle.getObject());
        
        wmTwo.fireAllRules();

        assertThat(listTwo.size()).as("Should not have fired").isEqualTo(0);
        
        wmTwo.insert( hair );
        wmTwo.insert( happy );
        wmTwo.insert( match );
        
        wmTwo.fireAllRules();

        assertThat(listTwo.size() > 0).as("Should have fired").isTrue();

    }

}
