/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.impl.domain.variable.listener;

import java.util.ArrayList;
import java.util.List;

public class PlanningVariableListenerSupport {

    private List<PlanningVariableListener> variableListenerList = new ArrayList<PlanningVariableListener>();

    public void beforeEntityAdded(Object entity) {

    }

    public void afterEntityAdded(Object entity) {

    }

    public void beforeVariableChanged(Object entity, String variableName) {

    }

    public void afterVariableChanged(Object entity, String variableName) {

    }

    public void beforeEntityRemoved(Object entity) {

    }

    public void afterEntityRemoved(Object entity) {

    }

}
