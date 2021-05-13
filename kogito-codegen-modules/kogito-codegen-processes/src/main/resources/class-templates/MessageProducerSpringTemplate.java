/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.test;

import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.services.event.impl.AbstractMessageProducer;

@org.springframework.stereotype.Component()
public class MessageProducer extends AbstractMessageProducer<$DataType$, $DataEventType$> {

    @org.springframework.beans.factory.annotation.Autowired()
    MessageProducer(EventEmitter emitter) {
        super(emitter,"$Trigger$");
    }

    protected $DataEventType$ dataEventTypeConstructor($DataType$ e, KogitoProcessInstance pi, String trigger) {
        return new $DataEventType$(
                trigger,
                "",
                e,
                pi.getStringId(),
                pi.getParentProcessInstanceStringId(),
                pi.getRootProcessInstanceId(),
                pi.getProcessId(),
                pi.getRootProcessId(),
                String.valueOf(pi.getState()),
                pi.getReferenceId());
    }
}
