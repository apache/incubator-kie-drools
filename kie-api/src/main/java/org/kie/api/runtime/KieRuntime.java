/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.runtime;

import java.util.Map;

import org.kie.api.KieBase;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.api.runtime.rule.RuleRuntime;
import org.kie.api.time.SessionClock;

public interface KieRuntime
    extends
    RuleRuntime,
    ProcessRuntime,
    KieRuntimeEventManager {

    /**
     * @return the session clock instance assigned to this session
     */
    <T extends SessionClock> T getSessionClock();

    /**
     * Sets a global value in this session
     * @param identifier the global identifier
     * @param value the value assigned to the global identifier
     */
    void setGlobal(String identifier,
                   Object value);

    Object getGlobal(String identifier);

    Globals getGlobals();

    Calendars getCalendars();

    Environment getEnvironment();

    /**
     * @return the KieBase reference from which this stateful session was created.
     */
    KieBase getKieBase();

    void registerChannel(String name,
                         Channel channel);

    void unregisterChannel(String name);

    Map< String, Channel> getChannels();

    KieSessionConfiguration getSessionConfiguration();
}
