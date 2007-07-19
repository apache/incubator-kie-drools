package org.drools.decisiontable.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.Agenda;
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.ObjectFilter;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.concurrent.Future;
import org.drools.event.AgendaEventListener;
import org.drools.event.RuleFlowEventListener;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.rule.Rule;
import org.drools.ruleflow.common.instance.ProcessInstance;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.GlobalResolver;

public class ExternalSheetListenerTest extends TestCase {

	private ExternalSheetListener esl;

	private Map assertedRows = new HashMap();

	private List currentRow = new ArrayList();

	protected void setUp() throws Exception {
		esl = new ExternalSheetListener(2, 2, new TestTemplateContainer(),
				new TestTemplateRuleBase(), new TestGenerator());

	}

	public void testRenderDrl() {
		String drl = esl.renderDRL();
		assertEquals("Test Template Header\nTest Generated DRL\n", drl);
	}

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
		for (Iterator it = assertedRows.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			Row row = (Row)entry.getKey();
			List cells = (List) entry.getValue();
			// first column is not part of the decision table
			int i = 1;
			for (Iterator it2 = cells.iterator(); it2.hasNext(); i++) {
				Cell cell = (Cell) it2.next();
				assertEquals("row" + row.getRowNumber() + "col" + i, cell.getValue());
				assertEquals("Pattern " + i, cell.getColumn());
			}
		}
	}

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

				public void addEventListener(WorkingMemoryEventListener arg0) {

				}

				public void addEventListener(AgendaEventListener arg0) {

				}

				public void addEventListener(RuleFlowEventListener arg0) {

				}

				public FactHandle insert(Object fact)
						throws FactException {
					if (fact instanceof Row) {
						assertedRows.put(fact, currentRow);
						currentRow = new ArrayList();
					} else {
						currentRow.add(fact);
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

				public void fireAllRules() throws FactException {

				}

				public void fireAllRules(AgendaFilter arg0)
						throws FactException {

				}

				public Agenda getAgenda() {
					return null;
				}

				public List getAgendaEventListeners() {
					return null;
				}

				public FactHandle getFactHandle(Object arg0) {
					return null;
				}

				public List getFactHandles() {
					return null;
				}

				public AgendaGroup getFocus() {
					return null;
				}

				public Object getGlobal(String arg0) {
					return null;
				}

				public Object getObject(FactHandle arg0) {
					return null;
				}

				public QueryResults getQueryResults(String arg0) {
					return null;
				}

				public RuleBase getRuleBase() {
					return null;
				}

				public List getWorkingMemoryEventListeners() {
					return null;
				}

				public void update(FactHandle arg0, Object arg1)
						throws FactException {

				}

				public void removeEventListener(WorkingMemoryEventListener arg0) {

				}

				public void removeEventListener(AgendaEventListener arg0) {

				}

				public void removeEventListener(RuleFlowEventListener arg0) {

				}

				public void retract(FactHandle arg0) throws FactException {

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

                public Iterator iterateFactHandles() {
                    return null;
                }

                public Iterator iterateFactHandles(ObjectFilter filter) {
                    return null;
                }

                public Iterator iterateObjects() {
                    return null;
                }

                public Iterator iterateObjects(ObjectFilter filter) {
                    return null;
                }

                public Future asyncInsert(Object object) {
                    // TODO Auto-generated method stub
                    return null;
                }

                public Future asyncInsert(Object[] list) {
                    // TODO Auto-generated method stub
                    return null;
                }

                public Future asyncInsert(Collection collection) {
                    // TODO Auto-generated method stub
                    return null;
                }

                public Future asyncFireAllRules() {
                    // TODO Auto-generated method stub
                    return null;
                }

                public Future asyncFireAllRules(AgendaFilter agendaFilter) {
                    // TODO Auto-generated method stub
                    return null;
                }

                public Future asyncUpdate(FactHandle factHandle,
                                                Object object) {
                    // TODO Auto-generated method stub
                    return null;
                }

                public Future asyncRetract(FactHandle factHandle) {
                    // TODO Auto-generated method stub
                    return null;
                }

                public QueryResults getQueryResults(String query,
                                                    Object[] arguments) {
                    // TODO Auto-generated method stub
                    return null;
                }

                public void modifyInsert(FactHandle factHandle,
                                         Object object,
                                         Rule rule,
                                         Activation activation) {
                    // TODO Auto-generated method stub

                }

                public void modifyRetract(FactHandle factHandle,
                                          Rule rule,
                                          Activation activation) {
                    // TODO Auto-generated method stub

                }

                public void modifyInsert(FactHandle factHandle,
                                         Object object) {
                    // TODO Auto-generated method stub

                }

                public void modifyRetract(FactHandle factHandle) {
                    // TODO Auto-generated method stub

                }

                public void halt() {
                    // TODO Auto-generated method stub

                }

                public void fireAllRules(int fireLimit) throws FactException {
                    // TODO Auto-generated method stub

                }

                public void fireAllRules(AgendaFilter agendaFilter,
                                         int fireLimit) throws FactException {
                    // TODO Auto-generated method stub

                }

				public Future asyncInsert(List list) {
					// TODO Auto-generated method stub
					return null;
				}

                public GlobalResolver getGlobalResolver() {
                    // TODO Auto-generated method stub
                    return null;
                }

                public List getRuleFlowEventListeners() {
                    // TODO Auto-generated method stub
                    return null;
                }

                public void clearActivationGroup(String group) {
                    // TODO Auto-generated method stub

                }

                public void clearRuleFlowGroup(String group) {
                    // TODO Auto-generated method stub

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
			return new Column[] { new Column("Pattern 1"),
					new Column("Pattern 2"), new Column("Pattern 3") };
		}

		public String getHeader() {
			return "Test Template Header";
		}

		public Map getTemplates() {
			return null;
		}

		public void setHeader(String head) {
		}

		public void setTemplates(Map templates) {

		}
	}

}
