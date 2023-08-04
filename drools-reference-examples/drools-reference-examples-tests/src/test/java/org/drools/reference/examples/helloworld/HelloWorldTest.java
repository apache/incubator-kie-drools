/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.reference.examples.helloworld;

import java.util.ArrayList;
import java.util.List;

import org.drools.base.util.Drools;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

class HelloWorldTest {

    @Test
    void helloWorld() {
        KieServices kieServices = KieServices.get();
        ReleaseId releaseId = kieServices.newReleaseId("org.drools", "drools-reference-examples-kjar", Drools.getFullVersion());
        KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        KieSession kieSession = kieContainer.newKieSession("HelloWorldKS");

        List<String> list = new ArrayList<>();
        kieSession.setGlobal( "list", list );

        Message message = new Message();
        message.setMessage( "Hello World" );
        message.setStatus( Message.HELLO );
        kieSession.insert( message );

        kieSession.fireAllRules();

        assertThat(list).containsExactly("Hello World", "Goodbye cruel world");

        kieSession.dispose();
    }


}
