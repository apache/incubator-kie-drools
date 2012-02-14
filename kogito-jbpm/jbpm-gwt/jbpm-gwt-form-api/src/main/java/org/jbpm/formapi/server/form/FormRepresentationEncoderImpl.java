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
package org.jbpm.formapi.server.form;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.jbpm.formapi.shared.api.Mappable;
import org.jbpm.formapi.shared.form.AbstractFormRepresentationEncoder;

public class FormRepresentationEncoderImpl extends AbstractFormRepresentationEncoder {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat(getDateFormatString());
    
    @Override
    public Object fromMap(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        Object objClassName = map.get("@className");
        if (objClassName == null) {
            return null;
        }
        String className = (String) objClassName;
        try {
            Class<?> klass = Class.forName(className);
            Object newInstance = klass.newInstance();
            if (newInstance instanceof Mappable) {
                Mappable mappable = (Mappable) newInstance;
                mappable.setDataMap(map);
            }
            return newInstance;
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public String formatDate(Date date) {
        return FORMAT.format(date);
    }
}
