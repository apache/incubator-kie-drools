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

package org.drools.example.cdi.cdiexample;

import java.io.PrintStream;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;

public class CDIInstanceExample {

    @Inject
    @KSession("ksession-optional")
    private Instance<KieSession> kSession;

    public void go(PrintStream out) {

        KieSession ksession = kSession.get();

        ksession.setGlobal("out", out);
        ksession.insert(new Message("Dave", "Hello, HAL. Do you read me, HAL?"));
        ksession.fireAllRules();
    }

    public static void main(String[] args) {
        Weld w = new Weld();

        WeldContainer wc = w.initialize();
        CDIInstanceExample bean = wc.instance().select(CDIInstanceExample.class).get();
        bean.go(System.out);

        w.shutdown();
    }

}
