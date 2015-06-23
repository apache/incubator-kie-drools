/*
 * Copyright 2015 JBoss Inc
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

package org.drools.examples.buspass;

import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.io.ResourceFactory;

import java.util.Scanner;

public class BussPassJTMSExample {
    public static void main(final String[] args) {
        KieContainer kc;
        KieSession ksession;

        try {
            System.setProperty("drools.negatable", "on");
            kc = KieServices.Factory.get().getKieClasspathContainer();
            ksession = kc.newKieSession("BussPassJTMSKS");
        } catch( Exception e) {
            throw new RuntimeException( e );
        } finally {
            System.setProperty("drools.negatable", "off");
        }

        Person yoda = new Person("Yoda", 15);
        ksession.insert(yoda);
        FactHandle badYodaFh = ksession.insert(new BadBehaviour(yoda));
        ksession.fireAllRules();
        pause();

        Person darth = new Person("Darth", 15);
        ksession.insert(darth);
        ksession.fireAllRules();

        ksession.dispose(); // Stateful rule session must always be disposed when finished
    }

    public static void pause() {
        System.out.println( "Pressure enter to continue" );
        Scanner keyboard = new Scanner(System.in);
        keyboard.nextLine();
    }
}
