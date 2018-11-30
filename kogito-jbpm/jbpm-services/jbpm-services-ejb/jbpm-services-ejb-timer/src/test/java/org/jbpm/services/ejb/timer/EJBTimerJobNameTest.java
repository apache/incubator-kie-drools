/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.services.ejb.timer;

import java.util.Collections;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.time.impl.IntervalTrigger;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.process.instance.timer.TimerManager.StartProcessJobContext;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.EnvironmentName;

public class EJBTimerJobNameTest {

    @Test
    public void testStartProcessJobContextUniqueJobNamesAmongSessions() {

        // we set the same process ID 
        StartProcessJobContext ctxA = new StartProcessJobContext(new TimerInstance(), new IntervalTrigger(), "processId", Collections.emptyMap(), createRuntime(1L, "container-1"));
        StartProcessJobContext ctxB = new StartProcessJobContext(new TimerInstance(), new IntervalTrigger(), "processId", Collections.emptyMap(), createRuntime(2L, "container-2"));

        EjbSchedulerService service = new EjbSchedulerService();
        String jobNameA = service.getJobName(ctxA, 1L);
        String jobNameB = service.getJobName(ctxB, 1L);
        Assert.assertNotEquals(jobNameA, jobNameB);
    }

    private InternalKnowledgeRuntime createRuntime(Long id, String containerId) {
        InternalKnowledgeBase kBase2 = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSessionImpl knowledge = new StatefulKnowledgeSessionImpl(id, kBase2);
        knowledge.getEnvironment().set(EnvironmentName.DEPLOYMENT_ID, containerId);
        return knowledge;
    }
}
