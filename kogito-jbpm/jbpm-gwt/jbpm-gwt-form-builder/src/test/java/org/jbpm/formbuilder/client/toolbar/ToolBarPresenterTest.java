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
package org.jbpm.formbuilder.client.toolbar;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.bus.GetFormRepresentationResponseEvent;
import org.jbpm.formbuilder.client.bus.ui.EmbededIOReferenceEvent;
import org.jbpm.formbuilder.client.bus.ui.TaskSelectedEvent;
import org.jbpm.formbuilder.client.command.LoadFormCommand;
import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.client.resources.FormBuilderResources;
import org.jbpm.formbuilder.shared.task.TaskRef;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.resources.client.ImageResource;

public class ToolBarPresenterTest extends TestCase {

	private CommonGlobals cg;
	private FormBuilderGlobals fbg;
	private FormBuilderResources res;
	private I18NConstants i18n;
	private EventBus bus;
	private ToolBarView view;
	private ToolRegistration toolreg;
	
	@Before
	@Override
	protected void setUp() throws Exception {
		cg = CommonGlobals.getInstance();
		fbg = FormBuilderGlobals.getInstance();
		bus = new SimpleEventBus();
		i18n = EasyMock.createMock(I18NConstants.class);
		res = EasyMock.createMock(FormBuilderResources.class);
		cg.registerEventBus(bus);
		fbg.registerResources(res);
		fbg.registerI18n(i18n);
		toolreg = EasyMock.createMock(ToolRegistration.class);
		
		view = EasyMock.createMock(ToolBarView.class);
		
		EasyMock.expect(view.addButton(
				EasyMock.anyObject(ImageResource.class), 
				EasyMock.anyObject(String.class), 
				EasyMock.anyObject(ClickHandler.class))).
			andReturn(toolreg).times(4);
		
		EasyMock.expect(res.saveButton()).andReturn(null).once();
		EasyMock.expect(i18n.SaveChangesButton()).andReturn("Save").once();
		
		EasyMock.expect(res.refreshButton()).andReturn(null).once();
		EasyMock.expect(i18n.RefreshFromServerButton()).andReturn("Refresh").once();
		
		EasyMock.expect(res.undoButton()).andReturn(null).once();
		EasyMock.expect(i18n.UndoButton()).andReturn("Undo").once();

		EasyMock.expect(res.redoButton()).andReturn(null).once();
		EasyMock.expect(i18n.RedoButton()).andReturn("Redo").once();
		
	}
	
	@After
	@Override
	protected void tearDown() throws Exception {
		this.bus = null;
		this.view = null;
		this.res = null;
		this.i18n = null;
		cg.registerEventBus(null);
		fbg.registerResources(null);
		fbg.registerI18n(null);
		cg = null;
		fbg = null;
	}

	@Test
	public void testToolBarStartUp() throws Exception {
		EasyMock.replay(view, res, toolreg, i18n);
		new ToolBarPresenter(view);
		EasyMock.verify(view, res, toolreg, i18n);
	}
	
	@Test
	public void testGetFormRepresentationResponseLoadType() throws Exception {
		String saveType = LoadFormCommand.class.getName();
		String warningText = "warning";
		EasyMock.expect(i18n.RefreshButtonWarning()).andReturn(warningText).once();
		view.showDialog(EasyMock.eq(warningText), EasyMock.anyObject(ClickHandler.class));
		EasyMock.expectLastCall().once();
		
		EasyMock.replay(view, res, toolreg, i18n);
		new ToolBarPresenter(view);
		bus.fireEvent(new GetFormRepresentationResponseEvent(null, saveType));
		EasyMock.verify(view, res, toolreg, i18n);
		
	}

	@Test
	public void testGetFormRepresentationResponseOtherType() throws Exception {
		String saveType = "ANYTHING_ELSE";
		EasyMock.replay(view, res, toolreg, i18n);
		new ToolBarPresenter(view);
		bus.fireEvent(new GetFormRepresentationResponseEvent(null, saveType));
		EasyMock.verify(view, res, toolreg, i18n);
	}

