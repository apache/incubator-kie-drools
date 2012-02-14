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
package org.jbpm.formbuilder.client.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.form.LayoutFormItem;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.api.InputData;
import org.jbpm.formapi.shared.api.OutputData;
import org.jbpm.formbuilder.client.bus.FormDataPopulatedEvent;
import org.jbpm.formbuilder.client.bus.GetFormRepresentationEvent;
import org.jbpm.formbuilder.client.bus.GetFormRepresentationResponseEvent;
import org.jbpm.formbuilder.client.bus.GetFormRepresentationResponseHandler;
import org.jbpm.formbuilder.client.bus.RegisterLayoutEvent;
import org.jbpm.formbuilder.client.bus.UndoableEvent;
import org.jbpm.formbuilder.client.bus.UndoableHandler;
import org.jbpm.formbuilder.client.bus.ui.FormSavedEvent;
import org.jbpm.formbuilder.client.bus.ui.GetFormDisplayEvent;
import org.jbpm.formbuilder.client.bus.ui.TaskSelectedEvent;
import org.jbpm.formbuilder.client.bus.ui.UpdateFormViewEvent;
import org.jbpm.formbuilder.client.form.FBForm;
import org.jbpm.formbuilder.shared.task.TaskPropertyRef;
import org.jbpm.formbuilder.shared.task.TaskRef;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

public class LayoutPresenterTest extends TestCase {

	private CommonGlobals cg;
	private LayoutView view;
	private EventBus bus;
	private PickupDragController drag;
	
	@Before
	@Override
	protected void setUp() throws Exception {
		cg = CommonGlobals.getInstance();
		bus = new SimpleEventBus();
		drag = EasyMock.createMock(PickupDragController.class);
		cg.registerEventBus(bus);
		cg.registerDragController(drag);
		view = EasyMock.createMock(LayoutView.class);
		view.startDropController(EasyMock.anyObject(PickupDragController.class), EasyMock.same(view));
		EasyMock.expectLastCall().once();
	}
	
	@After
	@Override
	protected void tearDown() throws Exception {
		view = null;
		cg.registerDragController(null);
		cg.registerEventBus(null);
		drag = null;
		bus = null;
		cg = null;
	}
	
	@Test
	public void testLayoutStartUp() throws Exception {
		EasyMock.replay(view, drag);
		new LayoutPresenter(view);
		EasyMock.verify(view, drag);
	}
	
	@Test
	public void testRegisterLayout() throws Exception {
		LayoutFormItem layout = EasyMock.createMock(LayoutFormItem.class);
		view.startDropController(EasyMock.same(drag), EasyMock.same(layout));
		EasyMock.expectLastCall().once();
		EasyMock.replay(view, drag, layout);
		new LayoutPresenter(view);
		bus.fireEvent(new RegisterLayoutEvent(layout));
		EasyMock.verify(view, drag, layout);
	}
	
	@Test
	public void testGetFormRepresentation() throws Exception {
		FBForm mockForm = EasyMock.createMock(FBForm.class);
		EasyMock.expect(view.getFormDisplay()).andReturn(mockForm);
		final FormRepresentation formRep = new FormRepresentation();
		final String saveType = "ANYTHING";
		EasyMock.expect(mockForm.createRepresentation()).andReturn(formRep);
		EasyMock.replay(view, drag, mockForm);
		bus.addHandler(GetFormRepresentationResponseEvent.TYPE, new GetFormRepresentationResponseHandler() {
			@Override
			public void onEvent(GetFormRepresentationResponseEvent event) {
				assertEquals("both forms should be the same", event.getRepresentation(), formRep);
				assertEquals("both saveTypes should be the same", event.getSaveType(), saveType);
			}
		});
		new LayoutPresenter(view);
		bus.fireEvent(new GetFormRepresentationEvent(saveType));
		EasyMock.verify(view, drag, mockForm);
	}
	
	@Test
	public void testGetFormDisplay() throws Exception {
		FBForm mockForm = EasyMock.createMock(FBForm.class);
		EasyMock.expect(view.getFormDisplay()).andReturn(mockForm);
		EasyMock.replay(view, drag, mockForm);
		new LayoutPresenter(view);
		GetFormDisplayEvent event = new GetFormDisplayEvent();
		bus.fireEvent(event);
		EasyMock.verify(view, drag, mockForm);
		assertEquals("mockForm and formDisplay should be the same", mockForm, event.getFormDisplay());
	}
    
