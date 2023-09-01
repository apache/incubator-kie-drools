package org.drools.examples.state;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class StateExampleUsingAgendaGroup {

    /**
     * @param args
     */
    public static void main(final String[] args) {
        // KieServices is the factory for all KIE services 
        KieServices ks = KieServices.Factory.get();
        
        // From the kie services, a container is created from the classpath
        KieContainer kc = ks.getKieClasspathContainer();
        execute( kc );
    }

    public static void execute( KieContainer kc ) {
        // From the container, a session is created based on
        // its definition and configuration in the META-INF/kmodule.xml file
        KieSession ksession = kc.newKieSession("StateAgendaGroupKS");

        // To setup a file based audit logger, uncomment the next line
        // KieRuntimeLogger logger = ks.getLoggers().newFileLogger( ksession, "./state" );

        final State a = new State( "A" );
        final State b = new State( "B" );
        final State c = new State( "C" );
        final State d = new State( "D" );

        ksession.insert( a );
        ksession.insert( b );
        ksession.insert( c );
        ksession.insert( d );

        ksession.fireAllRules();

        // logger.close();

        ksession.dispose(); // Stateful rule session must always be disposed when finished
    }

}
