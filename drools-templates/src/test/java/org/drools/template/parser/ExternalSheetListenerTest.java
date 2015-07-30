/*
 * Copyright 2015 JBoss Inc
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

package org.drools.template.parser;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.api.time.SessionClock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ExternalSheetListenerTest {

    private TemplateDataListener esl;

    private Map<Row, List<StringCell>> assertedRows = new HashMap<Row, List<StringCell>>();

    private List<StringCell> currentRow = new ArrayList<StringCell>();

    @Before
    public void setUp() throws Exception {
        esl = new TemplateDataListener(2, 2, new TestTemplateContainer(),
                                       new TestTemplateRuleBase(), new TestGenerator());

    }

    @Test
    public void testRenderDrl() {
        String drl = esl.renderDRL();
        assertEquals("Test Template Header\nTest Generated DRL\n", drl);
    }

    @Test
    public void testRowHandling() {
        esl.newRow(0, 3);
        esl.newCell(0, 0, "row0col0", 0);
        esl.newCell(0, 1, "row0col1", 0);
        esl.newCell(0, 2, "row0col2", 0);
        esl.newRow(1, 3);
        esl.newCell(1, 0, "row1col0", 0);
        esl.newCell(1, 1, "row1col1", 0);
        esl.newCell(1, 2, "row1col2", 0);
        esl.newRow(2, 3);
        esl.newCell(2, 0, "row2col0", 0);
        esl.newCell(2, 1, "row2col1", 0);
        esl.newCell(2, 2, "row2col2", 0);
        esl.finishSheet();
        assertEquals(2, assertedRows.size());
        for (Map.Entry<Row, List<StringCell>> entry : assertedRows.entrySet()) {
            Row row = entry.getKey();
            List<StringCell> cells = entry.getValue();
            // first column is not part of the decision table
            int i = 1;
            for (StringCell cell : cells) {
                assertEquals("row" + row.getRowNumber() + "col" + i, cell.getValue());
                assertEquals("Pattern " + i, cell.getColumn().getName());
                i++;
            }
        }
    }

    @Test
    public void testRowHandlingBlankRows() {
        esl.newRow(0, 3);
        esl.newCell(0, 0, "row0col0", 0);
        esl.newCell(0, 1, "row0col1", 0);
        esl.newCell(0, 2, "row0col2", 0);
        esl.newRow(1, 3);
        esl.newCell(1, 0, "row1col0", 0);
        esl.newCell(1, 1, "row1col1", 0);
        esl.newCell(1, 2, "row1col2", 0);
        esl.newRow(2, 3);
        esl.newCell(2, 0, "row2col0", 0);
        esl.newCell(2, 1, "row2col1", 0);
        esl.newCell(2, 2, "row2col2", 0);
        esl.newRow(3, 3);
        esl.newCell(3, 0, "", 0);
        esl.newCell(3, 1, "", 0);
        esl.newCell(3, 2, "", 0);
        esl.newRow(4, 3);
        esl.newCell(4, 0, "", 0);
        esl.newCell(4, 1, "", 0);
        esl.newCell(4, 2, "", 0);

        esl.finishSheet();
        assertEquals(2, assertedRows.size());
    }

    private class TestTemplateRuleBase implements TemplateRuleBase {

        public KieSession newStatefulSession() {
            return new KieSession() {

                @Override
                public int getId() {
                    return 0;
                }

                @Override
                public long getIdentifier() {
                    return 0L;
                }

                @Override
                public void dispose() {

                }

                @Override
                public void destroy() {

                }

                @Override
                public <T> T execute(Command<T> command) {
                    return null;
                }

                @Override
                public <T extends SessionClock> T getSessionClock() {
                    return null;
                }

                @Override
                public void setGlobal(String identifier, Object value) {

                }

                @Override
                public Object getGlobal(String identifier) {
                    return null;
                }

                @Override
                public Globals getGlobals() {
                    return null;
                }

                @Override
                public Calendars getCalendars() {
                    return null;
                }

                @Override
                public Environment getEnvironment() {
                    return null;
                }

                @Override
                public KieBase getKieBase() {
                    return null;
                }

                @Override
                public void registerChannel(String name, Channel channel) {

                }

                @Override
                public void unregisterChannel(String name) {

                }

                @Override
                public Map<String, Channel> getChannels() {
                    return null;
                }

                @Override
                public KieSessionConfiguration getSessionConfiguration() {
                    return null;
                }

                @Override
                public void halt() {

                }

                @Override
                public org.kie.api.runtime.rule.Agenda getAgenda() {
                    return null;
                }

                @Override
                public EntryPoint getEntryPoint(String name) {
                    return null;
                }

                @Override
                public Collection<? extends EntryPoint> getEntryPoints() {
                    return null;
                }

                @Override
                public org.kie.api.runtime.rule.QueryResults getQueryResults(String query, Object... arguments) {
                    return null;
                }

                @Override
                public LiveQuery openLiveQuery(String query, Object[] arguments, ViewChangedEventListener listener) {
                    return null;
                }

                @Override
                public String getEntryPointId() {
                    return null;
                }

                @Override
                public FactHandle insert(Object fact) {
                    if (fact instanceof Row) {
                        assertedRows.put((Row) fact, currentRow);
                        currentRow = new ArrayList<StringCell>();
                    } else if (fact instanceof StringCell) {
                        currentRow.add((StringCell) fact);
                    }
                    return null;
                }

                @Override
                public void retract(org.kie.api.runtime.rule.FactHandle handle) {

                }

                @Override
                public void delete(org.kie.api.runtime.rule.FactHandle handle) {

                }

                @Override
                public void update(org.kie.api.runtime.rule.FactHandle handle, Object object) {

                }

                @Override
                public org.kie.api.runtime.rule.FactHandle getFactHandle(Object object) {
                    return null;
                }

                @Override
                public Object getObject(org.kie.api.runtime.rule.FactHandle factHandle) {
                    return null;
                }

                @Override
                public Collection<? extends Object> getObjects() {
                    return null;
                }

                @Override
                public Collection<? extends Object> getObjects(ObjectFilter filter) {
                    return null;
                }

                @Override
                public <T extends org.kie.api.runtime.rule.FactHandle> Collection<T> getFactHandles() {
                    return null;
                }

                @Override
                public <T extends org.kie.api.runtime.rule.FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
                    return null;
                }

                @Override
                public long getFactCount() {
                    return 0L;
                }

                @Override
                public KieRuntimeLogger getLogger() {
                    return null;
                }

                @Override
                public void addEventListener(ProcessEventListener listener) {

                }

                @Override
                public void removeEventListener(ProcessEventListener listener) {

                }

                @Override
                public Collection<ProcessEventListener> getProcessEventListeners() {
                    return null;
                }

                @Override
                public ProcessInstance startProcess(String processId) {
                    return null;
                }

                @Override
                public ProcessInstance startProcess(String processId, Map<String, Object> parameters) {
                    return null;
                }

                @Override
                public ProcessInstance createProcessInstance(String processId, Map<String, Object> parameters) {
                    return null;
                }

                @Override
                public ProcessInstance startProcessInstance(long processInstanceId) {
                    return null;
                }

                @Override
                public void signalEvent(String type, Object event) {

                }

                @Override
                public void signalEvent(String type, Object event, long processInstanceId) {

                }

                @Override
                public Collection<ProcessInstance> getProcessInstances() {
                    return null;
                }

                @Override
                public ProcessInstance getProcessInstance(long processInstanceId) {
                    return null;
                }

                @Override
                public ProcessInstance getProcessInstance(long processInstanceId, boolean readonly) {
                    return null;
                }

                @Override
                public void abortProcessInstance(long processInstanceId) {

                }

                @Override
                public org.kie.api.runtime.process.WorkItemManager getWorkItemManager() {
                    return null;
                }

                @Override
                public void addEventListener(RuleRuntimeEventListener listener) {

                }

                @Override
                public void removeEventListener(RuleRuntimeEventListener listener) {

                }

                @Override
                public Collection<RuleRuntimeEventListener> getRuleRuntimeEventListeners() {
                    return null;
                }

                @Override
                public void addEventListener(org.kie.api.event.rule.AgendaEventListener listener) {

                }

                @Override
                public void removeEventListener(org.kie.api.event.rule.AgendaEventListener listener) {

                }

                @Override
                public Collection<org.kie.api.event.rule.AgendaEventListener> getAgendaEventListeners() {
                    return null;
                }

                @Override
                public int fireAllRules() {
                    return 0;
                }

                @Override
                public int fireAllRules(int max) {
                    return 0;
                }

                @Override
                public int fireAllRules(org.kie.api.runtime.rule.AgendaFilter agendaFilter) {
                    return 0;
                }

                @Override
                public int fireAllRules(org.kie.api.runtime.rule.AgendaFilter agendaFilter, int max) {
                    return 0;
                }

                @Override
                public void fireUntilHalt() {

                }

                @Override
                public void fireUntilHalt(org.kie.api.runtime.rule.AgendaFilter agendaFilter) {

                }
            };
        }
    }

    private class TestGenerator implements Generator {

        public void generate(String templateName, Row row) {
        }

        public String getDrl() {
            return "Test Generated DRL";
        }

    }

    private class TestTemplateContainer implements TemplateContainer {

        public void addColumn(Column c) {
        }

        public void addTemplate(RuleTemplate template) {
        }

        public Column[] getColumns() {
            return new Column[]{new StringColumn("Pattern 1"),
                                new StringColumn("Pattern 2"), new StringColumn("Pattern 3")};
        }

        public String getHeader() {
            return "Test Template Header";
        }

        public Map<String, RuleTemplate> getTemplates() {
            return null;
        }

        public void setHeader(String head) {
        }

        public void setTemplates(Map<String, RuleTemplate> templates) {

        }

        public Column getColumn(String name) {
            return null;
        }
    }

}
