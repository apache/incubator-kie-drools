/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.casemgmt.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.api.model.instance.CommentInstance;
import org.jbpm.casemgmt.impl.event.CommentNotificationEventListener;
import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.jbpm.casemgmt.impl.util.CommentNotificationEventListenerFactory;
import org.jbpm.casemgmt.impl.util.TestNotificationPublisher;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.task.impl.model.UserImpl;
import org.junit.After;
import org.junit.Test;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaseCommentNotificationTest extends AbstractCaseServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CaseCommentNotificationTest.class);

    @After
    public void reset() {
        CommentNotificationEventListenerFactory.removeExisting("test");
    }
    
    @Override
    protected List<String> getProcessDefinitionFiles() {
        List<String> processes = new ArrayList<String>();      
        processes.add("cases/UserTaskWithStageCaseAutoStart.bpmn2");
        return processes;
    }
    
    protected DeploymentUnit prepareDeploymentUnit() {
        assertThat(deploymentService).isNotNull();
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        final DeploymentDescriptor descriptor = new DeploymentDescriptorImpl();
        descriptor.getBuilder()
        .addEventListener(new ObjectModel(
                "mvel",
                "new org.jbpm.kie.services.impl.IdentityProviderAwareProcessListener(ksession)"
        ))
        .addEventListener(new ObjectModel(
                "mvel",
                "org.jbpm.casemgmt.impl.util.CommentNotificationEventListenerFactory.get(\"test\")"
        ));
        deploymentUnit.setDeploymentDescriptor(descriptor);
        deploymentUnit.setStrategy(RuntimeStrategy.PER_CASE);

        deploymentService.deploy(deploymentUnit);
        return deploymentUnit;
    }

    @Test
    public void testCommentsNotificationWithoutTemplate() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        
        TestNotificationPublisher publisher = new TestNotificationPublisher(true);
        CommentNotificationEventListener listener = CommentNotificationEventListenerFactory.get("test");
        listener.addPublisher(publisher);

        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_STAGE_AUTO_START_CASE_P_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_STAGE_AUTO_START_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());

            Collection<CommentInstance> caseComments = caseService.getCaseComments(FIRST_CASE_ID, new QueryContext());
            assertNotNull(caseComments);
            assertEquals(0, caseComments.size());

            caseService.addCaseComment(FIRST_CASE_ID, "poul", "just a tiny comment refering to @owner");

            caseComments = caseService.getCaseComments(FIRST_CASE_ID, new QueryContext());
            assertNotNull(caseComments);
            assertEquals(1, caseComments.size());

            CommentInstance comment = caseComments.iterator().next();
            assertComment(comment, "poul", "just a tiny comment refering to @owner");
            
            String expectedNotification = "Publishing notification from cases@jbpm.org, with subject You have been mentioned in case (CASE-0000000001) comment to [[UserImpl:'john']] with body just a tiny comment refering to john";
            
            List<String> published = publisher.get();
            assertThat(published).hasSize(1);
            assertThat(published.get(0)).isEqualTo(expectedNotification);

            caseService.updateCaseComment(FIRST_CASE_ID, comment.getId(), comment.getAuthor(), "Updated " + comment.getComment());
            caseComments = caseService.getCaseComments(FIRST_CASE_ID, new QueryContext());
            assertNotNull(caseComments);
            assertEquals(1, caseComments.size());

            comment = caseComments.iterator().next();
            assertComment(comment, "poul", "Updated just a tiny comment refering to @owner");

            expectedNotification = "Publishing notification from cases@jbpm.org, with subject You have been mentioned in case (CASE-0000000001) comment to [[UserImpl:'john']] with body Updated just a tiny comment refering to john";
            
            published = publisher.get();
            assertThat(published).hasSize(1);
            assertThat(published.get(0)).isEqualTo(expectedNotification);
            
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }    
    
    @Test
    public void testCommentsNotificationWithTemplate() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        
        TestNotificationPublisher publisher = new TestNotificationPublisher(false);
        CommentNotificationEventListener listener = CommentNotificationEventListenerFactory.get("test");
        listener.addPublisher(publisher);

        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_STAGE_AUTO_START_CASE_P_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_STAGE_AUTO_START_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());

            Collection<CommentInstance> caseComments = caseService.getCaseComments(FIRST_CASE_ID, new QueryContext());
            assertNotNull(caseComments);
            assertEquals(0, caseComments.size());

            caseService.addCaseComment(FIRST_CASE_ID, "poul", "just a tiny comment refering to @owner");

            caseComments = caseService.getCaseComments(FIRST_CASE_ID, new QueryContext());
            assertNotNull(caseComments);
            assertEquals(1, caseComments.size());

            CommentInstance comment = caseComments.iterator().next();
            assertComment(comment, "poul", "just a tiny comment refering to @owner");
            
            String expectedNotification = "Publishing notification from cases@jbpm.org, with subject You have been mentioned in case (CASE-0000000001) comment to [[UserImpl:'john']] with template mentioned-in-comment";
            
            List<String> published = publisher.get();
            assertThat(published).hasSize(1);
            assertThat(published.get(0)).isEqualTo(expectedNotification);

            caseService.updateCaseComment(FIRST_CASE_ID, comment.getId(), comment.getAuthor(), "Updated " + comment.getComment());
            caseComments = caseService.getCaseComments(FIRST_CASE_ID, new QueryContext());
            assertNotNull(caseComments);
            assertEquals(1, caseComments.size());

            comment = caseComments.iterator().next();
            assertComment(comment, "poul", "Updated just a tiny comment refering to @owner");

            expectedNotification = "Publishing notification from cases@jbpm.org, with subject You have been mentioned in case (CASE-0000000001) comment to [[UserImpl:'john']] with template mentioned-in-comment";
            
            published = publisher.get();
            assertThat(published).hasSize(1);
            assertThat(published.get(0)).isEqualTo(expectedNotification);
            
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    } 
}
