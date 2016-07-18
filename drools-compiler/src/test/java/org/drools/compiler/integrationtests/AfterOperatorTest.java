/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.factmodel.events.Event;
import org.drools.compiler.factmodel.events.EventA;
import org.drools.compiler.factmodel.events.EventB;
import org.drools.core.time.SessionPseudoClock;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.utils.KieHelper;

public class AfterOperatorTest extends CommonTestMethodBase {

    @Test(timeout = 10000)
    public void testExpireEventsWhenSharingAllRules() throws InstantiationException, IllegalAccessException {
        final StringBuilder drlBuilder = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            drlBuilder.append(" import " + EventA.class.getCanonicalName() + ";\n");
            drlBuilder.append(" import " + EventB.class.getCanonicalName() + ";\n");
            drlBuilder.append(" declare " + EventA.class.getName() + " @role( event ) @duration(duration) end");
            drlBuilder.append(" declare " + EventB.class.getName() + " @role( event ) @duration(duration) end");
            drlBuilder.append(" rule R" + i + " when \n");
            drlBuilder.append("   $event1: " + EventA.class.getName() + "()\n");
            drlBuilder.append("   $event2: " + EventB.class.getName() + "(this != $event1, this after [1,10] $event1)\n");
            drlBuilder.append( "then end\n" );
        }

        final SortedSet<Event> events = new TreeSet<Event>();
        events.addAll(getEvents(EventA.class, 64 / 2, 2, 100, 0));
        events.addAll(getEvents(EventB.class, 64 / 2, 5, 100, 0));

        final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
        final KieSessionConfiguration sessionConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConf.setOption(ClockTypeOption.get("pseudo"));
        final KieSession kieSession =
                new KieHelper().addContent(drlBuilder.toString(), ResourceType.DRL).build(kieBaseConfiguration).newKieSession(sessionConf, null);

        Assert.assertEquals("The rules didn't fire expected amount of times!", 2048, insertEventsAndFire(kieSession, events));
    }

    private <T extends Event> SortedSet<T> getEvents(final Class<T> eventClass, final int eventsCount,
            final long startTime, final long timeIncrement, long duration) throws IllegalAccessException, InstantiationException {
        final SortedSet<T> resultList = new TreeSet<T>();

        long actualTime = startTime;
        for (int i = 0; i < eventsCount; i++) {
            final T event = eventClass.newInstance();
            event.setTimeValue(actualTime);
            event.setDuration(duration);
            resultList.add(event);
            actualTime = actualTime + timeIncrement;
        }
        return resultList;
    }

    private int insertEventsAndFire(final KieSession kieSession, final SortedSet<Event> events) {
        final SessionPseudoClock sessionClock = kieSession.getSessionClock();

        final long startTime = sessionClock.getCurrentTime();
        int fireCount = 0;
        for (Event event : events) {
            final long eventTime = startTime + event.getTimeValue();
            sessionClock.advanceTime(eventTime - sessionClock.getCurrentTime(), TimeUnit.MILLISECONDS);
            kieSession.insert(event);
            fireCount += kieSession.fireAllRules();
        }
        return fireCount;
    }

}
