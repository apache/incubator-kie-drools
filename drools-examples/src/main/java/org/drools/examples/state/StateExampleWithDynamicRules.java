/*
 * Copyright 2010 JBoss Inc
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

package org.drools.examples.state;

import java.io.IOException;
import java.io.InputStreamReader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;

public class StateExampleWithDynamicRules {

    /**
     * @param args
     */
    public static void main(final String[] args) {

        PackageBuilder builder = new PackageBuilder();
        try {
            builder.addPackageFromDrl( new InputStreamReader( StateExampleWithDynamicRules.class.getResourceAsStream( "StateExampleUsingSalience.drl" ) ) );
        } catch (DroolsParserException e) {
            throw new IllegalArgumentException("Invalid drl", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read drl", e);
        }

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        final StatefulSession session = ruleBase.newStatefulSession();

//        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( session );
//        logger.setFileName( "log/state" );

        final State a = new State( "A" );
        final State b = new State( "B" );
        final State c = new State( "C" );
        final State d = new State( "D" );
        final State e = new State( "E" );

        // By setting dynamic to TRUE, Drools will use JavaBean
        // PropertyChangeListeners so you don't have to call update().
        final boolean dynamic = true;

        session.insert( a,
                        dynamic );
        session.insert( b,
                        dynamic );
        session.insert( c,
                        dynamic );
        session.insert( d,
                        dynamic );
        session.insert( e,
                        dynamic );

        session.fireAllRules();

        builder = new PackageBuilder();
        try {
            builder.addPackageFromDrl( new InputStreamReader( StateExampleWithDynamicRules.class.getResourceAsStream( "StateExampleDynamicRule.drl" ) ) );
        } catch (DroolsParserException ex) {
            throw new IllegalArgumentException("Invalid drl", ex);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not read drl", ex);
        }
        ruleBase.addPackage( builder.getPackage() );

        session.fireAllRules();
        session.dispose();

//        logger.writeToDisk();
    }

}
