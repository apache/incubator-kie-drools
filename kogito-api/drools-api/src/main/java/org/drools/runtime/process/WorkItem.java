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

package org.drools.runtime.process;

import java.util.Map;

public interface WorkItem {

    int PENDING   = 0;
    int ACTIVE    = 1;
    int COMPLETED = 2;
    int ABORTED   = 3;

    long getId();

    String getName();

    int getState();

    Object getParameter(String name);

    Map<String, Object> getParameters();

    Object getResult(String name);

    Map<String, Object> getResults();

    long getProcessInstanceId();

}
