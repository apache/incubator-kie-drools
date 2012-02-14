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
package org.jbpm.formapi.client.form;

import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.effect.FBFormEffect;

/**
 * Base form item for multiple choice options
 */
public abstract class OptionsFormItem extends FBFormItem {

    public OptionsFormItem(List<FBFormEffect> formEffects) {
        super(formEffects);
    }

    public abstract void addItem(String label, String value);
    
    public abstract void deleteItem(String label);
    
    public abstract Map<String, String> getItems();
}
