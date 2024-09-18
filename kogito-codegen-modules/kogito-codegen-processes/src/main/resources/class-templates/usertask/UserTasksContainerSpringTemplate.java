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
package org.kie.kogito.usertask.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.kie.kogito.usertask.UserTask;

@org.springframework.web.context.annotation.ApplicationScope
@org.springframework.stereotype.Component
public class UserTasks implements org.kie.kogito.usertask.UserTasks {
    
    @org.springframework.beans.factory.annotation.Autowired
    Collection<UserTask> userTasks;

    private Map<String, UserTask> mappedUserTask = new HashMap<>();
    
    @jakarta.annotation.PostConstruct
    public void setup() {
        for (UserTask userTask : userTasks) {
            mappedUserTask.put(userTask.id(), userTask);
        }
    }
    
    public UserTask userTaskById(String userTaskId) {
        return mappedUserTask.get(userTaskId);
    }

    public Collection<String> userTaskIds() {
        return mappedUserTask.keySet();
    }
}