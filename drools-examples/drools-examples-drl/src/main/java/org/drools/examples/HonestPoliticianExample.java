package org.drools.examples;

import java.io.File;
import java.io.InputStreamReader;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.runtime.StatefulKnowledgeSession;

public class HonestPoliticianExample {

    /**
     * @param args
     */
    public static void main(final String[] args) throws Exception {

        KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        conf.setProperty( "drools.dump.dir",
                          "target" );

        final KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.addResource( new InputStreamReader( HonestPoliticianExample.class.getResourceAsStream( "HonestPolitician.drl" ) ),
                             KnowledgeType.DRL );

        final KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( builder.getKnowledgePackages() );

        final StatefulKnowledgeSession session = knowledgeBase.newStatefulKnowledgeSession();

        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( session );
        logger.setFileName( "log/honest-politician" );

        final Politician blair = new Politician( "blair",
                                                 true );
        final Politician bush = new Politician( "bush",
                                                true );
        final Politician chirac = new Politician( "chirac",
                                                  true );
        final Politician schroder = new Politician( "schroder",
                                                    true );

        session.insert( blair );
        session.insert( bush );
        session.insert( chirac );
        session.insert( schroder );

        session.fireAllRules();

        logger.writeToDisk();

        session.dispose();
    }

    public static class Politician {
        private String  name;

        private boolean honest;

        public Politician() {

        }

        public Politician(String name,
                          boolean honest) {
            super();
            this.name = name;
            this.honest = honest;
        }

        public boolean isHonest() {
            return honest;
        }

        public void setHonest(boolean honest) {
            this.honest = honest;
        }

        public String getName() {
            return name;
        }
    }

    public static class Hope {

        public Hope() {

        }

    }

}
