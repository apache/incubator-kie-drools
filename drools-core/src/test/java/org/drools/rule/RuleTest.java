package org.drools.rule;

import java.util.Calendar;

import org.drools.base.EnabledBoolean;

import junit.framework.TestCase;

/**
 * @author Michael Neale
 */
public class RuleTest extends TestCase {

    public void testDateEffective() {
        final Rule rule = new Rule( "myrule" );

        assertTrue( rule.isEffective(new TimeMachine(), null, null ) );

        final Calendar earlier = Calendar.getInstance();
        earlier.setTimeInMillis( 10 );

        rule.setDateEffective( earlier );

        assertTrue( rule.isEffective(new TimeMachine(), null, null ) );

        final Calendar later = Calendar.getInstance();
        later.setTimeInMillis( later.getTimeInMillis() + 100000000 );

        assertTrue( later.after( Calendar.getInstance() ) );

        rule.setDateEffective( later );
        assertFalse( rule.isEffective(new TimeMachine(), null, null ) );

    }

    public void testDateExpires() throws Exception {
        final Rule rule = new Rule( "myrule" );

        assertTrue( rule.isEffective(new TimeMachine(), null, null ) );

        final Calendar earlier = Calendar.getInstance();
        earlier.setTimeInMillis( 10 );

        rule.setDateExpires( earlier );

        assertFalse( rule.isEffective(new TimeMachine(), null, null ) );

        final Calendar later = Calendar.getInstance();
        later.setTimeInMillis( later.getTimeInMillis() + 100000000 );

        rule.setDateExpires( later );
        assertTrue( rule.isEffective(new TimeMachine(), null, null ) );

    }

    public void testDateEffectiveExpires() {
        final Rule rule = new Rule( "myrule" );

        final Calendar past = Calendar.getInstance();
        past.setTimeInMillis( 10 );

        final Calendar future = Calendar.getInstance();
        future.setTimeInMillis( future.getTimeInMillis() + 100000000 );

        rule.setDateEffective( past );
        rule.setDateExpires( future );

        assertTrue( rule.isEffective(new TimeMachine(), null, null ) );

        rule.setDateExpires( past );
        assertFalse( rule.isEffective(new TimeMachine(), null, null ) );

        rule.setDateExpires( future );
        rule.setDateEffective( future );



        assertFalse( rule.isEffective(new TimeMachine(), null, null ) );

    }

    public void testRuleEnabled() {
        final Rule rule = new Rule( "myrule" );
        rule.setEnabled( EnabledBoolean.ENABLED_FALSE );
        assertFalse( rule.isEffective(new TimeMachine(), null, null ) );

        final Calendar past = Calendar.getInstance();
        past.setTimeInMillis( 10 );

        rule.setDateEffective( past );
        assertFalse( rule.isEffective(new TimeMachine(), null, null ) );
        rule.setEnabled( EnabledBoolean.ENABLED_TRUE );

        assertTrue( rule.isEffective(new TimeMachine(), null, null ) );
    }

    public void testTimeMachine() {
        final Rule rule = new Rule( "myrule" );
        rule.setEnabled( EnabledBoolean.ENABLED_TRUE );
        assertTrue( rule.isEffective(new TimeMachine(), null, null ) );

        final Calendar future = Calendar.getInstance();
        future.setTimeInMillis( future.getTimeInMillis() + 100000000 );
        rule.setDateEffective(future);
        assertFalse( rule.isEffective(new TimeMachine(), null, null ) );

        assertTrue(rule.isEffective(new TimeMachine() {
        	public Calendar getNow() {
        		Calendar loveYouLongTime = Calendar.getInstance();
        		loveYouLongTime.setTimeInMillis(future.getTimeInMillis() + 1000000000000L);
        		return loveYouLongTime;
        	}
        }, null, null ));
    }

}
