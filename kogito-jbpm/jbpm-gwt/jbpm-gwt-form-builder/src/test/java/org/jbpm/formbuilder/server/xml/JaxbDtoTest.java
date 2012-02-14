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
package org.jbpm.formbuilder.server.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.jbpm.formapi.server.form.FormEncodingServerFactory;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.menu.FormEffectDescription;
import org.jbpm.formapi.shared.menu.MenuItemDescription;
import org.jbpm.formapi.shared.menu.MenuOptionDescription;
import org.jbpm.formapi.shared.menu.ValidationDescription;
import org.jbpm.formbuilder.server.GuvnorHelper;
import org.jbpm.formbuilder.server.RESTAbstractTest;
import org.jbpm.formbuilder.shared.task.TaskRef;

/**
 * test jaxb dto objects to see if they work properly
 */
public class JaxbDtoTest extends TestCase {

    private GuvnorHelper helper = new GuvnorHelper("http://www.redhat.com", "", "");
    
    public void testFileListDTOEmpty() throws Exception {
        FileListDTO dto = new FileListDTO();
        jaxbSimulation(dto, FileListDTO.class, FileListDTO.RELATED_CLASSES);
        
        FileListDTO dto2 = new FileListDTO(null);
        jaxbSimulation(dto2, FileListDTO.class, FileListDTO.RELATED_CLASSES);
        
        FileListDTO dto3 = new FileListDTO(new ArrayList<String>());
        jaxbSimulation(dto3, FileListDTO.class, FileListDTO.RELATED_CLASSES);
    }
    
    public void testPropertiesDTOEmpty() throws Exception {
        PropertiesDTO dto = new PropertiesDTO(new HashMap<String, String>());
        jaxbSimulation(dto, PropertiesDTO.class, PropertiesDTO.RELATED_CLASSES);
    }

    public void testPropertiesDTOOneItem() throws Exception {
        HashMap<String, String> props = new HashMap<String, String>();
        props.put("oneItemKey", "oneItemValue");
        PropertiesDTO dto = new PropertiesDTO(props);
        jaxbSimulation(dto, PropertiesDTO.class, PropertiesDTO.RELATED_CLASSES);
    }
    
    public void testPropertiesDTOManyItems() throws Exception {
        HashMap<String, String> props = new HashMap<String, String>();
        props.put("oneItemKey", "oneItemValue");
        props.put("anotherItemKey", "anotherItemValue");
        props.put("andYet", "oneMore");
        PropertiesDTO dto = new PropertiesDTO(props);
        jaxbSimulation(dto, PropertiesDTO.class, PropertiesDTO.RELATED_CLASSES);
    }
    
    public void testListValidationsDTOEmpty() throws Exception {
        ListValidationsDTO dto = new ListValidationsDTO();
        jaxbSimulation(dto, ListValidationsDTO.class, ListValidationsDTO.RELATED_CLASSES);
    }
    
    public void testListValidationsDTOManyItems() throws Exception {
        List<ValidationDescription> validations = new ArrayList<ValidationDescription>();
        ValidationDescription validation1 = new ValidationDescription();
        validation1.setClassName("org.jbpm.formbuilder.ThisClassDoesntExist");
        validation1.setProperties(new HashMap<String, String>());
        validations.add(validation1);
        ValidationDescription validation2 = new ValidationDescription();
        validation2.setClassName("org.jbpm.formbuilder.client.validation.NotEmptyValidationItem");
        Map<String, String> notEmptyProps = new HashMap<String, String>();
        notEmptyProps.put("message", "aaa");
        notEmptyProps.put("message2", "bbb");
        validation2.setProperties(notEmptyProps);
        validations.add(validation2);
        ValidationDescription validation3 = new ValidationDescription();
        validation3.setClassName("org.jbpm.formbuilder.client.validation.SomethingValidationItem");
        Map<String, String> somethingProps = new HashMap<String, String>();
        somethingProps.put("message", "aaa");
        validation3.setProperties(somethingProps);
        validations.add(validation3);
        ListValidationsDTO dto = new ListValidationsDTO(validations);
        jaxbSimulation(dto, ListValidationsDTO.class, ListValidationsDTO.RELATED_CLASSES);
    }
    
    public void testListTasksDTOEmpty() throws Exception {
        ListTasksDTO dto = new ListTasksDTO();
        jaxbSimulation(dto, ListTasksDTO.class, ListTasksDTO.RELATED_CLASSES);
    }
    
    public void testListTasksDTOElementsWithNoContent() throws Exception {
        List<TaskRef> tasks = new ArrayList<TaskRef>();
        TaskRef task1 = new TaskRef();
        tasks.add(task1);
        TaskRef task2 = new TaskRef();
        tasks.add(task2);
        ListTasksDTO dto = new ListTasksDTO(tasks);
        jaxbSimulation(dto, ListTasksDTO.class, ListTasksDTO.RELATED_CLASSES);
    }
    
