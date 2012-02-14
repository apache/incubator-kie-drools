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
package org.jbpm.formbuilder.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.easymock.EasyMock;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jbpm.formapi.shared.menu.FormEffectDescription;
import org.jbpm.formapi.shared.menu.MenuItemDescription;
import org.jbpm.formapi.shared.menu.MenuOptionDescription;
import org.jbpm.formapi.shared.menu.ValidationDescription;
import org.jbpm.formbuilder.server.xml.ListMenuItemsDTO;
import org.jbpm.formbuilder.server.xml.ListOptionsDTO;
import org.jbpm.formbuilder.server.xml.ListValidationsDTO;
import org.jbpm.formbuilder.server.xml.MenuGroupDTO;
import org.jbpm.formbuilder.server.xml.MenuItemDTO;
import org.jbpm.formbuilder.server.xml.MenuOptionDTO;
import org.jbpm.formbuilder.server.xml.PropertiesDTO;
import org.jbpm.formbuilder.server.xml.PropertiesItemDTO;
import org.jbpm.formbuilder.server.xml.SaveMenuItemDTO;
import org.jbpm.formbuilder.server.xml.ValidationDTO;
import org.jbpm.formbuilder.shared.menu.MenuService;
import org.jbpm.formbuilder.shared.menu.MenuServiceException;

public class RESTMenuServiceTest extends RESTAbstractTest {

