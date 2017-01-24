/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.spi;

import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.CaseAssignment;
import org.kie.api.runtime.process.CaseData;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.KnowledgeBaseFactory;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ProcessContextTest {


    @Test
    public void testProcessContextGetAssignment() {

        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KieSession ksession = kbase.newKieSession();
        assertNotNull(ksession);

        CaseInformation caseInfo = new CaseInformation();
        caseInfo.assign("owner", new OrganizationalEntity() {
            @Override
            public String getId() {
                return "testUser";
            }

            @Override
            public void writeExternal(ObjectOutput out) throws IOException {

            }

            @Override
            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

            }
        });
        ksession.insert(caseInfo);

        ProcessContext processContext = new ProcessContext(ksession);

        CaseAssignment caseAssignment = processContext.getCaseAssignment();
        assertNotNull(caseAssignment);
        Collection<OrganizationalEntity> forRole = caseAssignment.getAssignments("owner");
        assertNotNull(forRole);
        assertEquals(1, forRole.size());
    }

    @Test
    public void testProcessContextGetData() {

        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KieSession ksession = kbase.newKieSession();
        assertNotNull(ksession);

        CaseInformation caseInfo = new CaseInformation();
        caseInfo.add("test", "value");

        ksession.insert(caseInfo);

        ProcessContext processContext = new ProcessContext(ksession);

        CaseData caseData = processContext.getCaseData();
        assertNotNull(caseData);
        Map<String, Object> allData = caseData.getData();
        assertNotNull(allData);
        assertEquals(1, allData.size());
        assertEquals("value", caseData.getData("test"));
    }

    @Test
    public void testProcessContextGetDataAndAssignmentWithoutInsert() {

        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KieSession ksession = kbase.newKieSession();
        assertNotNull(ksession);

        ProcessContext processContext = new ProcessContext(ksession);

        CaseData caseData = processContext.getCaseData();
        assertNull(caseData);

        CaseAssignment caseAssignment = processContext.getCaseAssignment();
        assertNull(caseAssignment);
    }

    private class CaseInformation implements CaseData, CaseAssignment {

        private String definitionId;
        private Map<String, Object> data = new HashMap<>();
        private Map<String, OrganizationalEntity> assignment = new HashMap<>();

        @Override
        public void assign(String roleName, OrganizationalEntity entity) {
            assignment.put(roleName, entity);
        }

        @Override
        public void assignUser(String roleName, String userId) {
        }

        @Override
        public void assignGroup(String roleName, String groupId) {
        }

        @Override
        public void remove(String roleName, OrganizationalEntity entity) {
            assignment.remove(roleName);
        }

        @Override
        public Collection<OrganizationalEntity> getAssignments(String roleName) {
            OrganizationalEntity entity = assignment.get(roleName);
            if (entity == null) {
                return Collections.emptyList();
            }

            return Arrays.asList(entity);
        }

        @Override
        public Map<String, Object> getData() {
            return data;
        }

        @Override
        public Object getData(String name) {
            return data.get(name);
        }

        @Override
        public void add(String name, Object data) {
            this.data.put(name, data);
        }

        @Override
        public void remove(String name) {
            this.data.remove(name);
        }

        @Override
        public String getDefinitionId() {
            return definitionId;
        }
    }
}
