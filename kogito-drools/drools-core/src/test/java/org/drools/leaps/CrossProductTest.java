package org.drools.leaps;
/*
 * Copyright 2005 JBoss Inc
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



import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.ObjectType;

public class CrossProductTest extends TestCase {
    public void test1() throws Exception {
        ObjectType list1ObjectType = new ClassObjectType( List.class );
        ObjectType list2ObjectType = new ClassObjectType( List.class );

        Rule rule = new Rule( "rule-1" );

        Column list1Column = new Column( 0,
                                         list1ObjectType,
                                         "list1" );
        Column list2Column = new Column( 1,
                                         list2ObjectType,
                                         "list2" );

        rule.addPattern( list1Column );
        rule.addPattern( list2Column );

        final Declaration list1Declaration = rule.getDeclaration( "list1" );
        final Declaration list2Declaration = rule.getDeclaration( "list2" );

        final List values = new ArrayList();

        rule.setConsequence( new Consequence() {

            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) throws Exception {
                List list1 = (List) knowledgeHelper.get( list1Declaration );
                List list2 = (List) knowledgeHelper.get( list2Declaration );
                values.add( new List[]{list1, list2} );
            }

        } );

        Package pkg = new Package( "org.drools" );
        pkg.addRule( rule );

        RuleBase ruleBase = RuleBaseFactory.getInstance().newRuleBase( RuleBase.LEAPS );
        ruleBase.addPackage( pkg );

        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        workingMemory.assertObject( new ArrayList() );
        workingMemory.assertObject( new ArrayList() );
        workingMemory.assertObject( new LinkedList() );
        workingMemory.assertObject( new LinkedList() );

        workingMemory.fireAllRules();

        // A full cross product is 16, this is just 12
        assertEquals( 12,
                      values.size() );
    }

}