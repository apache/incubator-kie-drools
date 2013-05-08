/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.internal.fluent.runtime.process;

import java.util.Map;

import org.kie.api.runtime.process.ProcessRuntime;

/**
 * see {@link ProcessRuntime}
 */
public interface ProcessFluent<T> {

    T startProcess(String identifier, Map<String, Object> params);

    T startProcess(String identifier);

    T createProcessInstance(String identifier, Map<String, Object> params);

    T startProcessInstance(long processId);

    T signalEvent(String id, Object event, long processId);

    T signalEvent(String id, Object event);

    T abortProcessInstance(long processInstanceId);
}
