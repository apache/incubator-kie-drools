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
package org.jbpm.formbuilder.client.form;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.formapi.client.form.I18NFormItem;

public class I18NUtils implements I18NFormItem {

    private final Map<String, String> i18nMap = new HashMap<String, String>();
    private Format format;
    
    @Override
    public boolean containsLocale(String localeName) {
        return i18nMap.containsKey(localeName);
    }

    @Override
    public void saveI18nMap(Map<String, String> i18nMap) {
        this.i18nMap.clear();
        if (i18nMap != null) {
            this.i18nMap.putAll(i18nMap);
        }
    }

    @Override
    public Map<String, String> getI18nMap() {
        return this.i18nMap;
    }

    @Override
    public String getI18n(String key) {
        return this.i18nMap.get(key);
    }

    @Override
    public Format getFormat() {
        return format;
    }
    
    @Override
    public void setFormat(Format format) {
        this.format = format;
    }
}
