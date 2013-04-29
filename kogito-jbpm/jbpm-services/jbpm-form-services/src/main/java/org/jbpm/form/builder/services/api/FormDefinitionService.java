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
package org.jbpm.form.builder.services.api;

import java.util.List;
import java.util.Map;

import org.jbpm.form.builder.services.model.FormItemRepresentation;
import org.jbpm.form.builder.services.model.FormRepresentation;

public interface FormDefinitionService {

    List<FormRepresentation> getForms() throws FormServiceException;
    Map<String, FormItemRepresentation> getFormItems() throws FormServiceException;

    String /*formId*/ saveForm(FormRepresentation form) throws FormServiceException;
    String /*formItemId*/ saveFormItem(String formItemName, FormItemRepresentation formItem) throws FormServiceException;
    
    void deleteForm(String formId) throws FormServiceException;
    void deleteFormItem(String formItemId) throws FormServiceException;
    
    FormRepresentation getForm(String formId) throws FormServiceException;
    FormRepresentation getFormByUUID(String uuid) throws FormServiceException;
    FormItemRepresentation getFormItem(String formItemId) throws FormServiceException;

    void saveTemplate(String templateName, String content) throws FormServiceException;
    
    // TODO see where to put method renderTemplate(template t, Map<String, Object> inputs): html
}
