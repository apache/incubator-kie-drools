package org.drools.examples;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class GolfingExample {

    /**
     * @param args
     */
    public static void main(final String[] args) throws Exception {

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "golf.drl",
                                                                    GolfingExample.class ),
                              ResourceType.DRL );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        String[] names = new String[]{"Fred", "Joe", "Bob", "Tom"};
        String[] colors = new String[]{"red", "blue", "plaid", "orange"};
        int[] positions = new int[]{1, 2, 3, 4};

        for ( int n = 0; n < names.length; n++ ) {
            for ( int c = 0; c < colors.length; c++ ) {
                for ( int p = 0; p < positions.length; p++ ) {
                    ksession.insert( new Golfer( names[n],
                                                 colors[c],
                                                 positions[p] ) );
                }
            }
        }

        ksession.fireAllRules();

        ksession.dispose();

    }

    public static class Golfer {
        private String name;
        private String color;
        private int    position;

        public Golfer() {

        }

        public Golfer(String name,
                      String color,
                      int position) {
            super();
            this.name = name;
            this.color = color;
            this.position = position;
        }

        /**
         * @return the color
         */
        public String getColor() {
            return this.color;
        }

        /**
         * @return the name
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return the name
         */
        public int getPosition() {
            return this.position;
        }

    }
}
