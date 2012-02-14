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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jbpm.formapi.server.form.FormEncodingServerFactory;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationDecoder;
import org.jbpm.formapi.shared.menu.FormEffectDescription;
import org.jbpm.formapi.shared.menu.MenuItemDescription;
import org.jbpm.formapi.shared.menu.MenuOptionDescription;
import org.jbpm.formapi.shared.menu.ValidationDescription;
import org.jbpm.formbuilder.client.menu.items.CustomMenuItem;
import org.jbpm.formbuilder.server.xml.FormEffectDTO;
import org.jbpm.formbuilder.server.xml.ListMenuItemsDTO;
import org.jbpm.formbuilder.server.xml.ListOptionsDTO;
import org.jbpm.formbuilder.server.xml.ListValidationsDTO;
import org.jbpm.formbuilder.server.xml.PropertiesDTO;
import org.jbpm.formbuilder.server.xml.SaveMenuItemDTO;
import org.jbpm.formbuilder.shared.menu.MenuService;
import org.jbpm.formbuilder.shared.menu.MenuServiceException;

@Path("/menu")
public class RESTMenuService extends RESTBaseService {

    private MenuService menuService;
    
    public RESTMenuService() {
        FormEncodingFactory.register(FormEncodingServerFactory.getEncoder(), FormEncodingServerFactory.getDecoder());
    }
    
    @GET @Path("/items") 
    public Response listMenuItems() {
        init();
        try {
            Map<String, List<MenuItemDescription>> items = menuService.listMenuItems();
            ListMenuItemsDTO dto = new ListMenuItemsDTO(items);
            return Response.ok(dto, MediaType.APPLICATION_XML).build();
        } catch (MenuServiceException e) {
            return error("Problem reading menu items", e);
        }
    }

    @GET @Path("/options")
    public Response listMenuOptions() {
        init();
        try {
            List<MenuOptionDescription> options = menuService.listOptions();
            ListOptionsDTO dto = new ListOptionsDTO(options);
            return Response.ok(dto, MediaType.APPLICATION_XML).build();
        } catch (MenuServiceException e) {
            return error("Problem reading menu options", e);
        }
    }
    
    @GET @Path("/validations")
    public Response getValidations() {
        init();
        try {
            List<ValidationDescription> validations = menuService.listValidations();
            ListValidationsDTO dto = new ListValidationsDTO(validations);
            return Response.ok(dto, MediaType.APPLICATION_XML).build();
        } catch (MenuServiceException e) {
            return error("Problem reading validations", e);
        }
    }
    
    @POST @Path("/items")
    public Response saveMenuItem(SaveMenuItemDTO dto, @Context HttpServletRequest request) {
        init();
        try {
        	if (RESTUserService.hasDesignerPrivileges(request)) {
        		MenuItemDescription menuItem = toMenuItemDescription(dto, true);
        		menuService.saveMenuItem(dto.getGroupName(), menuItem);
        		return Response.status(Status.CREATED).build();
        	} else {
        		return Response.status(Status.UNAUTHORIZED).build();
        	}
        } catch (MenuServiceException e) {
            return Response.status(Status.CONFLICT).build();
        }
    }

    private MenuItemDescription toMenuItemDescription(SaveMenuItemDTO dto, boolean strict) throws MenuServiceException {
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        String json = dto.getClone();
        MenuItemDescription menuItem = new MenuItemDescription();
        try {
            FormItemRepresentation item = decoder.decodeItem(json);
            menuItem.setItemRepresentation(item);
        } catch (FormEncodingException e) {
            if (strict) {
                throw new MenuServiceException("Couldn't load formRepresentation from dto", e);
            }
            menuItem.setItemRepresentation(null);
        }
        menuItem.setClassName(CustomMenuItem.class.getName());
        menuItem.setName(dto.getName());
        List<FormEffectDescription> effects = new ArrayList<FormEffectDescription>();
        if (dto.getEffect() != null) {
            for (FormEffectDTO effectDto : dto.getEffect()) {
                FormEffectDescription effect = new FormEffectDescription();
                effect.setClassName(effectDto.getClassName());
                effects.add(effect);
            }
        }
        menuItem.setEffects(effects);
        List<String> allowedEvents = new ArrayList<String>();
        if (dto.getAllowedEvent() != null) {
            for (String evtName : dto.getAllowedEvent()) {
                allowedEvents.add(evtName);
            }
        }
        menuItem.setAllowedEvents(allowedEvents);
        return menuItem;
    }
    
    @DELETE @Path("/items")
    public Response deleteMenuItem(SaveMenuItemDTO dto, @Context HttpServletRequest request) {
        init();
        try {
        	if (RESTUserService.hasDesignerPrivileges(request)) {
        		MenuItemDescription menuItem = toMenuItemDescription(dto, false);
        		Map<String, List<MenuItemDescription>> items = menuService.listMenuItems();
        		List<MenuItemDescription> group = items.get(dto.getGroupName());
        		if (group == null || group.isEmpty()) {
        			return Response.noContent().build();
        		}
        		boolean found = false;
        		for (MenuItemDescription desc : group) {
        			if (desc.getName().equals(dto.getName())) {
        				found = true;
        				break;
        			}
        		}
        		if (!found) {
        			return Response.status(Status.CONFLICT).build();
        		}
        		menuService.deleteMenuItem(dto.getGroupName(), menuItem);
        		return Response.status(Status.ACCEPTED).build();
        	} else {
        		return Response.status(Status.UNAUTHORIZED).build();
        	}
        } catch (MenuServiceException e) {
            return error("Couldn't delete menu item " + dto.getGroupName() + ":" + dto.getName(), e);
        }
    }

    @GET @Path("/mappings")
    public Response getRepresentationMappings() {
        init();
        try {
            Map<String, String> props = menuService.getFormBuilderProperties();
            PropertiesDTO dto = new PropertiesDTO(props);
            return Response.ok(dto, MediaType.APPLICATION_XML).build();
        } catch (MenuServiceException e) {
            return error("Problem reading form builder properties", e);
        }
    }
    
    private void init() {
        if (menuService == null) {
            menuService = ServiceFactory.getInstance().getMenuService();
        }
    }
    
    /**
     * @param menuService the menuService to set (for test cases purpose)
     */
    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }
}
