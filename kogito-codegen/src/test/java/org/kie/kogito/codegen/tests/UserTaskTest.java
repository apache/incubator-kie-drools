/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.process.instance.impl.humantask.HumanTaskTransition;
import org.jbpm.process.instance.impl.humantask.phases.Claim;
import org.jbpm.process.instance.impl.humantask.phases.Release;
import org.jbpm.process.instance.impl.workitem.Active;
import org.jbpm.process.instance.impl.workitem.Complete;
import org.junit.jupiter.api.Test;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessWorkItemTransitionEvent;
import org.kie.api.runtime.process.WorkItemNotFoundException;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.codegen.AbstractCodegenTest;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;
import org.kie.kogito.process.workitem.InvalidTransitionException;
import org.kie.kogito.process.workitem.NotAuthorizedException;
import org.kie.kogito.services.identity.StaticIdentityProvider;

public class UserTaskTest extends AbstractCodegenTest {
    
    @Test
    public void testBasicUserTaskProcess() throws Exception {
        
        Application app = generateCodeProcessesOnly("usertask/UserTasksProcess.bpmn2");        
        assertThat(app).isNotNull();
        final List<String> workItemTransitionEvents = new ArrayList<>();
        ((DefaultProcessEventListenerConfig)app.config().process().processEventListeners()).listeners().add(new DefaultProcessEventListener() {

            @Override
            public void beforeWorkItemTransition(ProcessWorkItemTransitionEvent event) {
                workItemTransitionEvents.add("BEFORE:: " + event);
            }

            @Override
            public void afterWorkItemTransition(ProcessWorkItemTransitionEvent event) {
                workItemTransitionEvents.add("AFTER:: " + event);
            }
        });
                
        Process<? extends Model> p = app.processes().processById("UserTasksProcess");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE); 
        
        List<WorkItem> workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        assertEquals("FirstTask", workItems.get(0).getName());
        
        processInstance.completeWorkItem(workItems.get(0).getId(), null);
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        assertEquals("SecondTask", workItems.get(0).getName());
        
