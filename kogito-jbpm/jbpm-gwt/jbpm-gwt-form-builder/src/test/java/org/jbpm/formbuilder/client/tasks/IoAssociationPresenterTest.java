/*
 * Copyright 2011 JBoss Inc
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
package org.jbpm.formbuilder.client.tasks;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.common.handler.RightClickHandler;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.FormBuilderService;
import org.jbpm.formbuilder.client.MockHandlerRegistration;
import org.jbpm.formbuilder.client.bus.ExistingTasksResponseEvent;
import org.jbpm.formbuilder.client.bus.ui.EmbededIOReferenceEvent;
import org.jbpm.formbuilder.client.bus.ui.TaskNameFilterEvent;
import org.jbpm.formbuilder.client.bus.ui.TaskSelectedEvent;
import org.jbpm.formbuilder.client.bus.ui.TaskSelectedHandler;
import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.shared.task.TaskRef;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

public class IoAssociationPresenterTest extends TestCase {

	private FormBuilderGlobals fbg;
	private CommonGlobals cg;
	private IoAssociationView view;
	private SearchFilterView filterView;
	private AdvancedSearchView advancedView;
	private FormBuilderService service;
	private I18NConstants i18n;
	private EventBus bus;
	
	@Before
	@Override
	protected void setUp() throws Exception {
		cg = CommonGlobals.getInstance();
		fbg = FormBuilderGlobals.getInstance();
		view = EasyMock.createMock(IoAssociationView.class);
		filterView = EasyMock.createMock(SearchFilterView.class);
		advancedView = EasyMock.createMock(AdvancedSearchView.class);
		EasyMock.expect(view.getSearch()).andReturn(filterView);
		EasyMock.expect(filterView.getAdvancedView()).andReturn(advancedView);
		bus = new SimpleEventBus();
		service = EasyMock.createMock(FormBuilderService.class);
		i18n = EasyMock.createMock(I18NConstants.class);
		fbg.registerService(service);
		fbg.registerI18n(i18n);
		cg.registerEventBus(bus);
	}

	@After
	@Override
	protected void tearDown() throws Exception {
		view = null;
		filterView = null;
		advancedView = null;
		bus = null;
		service = null;
		i18n = null;
		fbg.registerService(null);
		fbg.registerI18n(null);
		cg.registerEventBus(null);
		cg = null;
		fbg = null;
	}
	
	@Test
	public void testIoAssociationStartUp() throws Exception {
		EasyMock.replay(view, service, i18n, filterView, advancedView);
		new IoAssociationPresenter(view);
		EasyMock.verify(view, service, i18n, filterView, advancedView);
	}

	@Test
	public void testTaskNameFilter() throws Exception {
		String filter = "";
		service.getExistingIoAssociations(EasyMock.eq(filter));
		EasyMock.expectLastCall().once();
		
		EasyMock.replay(view, service, i18n, filterView, advancedView);
		new IoAssociationPresenter(view);
		bus.fireEvent(new TaskNameFilterEvent(filter));
		EasyMock.verify(view, service, i18n, filterView, advancedView);
	}
	
	@Test 
	public void testExistingTasksResponseFromService() throws Exception {
		String filter = "";
		List<TaskRef> tasks = new ArrayList<TaskRef>();
		view.setTasks(EasyMock.eq(tasks));
		EasyMock.expectLastCall().once();
		
		EasyMock.replay(view, service, i18n, filterView, advancedView);
		new IoAssociationPresenter(view);
		bus.fireEventFromSource(new ExistingTasksResponseEvent(tasks, filter), service);
		EasyMock.verify(view, service, i18n, filterView, advancedView);
	}
	
	@Test
	public void testExistingTaskResponseFromAdvancedView() throws Exception {
		String filter = "";
		List<TaskRef> tasks = new ArrayList<TaskRef>();
		view.setTasks(EasyMock.eq(tasks));
		EasyMock.expectLastCall().once();
		
		EasyMock.replay(view, service, i18n, filterView, advancedView);
		new IoAssociationPresenter(view);
		bus.fireEventFromSource(new ExistingTasksResponseEvent(tasks, filter), advancedView);
		EasyMock.verify(view, service, i18n, filterView, advancedView);
	}
	
	@Test
	public void testTaskSelected() throws Exception {
		TaskRef task = new TaskRef();
		view.setSelectedTask(EasyMock.eq(task));
		EasyMock.expectLastCall().once();
		
		EasyMock.replay(view, service, i18n, filterView, advancedView);
		new IoAssociationPresenter(view);
		bus.fireEvent(new TaskSelectedEvent(task));
		EasyMock.verify(view, service, i18n, filterView, advancedView);
	}
	
	public void testEmbededIOReferenceNotNullTask() throws Exception {
		final TaskRef ioRef = new TaskRef();
		view.disableSearch();
		EasyMock.expectLastCall().once();
		view.setSelectedTask(EasyMock.eq(ioRef));
		EasyMock.expectLastCall().once();
		TaskSelectedHandler handler = EasyMock.createMock(TaskSelectedHandler.class);
		handler.onSelectedTask(EasyMock.anyObject(TaskSelectedEvent.class));
		EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
			public Object answer() throws Throwable {
				Object[] params = EasyMock.getCurrentArguments();
				TaskSelectedEvent event = (TaskSelectedEvent) params[0];
				assertNotNull("event.selectedTask shouldn't be null", event.getSelectedTask());
				assertEquals("ioRef and event.selectedTask should be the same", ioRef, event.getSelectedTask());
				return null;
			}
		}).once();
		bus.addHandler(TaskSelectedEvent.TYPE, handler);
		EasyMock.replay(view, service, i18n, filterView, advancedView, handler);
		new IoAssociationPresenter(view);
		bus.fireEvent(new EmbededIOReferenceEvent(ioRef, "jbpm"));
		EasyMock.verify(view, service, i18n, filterView, advancedView, handler);
	}
	
	public void testEmbededIOReferenceNullTask() throws Exception {
		EasyMock.replay(view, service, i18n, filterView, advancedView);
		new IoAssociationPresenter(view);
		bus.fireEvent(new EmbededIOReferenceEvent(null, "jbpm"));
		EasyMock.verify(view, service, i18n, filterView, advancedView);
	}
	
	public void testNewTaskRow() throws Exception {
		TaskRef task = new TaskRef();
		TaskRow row = EasyMock.createMock(TaskRow.class);
		EasyMock.expect(row.addRightClickHandler(EasyMock.isA(RightClickHandler.class))).
			andReturn(new MockHandlerRegistration()).once();
		EasyMock.expect(view.createTaskRow(EasyMock.same(task), EasyMock.eq(false))).
			andReturn(row);
		EasyMock.replay(view, service, i18n, filterView, advancedView, row);
		IoAssociationPresenter presenter = new IoAssociationPresenter(view);
		TaskRow row2 = presenter.newTaskRow(task, false);
		EasyMock.verify(view, service, i18n, filterView, advancedView, row);
		assertNotNull("row2 shouldn't be null", row2);
		assertSame("row and row2 should be the same", row, row2);
	}
	
	public void testAddQuickFormHandling() throws Exception {
		TaskRow row = EasyMock.createMock(TaskRow.class);
		EasyMock.expect(row.addRightClickHandler(EasyMock.isA(RightClickHandler.class))).
			andReturn(new MockHandlerRegistration()).once();
		
		EasyMock.replay(view, service, i18n, filterView, advancedView, row);
		IoAssociationPresenter presenter = new IoAssociationPresenter(view);
		presenter.addQuickFormHandling(row);
		EasyMock.verify(view, service, i18n, filterView, advancedView, row);
	}
}
