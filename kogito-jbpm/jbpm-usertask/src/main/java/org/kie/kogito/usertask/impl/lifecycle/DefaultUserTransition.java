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

import org.kie.kogito.usertask.lifecycle.UserTaskState;
import org.kie.kogito.usertask.lifecycle.UserTaskTransition;
import org.kie.kogito.usertask.lifecycle.UserTaskTransitionExecutor;

public class DefaultUserTransition implements UserTaskTransition {

    private String id;
    private UserTaskState source;
    private UserTaskState target;
    private UserTaskTransitionExecutor executor;

    public DefaultUserTransition(String id, UserTaskState source, UserTaskState target, UserTaskTransitionExecutor executor) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.executor = executor;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public UserTaskState source() {
        return source;
    }

    @Override
    public UserTaskState target() {
        return target;
    }

    @Override
    public UserTaskTransitionExecutor executor() {
        return executor;
    }

}
