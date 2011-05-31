package org.drools.compiler;

import static org.junit.Assert.assertEquals;

import org.drools.Cheese;
import org.drools.DroolsTestCase;
import org.drools.Person;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.BindingDescr;
import org.drools.lang.descr.ExprConstraintDescr;
import org.drools.lang.descr.ExprConstraintDescr.Type;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.LiteralDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.VariableDescr;
import org.drools.lang.descr.VariableRestrictionDescr;
import org.junit.Ignore;
import org.junit.Test;

public class QueryBuilderTest extends DroolsTestCase {

    @Test
    public void testRuleWithQuery() throws Exception {
        final PackageBuilder builder = new PackageBuilder();

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
        assertLength( 0,
                      builder.getErrors().getErrors() );

        RuleBase rbase = RuleBaseFactory.newRuleBase();
        rbase.addPackage( builder.getPackage() );
        StatefulSession session = rbase.newStatefulSession();

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
        final PackageBuilder builder = new PackageBuilder();

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

        assertLength( 0,
                      builder.getErrors().getErrors() );

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        StatefulSession session = ruleBase.newStatefulSession();

        session.insert( new Cheese( "stilton",
                                    15 ) );

        QueryResults results = session.getQueryResults( "query1",
                                                        "stilton" );
        assertEquals( 1,
                      results.size() );
        Object object = results.get( 0 ).get( 0 );
        assertEquals( new Cheese( "stilton",
                                  15 ),
                      object );

        results = session.getQueryResults( "query1",
                                           new Object[]{"cheddar"} );
        assertEquals( 0,
                      results.size() );

        session.insert( new Cheese( "dolcelatte",
                                    20 ) );
        results = session.getQueryResults( "query2",
                                           new Object[]{} );
        assertEquals( 2,
                      results.size() );
    }
}
