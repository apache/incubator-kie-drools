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

package org.drools.games.invaders;

import org.drools.games.GameUI;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Invaders3Main {

    public static void main(String[] args) {
        new Invaders3Main().init(true);
    }

    public Invaders3Main() {
    }

    public void init(boolean exitOnClose) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        final KieSession ksession = kc.newKieSession( "Invaders3KS");

        InvadersConfiguration conf = new InvadersConfiguration();
        conf.setExitOnClose(exitOnClose);

        GameUI ui = new GameUI(ksession, conf);
        ui.init();

        ksession.setGlobal( "conf", conf );
        ksession.setGlobal( "ui", ui );

        runKSession(ksession);
    }

    public void runKSession(final KieSession ksession) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(new Runnable() {
            public void run() {
                // run forever
                try {
                    ksession.fireUntilHalt();
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        });
    }
}
