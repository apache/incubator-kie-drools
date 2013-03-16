package org.drools.template.parser;

import org.drools.core.Agenda;
import org.drools.core.FactException;
import org.drools.core.FactHandle;
import org.drools.core.QueryResults;
import org.drools.core.RuleBase;
import org.drools.core.StatefulSession;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.event.AgendaEventListener;
import org.drools.core.event.RuleBaseEventListener;
import org.drools.core.event.WorkingMemoryEventListener;
import org.drools.core.process.instance.WorkItemManager;
import org.drools.core.spi.AgendaFilter;
import org.drools.core.spi.AgendaGroup;
import org.drools.core.spi.AsyncExceptionHandler;
import org.drools.core.spi.GlobalResolver;
import org.junit.Before;
import org.junit.Test;
import org.kie.runtime.Environment;
import org.kie.runtime.ObjectFilter;
import org.kie.runtime.process.ProcessInstance;
import org.kie.time.SessionClock;

import java.util.*;

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

        public StatefulSession newStatefulSession() {
            return new StatefulSession() {
                private static final long serialVersionUID = 510l;

                public void addEventListener(WorkingMemoryEventListener arg0) {

                }

                public void addEventListener(AgendaEventListener arg0) {

                }

                public FactHandle insert(Object fact)
                        throws FactException {
                    if (fact instanceof Row) {
                        assertedRows.put((Row) fact, currentRow);
                        currentRow = new ArrayList<StringCell>();
                    } else if (fact instanceof StringCell) {
                        currentRow.add((StringCell) fact);
                    }
                    return null;
                }

                public FactHandle insert(Object arg0, boolean arg1)
                        throws FactException {
                    return null;
                }

                public void clearAgenda() {

                }

                public void clearAgendaGroup(String arg0) {

                }

                public void dispose() {

                }

                public int fireAllRules() throws FactException {
                    return 0;
                }

                public int fireAllRules(AgendaFilter arg0)
                        throws FactException {
                    return 0;
                }

                public Agenda getAgenda() {
                    return null;
                }

                @SuppressWarnings("unchecked")
                public List getAgendaEventListeners() {
                    return null;
                }

                public FactHandle getFactHandle(Object arg0) {
                    return null;
                }

                public AgendaGroup getFocus() {
                    return null;
                }

                public Object getGlobal(String arg0) {
                    return null;
                }

                public QueryResults getQueryResults(String arg0) {
                    return null;
                }

                public RuleBase getRuleBase() {
                    return null;
                }

                @SuppressWarnings("unchecked")
                public List getWorkingMemoryEventListeners() {
                    return null;
                }

                public void update(org.kie.runtime.rule.FactHandle arg0, Object arg1)
                        throws FactException {

                }

                public void removeEventListener(WorkingMemoryEventListener arg0) {

                }

                public void removeEventListener(AgendaEventListener arg0) {

                }

                public void retract(org.kie.runtime.rule.FactHandle arg0) throws FactException {

                }

                public void delete(org.kie.runtime.rule.FactHandle arg0) throws FactException {

                }

                public void setAsyncExceptionHandler(AsyncExceptionHandler arg0) {

                }

                public void setFocus(String arg0) {

                }

                public void setFocus(AgendaGroup arg0) {

                }

                public void setGlobal(String arg0, Object arg1) {

                }

                public void setGlobalResolver(GlobalResolver globalResolver) {

                }

                public ProcessInstance startProcess(String processId) {
                    return null;
                }

                @SuppressWarnings("unchecked")
                public Iterator iterateFactHandles() {
                    return null;
                }

                @SuppressWarnings("unchecked")
                public Iterator iterateFactHandles(ObjectFilter filter) {
                    return null;
                }

                @SuppressWarnings("unchecked")
                public Iterator iterateObjects() {
                    return null;
                }

                @SuppressWarnings("unchecked")
                public Iterator iterateObjects(ObjectFilter filter) {
                    return null;
                }

                public QueryResults getQueryResults(String query,
                                                    Object[] arguments) {

                    return null;
                }

                public void modifyInsert(FactHandle factHandle,
                                         Object object) {


                }

                public void modifyRetract(FactHandle factHandle) {


                }

                public void halt() {


                }

                public int fireAllRules(int fireLimit) throws FactException {
                    return 0;
                }

                public int fireAllRules(AgendaFilter agendaFilter,
                                        int fireLimit) throws FactException {

                    return 0;
                }


                public GlobalResolver getGlobalResolver() {

                    return null;
                }

                @SuppressWarnings("unchecked")
                public List getRuleFlowEventListeners() {

                    return null;
                }

                public void clearActivationGroup(String group) {


                }

                public void clearRuleFlowGroup(String group) {


                }

                public void addEventListener(RuleBaseEventListener listener) {


                }

                @SuppressWarnings("unchecked")
                public List getRuleBaseEventListeners() {

                    return null;
                }

                public void removeEventListener(RuleBaseEventListener listener) {


                }

                @SuppressWarnings("unchecked")
                public List getRuleBaseUpdateListeners() {

                    return null;
                }

                public ProcessInstance getProcessInstance(long id) {

                    return null;
                }

                public ProcessInstance getProcessInstance(long id, boolean readOnly) {
                    
                    return null;
                }

                public WorkItemManager getWorkItemManager() {

                    return null;
                }

                @SuppressWarnings("unchecked")
                public Collection getProcessInstances() {

                    return null;
                }

                public ProcessInstance startProcess(String processId,
                                                    Map<String, Object> parameters) {

                    return null;
                }

                public FactHandle getFactHandleByIdentity(Object object) {

                    return null;
                }

                public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String id) {

                    return null;
                }

                public SessionClock getSessionClock() {
                    return null;
                }

                public void fireUntilHalt() {
                    // TODO Auto-generated method stub

                }

                public void fireUntilHalt(AgendaFilter agendaFilter) {
                    // TODO Auto-generated method stub

                }

                public Object getObject(org.kie.runtime.rule.FactHandle handle) {
                    // TODO Auto-generated method stub
                    return null;
                }

                public Environment getEnvironment() {
                    // TODO Auto-generated method stub
                    return null;
                }

                public Collection<? extends org.kie.runtime.rule.FactHandle> getFactHandles() {
                    // TODO Auto-generated method stub
                    return null;
                }

                public Collection<? extends org.kie.runtime.rule.FactHandle> getFactHandles(ObjectFilter filter) {
                    // TODO Auto-generated method stub
                    return null;
                }

                public Collection<Object> getObjects() {
                    // TODO Auto-generated method stub
                    return null;
                }

                public Collection<Object> getObjects(ObjectFilter filter) {
                    // TODO Auto-generated method stub
                    return null;
                }

                public String getEntryPointId() {
                    // TODO Auto-generated method stub
                    return null;
                }

                public long getFactCount() {
                    // TODO Auto-generated method stub
                    return 0;
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
