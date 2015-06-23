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

package org.drools.examples.circularTms;

import org.drools.examples.buspass.Person;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import java.util.Scanner;

public class CircularTmsExample {
    public static void main(final String[] args) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        KieSession ksession = kc.newKieSession("CircularTmsKS");

        FactHandle fh = ksession.insert( "A" );
        ksession.fireAllRules();
        pause();

        ksession.delete( fh );
        ksession.fireAllRules();

        ksession.dispose(); // Stateful rule session must always be disposed when finished
    }

    public static void pause() {
        System.out.println( "Pressure enter to contnue" );
        Scanner keyboard = new Scanner(System.in);
        keyboard.nextLine();
    }
}
