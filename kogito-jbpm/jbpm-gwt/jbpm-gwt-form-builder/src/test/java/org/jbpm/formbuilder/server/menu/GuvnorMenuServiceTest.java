/**
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
package org.jbpm.formbuilder.server.menu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.jbpm.formapi.server.form.FormEncodingServerFactory;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationDecoder;
import org.jbpm.formapi.shared.form.FormRepresentationEncoder;
import org.jbpm.formapi.shared.menu.FormEffectDescription;
import org.jbpm.formapi.shared.menu.MenuItemDescription;
import org.jbpm.formapi.shared.menu.MenuOptionDescription;
import org.jbpm.formapi.shared.menu.ValidationDescription;
import org.jbpm.formbuilder.client.effect.DoneEffect;
import org.jbpm.formbuilder.client.effect.RemoveEffect;
import org.jbpm.formbuilder.client.menu.items.CustomMenuItem;
import org.jbpm.formbuilder.server.RESTAbstractTest;
import org.jbpm.formbuilder.shared.menu.MenuServiceException;
import org.jbpm.formbuilder.shared.menu.MockMenuService;

public class GuvnorMenuServiceTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        FormEncodingFactory.register(FormEncodingServerFactory.getEncoder(), FormEncodingServerFactory.getDecoder());
    }
    
    public void testListOptionsURIProblem() throws Exception {
        abstractTestListOptionsProblem(URISyntaxException.class);
    }
    
    public void testListOptionsFileNotFoundProblem() throws Exception {
        abstractTestListOptionsProblem(FileNotFoundException.class);
    }
    
    public void testListOptionsIOProblem() throws Exception {
        abstractTestListOptionsProblem(IOException.class);
    }
    
    public void testListOptionsUnknownProblem() throws Exception {
        abstractTestListOptionsProblem(NullPointerException.class);
    }
    
    private void abstractTestListOptionsProblem(final Class<?> exceptionType) throws Exception {
        GuvnorMenuService service = createMockedService(exceptionType);
        try {
            service.listOptions();
            fail("listOptions shouldn't succeed");
        } catch (MenuServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be a " + exceptionType.getName(), cause.getClass().equals(exceptionType));
        }
    }

    private GuvnorMenuService createMockedService(final Class<?> exceptionType) {
        GuvnorMenuService service = new GuvnorMenuService() {
            @Override 
            protected URL asURL(String path) throws URISyntaxException {
                if (exceptionType != null && exceptionType.equals(URISyntaxException.class)) throw new URISyntaxException(path, "mocking");
                return super.asURL(path);
            }
            @Override 
            protected Reader createReader(URL url) throws FileNotFoundException, IOException {
                if (exceptionType != null) {
                    if (exceptionType.equals(FileNotFoundException.class)) throw new FileNotFoundException(url.toExternalForm());
                    if (exceptionType.equals(IOException.class)) throw new IOException(url.toExternalForm());
                    throw new NullPointerException();
                }
                return super.createReader(url);
            }
            @Override
            protected String readURL(URL url) throws FileNotFoundException, IOException {
                if (exceptionType != null) {
                    if (exceptionType.equals(FileNotFoundException.class)) throw new FileNotFoundException(url.toExternalForm());
                    if (exceptionType.equals(IOException.class)) throw new IOException(url.toExternalForm());
                    throw new NullPointerException();
                }
                return super.readURL(url);
            }
            @Override
            protected void writeToURL(URL url, String json) throws FileNotFoundException, IOException {
                if (exceptionType != null) {
                    if (exceptionType.equals(FileNotFoundException.class)) throw new FileNotFoundException(url.toExternalForm());
                    if (exceptionType.equals(IOException.class)) throw new IOException(url.toExternalForm());
                    throw new NullPointerException();
                }
                super.writeToURL(url, json);
            }
        };
        return service;
    }
    
    public void testListOptionsOK() throws Exception {
        GuvnorMenuService service = new GuvnorMenuService();
        List<MenuOptionDescription> options = service.listOptions();
        assertNotNull("options shouldn't be null", options);
        assertFalse("options shouldn't be empty", options.isEmpty());
    }
    
    public void testListItemsURIProblem() throws Exception {
        abstractTestListItemsProblem(URISyntaxException.class);
    }
    
    public void testListItemsFileNotFound() throws Exception {
        abstractTestListItemsProblem(FileNotFoundException.class);
    }
    
    public void testListItemsIOProblem() throws Exception {
        abstractTestListItemsProblem(IOException.class);
    }
    
    public void testListItemsEncodingProblem() throws Exception {
        GuvnorMenuService service = createMockedService(null);
        FormRepresentationDecoder decoder = EasyMock.createMock(FormRepresentationDecoder.class);
        FormEncodingFactory.register(FormEncodingFactory.getEncoder(), decoder);
        FormEncodingException exception = new FormEncodingException("Something going wrong");
        EasyMock.expect(decoder.decodeMenuItemsMap(EasyMock.anyObject(String.class))).andThrow(exception).once();
        
        EasyMock.replay(decoder);
        try {
            service.listMenuItems();
            fail("listOptions shouldn't succeed");
        } catch (MenuServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be a FormEncodingException", cause instanceof FormEncodingException);
        }
        EasyMock.verify(decoder);
    }
    
    public void testListItemsUnknownProblem() throws Exception {
        abstractTestListItemsProblem(NullPointerException.class);
    }
    
    private void abstractTestListItemsProblem(final Class<?> exceptionType) throws Exception {
        GuvnorMenuService service = createMockedService(exceptionType);
        try {
            service.listMenuItems();
            fail("listOptions shouldn't succeed");
        } catch (MenuServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be a " + exceptionType.getName(), cause.getClass().equals(exceptionType));
        }
    }
    
    public void testListItemsOK() throws Exception {
        GuvnorMenuService service = new GuvnorMenuService();
        Map<String, List<MenuItemDescription>> items = service.listMenuItems();
        assertNotNull("items shouldn't be null", items);
        assertFalse("items shouldn't be empty", items.isEmpty());
        for (String key : items.keySet()) {
            assertNotNull("items of key " + key + " shouldn't be null", items.get(key));
            assertFalse("items of key " + key + " shouldn't be empty", items.get(key).isEmpty());
        }
    }

    public void testListValidationsURIProblem() throws Exception {
        abstractTestListValidationsProblem(URISyntaxException.class);
    }
    
    public void testListValidationsFileNotFound() throws Exception {
        abstractTestListValidationsProblem(FileNotFoundException.class);
    }
    
    public void testListValidationsUnknownProblem() throws Exception {
        abstractTestListValidationsProblem(NullPointerException.class);
    }

    private void abstractTestListValidationsProblem(final Class<?> exceptionType) throws Exception {
        GuvnorMenuService service = createMockedService(exceptionType);
        try {
            service.listValidations();
            fail("listOptions shouldn't succeed");
        } catch (MenuServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be a " + exceptionType.getName(), cause.getClass().equals(exceptionType));
        }
    }
    
    public void testListValidationsOK() throws Exception {
        GuvnorMenuService service = new GuvnorMenuService();
        List<ValidationDescription> validations = service.listValidations();
        assertNotNull("validations shouldn't be null", validations);
        assertFalse("validations should'nt be empty", validations.isEmpty());
        for (ValidationDescription desc : validations) {
            assertNotNull("validations shouldn't contain null elements", desc);
            assertNotNull("validation className shouldn't be null", desc.getClassName());
            assertFalse("validation className shouldn't be empty", "".equals(desc.getClassName()));
        }
    }
    
    private void abstractTestSaveMenuItemProblem(final Class<?> exceptionType) throws Exception {
        GuvnorMenuService service = createMockedService(exceptionType);
        MenuItemDescription sampleDescription = new MenuItemDescription();
        sampleDescription.setAllowedEvents(new ArrayList<String>());
        sampleDescription.setEffects(new ArrayList<FormEffectDescription>());
        FormItemRepresentation item = RESTAbstractTest.createMockForm("form", "param1").getFormItems().iterator().next();
        sampleDescription.setItemRepresentation(item);
        sampleDescription.setName("name");
        try {
            service.saveMenuItem("group", sampleDescription);
            fail("saveMenuItem shouldn't succeed");
        } catch (MenuServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be a " + exceptionType.getName(), cause.getClass().equals(exceptionType));
        }
    }
    
    public void testSaveMenuItemURIProblem() throws Exception {
        abstractTestSaveMenuItemProblem(URISyntaxException.class);
    }
    
    public void testSaveMenuItemFileNotFound() throws Exception {
        abstractTestSaveMenuItemProblem(FileNotFoundException.class);
    }
    
    public void testSaveMenuItemIOProblem() throws Exception {
        abstractTestSaveMenuItemProblem(IOException.class);
    }

    public void testSaveMenuItemUnknownProblem() throws Exception {
        abstractTestSaveMenuItemProblem(NullPointerException.class);
    }

    public void testSaveMenuItemEncodingProblem() throws Exception {
        GuvnorMenuService service = createMockedService(null);
        MenuItemDescription sampleDescription = new MenuItemDescription();
        sampleDescription.setAllowedEvents(new ArrayList<String>());
        sampleDescription.setEffects(new ArrayList<FormEffectDescription>());
        FormItemRepresentation item = RESTAbstractTest.createMockForm("form", "param1").getFormItems().iterator().next();
        sampleDescription.setItemRepresentation(item);
        sampleDescription.setName("name");
        FormRepresentationEncoder encoder = EasyMock.createMock(FormRepresentationEncoder.class);
        FormEncodingException exception = new FormEncodingException();
        @SuppressWarnings("unchecked")
        Map<String, List<MenuItemDescription>> anyObject = EasyMock.anyObject(Map.class);
        EasyMock.expect(encoder.encodeMenuItemsMap(anyObject)).andThrow(exception).once();
        FormEncodingFactory.register(encoder, FormEncodingFactory.getDecoder());
        
        EasyMock.replay(encoder);
        try {
            service.saveMenuItem("group", sampleDescription);
            fail("saveMenuItem shouldn't succeed");
        } catch (MenuServiceException e) {
            assertNotNull("e shouldn't be null", e);
            Throwable cause = e.getCause();
            assertNotNull("cause shouldn't be null", cause);
            assertTrue("cause should be a FormEncodingException", cause instanceof FormEncodingException);
        }
        EasyMock.verify(encoder);
    }
    
    public void testSaveMenuItemOK() throws Exception {
        GuvnorMenuService service = new GuvnorMenuService();
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        File dbFile = new File(getClass().getResource("/menuItems.json").getFile());
        String jsonInitial = FileUtils.readFileToString(dbFile);
        Map<String, List<MenuItemDescription>> descsInitial = decoder.decodeMenuItemsMap(jsonInitial);
        MenuItemDescription desc = new MenuItemDescription();
        desc.setClassName(CustomMenuItem.class.getName());
        List<FormEffectDescription> effects = new ArrayList<FormEffectDescription>();
        FormEffectDescription effDesc1 = new FormEffectDescription();
        effDesc1.setClassName(RemoveEffect.class.getName());
        effects.add(effDesc1);
        FormEffectDescription effDesc2 = new FormEffectDescription();
        effDesc2.setClassName(DoneEffect.class.getName());
        effects.add(effDesc2);
        desc.setEffects(effects);
        File file = new File(getClass().getResource("testSaveMenuItem.json").getFile());
        String json = FileUtils.readFileToString(file);
        FormItemRepresentation itemRepresentation = decoder.decodeItem(json);
        desc.setName("test component");
        desc.setItemRepresentation(itemRepresentation);
        
        String groupName = "Test Components";
        service.saveMenuItem(groupName, desc);
        
        String jsonResult = FileUtils.readFileToString(dbFile);
        
        Map<String, List<MenuItemDescription>> descsResult = decoder.decodeMenuItemsMap(jsonResult);
        assertNotNull("saved menu items shouldn't be null", descsResult);
        assertNotNull("saved menu items should contain a list of " + groupName, descsResult.get(groupName));
        assertFalse(groupName + " list should not be empty", descsResult.get(groupName).isEmpty());
        assertFalse("descsInitial and descsResult should not be the same", descsInitial.equals(descsResult));
        
        service.deleteMenuItem(groupName, desc);
        
        String jsonFinal = FileUtils.readFileToString(dbFile);
        Map<String, List<MenuItemDescription>> descsFinal = decoder.decodeMenuItemsMap(jsonFinal);
        
        assertEquals("descsInitial and descsFinal should be the same", descsInitial.entrySet(), descsFinal.entrySet());
    }
    
    public void testGetFormBuilderProperties() throws Exception {
        GuvnorMenuService service = new GuvnorMenuService();
        Map<String, String> props = service.getFormBuilderProperties();
        assertNotNull("props shouldn't be null", props);
        assertFalse("props shouldn't be empty", props.isEmpty());
    }
    
    public void testMockMenuService() throws Exception {
        MockMenuService service = new MockMenuService();
        
        Map<String, String> formBuilderProperties = service.getFormBuilderProperties();
        assertNotNull("formBuilderProperties shouldn't be null", formBuilderProperties);
        assertFalse("formBuilderProperties shouldn't be empty", formBuilderProperties.isEmpty());
        
        Map<String, List<MenuItemDescription>> menuItems = service.listMenuItems();
        assertNotNull("menuItems shouldn't be null", menuItems);
        assertFalse("menuItems shouldn't be empty", menuItems.isEmpty());
        
        List<MenuOptionDescription> options = service.listOptions();
        assertNotNull("options shouldn't be null", options);
        assertFalse("options shouldn't be empty", options.isEmpty());
        
        List<ValidationDescription> validations = service.listValidations();
        assertNotNull("validations shouldn't be null", validations);
        assertFalse("validations shouldn't be empty", validations.isEmpty());
    }
}
