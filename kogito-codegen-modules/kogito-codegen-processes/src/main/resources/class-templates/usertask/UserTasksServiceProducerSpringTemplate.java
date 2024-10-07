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
package $Package$;

import java.util.List;
import org.kie.kogito.Application;
import org.kie.kogito.internal.process.workitem.KogitoWorkItemHandler;
import org.kie.kogito.jbpm.usertask.handler.UserTaskKogitoWorkItemHandler;
import org.kie.kogito.jbpm.usertask.handler.UserTaskKogitoWorkItemHandlerProcessListener;
import org.kie.kogito.process.ProcessService;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.ProcessServiceImpl;
import org.kie.kogito.usertask.UserTaskEventListenerConfig;
import org.kie.kogito.usertask.UserTaskAssignmentStrategyConfig;
import org.kie.kogito.usertask.UserTaskAssignmentStrategy;
import org.kie.kogito.usertask.UserTaskEventListener;
import org.kie.kogito.usertask.impl.DefaultUserTaskEventListenerConfig;
import org.kie.kogito.usertask.impl.DefaultUserTaskAssignmentStrategyConfig;
import org.kie.kogito.usertask.impl.UserTaskServiceImpl;
import org.kie.kogito.usertask.UserTaskService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserTaskServiceProducer {

    @Bean
    public UserTaskService userTaskService(Application application) {
        return new UserTaskServiceImpl(application);
    }

    @Bean
    public UserTaskEventListener userTaskListener(Processes processes) {
        return new UserTaskKogitoWorkItemHandlerProcessListener(processes);
    }

    @Bean
    public KogitoWorkItemHandler userTaskWorkItemHandler(Application application) {
        return new UserTaskKogitoWorkItemHandler(application);
    }

    @Bean 
    public UserTaskEventListenerConfig userTaskEventListenerConfig(List<UserTaskEventListener> userTaskEventListener) {
        return new DefaultUserTaskEventListenerConfig(userTaskEventListener);
    }
    
    @Bean 
    public UserTaskAssignmentStrategyConfig userTaskAssignmentStrategyConfig(List<UserTaskAssignmentStrategy> userTaskAssignmentStrategy) {
        return new DefaultUserTaskAssignmentStrategyConfig(userTaskAssignmentStrategy);
    }
}
