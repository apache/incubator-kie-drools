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
package org.drools.mvel.compiler.compiler;

import java.util.Arrays;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.BindingDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr.Type;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.QueryDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Person;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;

import static org.assertj.core.api.Assertions.assertThat;

public class PatternBuilderForQueryTest {

    @Test
    public void testRuleWithQuery() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        final PackageDescr packageDescr = new PackageDescr( "p1" );

        QueryDescr queryDescr = new QueryDescr( "query1" );
        queryDescr.addParameter( "String",
                                 "$name" );
        queryDescr.addParameter( "int",
                                 "$age" );
        queryDescr.addParameter( "String",
                                 "$likes" );
        packageDescr.addRule( queryDescr );
        AndDescr lhs = new AndDescr();
        queryDescr.setLhs( lhs );
        PatternDescr pattern = new PatternDescr( Person.class.getName() );
        lhs.addDescr( pattern );
        pattern.addConstraint( new BindingDescr( "$name", "name", true ) );
        pattern.addConstraint( new BindingDescr( "$age", "age", true ) );
        pattern.addConstraint( new BindingDescr( "$likes", "likes", true ) );

        RuleDescr ruleDescr = new RuleDescr( "rule-1" );
        packageDescr.addRule( ruleDescr );
        lhs = new AndDescr();
        ruleDescr.setLhs( lhs );

        pattern = new PatternDescr( Cheese.class.getName() );
        lhs.addDescr( pattern );
        pattern.addConstraint( new BindingDescr( "$type",
                                              "type" ) );

        pattern = new PatternDescr( "query1" );
        pattern.setQuery( true );
        lhs.addDescr( pattern );
        ExprConstraintDescr expr = new ExprConstraintDescr("'bobba'");
        expr.setPosition( 0 );
        expr.setType( Type.POSITIONAL );
        pattern.addConstraint(expr);
        
        expr = new ExprConstraintDescr("$age");
        expr.setPosition( 1 );
        expr.setType( Type.POSITIONAL );
        pattern.addConstraint( expr );
        
        expr = new ExprConstraintDescr("$type");
        expr.setPosition( 2 );
        expr.setType( Type.POSITIONAL );        
        pattern.addConstraint( expr );
        ruleDescr.setConsequence( "System.out.println(\"age: \" + $age);" );

        builder.addPackage( packageDescr );
        assertThat(builder.getErrors().getErrors()).hasSize(0);

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(Arrays.asList(builder.getPackage(packageDescr.getName())));
        final KieSession session = kbase.newKieSession();

        session.insert( new Person( "bobba",
                                    "stilton",
                                    90 ) );
        session.insert( new Person( "bobba",
                                    "brie",
                                    80 ) );
        session.insert( new Person( "bobba",
                                    "brie",
                                    75 ) );
        session.insert( new Person( "darth",
                                    "brie",
                                    100 ) );
        session.insert( new Person( "luke",
                                    "brie",
                                    25 ) );
        session.insert( new Cheese( "brie",
                                    25 ) );
        session.fireAllRules();
    }

    @Test
    public void testQuery() throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();

        final PackageDescr packageDescr = new PackageDescr( "p1" );
        final QueryDescr queryDescr = new QueryDescr( "query1" );
        queryDescr.addParameter( "String",
                                 "$type" );

        packageDescr.addRule( queryDescr );

        final AndDescr lhs = new AndDescr();
        queryDescr.setLhs( lhs );

        final PatternDescr pattern = new PatternDescr( Cheese.class.getName(),
                                                       "stilton" );
        lhs.addDescr( pattern );

        pattern.addConstraint( new ExprConstraintDescr("type == $type") );

        // Another query, no parameters
        QueryDescr queryDescr2 = new QueryDescr( "query2" );
        packageDescr.addRule( queryDescr2 );
        AndDescr lhs2 = new AndDescr();
        queryDescr2.setLhs( lhs2 );
        PatternDescr pattern2 = new PatternDescr( Cheese.class.getName() );
        lhs2.addDescr( pattern2 );

        builder.addPackage( packageDescr );

        assertThat(builder.getErrors().getErrors()).hasSize(0);

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(Arrays.asList(builder.getPackage(packageDescr.getName())));

        final KieSession session = kbase.newKieSession();

        session.insert( new Cheese( "stilton",
                                    15 ) );

        QueryResults results = session.getQueryResults( "query1",
                                                        "stilton" );
        assertThat(results.size()).isEqualTo(1);

        Object object = results.iterator().next().get("stilton");
        assertThat(object).isEqualTo(new Cheese( "stilton",
                15 ));

        results = session.getQueryResults( "query1",
                                           "cheddar");
        assertThat(results.size()).isEqualTo(0);

        session.insert(new Cheese("dolcelatte",
                                  20));
        results = session.getQueryResults( "query2"
                                         );
        assertThat(results.size()).isEqualTo(2);
    }
}
