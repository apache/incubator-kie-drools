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

package org.jbpm.casemgmt.impl.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.dashbuilder.DataSetCore;
import org.jbpm.casemgmt.api.CaseNotFoundException;
import org.jbpm.casemgmt.api.model.CaseStatus;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.api.model.instance.CommentInstance;
import org.jbpm.casemgmt.impl.utils.DefaultCaseServiceConfigurator;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.test.services.AbstractCaseServicesTest;
import org.kie.test.util.db.PoolingDataSourceWrapper;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;

public abstract class AbstractCaseServicesBaseTest extends AbstractCaseServicesTest {

    protected static final String ARTIFACT_ID = "case-module";
    protected static final String GROUP_ID = "org.jbpm.cases";
    protected static final String VERSION = "1.0.0";

    protected PoolingDataSourceWrapper ds;

    protected static final String EMPTY_CASE_P_ID = "EmptyCase";
    protected static final String USER_TASK_STAGE_CASE_P_ID = "UserTaskWithStageCase";
    protected static final String USER_TASK_CASE_P_ID = "UserTaskCase";
    protected static final String USER_TASK_STAGE_AUTO_START_CASE_P_ID = "UserTaskWithStageCaseAutoStart";
    protected static final String USER_TASK_STAGE_ADHOC_CASE_P_ID = "UserStageAdhocCase";
    protected static final String NO_START_NODE_CASE_P_ID = "NoStartNodeAdhocCase";
    protected static final String COND_CASE_P_ID = "CaseFileConditionalEvent";
    protected static final String TWO_STAGES_CASE_P_ID = "CaseWithTwoStages";
    protected static final String TWO_STAGES_CONDITIONS_CASE_P_ID = "CaseWithTwoStagesConditions";
    protected static final String EXPRESSION_CASE_P_ID = "ExpressionWithCaseFileItem";
    protected static final String USER_TASK_DATA_RESTRICTIONS_CASE_P_ID = "UserTaskCaseDataRestrictions";
    protected static final String MULTI_STAGE_CASE_P_ID = "multiplestages";
    protected static final String USER_TASK_DATA_CASE_P_ID = "UserTaskCaseData";
    
    protected static final String SUBPROCESS_P_ID = "DataVerification";

    protected static final String FIRST_CASE_ID = "CASE-0000000001";
    protected static final String HR_CASE_ID = "HR-0000000001";

    protected static final String USER = "john";

    private static final String TEST_DOC_STORAGE = "target/docs";

    @Override
    protected void configureServices() {
        super.configureServices();
        this.emf = ((DefaultCaseServiceConfigurator) caseConfigurator).getEmf();
    }

    protected DeploymentUnit prepareDeploymentUnit() throws Exception {
        identityProvider.setName(USER);
        return createAndDeployUnit(GROUP_ID, ARTIFACT_ID, VERSION);                
    }

    protected void close() {
        DataSetCore.set(null);
        caseConfigurator.close();
        EntityManagerFactoryManager.get().clear();
        closeDataSource();
    }

    protected void prepareDocumentStorage() {
        System.setProperty("org.jbpm.document.storage", TEST_DOC_STORAGE);
        deleteFolder(TEST_DOC_STORAGE);
    }

    protected void clearDocumentStorageProperty() {
        System.clearProperty("org.jbpm.document.storage");
    }

    protected static void waitForTheOtherThreads(CyclicBarrier barrier) {
        try {
            barrier.await();
        } catch (InterruptedException e) {
            fail("Thread 1 was interrupted while waiting for the other threads!");
        } catch (BrokenBarrierException e) {
            fail("Thread 1's barrier was broken while waiting for the other threads!");
        }
    }

    protected void assertComment(CommentInstance comment, String author, String content) {
        assertThat(comment).isNotNull();
        assertThat(comment.getAuthor()).isEqualTo(author);
        assertThat(comment.getComment()).isEqualTo(content);
    }

    protected void assertTask(TaskSummary task, String actor, String name, Status status) {
        assertThat(task).isNotNull();
        assertThat(task.getName()).isEqualTo(name);
        assertThat(task.getActualOwnerId()).isEqualTo(actor);
        assertThat(task.getStatus()).isEqualTo(status);
    }

    protected void assertCaseInstance(String caseId, String name) {
        CaseInstance cInstance = caseService.getCaseInstance(caseId, true, false, false, false);
        assertThat(cInstance).isNotNull();
        assertThat(cInstance.getCaseId()).isEqualTo(caseId);
        assertThat(cInstance.getCaseFile()).isNotNull();
        assertThat(cInstance.getCaseFile().getData("name")).isEqualTo(name);
    }

    public void assertCaseInstanceActive(String caseId) {
        try {
            CaseInstance caseInstance = caseService.getCaseInstance(caseId);
            assertThat(caseInstance).isNotNull();
            assertThat(caseInstance.getStatus()).isEqualTo(CaseStatus.OPEN.getId());
        } catch (CaseNotFoundException ex) {
            fail("Case instance is not active");
        }
    }

    public void assertCaseInstanceNotActive(String caseId) {
        try {
            CaseInstance caseInstance = caseService.getCaseInstance(caseId);
            assertThat(caseInstance).isNotNull();
            assertThat(caseInstance.getStatus()).isIn(CaseStatus.CLOSED.getId(), CaseStatus.CANCELLED.getId());
        } catch (CaseNotFoundException ex) {
            // in case it does not exist at all
        }
        
    }
}