	@Test
	public void testEmbededIOReferenceNoProfile() throws Exception {
		EasyMock.replay(view, res, toolreg, i18n);
		new ToolBarPresenter(view);
		bus.fireEvent(new EmbededIOReferenceEvent(null, null));
		EasyMock.verify(view, res, toolreg, i18n);
	}
	
	@Test
	public void testEmbededIOReferenceWithProfile() throws Exception {
		toolreg.remove();
		EasyMock.expectLastCall().once();
		EasyMock.replay(view, res, toolreg, i18n);
		new ToolBarPresenter(view);
		bus.fireEvent(new EmbededIOReferenceEvent(null, "jbpm"));
		EasyMock.verify(view, res, toolreg, i18n);
	}

	@Test
	public void testTaskSelectedNullTask() throws Exception {
		EasyMock.replay(view, res, toolreg, i18n);
		new ToolBarPresenter(view);
		bus.fireEvent(new TaskSelectedEvent(null));
		EasyMock.verify(view, res, toolreg, i18n);
	}
	
	@Test
	public void testTaskSelected() throws Exception {
		String packageText = "package";
		String packageName = "somePackage";
		String processText = "process";
		String processId = "processId";
		String taskText = "task";
		String taskName = "someTask";
		
		EasyMock.expect(i18n.PackageLabel()).andReturn(packageText).once();
		EasyMock.expect(i18n.ProcessLabel()).andReturn(processText).once();
		EasyMock.expect(i18n.TaskNameLabel()).andReturn(taskText).once();
		
		EasyMock.expect(view.addMessage(EasyMock.eq(packageText), EasyMock.eq(packageName))).
			andReturn(toolreg).once();
		EasyMock.expect(view.addMessage(EasyMock.eq(processText), EasyMock.eq(processId))).
			andReturn(toolreg).once();
		EasyMock.expect(view.addMessage(EasyMock.eq(taskText), EasyMock.eq(taskName))).
			andReturn(toolreg).once();

		TaskRef ioRef = new TaskRef();
		ioRef.setPackageName(packageName);
		ioRef.setProcessId(processId);
		ioRef.setTaskId(taskName);
		
		EasyMock.replay(view, res, toolreg, i18n);
		new ToolBarPresenter(view);
		bus.fireEvent(new TaskSelectedEvent(ioRef));
		EasyMock.verify(view, res, toolreg, i18n);
	}
	
	@Test
	public void testTaskDeselected() throws Exception {
		String packageText = "package";
		String packageName = "somePackage";
		String processText = "process";
		String processId = "processId";
		String taskText = "task";
		String taskName = "someTask";
		
		EasyMock.expect(i18n.PackageLabel()).andReturn(packageText).once();
		EasyMock.expect(i18n.ProcessLabel()).andReturn(processText).once();
		EasyMock.expect(i18n.TaskNameLabel()).andReturn(taskText).once();
		
		EasyMock.expect(view.addMessage(EasyMock.eq(packageText), EasyMock.eq(packageName))).
			andReturn(toolreg).once();
		EasyMock.expect(view.addMessage(EasyMock.eq(processText), EasyMock.eq(processId))).
			andReturn(toolreg).once();
		EasyMock.expect(view.addMessage(EasyMock.eq(taskText), EasyMock.eq(taskName))).
			andReturn(toolreg).once();

		toolreg.remove();
		EasyMock.expectLastCall().times(3);
		
		TaskRef ioRef = new TaskRef();
		ioRef.setPackageName(packageName);
		ioRef.setProcessId(processId);
		ioRef.setTaskId(taskName);
		
		EasyMock.replay(view, res, toolreg, i18n);
		new ToolBarPresenter(view);
		bus.fireEvent(new TaskSelectedEvent(ioRef));
		bus.fireEvent(new TaskSelectedEvent(null));
		EasyMock.verify(view, res, toolreg, i18n);
	}
}