    public void testListTasksDTOElementsWithContent() throws Exception {
        List<TaskRef> tasks = new ArrayList<TaskRef>();
        TaskRef task1 = new TaskRef();
        task1.setPackageName("myPackage");
        task1.setProcessId("myProcessId");
        task1.setProcessName("myProcessName");
        task1.setTaskId("myTaskId");
        task1.addInput("myInput1", "myValue1");
        task1.addInput("myInput2", "myValue2");
        task1.addOutput("myOutput1", "myValue3");
        task1.addOutput("myOutput2", "myValue4");
        Map<String, String> metaData1 = new HashMap<String, String>();
        metaData1.put("myMetaDataKey1", "myMetaDataValue1");
        metaData1.put("myMetaDataKey2", "myMetaDataValue2");
        task1.setMetaData(metaData1);
        tasks.add(task1);
        TaskRef task2 = new TaskRef();
        task2.setPackageName("yourPackage");
        task2.setProcessId("yourProcessId");
        task2.setProcessName("yourProcessName");
        task2.setTaskId("yourTaskId");
        task2.addInput("yourInput1", "yourValue1");
        task2.addInput("yourInput2", "yourValue2");
        task2.addOutput("yourOutput1", "yourValue3");
        task2.addOutput("yourOutput2", "yourValue4");
        Map<String, String> metaData2 = new HashMap<String, String>();
        metaData2.put("yourMetaDataKey1", "yourMetaDataValue1");
        metaData2.put("yourMetaDataKey2", "yourMetaDataValue2");
        task2.setMetaData(metaData2);
        tasks.add(task2);
        ListTasksDTO dto = new ListTasksDTO(tasks);
        jaxbSimulation(dto, ListTasksDTO.class, ListTasksDTO.RELATED_CLASSES);
    }
    
    public void testListFormsDTOEmpty() throws Exception {
        ListFormsDTO dto = new ListFormsDTO();
        jaxbSimulation(dto, ListFormsDTO.class, ListFormsDTO.RELATED_CLASSES);
        ListFormsDTO dto2 = new ListFormsDTO(new ArrayList<FormRepresentation>());
        jaxbSimulation(dto2, ListFormsDTO.class, ListFormsDTO.RELATED_CLASSES);
    }

    public void testListFormsOneItem() throws Exception {
        FormEncodingFactory.register(FormEncodingServerFactory.getEncoder(), FormEncodingServerFactory.getDecoder()); // this is important
        FormRepresentation form = RESTAbstractTest.createMockForm("myForm", "param1", "param2", "param3");
        ListFormsDTO dto = new ListFormsDTO(form);
        jaxbSimulation(dto, ListFormsDTO.class, ListFormsDTO.RELATED_CLASSES);
    }
    
    public void testListFormsDTOManyItems() throws Exception {
        FormEncodingFactory.register(FormEncodingServerFactory.getEncoder(), FormEncodingServerFactory.getDecoder()); // this is important
        FormRepresentation form1 = RESTAbstractTest.createMockForm("myForm", "param1", "param2", "param3");
        FormRepresentation form2 = RESTAbstractTest.createMockForm("otherForm", "paramA", "paramB", "paramC");
        List<FormRepresentation> forms = new ArrayList<FormRepresentation>();
        forms.add(form1);
        forms.add(form2);
        ListFormsDTO dto = new ListFormsDTO(forms);
        jaxbSimulation(dto, ListFormsDTO.class, ListFormsDTO.RELATED_CLASSES);
    }
    
    public void testListFormsItemsDTOEmpty() throws Exception {
        ListFormsItemsDTO dto = new ListFormsItemsDTO();
        jaxbSimulation(dto, ListFormsItemsDTO.class, ListFormsItemsDTO.RELATED_CLASSES);
        ListFormsItemsDTO dto2 = new ListFormsItemsDTO(new HashMap<String, FormItemRepresentation>());
        jaxbSimulation(dto2, ListFormsItemsDTO.class, ListFormsItemsDTO.RELATED_CLASSES);
    }
    
    public void testListFormsItemsDTOOneItem() throws Exception {
        HashMap<String, FormItemRepresentation> items = new HashMap<String, FormItemRepresentation>();
        FormRepresentation form = RESTAbstractTest.createMockForm("myForm", "myParam1", "myParam2", "myParam3");
        Iterator<FormItemRepresentation> iter = form.getFormItems().iterator();
        items.put("name1", iter.next());
        ListFormsItemsDTO dto = new ListFormsItemsDTO(items);
        jaxbSimulation(dto, ListFormsItemsDTO.class, ListFormsItemsDTO.RELATED_CLASSES);
    }
    
