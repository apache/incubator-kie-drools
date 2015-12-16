/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.examples.birdsfly;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.Scanner;

public class BirdsFlyExample {
    public static void main(final String[] args) {
        KieContainer kc;
        KieSession ksession;

        try {
            System.setProperty("drools.negatable", "on");
            kc = KieServices.Factory.get().getKieClasspathContainer();
            ksession = kc.newKieSession("BirdsFlyKS");
        } catch( Exception e) {
            throw new RuntimeException( e );
        } finally {
            System.setProperty("drools.negatable", "off");
        }

        Bird d1 = new Bird( "D1", "Dove" );
        ksession.insert( d1 );
        ksession.fireAllRules();
        pause();

        Bird d2 = new Bird( "D2", "Dove" );
        ksession.insert( d2 );
        Broken broken1 = new Broken( d2, "wing" );
        ksession.insert( broken1 );
        ksession.fireAllRules();
        pause();

        Bird p1 = new Bird( "P1", "Penguin" );
        ksession.insert( p1 );
        ksession.fireAllRules();
        pause();

        Bird p2 = new Bird( "P2", "Penguin" );
        ksession.insert( p2 );
        Rocket r1 = new Rocket( p2 );
        ksession.insert( r1 );
        ksession.fireAllRules();

        ksession.dispose(); // Stateful rule session must always be disposed when finished
    }

    public static void pause() {
        System.out.println( "Pressure enter to continue" );
        Scanner keyboard = new Scanner(System.in);
        keyboard.nextLine();
    }
}
