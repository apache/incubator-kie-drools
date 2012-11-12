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
package org.jbpm.form.builder.services;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.jbpm.form.builder.services.encoders.FormEncodingServerFactory;
import org.jbpm.form.builder.services.impl.fs.FSFileService;
import org.jbpm.form.builder.services.impl.fs.FSFormDefinitionService;
import org.jbpm.form.builder.services.model.FormItemRepresentation;
import org.jbpm.form.builder.services.model.FormRepresentation;
import org.jbpm.form.builder.services.model.forms.FormEncodingFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class FormDefinitionServiceBaseTest{
    @Inject
    private FSFormDefinitionService service;
    private String baseUrl = "/tmp/formDefinitionServiceBaseTestFolder";
    private String fileSeparator = System.getProperty("file.separator");
    
    @Before
    public void setUp() throws Exception {
        FormEncodingFactory.register(FormEncodingServerFactory.getEncoder(), FormEncodingServerFactory.getDecoder());
        FileUtils.deleteDirectory(new File(baseUrl));
        service.setBaseUrl(baseUrl);
        ((FSFileService)service.getFileService()).setBaseUrl(baseUrl);
    }
    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File(baseUrl));
    }
    
    //test happy path for insert for GuvnorFormDefinitionService.saveForm(...)
    @Test
    public void testSaveFormOK() throws Exception {
        
        
        FormRepresentation form = MockFormHelper.createMockForm("form1", "oneParam");
        
        
        String formId = service.saveForm(form);
        
        assertEquals(formId, form.getName());
        
        String formUrl = baseUrl+fileSeparator+"form1"+"AutoForm.formdef";
        assertTrue(new File(formUrl).exists());
        
        FileUtils.deleteQuietly(new File(formUrl));
        
        
    }
    @Test
    public void testSaveFormItemOK() throws Exception {
        

        
       
        
        FormItemRepresentation item = MockFormHelper.createMockForm("form1", "oneParam").getFormItems().iterator().next();
        
        
        String itemId = service.saveFormItem("item1", item);
        
        String itemUrl = baseUrl + fileSeparator + "formItemDefinition_item1.json";
        
        assertTrue(new File(itemUrl).exists());
        
        FileUtils.deleteQuietly(new File(itemUrl));
        
    }

    @Test
    public void testGetFormOK() throws Exception {
        
        FormRepresentation form = MockFormHelper.createMockForm("form1", "oneParam");
       
        
        String formId = service.saveForm(form);
        
        String formUrl = baseUrl + fileSeparator + "form1AutoForm" +".formdef";
  
        FormRepresentation form1 = service.getForm("form1AutoForm");
        
        assertNotNull(form1);
        
        
    }
    


    @Test
    public void testGetFormItemOK() throws Exception {
        

        FormRepresentation form = MockFormHelper.createMockForm("myForm", "myParam");
        String formId = service.saveForm(form);
        
        String formUrl = baseUrl + fileSeparator + "myFormAutoForm" +".formdef";
   
        assertTrue(new File(formUrl).exists());
        
        FormRepresentation form2 = service.getForm(formId);
        
        assertNotNull(form2);
        
    }

    @Test
    public void testGetFormsOK() throws Exception {
        
        FormRepresentation form1 = MockFormHelper.createMockForm("form1", "oneParam"); // this add to the name AutoForm
        service.saveForm(form1);
        FormRepresentation form2 = MockFormHelper.createMockForm("form2", "anotherParam");// this add to the name AutoForm
        service.saveForm(form2);
        
       
        List<FormRepresentation> forms = service.getForms();
        assertEquals(2, forms.size());
        boolean form1ok = false;
        boolean form2ok = false;
        if("form1AutoForm".equals(forms.get(0).getName()) || "form1AutoForm".equals(forms.get(1).getName())){
            form1ok = true;
        }
        if("form2AutoForm".equals(forms.get(0).getName()) || "form2AutoForm".equals(forms.get(1).getName())){
            form2ok = true;
        }
        assertTrue(form1ok);
        assertTrue(form2ok);
        
    }

    @Test
    public void testDeleteFormOK() throws Exception {
        
        FormRepresentation form1 = MockFormHelper.createMockForm("form1", "oneParam");
        String formUrl = service.saveForm(form1);
        System.out.println("Form Url = "+formUrl);
        
        String deleteUrl = baseUrl + fileSeparator + formUrl + ".formdef";
        assertTrue(new File(deleteUrl).exists());
        
        service.deleteForm("form1"+"AutoForm");
        
        assertFalse(new File(deleteUrl).exists());
        
        
    }

    @Test
    public void testDeleteFormItemOK() throws Exception {
        
        FormItemRepresentation item = MockFormHelper.createMockForm("form1", "oneParam").getFormItems().iterator().next();
        
        
        String itemId = service.saveFormItem("item1", item);
        
        String itemUrl = baseUrl + fileSeparator + "formItemDefinition_"+itemId +".json";
        
        assertTrue(new File(itemUrl).exists());
        
        service.deleteFormItem("formItemDefinition_"+itemId);
        
        assertFalse(new File(itemUrl).exists());
        
    }
    