    public void testListFormsItemsDTOManyItems() throws Exception {
        HashMap<String, FormItemRepresentation> items = new HashMap<String, FormItemRepresentation>();
        FormRepresentation form = RESTAbstractTest.createMockForm("myForm", "myParam1", "myParam2", "myParam3");
        Iterator<FormItemRepresentation> iter = form.getFormItems().iterator();
        items.put("name1", iter.next());
        items.put("name2", iter.next());
        items.put("name3", iter.next());
        items.put("name4", iter.next());
        ListFormsItemsDTO dto = new ListFormsItemsDTO(items);
        jaxbSimulation(dto, ListFormsItemsDTO.class, ListFormsItemsDTO.RELATED_CLASSES);
    }
    
    public void testListMenuItemsDTOEmpty() throws Exception {
        ListMenuItemsDTO dto = new ListMenuItemsDTO();
        jaxbSimulation(dto, ListMenuItemsDTO.class, ListMenuItemsDTO.RELATED_CLASSES);
    }
    
    public void testListMenuItemsDTOEmptyGroups() throws Exception {
        Map<String, List<MenuItemDescription>> items = new HashMap<String, List<MenuItemDescription>>();
        items.put("oneGroup", new ArrayList<MenuItemDescription>());
        items.put("nullGroup", null);
        ListMenuItemsDTO dto = new ListMenuItemsDTO(items);
        jaxbSimulation(dto, ListMenuItemsDTO.class, ListMenuItemsDTO.RELATED_CLASSES);
    }
    
    public void testListMenuItemsDTOManyItems() throws Exception {
        Map<String, List<MenuItemDescription>> items = new HashMap<String, List<MenuItemDescription>>();
        List<MenuItemDescription> itemsOfGroup1 = new ArrayList<MenuItemDescription>();
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
        itemsOfGroup1.add(menuItem1);
        itemsOfGroup1.add(menuItem2);
        List<MenuItemDescription> itemsOfGroup2 = new ArrayList<MenuItemDescription>();

        MenuItemDescription menuItem3 = new MenuItemDescription();
        menuItem3.setClassName("org.jbpm.formbuilder.client.menu.items.TableLayoutMenuItem");
        menuItem3.setEffects(effects);
        MenuItemDescription menuItem4 = new MenuItemDescription();
        MenuItemDescription menuItem5 = new MenuItemDescription();
        menuItem5.setClassName("org.jbpm.formbuilder.client.menu.items.HeaderMenuItem");
        menuItem5.setEffects(effects);
        menuItem5.setAllowedEvents(allowedEvents);
        menuItem5.setItemRepresentation(RESTAbstractTest.createMockForm("", "param2").getFormItems().iterator().next());
        menuItem5.setName("some name");
        itemsOfGroup2.add(menuItem3);
        itemsOfGroup2.add(menuItem4);
        itemsOfGroup2.add(menuItem5);
        items.put("oneGroup", itemsOfGroup1);
        items.put("twoGroups", itemsOfGroup2);
        ListMenuItemsDTO dto = new ListMenuItemsDTO(items);
        jaxbSimulation(dto, ListMenuItemsDTO.class, ListMenuItemsDTO.RELATED_CLASSES);
    }
    
    public void testListOptionsDTOEmpty() throws Exception {
        ListOptionsDTO dto = new ListOptionsDTO();
        jaxbSimulation(dto, ListOptionsDTO.class, ListOptionsDTO.RELATED_CLASSES);
        ListOptionsDTO dto2 = new ListOptionsDTO(new ArrayList<MenuOptionDescription>());
        jaxbSimulation(dto2, ListOptionsDTO.class, ListOptionsDTO.RELATED_CLASSES);
    }
    
    public void testListOptionsManyItems() throws Exception {
        List<MenuOptionDescription> options = new ArrayList<MenuOptionDescription>();
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
        
        options.add(option1);
        options.add(option2);
        ListOptionsDTO dto = new ListOptionsDTO(options);
        jaxbSimulation(dto, ListOptionsDTO.class, ListOptionsDTO.RELATED_CLASSES);
    }
    
