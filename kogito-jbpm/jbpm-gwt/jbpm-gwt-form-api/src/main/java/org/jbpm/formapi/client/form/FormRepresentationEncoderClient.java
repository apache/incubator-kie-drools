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

import java.util.Date;
import java.util.Map;

import org.jbpm.formapi.common.reflect.ReflectionHelper;
import org.jbpm.formapi.shared.form.AbstractFormRepresentationEncoder;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Encodes different objects on the client side to json
 */
public class FormRepresentationEncoderClient extends AbstractFormRepresentationEncoder {

    private static final DateTimeFormat FORMAT = DateTimeFormat.getFormat(getDateFormatString());
    
    @Override
    public String formatDate(Date date) {
        return FORMAT.format(date);
    }
    
    @Override
    public Object fromMap(Map<String, Object> map) {
        Object objClassName = map.get("@className");
        if (objClassName == null) {
            return null;
        }
        String className = (String) objClassName;
        try {
            return ReflectionHelper.newInstance(className);
        } catch (Exception e) {
            return null;
        }
    }
}