//    public void testSaveTemplateInsertOK() throws Exception {
//        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
//        HttpClient client = EasyMock.createMock(HttpClient.class);
//
//        Map<String, String> responses2 = new HashMap<String, String>();
//        Map<String, String> responses3 = new HashMap<String, String>();
//        
//        Map<String, Integer> statuses1 = new HashMap<String, Integer>();
//        statuses1.put("GET " + helper.getApiSearchUrl("somePackage") + "template.txt", 404);
//        responses2.put("POST " + helper.getRestBaseUrl() + "somePackage/assets", "OK");
//        responses3.put("PUT " + helper.getRestBaseUrl() + "somePackage/assets/template/source", "OK");
//        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
//            andAnswer(new MockAnswer(statuses1)).once();
//        EasyMock.expect(client.executeMethod(EasyMock.isA(MockPostMethod.class))).
//            andAnswer(new MockAnswer(responses2, new IllegalArgumentException("unexpected call"))).once();
//        EasyMock.expect(client.executeMethod(EasyMock.isA(MockPutMethod.class))).
//            andAnswer(new MockAnswer(responses3, new IllegalArgumentException("unexpected call"))).once();
//        service.getHelper().setClient(client);
//        
//        EasyMock.replay(client);
//        service.saveTemplate("somePackage", "template.txt", "my template content");
//        EasyMock.verify(client);
//    }
//    
//    public void testSaveTemplateUpdateOK() throws Exception {
//        GuvnorFormDefinitionService service = createService(baseUrl, "", "");
//        HttpClient client = EasyMock.createMock(HttpClient.class);
//
//        Map<String, String> responses1 = new HashMap<String, String>();
//        Map<String, String> responses2 = new HashMap<String, String>();
//        
//        responses1.put("GET " + helper.getApiSearchUrl("somePackage") + "template.txt", "old template content");
//        responses2.put("PUT " + helper.getRestBaseUrl() + "somePackage/assets/template/source", "OK");
//        EasyMock.expect(client.executeMethod(EasyMock.isA(MockGetMethod.class))).
//            andAnswer(new MockAnswer(responses1, new IllegalArgumentException("unexpected call"))).once();
//        EasyMock.expect(client.executeMethod(EasyMock.isA(MockPutMethod.class))).
//            andAnswer(new MockAnswer(responses2, new IllegalArgumentException("unexpected call"))).once();
//        service.getHelper().setClient(client);
//        
//        EasyMock.replay(client);
//        service.saveTemplate("somePackage", "template.txt", "my template content");
//        EasyMock.verify(client);
//    }
//

    
  
}
