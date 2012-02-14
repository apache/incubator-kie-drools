package org.jbpm.formbuilder.server.form;

import java.util.List;

import junit.framework.TestCase;

import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formbuilder.server.RESTAbstractTest;
import org.jbpm.formbuilder.shared.form.FormDefinitionService;
import org.jbpm.formbuilder.shared.form.FormServiceException;
import org.jbpm.formbuilder.shared.form.MockFormDefinitionService;
import org.jbpm.formbuilder.shared.task.TaskRef;

public class FormDefinitionServiceTest extends TestCase {

    public void testTemplateFormFromTask() throws Exception {
        TaskRef task = new TaskRef();
        task.setTaskId("MyTask");
        FormDefinitionService formService = new MockFormDefinitionService();
        FormRepresentation form = formService.createFormFromTask(task);
        assertNotNull("form shouldn't be null", form);
        assertTrue("form should contain two items", form.getFormItems().size() == 2);
    }
    
    public void testMockService() throws Exception {
        MockFormDefinitionService service = new MockFormDefinitionService();
        FormRepresentation form = RESTAbstractTest.createMockForm("myForm", "myParam");
        String formId = service.saveForm("pkgName", form);
        assertNotNull("formId shouldn't be null", formId);
        
        FormRepresentation form2 = service.getForm("pkgName", formId);
        assertNotNull("form2 shouldn't be null", form2);
        assertEquals("form and form2 should be the same", form, form2);
        
        FormRepresentation form3 = service.getForm("pkgName", "notARealFormId");
        assertNull("form2 should be null", form3);
        
        List<FormRepresentation> forms = service.getForms("pkgName");
        assertNotNull("forms shouldn't be null", forms);
        assertEquals("forms size should be 1", 1, forms.size());
        
        try {
            service.getForm("noRealPackage", "");
            fail("getForm(...) should not succeed");
        } catch (FormServiceException e) {
            assertNotNull("e shouldn't be null", e);
        }
        
        service.deleteForm("", "");
        service.deleteForm("pkgName", null);
        service.deleteForm("pkgName", formId);
    }
}
