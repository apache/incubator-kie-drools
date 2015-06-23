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

package org.drools.compiler.integrationtests.eventgenerator;

import org.kie.api.runtime.KieSession;


public abstract class AbstractEventListener {

    KieSession ksession;

    public AbstractEventListener(KieSession ksession) {
        this.ksession = ksession;
    }

    public void addEventToWM (Event ev){
        ksession.insert(ev);
        ksession.fireAllRules();
    }

    // send generated event and execute corresponding actions
    public abstract void generatedEventSent(Event e);

}
