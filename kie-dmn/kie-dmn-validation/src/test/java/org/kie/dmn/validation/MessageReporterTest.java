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
package org.kie.dmn.validation;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNMessage.Severity;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.v1_3.TDecision;

import static org.assertj.core.api.Assertions.assertThat;

class MessageReporterTest {

    private static final Msg.Message0 m0 = new Msg.Message0(DMNMessageType.KIE_API, "Hello World.");
    private static final Msg.Message1 m1 = new Msg.Message1(DMNMessageType.KIE_API, "Hello World %s");
    private static final Msg.Message2 m2 = new Msg.Message2(DMNMessageType.KIE_API, "Hello World %s %s");
    private static final Msg.Message3 m3 = new Msg.Message3(DMNMessageType.KIE_API, "Hello World %s %s %s");
    private static final Msg.Message4 m4 = new Msg.Message4(DMNMessageType.KIE_API, "Hello World %s %s %s %s");

    @Test
    void smokeTest() {
        MessageReporter ut = new MessageReporter(null);
        ut.report(Severity.INFO, aDecision(), m0);
        ut.report(Severity.INFO, aDecision(), m1, 1);
        ut.report(Severity.INFO, aDecision(), m2, 1, 2);
        ut.report(Severity.INFO, aDecision(), m3, 1, 2, 3);
        ut.report(Severity.INFO, aDecision(), m4, 1, 2, 3, 4);
        assertThat(ut.getMessages().getMessages()).hasSize(5);
        assertThat(ut.getMessages().getMessages().get(0).getText()).contains("Hello World");
        assertThat(ut.getMessages().getMessages().get(1).getText()).contains("Hello World 1");
        assertThat(ut.getMessages().getMessages().get(2).getText()).contains("Hello World 1 2");
        assertThat(ut.getMessages().getMessages().get(3).getText()).contains("Hello World 1 2 3");
        assertThat(ut.getMessages().getMessages().get(4).getText()).contains("Hello World 1 2 3 4");
    }

    private DMNModelInstrumentedBase aDecision() {
        Decision d = new TDecision();
        d.setId(UUID.randomUUID().toString());
        return d;
    }
}