        processInstance.completeWorkItem(workItems.get(0).getId(), null);
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        
        assertThat(workItemTransitionEvents).hasSize(8);
    }
    
    
    @Test
    public void testBasicUserTaskProcessPhases() throws Exception {
        
        Application app = generateCodeProcessesOnly("usertask/UserTasksProcess.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.processes().processById("UserTasksProcess");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE); 
        
        List<WorkItem> workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        WorkItem wi = workItems.get(0);
        assertEquals("FirstTask", wi.getName());
        assertEquals(Active.ID, wi.getPhase());
        assertEquals(Active.STATUS, wi.getPhaseStatus());
        
        processInstance.transitionWorkItem(workItems.get(0).getId(), new HumanTaskTransition(Complete.ID));
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        wi = workItems.get(0);
        assertEquals("SecondTask", wi.getName());
        assertEquals(Active.ID, wi.getPhase());
        assertEquals(Active.STATUS, wi.getPhaseStatus());
        
        processInstance.transitionWorkItem(workItems.get(0).getId(), new HumanTaskTransition(Complete.ID));
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
    
    @Test
    public void testBasicUserTaskProcessClaimAndCompletePhases() throws Exception {
        
        Application app = generateCodeProcessesOnly("usertask/UserTasksProcess.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.processes().processById("UserTasksProcess");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE); 
        
        List<WorkItem> workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        WorkItem wi = workItems.get(0);
        assertEquals("FirstTask", wi.getName());
        assertEquals(Active.ID, wi.getPhase());
        assertEquals(Active.STATUS, wi.getPhaseStatus());
        assertEquals(0, wi.getResults().size());
        
        processInstance.transitionWorkItem(workItems.get(0).getId(), new HumanTaskTransition(Claim.ID, Collections.singletonMap("test", "value")));
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        wi = workItems.get(0);
        assertEquals("FirstTask", wi.getName());
        assertEquals(Claim.ID, wi.getPhase());
        assertEquals(Claim.STATUS, wi.getPhaseStatus());
        assertEquals(2, wi.getResults().size());
        assertEquals("value", wi.getResults().get("test"));
        assertEquals(null, wi.getResults().get("ActorId"));
        
        processInstance.transitionWorkItem(workItems.get(0).getId(), new HumanTaskTransition(Complete.ID));
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        wi = workItems.get(0);
        assertEquals("SecondTask", wi.getName());
        assertEquals(Active.ID, wi.getPhase());
        assertEquals(Active.STATUS, wi.getPhaseStatus());
        assertEquals(0, wi.getResults().size());
        
        processInstance.abort();
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ABORTED);
    }
    
    @Test
    public void testBasicUserTaskProcessReleaseAndCompletePhases() throws Exception {
        
        Application app = generateCodeProcessesOnly("usertask/UserTasksProcess.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.processes().processById("UserTasksProcess");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE); 
        
        List<WorkItem> workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        WorkItem wi = workItems.get(0);
        assertEquals("FirstTask", wi.getName());
        assertEquals(Active.ID, wi.getPhase());
        assertEquals(Active.STATUS, wi.getPhaseStatus());
        assertEquals(0, wi.getResults().size());
        
        final String wiId = wi.getId();
        
        assertThrows(InvalidTransitionException.class, () -> 
            processInstance.transitionWorkItem(wiId, new HumanTaskTransition(Release.ID)));
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        wi = workItems.get(0);
        assertEquals("FirstTask", wi.getName());
        assertEquals(Active.ID, wi.getPhase());
        assertEquals(Active.STATUS, wi.getPhaseStatus());
        assertEquals(0, wi.getResults().size());
        
        processInstance.transitionWorkItem(workItems.get(0).getId(), new HumanTaskTransition(Complete.ID));
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        wi = workItems.get(0);
        assertEquals("SecondTask", wi.getName());
        assertEquals(Active.ID, wi.getPhase());
        assertEquals(Active.STATUS, wi.getPhaseStatus());
        assertEquals(0, wi.getResults().size());
        
        processInstance.abort();
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ABORTED);
    }
    
    @Test
    public void testBasicUserTaskProcessClaimAndCompletePhasesWithIdentity() throws Exception {
        
        Application app = generateCodeProcessesOnly("usertask/UserTasksProcess.bpmn2");        
        assertThat(app).isNotNull();
        final List<String> workItemTransitionEvents = new ArrayList<>();
        ((DefaultProcessEventListenerConfig)app.config().process().processEventListeners()).listeners().add(new DefaultProcessEventListener() {

            @Override
            public void beforeWorkItemTransition(ProcessWorkItemTransitionEvent event) {
                workItemTransitionEvents.add("BEFORE:: " + event);
            }

            @Override
            public void afterWorkItemTransition(ProcessWorkItemTransitionEvent event) {
                workItemTransitionEvents.add("AFTER:: " + event);
            }
        });
                
        Process<? extends Model> p = app.processes().processById("UserTasksProcess");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE); 
        
        List<WorkItem> workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        WorkItem wi = workItems.get(0);
        assertEquals("FirstTask", wi.getName());
        assertEquals(Active.ID, wi.getPhase());
        assertEquals(Active.STATUS, wi.getPhaseStatus());
        assertEquals(0, wi.getResults().size());
        
        IdentityProvider identity = new StaticIdentityProvider("john");
        
        processInstance.transitionWorkItem(workItems.get(0).getId(), new HumanTaskTransition(Claim.ID, Collections.singletonMap("test", "value"), identity));
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        wi = workItems.get(0);
        assertEquals("FirstTask", wi.getName());
        assertEquals(Claim.ID, wi.getPhase());
        assertEquals(Claim.STATUS, wi.getPhaseStatus());
        assertEquals(2, wi.getResults().size());
        assertEquals("value", wi.getResults().get("test"));
        assertEquals("john", wi.getResults().get("ActorId"));
        
        processInstance.transitionWorkItem(workItems.get(0).getId(), new HumanTaskTransition(Complete.ID));
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        wi = workItems.get(0);
        assertEquals("SecondTask", wi.getName());
        assertEquals(Active.ID, wi.getPhase());
        assertEquals(Active.STATUS, wi.getPhaseStatus());
        assertEquals(0, wi.getResults().size());
        
        processInstance.abort();
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ABORTED);
        
        assertThat(workItemTransitionEvents).hasSize(10);
    }
    
    @Test
    public void testBasicUserTaskProcessClaimAndCompleteWrongUser() throws Exception {
        
        Application app = generateCodeProcessesOnly("usertask/UserTasksProcess.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.processes().processById("UserTasksProcess");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE); 
        
        List<WorkItem> workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        WorkItem wi = workItems.get(0);
        assertEquals("FirstTask", wi.getName());
        assertEquals(Active.ID, wi.getPhase());
        assertEquals(Active.STATUS, wi.getPhaseStatus());
        assertEquals(0, wi.getResults().size());
        
        final String wiId = wi.getId();
        IdentityProvider identity = new StaticIdentityProvider("kelly");
        
        // if user that is not authorized to work on work item both listing and getting by id should apply it
        List<WorkItem> securedWorkItems = processInstance.workItems(SecurityPolicy.of(identity));
        assertEquals(0, securedWorkItems.size());        
        assertThrows(WorkItemNotFoundException.class, () ->processInstance.workItem(wiId, SecurityPolicy.of(identity)));        
        
        assertThrows(NotAuthorizedException.class, () -> 
            processInstance.transitionWorkItem(wiId, new HumanTaskTransition(Claim.ID, null, identity)));
        
        assertThrows(NotAuthorizedException.class, () -> 
            processInstance.completeWorkItem(wiId, null, SecurityPolicy.of(identity)));
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        wi = workItems.get(0);
        assertEquals("FirstTask", wi.getName());
        assertEquals(Active.ID, wi.getPhase());
        assertEquals(Active.STATUS, wi.getPhaseStatus());
        assertEquals(0, wi.getResults().size());
        
        IdentityProvider identityCorrect = new StaticIdentityProvider("john");
        
        processInstance.transitionWorkItem(workItems.get(0).getId(), new HumanTaskTransition(Complete.ID, null, identityCorrect));
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        wi = workItems.get(0);
        assertEquals("SecondTask", wi.getName());
        assertEquals(Active.ID, wi.getPhase());
        assertEquals(Active.STATUS, wi.getPhaseStatus());
        assertEquals(0, wi.getResults().size());
        
        processInstance.abort();
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ABORTED);
    }
    
    @Test
    public void testApprovalWithExcludedOwnerViaPhases() throws Exception {
        
        Application app = generateCodeProcessesOnly("usertask/approval.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.processes().processById("approvals");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status()); 
        
        StaticIdentityProvider identity = new StaticIdentityProvider("admin", Collections.singletonList("managers"));
        SecurityPolicy policy = SecurityPolicy.of(identity);
        
        processInstance.workItems(policy);
        
        List<WorkItem> workItems = processInstance.workItems(policy);
        assertEquals(1, workItems.size());
        HumanTaskTransition transition = new HumanTaskTransition(Complete.ID, null, identity);
        processInstance.transitionWorkItem(workItems.get(0).getId(), transition);
        // actual owner of the first task is excluded owner on the second task so won't find it
        workItems = processInstance.workItems(policy);        
        assertEquals(0, workItems.size());
        
        identity = new StaticIdentityProvider("john", Collections.singletonList("managers"));
        policy = SecurityPolicy.of(identity);
        
        workItems = processInstance.workItems(policy);        
        assertEquals(1, workItems.size());
        
        transition = new HumanTaskTransition(Complete.ID, null, identity);
        processInstance.transitionWorkItem(workItems.get(0).getId(), transition);
        
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());
    }
    
    @Test
    public void testApprovalWithExcludedOwner() throws Exception {
        
        Application app = generateCodeProcessesOnly("usertask/approval.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.processes().processById("approvals");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE, processInstance.status()); 
        
        StaticIdentityProvider identity = new StaticIdentityProvider("admin", Collections.singletonList("managers"));
        SecurityPolicy policy = SecurityPolicy.of(identity);
        
        processInstance.workItems(policy);
        
        List<WorkItem> workItems = processInstance.workItems(policy);
        assertEquals(1, workItems.size());
        
        processInstance.completeWorkItem(workItems.get(0).getId(), null, policy);
        // actual owner of the first task is excluded owner on the second task so won't find it
        workItems = processInstance.workItems(policy);        
        assertEquals(0, workItems.size());
        
        identity = new StaticIdentityProvider("john", Collections.singletonList("managers"));
        policy = SecurityPolicy.of(identity);
        
        workItems = processInstance.workItems(policy);        
        assertEquals(1, workItems.size());
        
        processInstance.completeWorkItem(workItems.get(0).getId(), null, policy);
        
        assertEquals(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED, processInstance.status());
    }    
    
    @Test
    public void testRESTApiForUserTasks() throws Exception {
        
        Application app = generateCodeProcessesOnly("usertask/UserTasksProcess.bpmn2");        
        assertThat(app).isNotNull();
        
        Class<?> resourceClazz = Class.forName("org.kie.kogito.test.UserTasksProcessResource", true, testClassLoader());
        assertNotNull(resourceClazz);
        Set<String> completeTaskPaths = new LinkedHashSet<>();
        Method[] methods = resourceClazz.getMethods();
        for (Method m : methods) {
            if (m.getName().startsWith("completeTask")) {
                Annotation[] annotations = m.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().getSimpleName().equals("Path")) {
                        completeTaskPaths.add(annotation.toString().replaceAll("\\\"", ""));
                    }
                }
            }
        }
        // there must be two distinct paths for user tasks
        assertThat(completeTaskPaths).hasSize(2).containsOnly("@javax.ws.rs.Path(value=/{id}/FirstTask/{workItemId})", 
                                                                 "@javax.ws.rs.Path(value=/{id}/SecondTask/{workItemId})");
    }
    
    @Test
    public void testBasicUserTaskProcessCancelAndTriggerNode() throws Exception {
        
        Application app = generateCodeProcessesOnly("usertask/UserTasksProcess.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.processes().processById("UserTasksProcess");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE); 
        
        List<WorkItem> workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        WorkItem wi = workItems.get(0);
        assertEquals("FirstTask", wi.getName());
        assertEquals(Active.ID, wi.getPhase());
        assertEquals(Active.STATUS, wi.getPhaseStatus());
        
        processInstance.transitionWorkItem(workItems.get(0).getId(), new HumanTaskTransition(Complete.ID));
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        wi = workItems.get(0);
        assertEquals("SecondTask", wi.getName());
        assertEquals(Active.ID, wi.getPhase());
        assertEquals(Active.STATUS, wi.getPhaseStatus());
        
        String firstSecondTaskNodeInstanceId = wi.getNodeInstanceId();
        
        processInstance.cancelNodeInstance(wi.getNodeInstanceId());
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        processInstance.triggerNode("UserTask_2");
        workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        wi = workItems.get(0);
        assertEquals("SecondTask", wi.getName());
        assertEquals(Active.ID, wi.getPhase());
        assertEquals(Active.STATUS, wi.getPhaseStatus());
        // since it was triggered again it must have different node instance id
        assertNotEquals(firstSecondTaskNodeInstanceId, wi.getNodeInstanceId());
        processInstance.transitionWorkItem(workItems.get(0).getId(), new HumanTaskTransition(Complete.ID));
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
    
    @Test
    public void testBasicUserTaskProcessCancelAndRetriggerNode() throws Exception {
        
        Application app = generateCodeProcessesOnly("usertask/UserTasksProcess.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.processes().processById("UserTasksProcess");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE); 
        
        List<WorkItem> workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        WorkItem wi = workItems.get(0);
        assertEquals("FirstTask", wi.getName());
        assertEquals(Active.ID, wi.getPhase());
        assertEquals(Active.STATUS, wi.getPhaseStatus());
        
        processInstance.transitionWorkItem(workItems.get(0).getId(), new HumanTaskTransition(Complete.ID));
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        wi = workItems.get(0);
        assertEquals("SecondTask", wi.getName());
        assertEquals(Active.ID, wi.getPhase());
        assertEquals(Active.STATUS, wi.getPhaseStatus());
        
        String firstSecondTaskNodeInstanceId = wi.getNodeInstanceId();
        
        processInstance.retriggerNodeInstance(wi.getNodeInstanceId());
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_ACTIVE);
                
        workItems = processInstance.workItems();
        assertEquals(1, workItems.size());
        wi = workItems.get(0);
        assertEquals("SecondTask", wi.getName());
        assertEquals(Active.ID, wi.getPhase());
        assertEquals(Active.STATUS, wi.getPhaseStatus());
        // since it was retriggered it must have different node instance id
        assertNotEquals(firstSecondTaskNodeInstanceId, wi.getNodeInstanceId());
        processInstance.transitionWorkItem(workItems.get(0).getId(), new HumanTaskTransition(Complete.ID));
        
        assertThat(processInstance.status()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
}
