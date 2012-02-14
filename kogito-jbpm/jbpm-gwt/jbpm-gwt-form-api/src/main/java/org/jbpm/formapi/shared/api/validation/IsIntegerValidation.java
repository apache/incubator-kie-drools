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
package org.jbpm.formapi.shared.api.validation;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.formapi.shared.api.FBValidation;

public class IsIntegerValidation implements FBValidation {

    private Map<String, Object> propertiesMap = new HashMap<String, Object>();
    
    @Override
    public boolean isValid(Object item) {
        if (item == null) {
            return false;
        }
        if (item instanceof Integer) {
            return true;
        }
        try {
            Integer.parseInt(item.toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    @Override
    public String getValidationId() {
        return "isInteger";
    }

    @Override
    public FBValidation cloneValidation() {
        IsIntegerValidation validation = new IsIntegerValidation();
        validation.setDataMap(getDataMap());
        return validation;
    }

    @Override
    public Map<String, Object> getDataMap() {
        if (!propertiesMap.containsKey("@className") ||
                !IsIntegerValidation.class.getName().equals(propertiesMap.get("@className"))) {
            propertiesMap.put("@className", IsIntegerValidation.class.getName());
        }
        return propertiesMap;
    }

    @Override
    public void setDataMap(Map<String, Object> dataMap) {
        if (dataMap == null) {
            dataMap = new HashMap<String, Object>();        
        }
        this.propertiesMap = dataMap;
    }
}
