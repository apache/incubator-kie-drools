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
import org.jbpm.form.builder.services.tasks.TaskRef;



public interface FormDefinitionService {

    List<FormRepresentation> getForms(String pkgName) throws FormServiceException;
    Map<String, FormItemRepresentation> getFormItems(String pkgName) throws FormServiceException;

    String /*formId*/ saveForm(String pkgName, FormRepresentation form) throws FormServiceException;
    String /*formItemId*/ saveFormItem(String pkgName, String formItemName, FormItemRepresentation formItem) throws FormServiceException;
    
    void deleteForm(String pkgName, String formId) throws FormServiceException;
    void deleteFormItem(String pkgName, String formItemId) throws FormServiceException;
    
    FormRepresentation getForm(String pkgName, String formId) throws FormServiceException;
    FormRepresentation getFormByUUID(String packageName, String uuid) throws FormServiceException;
    FormItemRepresentation getFormItem(String pkgName, String formItemId) throws FormServiceException;

    FormRepresentation getAssociatedForm(String pkgName, TaskRef task) throws FormServiceException;
    FormRepresentation createFormFromTask(TaskRef task) throws FormServiceException;
    
    void saveTemplate(String packageName, String templateName, String content) throws FormServiceException;
    
    // TODO see where to put method renderTemplate(template t, Map<String, Object> inputs): html
}
