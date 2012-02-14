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

import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class SmallerThanValidation implements FBValidation {
    
    private Map<String, Object> propertiesMap = new HashMap<String, Object>();
    private Comparable<?> value;
    
    @Override
    public boolean isValid(Object obj) {
        if (obj instanceof Comparable<?>) {
            @SuppressWarnings("unchecked")
            Comparable<Object> comp = (Comparable<Object>) obj;
            return comp.compareTo(value) < 0;
        }
        return String.valueOf(obj).compareTo(String.valueOf(value)) < 0;
    }

    @Override
    public String getValidationId() {
        return "smallerThan";
    }

    @Override
    public FBValidation cloneValidation() {
        SmallerThanValidation validation = new SmallerThanValidation();
        validation.setDataMap(getDataMap());
        return validation;
    }

    @Override
    public Map<String, Object> getDataMap() {
        if (!propertiesMap.containsKey("@className") ||
                !SmallerThanValidation.class.getName().equals(propertiesMap.get("@className"))) {
            propertiesMap.put("@className", SmallerThanValidation.class.getName());
        }
        propertiesMap.put("value", this.value);
        return propertiesMap;
    }

    @Override
    public void setDataMap(Map<String, Object> dataMap) {
        if (dataMap == null) {
            dataMap = new HashMap<String, Object>();
        }
        this.propertiesMap = dataMap;
        this.value = (Comparable<?>) dataMap.get("value");
    }
}
