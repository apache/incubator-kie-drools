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

package org.drools.compiler.integrationtests;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.drools.compiler.Alarm;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Message;
import org.drools.compiler.Sensor;
import org.drools.core.ObjectFilter;
import org.drools.core.base.mvel.MVELDebugHandler;
import org.drools.core.event.DefaultAgendaEventListener;
import org.junit.Test;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.logger.KnowledgeRuntimeLogger;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;
import org.mvel2.MVELRuntime;
import org.mvel2.debug.Debugger;
import org.mvel2.debug.Frame;

/**
 * This is a sample class to launch a rule.
 */
public class ActivateAndDeleteOnListenerTest extends CommonTestMethodBase {

    @Test
    public void testActivateOnMatchAndDelete() throws Exception {

        String str = "";
        str += "package org.drools.compiler.integrationtests \n";
        str += "import " + Alarm.class.getCanonicalName() + " \n";
        str += "import " + Sensor.class.getCanonicalName() + " \n";
        str += "rule StringRule  @Propagation(EAGER) ruleflow-group \"DROOLS_SYSTEM\"\n";
        str += " when \n";
        str += " $a : Alarm() \n";
        str += " $s : Sensor() \n";
        str += " then \n";
        str += "end \n";

        // load up the knowledge base
        KnowledgeBase kbase = readKnowledgeBase(str);
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase, ForceEagerActivationOption.YES);

        ksession.addEventListener(new DefaultAgendaEventListener(){
            @Override
            public void matchCreated(MatchCreatedEvent event) {

                Collection<? extends FactHandle> alarms = event.getKieRuntime().getFactHandles(new ObjectFilter() {
                    @Override
                    public boolean accept(Object o) {
                        if (o instanceof Alarm) {
                            return true;
                        }

                        return false;
                    }
                });

                for (FactHandle alarm : alarms) {
                    event.getKieRuntime().delete(alarm);
                }
            }
        });

        // go !
        Alarm alarm = new Alarm();
        alarm.setMessage("test");
        alarm.setNumber(123);

        ksession.insert(alarm);

        Sensor sensor = new Sensor();
        sensor.setPressure(1);
        sensor.setTemperature(25);

        ksession.insert(sensor);
    }

    @Test
    public void testActivateOnMatchAndUpdate() throws Exception {

        String str = "";
        str += "package org.drools.compiler.integrationtests \n";
        str += "import " + Alarm.class.getCanonicalName() + " \n";
        str += "import " + Sensor.class.getCanonicalName() + " \n";
        str += "rule StringRule  @Propagation(EAGER) ruleflow-group \"DROOLS_SYSTEM\"\n";
        str += " when \n";
        str += " $a : Alarm() \n";
        str += " $s : Sensor() \n";
        str += " then \n";
        str += "end \n";

        // load up the knowledge base
        KnowledgeBase kbase = readKnowledgeBase(str);
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase, ForceEagerActivationOption.YES);

        ksession.addEventListener(new DefaultAgendaEventListener(){
            @Override
            public void matchCreated(MatchCreatedEvent event) {
                Collection<? extends FactHandle> alarms = event.getKieRuntime().getFactHandles(new ObjectFilter() {
                    @Override
                    public boolean accept(Object o) {
                        if (o instanceof Alarm) {
                            return true;
                        }

                        return false;
                    }
                });

                for (FactHandle alarm : alarms) {
                    event.getKieRuntime().update(alarm, new Alarm());
                }
            }
        });

        // go !
        Alarm alarm = new Alarm();
        alarm.setMessage("test");
        alarm.setNumber(123);

        ksession.insert(alarm);

        Sensor sensor = new Sensor();
        sensor.setPressure(1);
        sensor.setTemperature(25);

        ksession.insert(sensor);
    }



    private KnowledgeBase readKnowledgeBase(String drl) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(
                ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
           fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
    }

}
