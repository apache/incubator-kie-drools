/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.integrationtests;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.jupiter.api.Test;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;

import static org.assertj.core.api.Assertions.assertThat;

public class AgendaFilterTest extends AbstractBaseTest {

    public static class Message {

        private String message;

        private int status;

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    @Test
    public void testGetListeners() {
        // JBRULES-3378
        if (builder.hasErrors()) {
            throw new RuntimeException(builder.getErrors().toString());
        }

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        assertThat(kruntime).isNotNull();

        assertThat(kruntime.getKieSession().getAgendaEventListeners()).isNotEmpty();
        assertThat(kruntime.getProcessEventManager().getProcessEventListeners()).isEmpty();
        assertThat(kruntime.getKieSession().getRuleRuntimeEventListeners()).isEmpty();

        kruntime.getKieSession().dispose();
    }
}
