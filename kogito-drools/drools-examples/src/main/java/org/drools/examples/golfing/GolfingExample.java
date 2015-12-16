/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.examples.golfing;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class GolfingExample {

    /**
     * @param args
     */
    public static void main(final String[] args) {

        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        System.out.println(kc.verify().getMessages().toString());
        execute( kc );
    }

    public static void execute( KieContainer kc ) {
        KieSession ksession = kc.newKieSession("GolfingKS");

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
