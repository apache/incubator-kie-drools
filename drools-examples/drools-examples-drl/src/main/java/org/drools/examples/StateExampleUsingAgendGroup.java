package org.drools.examples;

import java.io.InputStreamReader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.compiler.PackageBuilder;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.DefaultAgendaEventListener;

public class StateExampleUsingAgendGroup {

    /**
     * @param args
     */
    public static void main(final String[] args) throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( StateExampleUsingAgendGroup.class.getResourceAsStream( "StateExampleUsingAgendGroup.drl" ) ) );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        final StatefulSession session = ruleBase.newStatefulSession();

        session.addEventListener( new DefaultAgendaEventListener() {
            public void afterActivationFired(final AfterActivationFiredEvent arg0) {
                super.afterActivationFired( arg0,
                                            session );
            }
        } );

        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( session );
        logger.setFileName( "log/state" );

        final State a = new State( "A" );
        final State b = new State( "B" );
        final State c = new State( "C" );
        final State d = new State( "D" );

        // By setting dynamic to TRUE, Drools will use JavaBean
        // PropertyChangeListeners so you don't have to call modifyObject().
        final boolean dynamic = true;

        session.insert( a,
                        dynamic );
        session.insert( b,
                        dynamic );
        session.insert( c,
                        dynamic );
        session.insert( d,
                        dynamic );

        session.fireAllRules();
        session.dispose();

        logger.writeToDisk();
    }

}
