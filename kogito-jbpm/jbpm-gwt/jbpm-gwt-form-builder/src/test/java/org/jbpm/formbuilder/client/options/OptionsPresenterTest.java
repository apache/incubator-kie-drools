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
package org.jbpm.formbuilder.client.options;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formbuilder.client.bus.MenuOptionAddedEvent;
import org.jbpm.formbuilder.client.bus.ui.EmbededIOReferenceEvent;
import org.jbpm.formbuilder.client.command.BaseCommand;
import org.jbpm.formbuilder.shared.task.TaskRef;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.MenuItem;

public class OptionsPresenterTest extends TestCase {

    private OptionsView view;
    private CommonGlobals cg;
    private EventBus bus;
    
    @Before
    @Override
    protected void setUp() throws Exception {
        view = EasyMock.createMock(OptionsView.class);
        bus = new SimpleEventBus();
        cg = CommonGlobals.getInstance();
        cg.registerEventBus(bus);
    }
    
    @After
    @Override
    protected void tearDown() throws Exception {
        view = null;
        bus = null;
        cg.registerEventBus(bus);
    }
    
    @Test
    public void testOptionsStartUp() throws Exception {
        EasyMock.replay(view);
        new OptionsPresenter(view);
        EasyMock.verify(view);
    }

    public void testMenuOptionAdded() throws Exception {
        MainMenuOption option = new MainMenuOption();
        option.setEnabled(true);
        option.setHtml("Option1");
        
        view.addItem(EasyMock.same(option));
        EasyMock.expectLastCall().once();
        EasyMock.replay(view);
        new OptionsPresenter(view);
        bus.fireEvent(new MenuOptionAddedEvent(option));
        EasyMock.verify(view);
    }
    
    public void testEmbededIOReferenceItemsEmpty() throws Exception {
        EasyMock.expect(view.getItems()).andReturn(new ArrayList<MenuItem>());
        EasyMock.replay(view);
        new OptionsPresenter(view);
        bus.fireEvent(new EmbededIOReferenceEvent(new TaskRef(), "jbpm"));
        EasyMock.verify(view);
    }

    public void testEmbededIOReferenceItemsOneElem() throws Exception {
        ArrayList<MenuItem> items = new ArrayList<MenuItem>();
        MenuItem mockItem1 = EasyMock.createMock(MenuItem.class);
        BaseCommand mockCommand1 = EasyMock.createMock(BaseCommand.class);
        EasyMock.expect(mockItem1.getCommand()).andReturn(mockCommand1);
        items.add(mockItem1);
        mockCommand1.setEmbeded(EasyMock.eq("jbpm"));
        EasyMock.expectLastCall().once();
        
        EasyMock.expect(view.getItems()).andReturn(items);
        EasyMock.replay(view, mockItem1, mockCommand1);
        new OptionsPresenter(view);
        bus.fireEvent(new EmbededIOReferenceEvent(new TaskRef(), "jbpm"));
        EasyMock.verify(view, mockItem1, mockCommand1);
    }
    
    public void testEmbededIOReferenceItemsManyElems() throws Exception {
        ArrayList<MenuItem> items = new ArrayList<MenuItem>();
        MenuItem mockItem1 = EasyMock.createMock(MenuItem.class);
        MenuItem mockItem2 = EasyMock.createMock(MenuItem.class);
        BaseCommand mockCommand1 = EasyMock.createMock(BaseCommand.class);
        BaseCommand mockCommand2 = EasyMock.createMock(BaseCommand.class);
        EasyMock.expect(mockItem1.getCommand()).andReturn(mockCommand1);
        EasyMock.expect(mockItem2.getCommand()).andReturn(mockCommand2);
        mockCommand1.setEmbeded(EasyMock.eq("jbpm"));
        EasyMock.expectLastCall().once();
        mockCommand2.setEmbeded(EasyMock.eq("jbpm"));
        EasyMock.expectLastCall().once();
        
        items.add(mockItem1);
        items.add(mockItem2);
        
        EasyMock.expect(view.getItems()).andReturn(items);
        EasyMock.replay(view, mockItem1, mockCommand1);
        new OptionsPresenter(view);
        bus.fireEvent(new EmbededIOReferenceEvent(new TaskRef(), "jbpm"));
        EasyMock.verify(view, mockItem1, mockCommand1);
    }
}
