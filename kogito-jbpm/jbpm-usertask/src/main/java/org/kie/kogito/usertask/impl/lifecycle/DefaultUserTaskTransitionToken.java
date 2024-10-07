/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.usertask.impl.lifecycle;

import java.util.Map;

import org.kie.kogito.usertask.lifecycle.UserTaskState;
import org.kie.kogito.usertask.lifecycle.UserTaskTransitionToken;

public class DefaultUserTaskTransitionToken implements UserTaskTransitionToken {

    private String transition;
    private Map<String, Object> data;
    private UserTaskState source;
    private UserTaskState target;

    public DefaultUserTaskTransitionToken(String transition, UserTaskState source, UserTaskState target, Map<String, Object> data) {
        this.transition = transition;
        this.source = source;
        this.target = target;
        this.data = data;
    }

    @Override
    public String transitionId() {
        return transition;
    }

    @Override
    public Map<String, Object> data() {
        return data;
    }

    @Override
    public UserTaskState source() {
        return source;
    }

    @Override
    public UserTaskState target() {
        return target;
    }

}
