/*
 * Created on Mar 19, 2004
 * @author Daniel Selman
 */

package org.javarules.jsr94;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.StatefulRuleSession;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.Rule;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;

import org.drools.jsr94.rules.RuleServiceProviderImpl;

/**
 * Simple test harness for running JSR-94 compliant rule engines
 * and exercising the runtime and admin API.
 * <br/>
 * This class was originally written by Daniel Selman from JavaRules.org
 * and is the "entry exam" for real JSR94 TCK testing. I've modified it
 * a little bit from his original version and kept a copy of it here for
 * the sake of posterity.
 *
 * @author N. Alex Rupp
 * @author Daniel Selman
 */

public class Main
{
    public static void main( String[] args ) throws Exception
    {
        String DROOLS_RULE_SERVICE_PROVIDER_CLASS =
            "org.drools.jsr94.rules.RuleServiceProviderImpl";
        String DROOLS_RULE_SERVICE_PROVIDER = "http://drools.org/";
        RuleServiceProviderManager.registerRuleServiceProvider(
            DROOLS_RULE_SERVICE_PROVIDER, RuleServiceProviderImpl.class );
        Class.forName( DROOLS_RULE_SERVICE_PROVIDER_CLASS );
        runRuleset( DROOLS_RULE_SERVICE_PROVIDER, "testagent.drl", "Gecko" );
    }

    public static void runRuleset(
            String ruleServiceProvider, String fileName, String userAgent )
        throws Exception
    {
        log( "Rule Execution Set to run: " + fileName );
        log( "Administrator API - Parse and Deploy RuleSet" );
        // Get the rule service provider from the provider manager.
        RuleServiceProvider serviceProvider =
            RuleServiceProviderManager.getRuleServiceProvider(
                ruleServiceProvider );
        // get the RuleAdministrator
        RuleAdministrator ruleAdministrator =
            serviceProvider.getRuleAdministrator( );
        // get the RuleAdministrator
        log( "Got RuleAdministrator implementation: " + ruleAdministrator );
        // get an input stream to the file to load
        InputStream inStream = Main.class.getResourceAsStream( fileName );
        log( "Acquired InputStream to input file: " + inStream );
        // load a RuleExecutionSet
        RuleExecutionSet ruleExecutionSet =
            ruleAdministrator.getLocalRuleExecutionSetProvider( null )
                .createRuleExecutionSet( inStream, null );
        log( "Loaded RuleExecutionSet: " + ruleExecutionSet );
        // print the metadata about the ruleset
        log( "Name: " + ruleExecutionSet.getName( ) );
        log( "Description: " + ruleExecutionSet.getDescription( ) );
        log( "Rules:" );
        // print the names of the rules in the ruleset
        for ( Iterator it = ruleExecutionSet.getRules( ).iterator( );
              it.hasNext( ); )
        {
            Rule rule = ( Rule ) it.next( );
            log( rule.getName( ) );
        }
        // register the ruleset under a "random" URI
        String uri = "uri_" + System.currentTimeMillis( );
        ruleAdministrator
                         .registerRuleExecutionSet( uri, ruleExecutionSet, null );
        log( "Bound RuleExecutionSet to URI: " + uri );
        log( "Runtime API - Query Deployed RuleSets" );
        RuleRuntime ruleRuntime = serviceProvider.getRuleRuntime( );
        // get the RuleRuntime from JNDI
        log( "Acquired RuleRuntime: " + ruleRuntime );
        // print the uris of the registered rulesets
        List uriList = ruleRuntime.getRegistrations( );
        log( "Registered RuleExecutionSets:" );
        for ( Iterator it = uriList.iterator( ); it.hasNext( ); )
        {
            log( "Ruleset URI: " + it.next( ) );
        }
        log( "Runtime API - Stateless Execution" );
        // create a StatelessRuleSession
        StatelessRuleSession statelessRuleSession = ( StatelessRuleSession )
            ruleRuntime.createRuleSession(
                uri, new HashMap( ), RuleRuntime.STATELESS_SESSION_TYPE );
        log( "Got Stateful Rule Session " + uri );
        log( "Implementation: " + statelessRuleSession );
        // add the user's User-Agent string to the session and execute it
        List inputList = new LinkedList( );
        inputList.add( userAgent );
        log( "Adding browser User-Agent to session: " + userAgent );
        List resultList = statelessRuleSession.executeRules( inputList );
        log( "Called executeRules on Stateless Rule Session: "
            + statelessRuleSession );
        log( "Result of calling executeRules: " + resultList );
        // release the session
        statelessRuleSession.release( );
        log( "Released Stateless Rule Session." );
        log( "Runtime API - Stateful Execution" );
        // create a StatefulRuleSession
        StatefulRuleSession statefulRuleSession = ( StatefulRuleSession )
            ruleRuntime.createRuleSession(
                uri, new HashMap( ), RuleRuntime.STATEFUL_SESSION_TYPE );
        log( "Got Stateful Rule Session " + uri );
        log( ": " + statefulRuleSession );
        // add an Object to the statefulRuleSession
        log( "Adding browser User-Agent to session: " + userAgent );
        statefulRuleSession.addObjects( inputList );
        log( "Called addObject on Stateful Rule Session: "
            + statefulRuleSession );
        statefulRuleSession.executeRules( );
        log( "Called executeRules" );
        // extract the Objects from the statefulRuleSession
        resultList = statefulRuleSession.getObjects( );
        log( "Result of calling getObjects: " + resultList );
        // release the statefulRuleSession so it is reset and repooled
        statefulRuleSession.release( );
        log( "Released Stateful Rule Session." );
    }

    private static void log( String msg )
    {
        System.out.println( msg );
    }
}

