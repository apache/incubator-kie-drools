/*
 * Copyright 2010 JBoss Inc
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

import org.kie.api.KieBase;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.api.runtime.rule.Session;
import org.kie.api.time.SessionClock;

import java.util.Map;

public interface KieRuntime
    extends
    Session,
    ProcessRuntime,
    KieRuntimeEventManager {

    /**
     * Returns the session clock instance assigned to this session
     * @return
     */
    public <T extends SessionClock> T getSessionClock();

    /**
     * Sets a global value on the internal collection
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
     * Returns the KieBase reference from which this stateful session was created.
     */
    KieBase getKieBase();

    void registerChannel(String name,
                         Channel channel);

    void unregisterChannel(String name);

    Map< String, Channel> getChannels();
    
    KieSessionConfiguration getSessionConfiguration();

}
