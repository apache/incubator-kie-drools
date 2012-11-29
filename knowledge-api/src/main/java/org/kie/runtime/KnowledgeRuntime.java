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

package org.kie.runtime;

import org.kie.event.KnowledgeRuntimeEventManager;
import org.kie.runtime.process.ProcessRuntime;
import org.kie.runtime.rule.WorkingMemory;

public interface KnowledgeRuntime
    extends
    WorkingMemory,
    ProcessRuntime,
    KnowledgeRuntimeEventManager,
    KieRuntime {

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

}
