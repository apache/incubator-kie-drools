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
package org.jbpm.formbuilder.shared.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formbuilder.shared.task.TaskRef;

public class MockFormDefinitionService extends AbstractBaseFormDefinitionService implements FormDefinitionService {

    private Map<String, List<FormRepresentation>> forms = new HashMap<String, List<FormRepresentation>>();
    private Map<String, List<Map.Entry<String, FormItemRepresentation>>> items = 
        new HashMap<String, List<Map.Entry<String, FormItemRepresentation>>>();
    
    @Override
    public List<FormRepresentation> getForms(String pkgName) {
        return forms.get(pkgName);
    }
    
    @Override
    public FormRepresentation getForm(String pkgName, String formId) throws FormServiceException {
        List<FormRepresentation> list = forms.get(pkgName);
        if (list == null) {
            throw new FormServiceException();
        }
        FormRepresentation form = null;
        for (FormRepresentation f : list) {
            if (formId.equals(f.getName())) {
                form = f;
                break;
            }
        }
        return form;
    }
    
    @Override
    public FormRepresentation getFormByUUID(String pkgName, String uuid) throws FormServiceException {
        List<FormRepresentation> list = forms.get(pkgName);
        if (list == null) {
            throw new FormServiceException();
        }
        return list.isEmpty() ? null : list.iterator().next();
    }
    
    @Override
    public FormItemRepresentation getFormItem(String pkgName, String formItemId)
            throws FormServiceException {
        List<Map.Entry<String, FormItemRepresentation>> list = items.get(pkgName);
        if (list == null) {
            throw new FormServiceException();
        }
        FormItemRepresentation item = null;
        for (Map.Entry<String, FormItemRepresentation> i : list) {
            if (formItemId.equals(i.getKey())) {
                item = i.getValue();
                break;
            }
        }
        return item;
    }
    
    @Override
    public Map<String, FormItemRepresentation> getFormItems(String pkgName) {
        List<Map.Entry<String, FormItemRepresentation>> list = items.get(pkgName);
        Map<String, FormItemRepresentation> retval = null;
        if (list != null) {
            retval = new HashMap<String, FormItemRepresentation>();
            for (Map.Entry<String, FormItemRepresentation> entry : list) {
                retval.put(entry.getKey(), entry.getValue());
            }
        }
        return retval;
    }

    @Override
    public String saveForm(String pkgName, FormRepresentation form) {
        updateFormName(form);
        List<FormRepresentation> list = forms.get(pkgName);
        if (list == null) {
            list = new ArrayList<FormRepresentation>();
        }
        list.add(form);
        forms.put(pkgName, list);
        return form.getName();
    }

    @Override
    public String saveFormItem(String pkgName, String formItemName, final FormItemRepresentation formItem) {
        StringBuilder builder = new StringBuilder();
        updateItemName(formItemName, builder);
        formItemName = builder.toString();
        List<Map.Entry<String, FormItemRepresentation>> list = items.get(pkgName);
        if (list == null) {
            list = new ArrayList<Map.Entry<String, FormItemRepresentation>>();
        }
        final String itemName = formItemName;
        list.add(new Map.Entry<String, FormItemRepresentation>() {
            @Override
            public String getKey() {
                return itemName;
            }
            @Override
            public FormItemRepresentation getValue() {
                return formItem;
            }
            @Override
            public FormItemRepresentation setValue(FormItemRepresentation value) {
                return formItem;
            }
        });
        items.put(pkgName, list);
        return itemName;
    }
    
    @Override
    public void deleteForm(String pkgName, String formId) {
        List<FormRepresentation> list = forms.get(pkgName);
        if (list != null) {
            FormRepresentation toRemove = null;
            if (formId != null) {
                for (FormRepresentation form : list) {
                    if (formId.equals(form.getName())) {
                        toRemove = form;
                        break;
                    }
                }
            }
            if (toRemove != null) {
                list.remove(toRemove);
            }
            forms.put(pkgName, list);
        }
    }
    
    @Override
    public void deleteFormItem(String pkgName, String formItemId) {
        List<Map.Entry<String, FormItemRepresentation>> list = items.get(pkgName);
        if (list != null) {
            Map.Entry<String, FormItemRepresentation> toRemove = null;
            for (Map.Entry<String, FormItemRepresentation> item: list) {
                if (formItemId.equals(item.getKey())) {
                    toRemove = item;
                    break;
                }
            }
            if (toRemove != null) {
                list.remove(toRemove);
            }
            items.put(pkgName, list);
        }
    }

    @Override
    public FormRepresentation getAssociatedForm(String pkgName, TaskRef task) throws FormServiceException {
        List<FormRepresentation> forms = this.forms.get(pkgName);
        FormRepresentation retval = null;
        for (FormRepresentation form : forms) {
            if (form.getTaskId() != null && form.getTaskId().equals(task.getTaskId())) {
                retval = form;
                break;
            }
        }
        return retval;
    }

    @Override
    public void saveTemplate(String packageName, String templateName, String content) throws FormServiceException {
        // do nothing
    }
}
