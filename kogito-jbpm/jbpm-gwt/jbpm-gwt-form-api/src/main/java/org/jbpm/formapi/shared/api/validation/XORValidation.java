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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.shared.api.FBValidation;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;

import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class XORValidation implements FBValidation {

    private Map<String, Object> propertiesMap = new HashMap<String, Object>();
    private List<FBValidation> validations = new ArrayList<FBValidation>();
    
    @Override
    public boolean isValid(Object obj) {
        int trueCount = 0;
        if (validations != null && !validations.isEmpty()) {
            for (FBValidation subValidation : validations) {
                boolean result = subValidation.isValid(obj);
                if (result) {
                    trueCount++;
                }
            }
        }
        return trueCount == 1;
    }

    @Override
    public String getValidationId() {
        return "xor";
    }

    @Override
    public Map<String, Object> getDataMap() {
        if (!propertiesMap.containsKey("@className") || !XORValidation.class.getName().equals(propertiesMap.get("@className"))) {
            propertiesMap.put("@className", XORValidation.class.getName());
        }
        List<Object> validationsMap = new ArrayList<Object>();
        if (validations != null && !validations.isEmpty()) {
            for (FBValidation validation : validations) {
                validationsMap.add(validation.getDataMap());
            }
        }
        propertiesMap.put("validations", validationsMap);
        return propertiesMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setDataMap(Map<String, Object> dataMap)
            throws FormEncodingException {
        if (dataMap == null) {
            dataMap = new HashMap<String, Object>();
        }
        this.propertiesMap = dataMap;
        List<Object> validationsMap = (List<Object>) dataMap.get("validations");
        this.validations.clear();
        if (validationsMap != null) {
            for (Object obj : validationsMap) {
                Map<String, Object> subMap = (Map<String, Object>) obj;
                FBValidation subVal = (FBValidation) FormEncodingFactory.getDecoder().decode(subMap);
                this.validations.add(subVal);
            }
        }
    }
    
    @Override
    public FBValidation cloneValidation() {
        XORValidation validation = new XORValidation();
        validation.validations.addAll(validations);
        validation.propertiesMap.putAll(propertiesMap);
        return validation;
    }
    
    public void addValidation(FBValidation validation) {
        if (this.validations == null) {
            this.validations = new ArrayList<FBValidation>();
        }
        this.validations.add(validation);
    }
    
    public List<FBValidation> getValidations() {
        return validations;
    }
}
