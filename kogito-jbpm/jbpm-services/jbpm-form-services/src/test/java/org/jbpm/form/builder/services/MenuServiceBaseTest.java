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
package org.jbpm.form.builder.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.jbpm.form.builder.services.api.MenuServiceException;
import org.jbpm.form.builder.services.encoders.FormEncodingServerFactory;
import org.jbpm.form.builder.services.impl.fs.FSMenuService;
import org.jbpm.form.builder.services.model.FormItemRepresentation;
import org.jbpm.form.builder.services.model.forms.FormEncodingException;
import org.jbpm.form.builder.services.model.forms.FormEncodingFactory;
import org.jbpm.form.builder.services.model.forms.FormRepresentationDecoder;
import org.jbpm.form.builder.services.model.forms.FormRepresentationEncoder;
import org.jbpm.form.builder.services.model.menu.FormEffectDescription;
import org.jbpm.form.builder.services.model.menu.MenuItemDescription;
import org.jbpm.form.builder.services.model.menu.MenuOptionDescription;
import org.jbpm.form.builder.services.model.menu.ValidationDescription;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Before;
import org.junit.Test;

public abstract class MenuServiceBaseTest extends AbstractBaseTest {

    @Before
    public void setUp() throws Exception {
        FormEncodingFactory.register(FormEncodingServerFactory.getEncoder(), FormEncodingServerFactory.getDecoder());
    }
    
    @Test
    public void testListOptionsURIProblem() throws Exception {
        abstractTestListOptionsProblem(URISyntaxException.class);
    }
    @Test
    public void testListOptionsFileNotFoundProblem() throws Exception {
        abstractTestListOptionsProblem(FileNotFoundException.class);
    }
    @Test
    public void testListOptionsIOProblem() throws Exception {
        abstractTestListOptionsProblem(IOException.class);
    }
    @Test
    public void testListOptionsUnknownProblem() throws Exception {
        abstractTestListOptionsProblem(NullPointerException.class);
    }
    
