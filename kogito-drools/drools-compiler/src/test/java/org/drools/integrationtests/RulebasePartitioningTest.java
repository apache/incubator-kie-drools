/*
 * Copyright 2008 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.integrationtests;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;

/**
 * @author: <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 */
public class RulebasePartitioningTest extends TestCase {

    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }
    
    public void testRulebasePartitions1() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_rulebasePartitions1.drl" ) ) );
        final org.drools.rule.Package pkg = builder.getPackage();

        RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setMultithreadEvaluation( true );

        RuleBase ruleBase = getRuleBase( config );
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        StatefulSession session = ruleBase.newStatefulSession();
        List result = new ArrayList();
        session.setGlobal( "results",
                           result );

        Cheese c1 = new Cheese( "stilton" );
        Cheese c2 = new Cheese( "brie" );
        Cheese c3 = new Cheese( "cheddar" );
        Cheese c4 = new Cheese( "stilton" );
        Person p1 = new Person( "bob" );
        Person p2 = new Person( "mark" );
        Person p3 = new Person( "michael" );
        Person p4 = new Person( "bob" );
        session.insert( c1 );
        session.insert( c2 );
        session.insert( c3 );
        session.insert( c4 );
        session.insert( p1 );
        session.insert( p2 );
        session.insert( p3 );
        session.insert( p4 );

//        session = SerializationHelper.getSerialisedStatefulSession( session,
//                                                                    ruleBase );
        result = (List) session.getGlobal( "results" );
        
        session.fireAllRules();
        assertEquals( 3,
                      result.size() );
        assertEquals( p4,
                      result.get( 0 ) );
        assertEquals( p1,
                      result.get( 1 ) );
        assertEquals( c3,
                      result.get( 2 ) );

        session.dispose();

    }

}
