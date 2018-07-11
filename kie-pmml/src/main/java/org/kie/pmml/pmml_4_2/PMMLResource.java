/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.kie.pmml.pmml_4_2;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.builder.model.KieBaseModel;

public class PMMLResource {
    private KieBaseModel kieBaseModel;
    private String packageName;
    private boolean isForMiningModel;
    private Map<String, String> pojoDefinitions;
    private Map<String, String> rules;


    public PMMLResource(String packageName) {
        this.packageName = packageName;
        this.pojoDefinitions = new HashMap<>();
        this.rules = new HashMap<>();
    }


    public KieBaseModel getKieBaseModel() {
        return kieBaseModel;
    }


    public void setKieBaseModel(KieBaseModel kieBaseModel) {
        this.kieBaseModel = kieBaseModel;
    }


    public String getPackageName() {
        return packageName;
    }


    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    public Map<String, String> getPojoDefinitions() {
        return pojoDefinitions;
    }

    public String addPojoDefinition(String pojoName, String pojoDefinition) {
        if (pojoName == null || pojoName.trim().isEmpty() || pojoDefinition == null || pojoDefinition.trim().isEmpty()) {
            return null;
        }
        return this.pojoDefinitions.put(pojoName, pojoDefinition);
    }

    public String addPojoDefinition(Map.Entry<String, String> pojoEntry) {
        if (pojoEntry == null) return null;
        return this.pojoDefinitions.put(pojoEntry.getKey(),pojoEntry.getValue());
    }


    public Map<String, String> getRules() {
        return rules;
    }

    public String addRules(String key, String value) {
        return rules.put(key, value);
    }


    public void addAllRules(Map<? extends String, ? extends String> m) {
        rules.putAll(m);
    }




    public boolean isForMiningModel() {
        return isForMiningModel;
    }


    public void setForMiningModel(boolean isForMiningModel) {
        this.isForMiningModel = isForMiningModel;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PMMLResource other = (PMMLResource) obj;
        if (packageName == null) {
            if (other.packageName != null) {
                return false;
            }
        } else if (!packageName.equals(other.packageName)) {
            return false;
        }
        return true;
    }


}
