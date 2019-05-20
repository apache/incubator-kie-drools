/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.core.common.InternalFactHandle;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnlinkingTest extends CommonTestMethodBase {

    @Test
    public void multipleJoinsUsingSameOTN() throws Exception {
        KieBase kbase = loadKnowledgeBase("test_LRUnlinking.drl");
        kbase = SerializationHelper.serializeObject( kbase );

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

        assertEquals( 0,
                      listOne.size(),
                      "Should not have fired");

        // WM Two - first round o inserts
        wmTwo.insert( name );
        wmTwo.insert( likes );
        wmTwo.insert( age );

        wmTwo.fireAllRules();

        assertEquals( 0,
                      listTwo.size(),
                      "Should not have fired");
        
        wmOne.insert( hair );
        wmOne.insert( happy );
        InternalFactHandle matchHandle = (InternalFactHandle) wmOne.insert( match );
        
        wmOne.fireAllRules();
        
        assertTrue(listOne.size() > 0,  "Should have fired");
                
        assertEquals(matchHandle.getObject(),
                     listOne.get( 0 ),
                     "Should have inserted the match Person");
        
        wmTwo.fireAllRules();
        
        assertEquals(0,
                     listTwo.size(),
                     "Should not have fired");
        
        wmTwo.insert( hair );
        wmTwo.insert( happy );
        wmTwo.insert( match );
        
        wmTwo.fireAllRules();

        assertTrue(listTwo.size() > 0,
                   "Should have fired");

    }

}
