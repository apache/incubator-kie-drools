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

package org.drools.example.api.kiecontainerfromkierepo;

import org.drools.core.util.Drools;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.PrintStream;

public class KieContainerFromKieRepoExample {

    public void go(PrintStream out) {
        KieServices ks = KieServices.Factory.get();

        // Install example1 in the local maven repo before to do this
        KieContainer kContainer = ks.newKieContainer(ks.newReleaseId("org.drools", "named-kiesession", Drools.getFullVersion()));

        KieSession kSession = kContainer.newKieSession("ksession1");
        kSession.setGlobal("out", out);

        Object msg1 = createMessage(kContainer, "Dave", "Hello, HAL. Do you read me, HAL?");
        kSession.insert(msg1);
        kSession.fireAllRules();
    }

    public static void main(String[] args) {
        new KieContainerFromKieRepoExample().go(System.out);
    }

    private static Object createMessage(KieContainer kContainer, String name, String text) {
        Object o = null;
        try {
            Class cl = kContainer.getClassLoader().loadClass("org.drools.example.api.namedkiesession.Message");
            o = cl.getConstructor(new Class[]{String.class, String.class}).newInstance(name, text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

}