	@Test
	public void testFormDataPopulated() throws Exception {
		final String name = "name", action = "action", method = "method",	
			taskId = "taskId", processId = "processId", enctype = "enctype"; 
		FBForm mockForm = EasyMock.createMock(FBForm.class);
		EasyMock.expect(mockForm.getName()).andReturn(name);
		EasyMock.expect(mockForm.getAction()).andReturn(action);
        EasyMock.expect(mockForm.getProcessId()).andReturn(processId);
        EasyMock.expect(mockForm.getTaskId()).andReturn(taskId);
        EasyMock.expect(mockForm.getMethod()).andReturn(method);
        EasyMock.expect(mockForm.getEnctype()).andReturn(enctype);
        
        mockForm.setName(EasyMock.eq(name));
        EasyMock.expectLastCall().once();
        mockForm.setAction(EasyMock.eq(action));
        EasyMock.expectLastCall().once();
        mockForm.setMethod(EasyMock.eq(method));
        EasyMock.expectLastCall().once();
        mockForm.setTaskId(EasyMock.eq(taskId));
        EasyMock.expectLastCall().once();
        mockForm.setProcessId(EasyMock.eq(processId));
        EasyMock.expectLastCall().once();
        mockForm.setEnctype(EasyMock.eq(enctype));
        EasyMock.expectLastCall().once();
        
		EasyMock.expect(view.getFormDisplay()).andReturn(mockForm).anyTimes();
		EasyMock.replay(view, drag, mockForm);
		bus.addHandler(UndoableEvent.TYPE, new UndoableHandler() {
			@Override
			public void undoAction(UndoableEvent event) { }
			@Override
			public void doAction(UndoableEvent event) { }
			@Override
			public void onEvent(UndoableEvent event) {
				assertEquals("name and event.name should be the same", name, event.getData("oldName"));
				assertEquals("action and event.action should be the same", action, event.getData("oldAction"));
				assertEquals("method and event.method should be the same", method, event.getData("oldMethod"));
				assertEquals("taskId and event.taskId should be the same", taskId, event.getData("oldTaskId"));
				assertEquals("processId and event.processId should be the same", processId, event.getData("oldProcessId"));
				assertEquals("enctype and event.enctype should be the same", enctype, event.getData("oldEnctype"));

				assertEquals("name and event.name should be the same", name, event.getData("newName"));
				assertEquals("action and event.action should be the same", action, event.getData("newAction"));
				assertEquals("method and event.method should be the same", method, event.getData("newMethod"));
				assertEquals("taskId and event.taskId should be the same", taskId, event.getData("newTaskId"));
				assertEquals("processId and event.processId should be the same", processId, event.getData("newProcessId"));
				assertEquals("enctype and event.enctype should be the same", enctype, event.getData("newEnctype"));
			}
		});
		new LayoutPresenter(view);
		bus.fireEvent(new FormDataPopulatedEvent(action, method, taskId, processId, enctype, name));
		EasyMock.verify(view, drag, mockForm);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testTaskSelected() throws Exception {
		final String taskId = "taskId", processId = "processId"; 
		TaskRef ioRef = new TaskRef();
		ioRef.setTaskId(taskId);
		ioRef.setProcessId(processId);
		ioRef.setInputs(new ArrayList<TaskPropertyRef>());
		ioRef.setOutputs(new ArrayList<TaskPropertyRef>());
		FBForm mockForm = EasyMock.createMock(FBForm.class);
		EasyMock.expect(mockForm.getTaskId()).andReturn(taskId).once();
		EasyMock.expect(mockForm.getProcessId()).andReturn(processId).once();
		EasyMock.expect(mockForm.getInputs()).andReturn(new HashMap<String, InputData>()).once();
		EasyMock.expect(mockForm.getOutputs()).andReturn(new HashMap<String, OutputData>()).once();
        mockForm.setTaskId(EasyMock.eq(taskId));
        EasyMock.expectLastCall().once();
        mockForm.setProcessId(EasyMock.eq(processId));
        EasyMock.expectLastCall().once();
        mockForm.setInputs(EasyMock.anyObject(Map.class));
        EasyMock.expectLastCall().once();
        mockForm.setOutputs(EasyMock.anyObject(Map.class));
        EasyMock.expectLastCall().once();
		EasyMock.expect(view.getFormDisplay()).andReturn(mockForm).anyTimes();
		
		bus.addHandler(UndoableEvent.TYPE, new UndoableHandler() {
			@Override
			public void doAction(UndoableEvent event) { }
			@Override
			public void undoAction(UndoableEvent event) { }
			@Override
			public void onEvent(UndoableEvent event) {
				assertEquals("taskId and event.oldTaskID should be the same", 
						taskId, event.getData("oldTaskID"));
				assertEquals("processId and event.oldProcessID should be the same", 
						processId, event.getData("oldProcessID"));

				assertEquals("taskId and event.newTaskID should be the same", 
						taskId, event.getData("newTaskID"));
				assertEquals("processId and event.newProcessID should be the same", 
						processId, event.getData("newProcessID"));
			}
		});
		
		EasyMock.replay(view, drag, mockForm);
		new LayoutPresenter(view); 
		bus.fireEvent(new TaskSelectedEvent(ioRef));
		EasyMock.verify(view, drag, mockForm);
	}
	
	@Test
	public void testFormSaved() throws Exception {
		FBForm mockForm = EasyMock.createMock(FBForm.class);
		EasyMock.expect(view.getFormDisplay()).andReturn(mockForm).once();
		mockForm.setSaved(EasyMock.eq(true));
		EasyMock.expectLastCall().once();
		FormRepresentation formRep = new FormRepresentation();
		
		EasyMock.replay(view, drag, mockForm);
		new LayoutPresenter(view);
		bus.fireEvent(new FormSavedEvent(formRep));
		EasyMock.verify(view, drag, mockForm);
	}

	@Test
	public void testUpdateFormView() throws Exception {
		final FormRepresentation formRep = new FormRepresentation();
		FBForm mockForm = EasyMock.createMock(FBForm.class);
		EasyMock.expect(view.getFormDisplay()).andReturn(mockForm).anyTimes();
		EasyMock.expect(mockForm.createRepresentation()).andReturn(formRep);
		mockForm.populate(formRep);
		EasyMock.expectLastCall().once();
		
		bus.addHandler(UndoableEvent.TYPE, new UndoableHandler() {
			@Override
			public void undoAction(UndoableEvent event) { }
			@Override
			public void doAction(UndoableEvent event) { }
			@Override
			public void onEvent(UndoableEvent event) {
				assertNotNull("oldForm shouldn't be null", event.getData("oldForm"));
				assertNotNull("newForm shouldn't be null", event.getData("newForm"));
				assertEquals("oldForm and newForm should be the same",
						event.getData("oldForm"), event.getData("newForm"));
			}
		});
		
		EasyMock.replay(view, drag, mockForm);
		new LayoutPresenter(view);
		bus.fireEvent(new UpdateFormViewEvent(formRep));
		EasyMock.verify(view, drag, mockForm);
	}
}
