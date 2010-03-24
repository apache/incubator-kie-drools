package org.drools.verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.OperatorDescrType;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.RuleComponent;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.SubRule;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.solver.Solvers;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class SolversTest extends TestCase {

    /**
     * <pre>
     * when 
     * 		Foo( r &amp;&amp; r2 )
     * 		and
     * 		not Foo( r3 &amp;&amp; r4 )
     * </pre>
     * 
     * result:<br>
     * r && r2<br>
     * r3 && r4
     */
    public void testNotAnd() {
        RulePackage rulePackage = new RulePackage();
        rulePackage.setName( "testPackage" );

        VerifierRule rule = new VerifierRule( rulePackage );
        rule.setName( "testRule" );
        Pattern pattern = new Pattern( rule );

        Restriction r = new LiteralRestriction( pattern );
        Restriction r2 = new LiteralRestriction( pattern );
        Restriction r3 = new LiteralRestriction( pattern );
        Restriction r4 = new LiteralRestriction( pattern );

        Solvers solvers = new Solvers();

        solvers.startRuleSolver( rule );

        solvers.startOperator( OperatorDescrType.AND );
        solvers.startPatternSolver( pattern );
        solvers.startOperator( OperatorDescrType.AND );
        solvers.addPatternComponent( r );
        solvers.addPatternComponent( r2 );
        solvers.endOperator();
        solvers.endPatternSolver();

        solvers.startNot();
        solvers.startPatternSolver( pattern );
        solvers.startOperator( OperatorDescrType.AND );
        solvers.addPatternComponent( r3 );
        solvers.addPatternComponent( r4 );
        solvers.endOperator();
        solvers.endPatternSolver();
        solvers.endNot();

        solvers.endOperator();

        solvers.endRuleSolver();

        List<SubRule> list = solvers.getRulePossibilities();
        assertEquals( 1,
                      list.size() );
        assertEquals( 2,
                      list.get( 0 ).getItems().size() );

        List<Restriction> result = new ArrayList<Restriction>();
        result.add( r );
        result.add( r2 );

        List<Restriction> result2 = new ArrayList<Restriction>();
        result2.add( r3 );
        result2.add( r4 );

        Object[] possibilies = list.get( 0 ).getItems().toArray();
        SubPattern p1 = (SubPattern) possibilies[0];
        SubPattern p2 = (SubPattern) possibilies[1];

        /*
         * Order may change but it doesn't matter.
         */
        if ( p1.getItems().containsAll( result ) ) {
            assertTrue( p2.getItems().containsAll( result2 ) );
        } else if ( p1.getItems().containsAll( result2 ) ) {
            assertTrue( p2.getItems().containsAll( result ) );
        } else {
            fail( "No items found." );
        }
    }

    /**
     * <pre>
     * when 
     * 		Foo( descr &amp;&amp; descr2 )
     * </pre>
     * 
     * result:<br>
     * descr && descr2
     */
    public void testBasicAnd() {

        VerifierRule rule = VerifierComponentMockFactory.createRule1();
        Pattern pattern = VerifierComponentMockFactory.createPattern1();

        Restriction r = new LiteralRestriction( pattern );
        Restriction r2 = new LiteralRestriction( pattern );

        Solvers solvers = new Solvers();

        solvers.startRuleSolver( rule );
        solvers.startPatternSolver( pattern );
        solvers.startOperator( OperatorDescrType.AND );
        solvers.addPatternComponent( r );
        solvers.addPatternComponent( r2 );
        solvers.endOperator();
        solvers.endPatternSolver();
        solvers.endRuleSolver();

        List<SubRule> list = solvers.getRulePossibilities();
        assertEquals( 1,
                      list.size() );
        assertEquals( 1,
                      list.get( 0 ).getItems().size() );

        List<Restriction> result = new ArrayList<Restriction>();
        result.add( r );
        result.add( r2 );

        Set<RuleComponent> set = list.get( 0 ).getItems();
        for ( RuleComponent component : set ) {
            SubPattern possibility = (SubPattern) component;
            assertTrue( possibility.getItems().containsAll( result ) );
        }
    }
}