    //test happy path for RESTMenuService.listMenuItems()
    public void testListMenuItemsOK() throws Exception {
        RESTMenuService restService = new RESTMenuService();
        MenuService menuService = EasyMock.createMock(MenuService.class);
        Map<String, List<MenuItemDescription>> retval = new HashMap<String, List<MenuItemDescription>>();
        List<MenuItemDescription> menuList = new ArrayList<MenuItemDescription>();
        MenuItemDescription menuItem1 = new MenuItemDescription();
        List<String> allowedEvents = new ArrayList<String>();
        allowedEvents.add("onclick");
        allowedEvents.add("onblur");
        allowedEvents.add("onfocus");
        List<FormEffectDescription> effects = new ArrayList<FormEffectDescription>();
        FormEffectDescription effect1 = new FormEffectDescription();
        effect1.setClassName("org.jbpm.formbuilder.client.effect.ResizeEffect");
        effects.add(effect1);
        FormEffectDescription effect2 = new FormEffectDescription();
        effect2.setClassName("org.jbpm.formbuilder.client.effect.RemoveEffect");
        effects.add(effect2);
        menuItem1.setAllowedEvents(allowedEvents);
        menuItem1.setClassName("org.jbpm.formbuilder.client.menu.items.ClientScriptMenuItem");
        menuItem1.setEffects(effects);
        MenuItemDescription menuItem2 = new MenuItemDescription();
        menuItem2.setClassName("org.jbpm.formbuilder.client.menu.items.TableLayoutMenuItem");
        menuItem2.setAllowedEvents(allowedEvents);
        menuList.add(menuItem1);
        menuList.add(menuItem2);
        retval.put("group", menuList);
        EasyMock.expect(menuService.listMenuItems()).andReturn(retval).once();
        restService.setMenuService(menuService);
        
        EasyMock.replay(menuService);
        Response resp = restService.listMenuItems();
        EasyMock.verify(menuService);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.OK);
        assertNotNull("resp.entity shouldn't be null", resp.getEntity());
        Object entity = resp.getEntity();
        assertNotNull("resp.metadata shouldn't be null", resp.getMetadata());
        Object contentType = resp.getMetadata().getFirst(HttpHeaderNames.CONTENT_TYPE);
        assertNotNull("resp.entity shouldn't be null", contentType);
        assertEquals("contentType should be application/xml but is" + contentType, contentType, MediaType.APPLICATION_XML);
        assertTrue("entity should be of type ListMenuItemsDTO", entity instanceof ListMenuItemsDTO);
        ListMenuItemsDTO dto = (ListMenuItemsDTO) entity;
        List<MenuGroupDTO> menuGroup = dto.getMenuGroup();
        assertNotNull("dto.menuGroup shouldn't be null", menuGroup);
        assertFalse("dto.menuGroup shouldn't be empty", menuGroup.isEmpty());
        assertEquals("dto.menuGroup should have one element but has " + menuGroup.size(), menuGroup.size(), 1);
        MenuGroupDTO groupDto = menuGroup.iterator().next();
        assertNotNull("groupDto shouldn't be null", groupDto);
        List<MenuItemDTO> menuItems = groupDto.getMenuItem();
        assertNotNull("menuItems shouldn't be null", menuItems);
        assertFalse("menuItems shouldn't be empty", menuItems.isEmpty());
        assertEquals("menuItems.size() should be " + menuList.size() + " but is " + menuItems.size(), menuItems.size(), menuList.size());
    }
    
    //test response to a MenuServiceException on RESTMenuService.listMenuItems()
    public void testListMenuItemsServiceProblem() throws Exception {
        RESTMenuService restService = new RESTMenuService();
        MenuService menuService = EasyMock.createMock(MenuService.class);
        MenuServiceException exception = new MenuServiceException("Something going wrong");
        EasyMock.expect(menuService.listMenuItems()).andThrow(exception).once();
        restService.setMenuService(menuService);
        
        EasyMock.replay(menuService);
        Response resp = restService.listMenuItems();
        EasyMock.verify(menuService);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test happy path for RESTMenuService.listMenuOptions()
    public void testListMenuOptionsOK() throws Exception {
        RESTMenuService restService = new RESTMenuService();
        MenuService menuService = EasyMock.createMock(MenuService.class);
        List<MenuOptionDescription> retval = new ArrayList<MenuOptionDescription>();
        MenuOptionDescription option1 = new MenuOptionDescription();
        option1.setCommandClass("aaa");
        option1.setHtml("bbb");
        MenuOptionDescription option2 = new MenuOptionDescription();
        List<MenuOptionDescription> subMenu = new ArrayList<MenuOptionDescription>();
        MenuOptionDescription option2_1 = new MenuOptionDescription();
        option2_1.setCommandClass("ccc");
        MenuOptionDescription option2_2 = new MenuOptionDescription();
        subMenu.add(option2_1);
        subMenu.add(option2_2);
        option2.setHtml("eee");
        option2.setSubMenu(subMenu);
        retval.add(option1);
        retval.add(option2);
        EasyMock.expect(menuService.listOptions()).andReturn(retval).once();
        restService.setMenuService(menuService);
        
        EasyMock.replay(menuService);
        Response resp = restService.listMenuOptions();
        EasyMock.verify(menuService);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.OK);
        assertNotNull("resp.entity shouldn't be null", resp.getEntity());
        Object entity = resp.getEntity();
        assertNotNull("resp.metadata shouldn't be null", resp.getMetadata());
        Object contentType = resp.getMetadata().getFirst(HttpHeaderNames.CONTENT_TYPE);
        assertNotNull("resp.entity shouldn't be null", contentType);
        assertEquals("contentType should be application/xml but is" + contentType, contentType, MediaType.APPLICATION_XML);
        assertTrue("entity should be of type ListOptionsDTO", entity instanceof ListOptionsDTO);
        ListOptionsDTO dto = (ListOptionsDTO) entity;
        List<MenuOptionDTO> options = dto.getMenuOption();
        assertNotNull("options shouldn't be null", options);
        assertFalse("options shouldn't be empty", options.isEmpty());
        assertEquals("options should have " + retval.size() + " elements but has " + options.size(), options.size(), retval.size());
    }
    
    //test response to a MenuServiceException on RESTMenuService.listMenuOptions()
    public void testListMenuOptionsServiceProblem() throws Exception {
        RESTMenuService restService = new RESTMenuService();
        MenuService menuService = EasyMock.createMock(MenuService.class);
        MenuServiceException exception = new MenuServiceException("Something going wrong");
        EasyMock.expect(menuService.listOptions()).andThrow(exception).once();
        restService.setMenuService(menuService);
        
        EasyMock.replay(menuService);
        Response resp = restService.listMenuOptions();
        EasyMock.verify(menuService);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test happy path for RESTMenuService.getValidations()
    public void testGetValidationsOK() throws Exception {
        RESTMenuService restService = new RESTMenuService();
        MenuService menuService = EasyMock.createMock(MenuService.class);
        List<ValidationDescription> retval = new ArrayList<ValidationDescription>();
        ValidationDescription validation1 = new ValidationDescription();
        validation1.setClassName("aaa");
        validation1.setProperties(new HashMap<String, String>());
        ValidationDescription validation2 = new ValidationDescription();
        validation2.setClassName("bbb");
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("ccc", "CCC");
        properties.put("ddd", "DDD");
        validation2.setProperties(properties);
        retval.add(validation1);
        retval.add(validation2);
        EasyMock.expect(menuService.listValidations()).andReturn(retval).once();
        restService.setMenuService(menuService);
        
        EasyMock.replay(menuService);
        Response resp = restService.getValidations();
        EasyMock.verify(menuService);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.OK);
        assertNotNull("resp.entity shouldn't be null", resp.getEntity());
        Object entity = resp.getEntity();
        assertNotNull("resp.metadata shouldn't be null", resp.getMetadata());
        Object contentType = resp.getMetadata().getFirst(HttpHeaderNames.CONTENT_TYPE);
        assertNotNull("resp.entity shouldn't be null", contentType);
        assertEquals("contentType should be application/xml but is" + contentType, contentType, MediaType.APPLICATION_XML);
        assertTrue("entity should be of type ListOptionsDTO", entity instanceof ListValidationsDTO);
        ListValidationsDTO dto = (ListValidationsDTO) entity;
        List<ValidationDTO> validations = dto.getValidation();
        assertNotNull("validations shouldn't be null", validations);
        assertFalse("validations shouldn't be empty", validations.isEmpty());
        assertEquals("validations should have " + retval.size() + " elements but has " + validations.size(), validations.size(), retval.size());
    }
    
    public void testGetValidationsServiceProblem() throws Exception {
        RESTMenuService restService = new RESTMenuService();
        MenuService menuService = EasyMock.createMock(MenuService.class);
        MenuServiceException exception = new MenuServiceException("Something going wrong");
        EasyMock.expect(menuService.listValidations()).andThrow(exception).once();
        restService.setMenuService(menuService);
        
        EasyMock.replay(menuService);
        Response resp = restService.getValidations();
        EasyMock.verify(menuService);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
    //test happy path for RESTMenuService.saveMenuItem(...)
    public void testSaveMenuItemOK() throws Exception {
        RESTMenuService restService = new RESTMenuService();
        MenuService menuService = EasyMock.createMock(MenuService.class);
        HttpServletRequest mockRequest = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mockRequest.isUserInRole(EasyMock.anyObject(String.class))).andReturn(true).times(3);
        menuService.saveMenuItem(EasyMock.same("groupName"), EasyMock.anyObject(MenuItemDescription.class));
        EasyMock.expectLastCall().once();
        restService.setMenuService(menuService);
        SaveMenuItemDTO dto = new SaveMenuItemDTO();
        List<String> allowedEvents = new ArrayList<String>();
        allowedEvents.add("onclick");
        allowedEvents.add("onfocus");
        allowedEvents.add("onblur");
        dto.setAllowedEvent(allowedEvents);
        dto.setClone("{}");
        dto.setGroupName("groupName");
        dto.setName("myItem");
        
        EasyMock.replay(mockRequest, menuService);
        Response resp = restService.saveMenuItem(dto, mockRequest);
        EasyMock.verify(mockRequest, menuService);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.CREATED);
    }
    
    //test response to a MenuServiceException for RESTMenuService.saveMenuItem(...)
    public void testSaveMenuItemServiceProblem() throws Exception {
        RESTMenuService restService = new RESTMenuService();
        MenuService menuService = EasyMock.createMock(MenuService.class);
        HttpServletRequest mockRequest = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mockRequest.isUserInRole(EasyMock.anyObject(String.class))).andReturn(true).times(3);
        menuService.saveMenuItem(EasyMock.same("groupName"), EasyMock.anyObject(MenuItemDescription.class));
        MenuServiceException exception = new MenuServiceException("Something went wrong");
        EasyMock.expectLastCall().andThrow(exception).once();
        restService.setMenuService(menuService);
        SaveMenuItemDTO dto = new SaveMenuItemDTO();
        List<String> allowedEvents = new ArrayList<String>();
        allowedEvents.add("onclick");
        allowedEvents.add("onfocus");
        allowedEvents.add("onblur");
        dto.setAllowedEvent(allowedEvents);
        dto.setClone("{}");
        dto.setGroupName("groupName");
        dto.setName("myItem");
        
        EasyMock.replay(mockRequest, menuService);
        Response resp = restService.saveMenuItem(dto, mockRequest);
        EasyMock.verify(mockRequest, menuService);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.CONFLICT);
    }
    
    //test what happens when a functionalanalyst tries to save a menu item
    public void testSaveMenuItemPermissionProblem() throws Exception {
    	RESTMenuService restService = new RESTMenuService();
        HttpServletRequest mockRequest = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mockRequest.isUserInRole(EasyMock.anyObject(String.class))).andReturn(false).times(3);
        SaveMenuItemDTO dto = new SaveMenuItemDTO();
        List<String> allowedEvents = new ArrayList<String>();
        allowedEvents.add("onclick");
        allowedEvents.add("onfocus");
        allowedEvents.add("onblur");
        dto.setAllowedEvent(allowedEvents);
        dto.setClone("{}");
        dto.setGroupName("groupName");
        dto.setName("myItem");
        
        EasyMock.replay(mockRequest);
        Response resp = restService.saveMenuItem(dto, mockRequest);
        EasyMock.verify(mockRequest);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.UNAUTHORIZED);
    }
    
    //test happy path for RESTMenuService.deleteMenuItem(...)
    public void testDeleteMenuItemOK() throws Exception {
        RESTMenuService restService = new RESTMenuService();
        MenuService menuService = EasyMock.createMock(MenuService.class);
        HttpServletRequest mockRequest = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mockRequest.isUserInRole(EasyMock.anyObject(String.class))).andReturn(true).times(3);
        menuService.deleteMenuItem(EasyMock.same("groupName"), EasyMock.anyObject(MenuItemDescription.class));
        EasyMock.expectLastCall().once();
        Map<String, List<MenuItemDescription>> initialMenuItems = new HashMap<String, List<MenuItemDescription>>();
        List<MenuItemDescription> descriptions = new ArrayList<MenuItemDescription>();
        MenuItemDescription description = new MenuItemDescription();
        description.setName("myItem");
        descriptions.add(description);
        initialMenuItems.put("groupName", descriptions);
        EasyMock.expect(menuService.listMenuItems()).andReturn(initialMenuItems).once();
        restService.setMenuService(menuService);
        SaveMenuItemDTO dto = new SaveMenuItemDTO();
        List<String> allowedEvents = new ArrayList<String>();
        allowedEvents.add("onclick");
        allowedEvents.add("onfocus");
        allowedEvents.add("onblur");
        dto.setAllowedEvent(allowedEvents);
        dto.setClone("{}");
        dto.setGroupName("groupName");
        dto.setName("myItem");
        
        EasyMock.replay(mockRequest, menuService);
        Response resp = restService.deleteMenuItem(dto, mockRequest);
        EasyMock.verify(mockRequest, menuService);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.ACCEPTED);
    }
    
    //test response for not finding the specified item at RESTMenuService.deleteMenuItem(...)
    public void testDeleteMenuItemEmptyGroup() throws Exception {
        RESTMenuService restService = new RESTMenuService();
        MenuService menuService = EasyMock.createMock(MenuService.class);
        HttpServletRequest mockRequest = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mockRequest.isUserInRole(EasyMock.anyObject(String.class))).andReturn(true).times(3);
        EasyMock.expect(menuService.listMenuItems()).andReturn(new HashMap<String, List<MenuItemDescription>>()).once();
        restService.setMenuService(menuService);
        SaveMenuItemDTO dto = new SaveMenuItemDTO();
        List<String> allowedEvents = new ArrayList<String>();
        allowedEvents.add("onclick");
        allowedEvents.add("onfocus");
        allowedEvents.add("onblur");
        dto.setAllowedEvent(allowedEvents);
        dto.setClone("{}");
        dto.setGroupName("groupName");
        dto.setName("myItem");
        
        EasyMock.replay(mockRequest, menuService);
        Response resp = restService.deleteMenuItem(dto, mockRequest);
        EasyMock.verify(mockRequest, menuService);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.NO_CONTENT);
    }
    
    //test response for not finding the specified item at RESTMenuService.deleteMenuItem(...)
    public void testDeleteMenuItemNotFound() throws Exception {
        RESTMenuService restService = new RESTMenuService();
        MenuService menuService = EasyMock.createMock(MenuService.class);
        HttpServletRequest mockRequest = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mockRequest.isUserInRole(EasyMock.anyObject(String.class))).andReturn(true).times(3);
        Map<String, List<MenuItemDescription>> initialMenuItems = new HashMap<String, List<MenuItemDescription>>();
        List<MenuItemDescription> descriptions = new ArrayList<MenuItemDescription>();
        MenuItemDescription description = new MenuItemDescription();
        description.setName("anotherItem");
        descriptions.add(description);
        initialMenuItems.put("groupName", descriptions);
        EasyMock.expect(menuService.listMenuItems()).andReturn(initialMenuItems).once();
        restService.setMenuService(menuService);
        SaveMenuItemDTO dto = new SaveMenuItemDTO();
        List<String> allowedEvents = new ArrayList<String>();
        allowedEvents.add("onclick");
        allowedEvents.add("onfocus");
        allowedEvents.add("onblur");
        dto.setAllowedEvent(allowedEvents);
        dto.setClone("{}");
        dto.setGroupName("groupName");
        dto.setName("myItem");
        
        EasyMock.replay(mockRequest, menuService);
        Response resp = restService.deleteMenuItem(dto, mockRequest);
        EasyMock.verify(mockRequest, menuService);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.CONFLICT);
    }
    
    //test response to a MenuServiceException at RESTMenuService.deleteMenuItem(...)
    public void testDeleteMenuItemServiceProblem() throws Exception {
        MenuServiceException exception = new MenuServiceException("Something going wrong deleting an item");
        RESTMenuService restService = new RESTMenuService();
        MenuService menuService = EasyMock.createMock(MenuService.class);
        HttpServletRequest mockRequest = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mockRequest.isUserInRole(EasyMock.anyObject(String.class))).andReturn(true).times(3);
        menuService.deleteMenuItem(EasyMock.same("groupName"), EasyMock.anyObject(MenuItemDescription.class));
        EasyMock.expectLastCall().andThrow(exception).once();
        Map<String, List<MenuItemDescription>> initialMenuItems = new HashMap<String, List<MenuItemDescription>>();
        List<MenuItemDescription> descriptions = new ArrayList<MenuItemDescription>();
        MenuItemDescription description = new MenuItemDescription();
        description.setName("myItem");
        descriptions.add(description);
        initialMenuItems.put("groupName", descriptions);
        EasyMock.expect(menuService.listMenuItems()).andReturn(initialMenuItems).once();
        restService.setMenuService(menuService);
        SaveMenuItemDTO dto = new SaveMenuItemDTO();
        List<String> allowedEvents = new ArrayList<String>();
        allowedEvents.add("onclick");
        allowedEvents.add("onfocus");
        allowedEvents.add("onblur");
        dto.setAllowedEvent(allowedEvents);
        dto.setClone("{}");
        dto.setGroupName("groupName");
        dto.setName("myItem");
        
        EasyMock.replay(mockRequest, menuService);
        Response resp = restService.deleteMenuItem(dto, mockRequest);
        EasyMock.verify(mockRequest, menuService);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
    
  //test what happens when a functionalanalyst tries to delete a menu item
    public void testDeleteMenuItemUnauthorized() throws Exception {
        RESTMenuService restService = new RESTMenuService();
        HttpServletRequest mockRequest = EasyMock.createMock(HttpServletRequest.class);
        EasyMock.expect(mockRequest.isUserInRole(EasyMock.anyObject(String.class))).andReturn(false).times(3);
        Map<String, List<MenuItemDescription>> initialMenuItems = new HashMap<String, List<MenuItemDescription>>();
        List<MenuItemDescription> descriptions = new ArrayList<MenuItemDescription>();
        MenuItemDescription description = new MenuItemDescription();
        description.setName("myItem");
        descriptions.add(description);
        initialMenuItems.put("groupName", descriptions);
        SaveMenuItemDTO dto = new SaveMenuItemDTO();
        List<String> allowedEvents = new ArrayList<String>();
        allowedEvents.add("onclick");
        allowedEvents.add("onfocus");
        allowedEvents.add("onblur");
        dto.setAllowedEvent(allowedEvents);
        dto.setClone("{}");
        dto.setGroupName("groupName");
        dto.setName("myItem");
        
        EasyMock.replay(mockRequest);
        Response resp = restService.deleteMenuItem(dto, mockRequest);
        EasyMock.verify(mockRequest);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.UNAUTHORIZED);
    }
    
    //test happy path for RESTMenuService.getRepresentationMappings()
    public void testGetRepresentationMappingsOK() throws Exception {
        RESTMenuService restService = new RESTMenuService();
        MenuService menuService = EasyMock.createMock(MenuService.class);
        restService.setMenuService(menuService);
        Map<String, String> retval = new HashMap<String, String>();
        retval.put("aaa", "bbb");
        retval.put("ccc", "ddd");
        EasyMock.expect(menuService.getFormBuilderProperties()).andReturn(retval).once();
        
        EasyMock.replay(menuService);
        Response resp = restService.getRepresentationMappings();
        EasyMock.verify(menuService);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.OK);
        assertNotNull("resp.entity shouldn't be null", resp.getEntity());
        Object entity = resp.getEntity();
        assertNotNull("resp.metadata shouldn't be null", resp.getMetadata());
        Object contentType = resp.getMetadata().getFirst(HttpHeaderNames.CONTENT_TYPE);
        assertNotNull("resp.entity shouldn't be null", contentType);
        assertEquals("contentType should be application/xml but is" + contentType, contentType, MediaType.APPLICATION_XML);
        assertTrue("entity should be of type PropertiesDTO", entity instanceof PropertiesDTO);
        PropertiesDTO dto = (PropertiesDTO) entity;
        List<PropertiesItemDTO> properties = dto.getProperty();
        assertNotNull("properties shouldn't be null", properties);
        assertFalse("properties shouldn't be empty", properties.isEmpty());
        assertEquals("properties should have " + retval.size() + " elements but has " + properties.size(), properties.size(), retval.size());
    }
    
    //test response to a MenuServiceException for RESTMenuService.getRepresentationMappings()
    public void testGetRepresentationMappingsServiceProblem() throws Exception {
        RESTMenuService restService = new RESTMenuService();
        MenuService menuService = EasyMock.createMock(MenuService.class);
        restService.setMenuService(menuService);
        MenuServiceException exception = new MenuServiceException("Something going wrong");
        EasyMock.expect(menuService.getFormBuilderProperties()).andThrow(exception).once();
        
        EasyMock.replay(menuService);
        Response resp = restService.getRepresentationMappings();
        EasyMock.verify(menuService);
        
        assertNotNull("resp shouldn't be null", resp);
        assertStatus(resp.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }
}
