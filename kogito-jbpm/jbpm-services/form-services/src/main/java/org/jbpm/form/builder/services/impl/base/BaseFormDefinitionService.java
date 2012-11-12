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
package org.jbpm.form.builder.services.impl.base;

import org.jbpm.form.builder.services.model.FormRepresentation;

public abstract class BaseFormDefinitionService {

    private static final String FORM_ID_PREFIX = "formDefinition_";
    private static final String ITEM_ID_PREFIX = "formItemDefinition_";
    
    /**
     * @param form FormRepresentation with name to be changed
     * @return true if its an update, false if it is an insert
     */
    protected boolean updateFormName(FormRepresentation form) {
        if (form.getName() == null || "null".equals(form.getName()) || "".equals(form.getName())) {
            form.setName(FORM_ID_PREFIX + System.currentTimeMillis());
            return false;
        } else if (!form.getName().startsWith(FORM_ID_PREFIX)){
            form.setName(FORM_ID_PREFIX + form.getName());
            return false;
        }
        return true;
    }
    
    protected boolean updateItemName(String formItemName, StringBuilder returnName) {
        if (formItemName == null || "null".equals(formItemName) || "".equals(formItemName)) {
            returnName.append(ITEM_ID_PREFIX).append(System.currentTimeMillis());
            return false;
        } else if (!formItemName.startsWith(ITEM_ID_PREFIX)){
            returnName.append(ITEM_ID_PREFIX).append(formItemName);
            return false;
        }
        returnName.append(formItemName);
        return true;
    }
    
    protected boolean isItemName(String assetId) {
        return assetId.startsWith(ITEM_ID_PREFIX) && assetId.endsWith(".json");
    }
    
    protected boolean isFormName(String assetId) {
        return assetId.endsWith(".formdef");
    }
}
