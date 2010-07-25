/**
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

package org.drools.runtime;

import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.event.KnowledgeRuntimeEventManager;
import org.drools.runtime.process.ProcessRuntime;
import org.drools.runtime.rule.WorkingMemory;
import org.drools.time.SessionClock;

public interface KnowledgeRuntime
    extends
    WorkingMemory,
    ProcessRuntime,
    KnowledgeRuntimeEventManager {

    /**
     * Returns the session clock instance assigned to this session
     * @return
     */
    public <T extends SessionClock> T getSessionClock();

    /**
     * Sets a global value on the internal collection
     * @param identifer the global identifier
     * @param value the value assigned to the global identifier
     */
    void setGlobal(String identifier,
                   Object object);

    Object getGlobal(String identifier);

    Globals getGlobals();

    Calendars getCalendars();

    Environment getEnvironment();

    /**
     * Returns the KnowledgeBase reference from which this stateful session was created.
     * 
     * @return
     */
    KnowledgeBase getKnowledgeBase();

    /**
     * @deprecated Use {@link #registerChannel(String, Channel)} instead.
     */
    @Deprecated
    void registerExitPoint(String name,
                           ExitPoint exitPoint);

    /**
     * @deprecated Use {@link #unregisterChannel(String)} instead.
     */
    @Deprecated
    void unregisterExitPoint(String name);

    void registerChannel(String name,
                         Channel channel);

    void unregisterChannel(String name);

    Map< String, Channel> getChannels();

}
