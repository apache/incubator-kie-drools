/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel;

import java.util.ArrayList;
import java.util.List;

import org.drools.model.codegen.execmodel.domain.Person;
import org.junit.Test;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;


public class ChannelTest extends BaseModelTest {

    public ChannelTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    public void testChannel(boolean isMvel) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "rule R \n" +
                     (isMvel ? "dialect \"mvel\"\n" : "dialect \"java\"\n") +
                     "when\n" +
                     "    $p: Person()\n" +
                     "then\n" +
                     "    channels[\"testChannel\"].send(\"Test Message\");\n" +
                     "end\n";

        KieSession ksession = getKieSession(str);
        TestChannel testChannel = new TestChannel();
        ksession.registerChannel("testChannel", testChannel);

        Person me = new Person("Mario");

        ksession.insert(me);
        ksession.fireAllRules();

        assertThat(testChannel.getChannelMessages()).contains("Test Message");
    }

    @Test
    public void testChannelWithJava() {
        testChannel(false);
    }

    @Test
    public void testChannelWithMvel() {
        testChannel(true);
    }

    public static class TestChannel implements Channel {

        private final List<Object> channelMessages = new ArrayList<Object>();

        @Override
        public void send(Object object) {
            channelMessages.add(object);
        }

        public List<Object> getChannelMessages() {
            return channelMessages;
        }
    }
}
