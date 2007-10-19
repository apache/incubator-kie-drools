package org.drools.rule;

import java.util.Calendar;

import junit.framework.TestCase;

/**
 * @author Michael Neale
 */
public class RuleTest extends TestCase {

    public void testDateEffective() {
        final Rule rule = new Rule( "myrule" );

        assertTrue( rule.isEffective(new TimeMachine()) );

        final Calendar earlier = Calendar.getInstance();
        earlier.setTimeInMillis( 10 );

        rule.setDateEffective( earlier );

        assertTrue( rule.isEffective(new TimeMachine()) );

        final Calendar later = Calendar.getInstance();
        later.setTimeInMillis( later.getTimeInMillis() + 100000000 );

        assertTrue( later.after( Calendar.getInstance() ) );

        rule.setDateEffective( later );
        assertFalse( rule.isEffective(new TimeMachine()) );

    }

    public void testDateExpires() throws Exception {
        final Rule rule = new Rule( "myrule" );

        assertTrue( rule.isEffective(new TimeMachine()) );

        final Calendar earlier = Calendar.getInstance();
        earlier.setTimeInMillis( 10 );

        rule.setDateExpires( earlier );

        assertFalse( rule.isEffective(new TimeMachine()) );

        final Calendar later = Calendar.getInstance();
        later.setTimeInMillis( later.getTimeInMillis() + 100000000 );

        rule.setDateExpires( later );
        assertTrue( rule.isEffective(new TimeMachine()) );

    }

    public void testDateEffectiveExpires() {
        final Rule rule = new Rule( "myrule" );

        final Calendar past = Calendar.getInstance();
        past.setTimeInMillis( 10 );

        final Calendar future = Calendar.getInstance();
        future.setTimeInMillis( future.getTimeInMillis() + 100000000 );

        rule.setDateEffective( past );
        rule.setDateExpires( future );

        assertTrue( rule.isEffective(new TimeMachine()) );

        rule.setDateExpires( past );
        assertFalse( rule.isEffective(new TimeMachine()) );

        rule.setDateExpires( future );
        rule.setDateEffective( future );



        assertFalse( rule.isEffective(new TimeMachine()) );

    }

    public void testRuleEnabled() {
        final Rule rule = new Rule( "myrule" );
        rule.setEnabled( false );
        assertFalse( rule.isEffective(new TimeMachine()) );

        final Calendar past = Calendar.getInstance();
        past.setTimeInMillis( 10 );

        rule.setDateEffective( past );
        assertFalse( rule.isEffective(new TimeMachine()) );
        rule.setEnabled( true );

        assertTrue( rule.isEffective(new TimeMachine()) );
    }

    public void testTimeMachine() {
        final Rule rule = new Rule( "myrule" );
        rule.setEnabled( true );
        assertTrue(rule.isEffective(new TimeMachine()));

        final Calendar future = Calendar.getInstance();
        future.setTimeInMillis( future.getTimeInMillis() + 100000000 );
        rule.setDateEffective(future);
        assertFalse(rule.isEffective(new TimeMachine()));

        assertTrue(rule.isEffective(new TimeMachine() {
        	public Calendar getNow() {
        		Calendar loveYouLongTime = Calendar.getInstance();
        		loveYouLongTime.setTimeInMillis(future.getTimeInMillis() + 1000000000000L);
        		return loveYouLongTime;
        	}
        }));



    }

}