    public void testFormPreviewDTO() throws Exception {
        FormPreviewDTO dto = new FormPreviewDTO();
        List<FormPreviewParameterDTO> inputs = new ArrayList<FormPreviewParameterDTO>();
        FormPreviewParameterDTO param1 = new FormPreviewParameterDTO();
        param1.setKey("a");
        param1.setValue("a1");
        FormPreviewParameterDTO param2 = new FormPreviewParameterDTO();
        param2.setKey("b");
        param2.setValue("b1");
        inputs.add(param1);
        inputs.add(param2);
        dto.setInput(inputs);
        dto.setRepresentation("{}");
        jaxbSimulation(dto, FormPreviewDTO.class, FormPreviewDTO.RELATED_CLASSES);
        
        FormPreviewDTO dto2 = new FormPreviewDTO();
        dto.setRepresentation("{}");
        jaxbSimulation(dto2, FormPreviewDTO.class, FormPreviewDTO.RELATED_CLASSES);
        
        FormPreviewDTO dto3 = new FormPreviewDTO();
        jaxbSimulation(dto3, FormPreviewDTO.class, FormPreviewDTO.RELATED_CLASSES);
        
        FormPreviewDTO dto4 = new FormPreviewDTO();
        dto4.setInput(new ArrayList<FormPreviewParameterDTO>());
        jaxbSimulation(dto4, FormPreviewDTO.class, FormPreviewDTO.RELATED_CLASSES);
    }

    public void testPackageListDTOEmpty() throws Exception {
        PackageListDTO dto = new PackageListDTO();
        jaxbSimulation(dto, PackageListDTO.class, PackageListDTO.RELATED_CLASSES);
        
        PackageListDTO dto2 = new PackageListDTO();
        dto2.setPackage(new ArrayList<PackageDTO>());
        jaxbSimulation(dto2, PackageListDTO.class, PackageListDTO.RELATED_CLASSES);
    }
    
    public void testPackageListDTOManyItems() throws Exception {
        PackageListDTO dto = new PackageListDTO();
        List<PackageDTO> packageList = new ArrayList<PackageDTO>();
        PackageDTO pdto1 = new PackageDTO();
        List<String> assets1 = new ArrayList<String>();
        MetaDataDTO metadata1 = new MetaDataDTO();
        metadata1.setFormat("drl");
        metadata1.setTitle("title1");
        metadata1.setUuid("uuid");
        assets1.add("aaaa");
        assets1.add("bbbb");
        pdto1.setAssets(assets1);
        pdto1.setTitle("title1");
        pdto1.setMetadata(metadata1);
        packageList.add(pdto1);
        
        PackageDTO pdto2 = new PackageDTO();
        List<String> assets2 = new ArrayList<String>();
        MetaDataDTO metadata2 = new MetaDataDTO();
        metadata2.setFormat("txt");
        metadata2.setTitle("title2");
        metadata2.setUuid("uuid2");
        assets2.add("cccc");
        assets2.add("dddd");
        pdto2.setAssets(assets2);
        pdto2.setTitle("title2");
        pdto2.setMetadata(metadata2);
        packageList.add(pdto2);
        dto.setPackage(packageList);
        jaxbSimulation(dto, PackageListDTO.class, PackageListDTO.RELATED_CLASSES);
    }
    
    public void testPackageListDTONullItems() throws Exception {
        PackageListDTO dto = new PackageListDTO();
        List<PackageDTO> packageList = new ArrayList<PackageDTO>();
        packageList.add(new PackageDTO());
        packageList.add(new PackageDTO());
        dto.setPackage(packageList);
        jaxbSimulation(dto, PackageListDTO.class, PackageListDTO.RELATED_CLASSES);
    }
    
    public void testAssetDTOEmpty() throws Exception {
        AssetDTO dto = new AssetDTO();
        jaxbSimulation(dto, AssetDTO.class, AssetDTO.RELATED_CLASSES);
        dto.setMetadata(new MetaDataDTO());
        jaxbSimulation(dto, AssetDTO.class, AssetDTO.RELATED_CLASSES);
    }
    
    public void testAssetDTOFull() throws Exception {
        AssetDTO dto = new AssetDTO();
        MetaDataDTO metadata = new MetaDataDTO();
        metadata.setFormat("fff");
        metadata.setTitle("tttt");
        metadata.setUuid("uuuu");
        dto.setMetadata(metadata);
        dto.setSourceLink("sssssss");
        jaxbSimulation(dto, AssetDTO.class, AssetDTO.RELATED_CLASSES);
    }
    
    private <T> void jaxbSimulation(T dto, Class<T> retType, Class<?>... boundTypes) throws JAXBException, IOException {
        String xml = helper.jaxbSerializing(dto, boundTypes);
        assertNotNull("xml shouldn't be null", xml);
        assertTrue("xml should have content", xml.length() > 0);
        ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes());
        T dto2 = helper.jaxbTransformation(retType, input, boundTypes);
        assertNotNull("dto2 shouldn't be null", dto2);
        assertTrue("dto2 should have a hash code different than zero", dto2.hashCode() != 0);
        assertTrue("dto2 and dto should be the same", dto2.equals(dto));
    }
}
