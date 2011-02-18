/**
 * Copyright 2005 JBoss Inc
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

package org.drools.event.rule;


import java.util.EventListener;

public interface AgendaEventListener
    extends
    EventListener {
    void activationCreated(ActivationCreatedEvent event);

    void activationCancelled(ActivationCancelledEvent event);

    void beforeActivationFired(BeforeActivationFiredEvent event);

    void afterActivationFired(AfterActivationFiredEvent event);

    void agendaGroupPopped(AgendaGroupPoppedEvent event);

    void agendaGroupPushed(AgendaGroupPushedEvent event);
}