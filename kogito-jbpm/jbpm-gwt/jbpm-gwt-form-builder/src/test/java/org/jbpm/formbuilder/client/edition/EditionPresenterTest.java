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
package org.jbpm.formbuilder.client.edition;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.bus.FormItemSelectionEvent;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formbuilder.client.bus.UndoableEvent;
import org.jbpm.formbuilder.client.bus.UndoableHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

public class EditionPresenterTest extends TestCase {

    private CommonGlobals cg;
    private EditionView view;
    private EventBus bus;
    
    @Before
    @Override
    protected void setUp() throws Exception {
        view = EasyMock.createMock(EditionView.class);
        cg = CommonGlobals.getInstance();
        bus = new SimpleEventBus();
        cg.registerEventBus(bus);
    }
    
    @After
    @Override
    protected void tearDown() throws Exception {
        view = null;
        bus = null;
        cg.registerEventBus(null);
        cg = null;
    }
    
    @Test
    public void testEditionStartUp() throws Exception {
        EasyMock.replay(view);
        new EditionPresenter(view);
        EasyMock.verify(view);
    }
    
    @Test
    public void testFormItemSelectionSelected() throws Exception {
        FBFormItem formItem = EasyMock.createMock(FBFormItem.class);
        view.selectTab();
        EasyMock.expectLastCall().once();
        view.populate(EasyMock.same(formItem));
        EasyMock.expectLastCall().once();
        EasyMock.replay(view, formItem);
        new EditionPresenter(view);
        bus.fireEvent(new FormItemSelectionEvent(formItem, true));
        EasyMock.verify(view, formItem);
    }
    
    @Test
    public void testFormItemSelectionDeselected() throws Exception {
        FBFormItem formItem = EasyMock.createMock(FBFormItem.class);
        EasyMock.replay(view, formItem);
        new EditionPresenter(view);
        bus.fireEvent(new FormItemSelectionEvent(formItem, false));
        EasyMock.verify(view, formItem);
    }
    
    @Test
    public void testOnSaveChanges() throws Exception {
        final FBFormItem formItem = EasyMock.createMock(FBFormItem.class);
        final Map<String, Object> oldProps = new HashMap<String, Object>();
        final Map<String, Object> newProps = new HashMap<String, Object>();
        
        UndoableHandler handler = EasyMock.createMock(UndoableHandler.class);
        handler.onEvent(EasyMock.isA(UndoableEvent.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                Object[] params = EasyMock.getCurrentArguments();
                UndoableEvent event = (UndoableEvent) params[0];
                assertNotNull("oldItems shouldn't be null", event.getData("oldItems"));
                assertNotNull("newItems shouldn't be null", event.getData("newItems"));
                assertNotNull("itemSelected shouldn't be null", event.getData("itemSelected"));
                assertSame("oldProps and oldItems should be the same", oldProps, event.getData("oldItems"));
                assertSame("newProps and newItems should be the same", newProps, event.getData("newItems"));
                assertSame("formItem and itemSelected should be the same", formItem, event.getData("itemSelected"));
                return null;
            }
        }).once();
        bus.addHandler(UndoableEvent.TYPE, handler);
        formItem.saveValues(EasyMock.same(newProps));
        EasyMock.expectLastCall().once();
        EasyMock.replay(view, formItem);
        EditionPresenter presenter = new EditionPresenter(view);
        presenter.onSaveChanges(oldProps, newProps, formItem);
        EasyMock.verify(view, formItem);
    }
    
    @Test
    public void testOnResetChanges() throws Exception {
        final FBFormItem formItem = EasyMock.createMock(FBFormItem.class);
        final Map<String, Object> newProps = new HashMap<String, Object>();
        UndoableHandler handler = EasyMock.createMock(UndoableHandler.class);
        handler.onEvent(EasyMock.isA(UndoableEvent.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                Object[] params = EasyMock.getCurrentArguments();
                UndoableEvent event = (UndoableEvent) params[0];
                assertNotNull("newItems shouldn't be null", event.getData("newItems"));
                assertNotNull("fakeItemSelected shouldn't be null", event.getData("fakeItemSelected"));
                assertSame("newProps and newItems should be the same", newProps, event.getData("newItems"));
                assertSame("formItem and fakeItemSelected should be the same", formItem, event.getData("itemSelected"));
                return null;
            }
        }).once();
        bus.addHandler(UndoableEvent.TYPE, handler);
        view.populate(EasyMock.same(formItem));
        EasyMock.expectLastCall().once();
        EasyMock.replay(view, formItem);
        EditionPresenter presenter = new EditionPresenter(view);
        presenter.onResetChanges(formItem, newProps);
        EasyMock.verify(view, formItem);
    }
}
