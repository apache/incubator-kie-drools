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
package org.jbpm.formbuilder.client.menu;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.menu.FBMenuItem;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.FormBuilderService;
import org.jbpm.formbuilder.client.RoleUtils;
import org.jbpm.formbuilder.client.bus.MenuItemAddedEvent;
import org.jbpm.formbuilder.client.bus.MenuItemFromServerEvent;
import org.jbpm.formbuilder.client.bus.MenuItemRemoveEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

public class MenuPresenterTest extends TestCase {

    private CommonGlobals cg;
    private EventBus bus;
    private PickupDragController drag;
    private MenuView view;
    
    @Before
    @Override
    protected void setUp() throws Exception {
        cg = CommonGlobals.getInstance();
        bus = new SimpleEventBus();
        cg.registerEventBus(bus);
        drag = EasyMock.createMock(PickupDragController.class);
        view = EasyMock.createMock(MenuView.class);
        cg.registerDragController(drag);
        view.startDropController(EasyMock.eq(drag));
        EasyMock.expectLastCall().once();
    }

    @After
    @Override
    protected void tearDown() throws Exception {
        this.bus = null;
        this.cg = null;
        this.drag = null;
        this.view = null;
    }
    
    @Test
    public void testMenuStartup() throws Exception {
        EasyMock.replay(view, drag);
        new MenuPresenter(view);
        EasyMock.verify(view, drag);
    }

    @Test
    public void testMenuItemAddedForUser() throws Exception {
        FormBuilderService service = EasyMock.createMock(FormBuilderService.class);
        FormBuilderGlobals.getInstance().registerService(service);
        FBMenuItem menuItem = EasyMock.createMock(FBMenuItem.class);
        String groupName = "group";
        service.getCurrentRoles(EasyMock.isA(FormBuilderService.RolesResponseHandler.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                Object[] params = EasyMock.getCurrentArguments();
                FormBuilderService.RolesResponseHandler handler = (FormBuilderService.RolesResponseHandler) params[0];
                List<String> roles = new ArrayList<String>();
                roles.add("functionalanalyst");
                handler.onResponse(roles);
                return null;
            }
        }).atLeastOnce();
        EasyMock.replay(service, menuItem, view, drag);
        RoleUtils.getInstance().reload();
        new MenuPresenter(view);
        bus.fireEvent(new MenuItemAddedEvent(menuItem, groupName));
        EasyMock.verify(service, menuItem, view, drag);
    }
    
    @Test
    public void testMenuItemAddedForDesigner() throws Exception {
        FormBuilderService service = EasyMock.createMock(FormBuilderService.class);
        FormBuilderGlobals.getInstance().registerService(service);
        FBMenuItem menuItem = EasyMock.createMock(FBMenuItem.class);
        String groupName = "group";
        view.addItem(EasyMock.same(groupName), EasyMock.same(menuItem));
        EasyMock.expectLastCall().once();
        service.getCurrentRoles(EasyMock.isA(FormBuilderService.RolesResponseHandler.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                Object[] params = EasyMock.getCurrentArguments();
                FormBuilderService.RolesResponseHandler handler = (FormBuilderService.RolesResponseHandler) params[0];
                List<String> roles = new ArrayList<String>();
                roles.add("webdesigner");
                handler.onResponse(roles);
                return null;
            }
        }).atLeastOnce();
        EasyMock.replay(service, menuItem, view, drag);
        RoleUtils.getInstance().reload();
        new MenuPresenter(view);
        bus.fireEvent(new MenuItemAddedEvent(menuItem, groupName));
        EasyMock.verify(service, menuItem, view, drag);
    }

    @Test
    public void testMenuItemRemoved() throws Exception  {
        FBMenuItem menuItem = EasyMock.createMock(FBMenuItem.class);
        String groupName = "group";
        view.removeItem(EasyMock.same(groupName), EasyMock.same(menuItem));
        EasyMock.expectLastCall().once();
        EasyMock.replay(menuItem, view, drag);
        RoleUtils.getInstance().reload();
        new MenuPresenter(view);
        bus.fireEvent(new MenuItemRemoveEvent(menuItem, groupName));
        EasyMock.verify(menuItem, view, drag);
    }

    @Test
    public void testMenuItemFromServerForUser() throws Exception {
        FormBuilderService service = EasyMock.createMock(FormBuilderService.class);
        FormBuilderGlobals.getInstance().registerService(service);
        FBMenuItem menuItem = EasyMock.createMock(FBMenuItem.class);
        String groupName = "group";
        service.getCurrentRoles(EasyMock.isA(FormBuilderService.RolesResponseHandler.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                Object[] params = EasyMock.getCurrentArguments();
                FormBuilderService.RolesResponseHandler handler = (FormBuilderService.RolesResponseHandler) params[0];
                List<String> roles = new ArrayList<String>();
                roles.add("functionalanalyst");
                handler.onResponse(roles);
                return null;
            }
        }).atLeastOnce();
        EasyMock.replay(service, menuItem, view, drag);
        RoleUtils.getInstance().reload();
        new MenuPresenter(view);
        bus.fireEvent(new MenuItemFromServerEvent(menuItem, groupName));
        EasyMock.verify(service, menuItem, view, drag);
    }
    
    @Test
    public void testMenuItemFromServerForDesigner() throws Exception {
        FormBuilderService service = EasyMock.createMock(FormBuilderService.class);
        FormBuilderGlobals.getInstance().registerService(service);
        FBMenuItem menuItem = EasyMock.createMock(FBMenuItem.class);
        String groupName = "group";
        view.addItem(EasyMock.same(groupName), EasyMock.same(menuItem));
        EasyMock.expectLastCall().once();
        service.getCurrentRoles(EasyMock.isA(FormBuilderService.RolesResponseHandler.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                Object[] params = EasyMock.getCurrentArguments();
                FormBuilderService.RolesResponseHandler handler = (FormBuilderService.RolesResponseHandler) params[0];
                List<String> roles = new ArrayList<String>();
                roles.add("webdesigner");
                handler.onResponse(roles);
                return null;
            }
        }).atLeastOnce();
        EasyMock.replay(service, menuItem, view, drag);
        RoleUtils.getInstance().reload();
        new MenuPresenter(view);
        bus.fireEvent(new MenuItemFromServerEvent(menuItem, groupName));
        EasyMock.verify(service, menuItem, view, drag);
    }
}