    private void abstractTestListOptionsProblem(final Class<?> exceptionType) throws Exception {
        FSMenuService service = createMockedService(exceptionType);
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

    private FSMenuService createMockedService(final Class<?> exceptionType) {
        FSMenuService service = new FSMenuService() {
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
    @Test
    public void testListOptionsOK() throws Exception {
        FSMenuService service = new FSMenuService();
        List<MenuOptionDescription> options = service.listOptions();
        assertNotNull("options shouldn't be null", options);
        assertFalse("options shouldn't be empty", options.isEmpty());
    }
    @Test
    public void testListItemsURIProblem() throws Exception {
        abstractTestListItemsProblem(URISyntaxException.class);
    }
    @Test
    public void testListItemsFileNotFound() throws Exception {
        abstractTestListItemsProblem(FileNotFoundException.class);
    }
    @Test
    public void testListItemsIOProblem() throws Exception {
        abstractTestListItemsProblem(IOException.class);
    }
    @Test
    public void testListItemsEncodingProblem() throws Exception {
        FSMenuService service = createMockedService(null);
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
    @Test
    public void testListItemsUnknownProblem() throws Exception {
        abstractTestListItemsProblem(NullPointerException.class);
    }
    
    private void abstractTestListItemsProblem(final Class<?> exceptionType) throws Exception {
        FSMenuService service = createMockedService(exceptionType);
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
    @Test
    public void testListItemsOK() throws Exception {
        FSMenuService service = new FSMenuService();
        Map<String, List<MenuItemDescription>> items = service.listMenuItems();
        assertNotNull("items shouldn't be null", items);
        assertFalse("items shouldn't be empty", items.isEmpty());
        for (String key : items.keySet()) {
            assertNotNull("items of key " + key + " shouldn't be null", items.get(key));
            assertFalse("items of key " + key + " shouldn't be empty", items.get(key).isEmpty());
        }
    }
    @Test
    public void testListValidationsURIProblem() throws Exception {
        abstractTestListValidationsProblem(URISyntaxException.class);
    }
    @Test
    public void testListValidationsFileNotFound() throws Exception {
        abstractTestListValidationsProblem(FileNotFoundException.class);
    }
    @Test
    public void testListValidationsUnknownProblem() throws Exception {
        abstractTestListValidationsProblem(NullPointerException.class);
    }

    private void abstractTestListValidationsProblem(final Class<?> exceptionType) throws Exception {
        FSMenuService service = createMockedService(exceptionType);
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
    @Test
    public void testListValidationsOK() throws Exception {
        FSMenuService service = new FSMenuService();
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
        FSMenuService service = createMockedService(exceptionType);
        MenuItemDescription sampleDescription = new MenuItemDescription();
        sampleDescription.setAllowedEvents(new ArrayList<String>());
        sampleDescription.setEffects(new ArrayList<FormEffectDescription>());
        FormItemRepresentation item = MockFormHelper.createMockForm("form", "param1").getFormItems().iterator().next();
        sampleDescription.setItemRepresentationMap(item.getDataMap());
        sampleDescription.setIconUrl("https://www.google.com/images/srpr/logo3w.png");
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
    @Test
    public void testSaveMenuItemURIProblem() throws Exception {
        abstractTestSaveMenuItemProblem(URISyntaxException.class);
    }
    @Test
    public void testSaveMenuItemFileNotFound() throws Exception {
        abstractTestSaveMenuItemProblem(FileNotFoundException.class);
    }
    @Test
    public void testSaveMenuItemIOProblem() throws Exception {
        abstractTestSaveMenuItemProblem(IOException.class);
    }
    @Test
    public void testSaveMenuItemUnknownProblem() throws Exception {
        abstractTestSaveMenuItemProblem(NullPointerException.class);
    }
    @Test
    public void testSaveMenuItemEncodingProblem() throws Exception {
        FSMenuService service = createMockedService(null);
        MenuItemDescription sampleDescription = new MenuItemDescription();
        sampleDescription.setAllowedEvents(new ArrayList<String>());
        sampleDescription.setEffects(new ArrayList<FormEffectDescription>());
        FormItemRepresentation item = MockFormHelper.createMockForm("form", "param1").getFormItems().iterator().next();
        sampleDescription.setItemRepresentationMap(item.getDataMap());
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
// THIS TEST REQUIRES THE REAL GWT Components so it should be executed in the showcase    
//    public void testSaveMenuItemOK() throws Exception {
//        FSMenuService service = new FSMenuService();
//        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
//        File dbFile = new File(getClass().getResource("/menuItems.json").getFile());
//        String jsonInitial = FileUtils.readFileToString(dbFile);
//        Map<String, List<MenuItemDescription>> descsInitial = decoder.decodeMenuItemsMap(jsonInitial);
//        MenuItemDescription desc = new MenuItemDescription();
//        desc.setClassName(CustomMenuItem.class.getName());
//        List<FormEffectDescription> effects = new ArrayList<FormEffectDescription>();
//        FormEffectDescription effDesc1 = new FormEffectDescription();
//        effDesc1.setClassName(RemoveEffect.class.getName());
//        effects.add(effDesc1);
//        FormEffectDescription effDesc2 = new FormEffectDescription();
//        effDesc2.setClassName(DoneEffect.class.getName());
//        effects.add(effDesc2);
//        desc.setEffects(effects);
//        File file = new File(getClass().getResource("testSaveMenuItem.json").getFile());
//        String json = FileUtils.readFileToString(file);
//        FormItemRepresentation itemRepresentation = decoder.decodeItem(json);
//        desc.setName("test component");
//        desc.setItemRepresentation(itemRepresentation);
//        
//        String groupName = "Test Components";
//        service.saveMenuItem(groupName, desc);
//        
//        String jsonResult = FileUtils.readFileToString(dbFile);
//        
//        Map<String, List<MenuItemDescription>> descsResult = decoder.decodeMenuItemsMap(jsonResult);
//        assertNotNull("saved menu items shouldn't be null", descsResult);
//        assertNotNull("saved menu items should contain a list of " + groupName, descsResult.get(groupName));
//        assertFalse(groupName + " list should not be empty", descsResult.get(groupName).isEmpty());
//        assertFalse("descsInitial and descsResult should not be the same", descsInitial.equals(descsResult));
//        
//        service.deleteMenuItem(groupName, desc);
//        
//        String jsonFinal = FileUtils.readFileToString(dbFile);
//        Map<String, List<MenuItemDescription>> descsFinal = decoder.decodeMenuItemsMap(jsonFinal);
//        
//        assertEquals("descsInitial and descsFinal should be the same", descsInitial.entrySet(), descsFinal.entrySet());
//    }
    @Test
    public void testGetFormBuilderProperties() throws Exception {
        FSMenuService service = new FSMenuService();
        Map<String, String> props = service.getFormBuilderProperties();
        assertNotNull("props shouldn't be null", props);
        assertFalse("props shouldn't be empty", props.isEmpty());
    }
    
    
}
