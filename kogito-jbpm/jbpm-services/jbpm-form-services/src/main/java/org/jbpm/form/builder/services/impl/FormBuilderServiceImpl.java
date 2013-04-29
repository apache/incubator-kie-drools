/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.form.builder.services.impl;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

import org.jbpm.form.builder.services.api.FormBuilderService;
import org.jbpm.form.builder.services.api.FormBuilderServiceException;
import org.jbpm.form.builder.services.encoders.FormEncodingServerFactory;
import org.jbpm.form.builder.services.model.FormItemRepresentation;
import org.jbpm.form.builder.services.model.FormRepresentation;
import org.jbpm.form.builder.services.model.Settings;
import org.jbpm.form.builder.services.model.forms.FormEncodingException;

/**
 *
 * @author salaboy
 */
@ApplicationScoped
public class FormBuilderServiceImpl implements FormBuilderService{

    public void getMenuItems() throws FormBuilderServiceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getMenuOptions() throws FormBuilderServiceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String saveFormGWT(Map<String, Object> form) throws FormBuilderServiceException {
    	FormRepresentation formRep = new FormRepresentation();
        String encode = null;
        try {
        	formRep.setDataMap(form);
            encode = saveForm(formRep);
        } catch (FormEncodingException ex) {
            Logger.getLogger(FormBuilderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return encode;
    }

    @Override
    public String saveForm(FormRepresentation form) throws FormBuilderServiceException {
        String encode = null;
        try {
            encode = FormEncodingServerFactory.getEncoder().encode(form);
        } catch (FormEncodingException ex) {
            Logger.getLogger(FormBuilderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return encode;
    }
    
    @Override
    public void saveFormItem(FormItemRepresentation formItem, String formItemName) throws FormBuilderServiceException {
    	throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void deleteForm(FormRepresentation form) throws FormBuilderServiceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteFile(String url) throws FormBuilderServiceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteFormItem(String formItemName, FormItemRepresentation formItem) throws FormBuilderServiceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void generateForm(FormRepresentation form, String language, Map<String, Object> inputs) throws FormBuilderServiceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getExistingIoAssociations(String filter) throws FormBuilderServiceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void selectIoAssociation(String pkgName, String processName, String taskName) throws FormBuilderServiceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getExistingValidations() throws FormBuilderServiceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getForm(String formName) throws FormBuilderServiceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getForms() throws FormBuilderServiceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void populateRepresentationFactory() throws FormBuilderServiceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void loadFormTemplate(FormRepresentation form, String language) throws FormBuilderServiceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getCurrentRoles(RolesResponseHandler handler) throws FormBuilderServiceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getUploadFileURL() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getUploadActionURL() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setPackageName(String packageName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void logout() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void applySettings(Settings settings) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void loadSettings() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getFormDisplay() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public FormRepresentation loadForm(String json) {
    	try {
    		return FormEncodingServerFactory.getDecoder().decode(json);
        } catch (FormEncodingException ex) {
            Logger.getLogger(FormBuilderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
