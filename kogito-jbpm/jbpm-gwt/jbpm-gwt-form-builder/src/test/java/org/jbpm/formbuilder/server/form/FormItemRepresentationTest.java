/**
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
package org.jbpm.formbuilder.server.form;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;

import org.jbpm.formapi.server.form.FormRepresentationDecoderImpl;
import org.jbpm.formapi.server.form.FormRepresentationEncoderImpl;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formbuilder.server.RESTAbstractTest;

public class FormItemRepresentationTest extends TestCase {

    public void testGetData() throws Exception {
        //get class names
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("/FormBuilder.properties"));
        Set<Object> classNames = props.keySet();
        //get classes
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (Object objClassName : classNames) {
            String className = (String) objClassName;
            if (className.endsWith("Representation")) {
                Class<?> klass = Class.forName(className);
                if (FormItemRepresentation.class.isAssignableFrom(klass)) {
                    classes.add(klass);
                }
            }
        }
        //create instances
        Map<Class<?>, FormItemRepresentation> instances = new HashMap<Class<?>, FormItemRepresentation>();
        for (Class<?> clazz : classes) {
            Object obj = clazz.newInstance();
            instances.put(clazz, (FormItemRepresentation) obj);
        }
        //create auxiliar map to keep uncovered fields
        Map<String, List<String>> uncovered = new HashMap<String, List<String>>();
        
        //for each object, check field coverage
        for (Class<?> clazz : instances.keySet()) {
            Field[] fields = clazz.getFields();
            List<String> uncoveredFields = new ArrayList<String>();
            FormItemRepresentation item = instances.get(clazz);
            Map<String, Object> data = item.getDataMap();
            for (Field field : fields) {
                String fieldName = field.getName();
                if (!data.containsKey(fieldName)) {
                    uncoveredFields.add(fieldName);
                }
            }
            if (!uncoveredFields.isEmpty()) {
                uncovered.put(clazz.getName(), uncoveredFields);
            }
        }
        //print messages and fail if uncovered isn't empty
        if (!uncovered.isEmpty()) {
            StringBuilder builder = new StringBuilder("FormItemRepresentation subclasses need total coverage of ");
            builder.append("fields on the getData method. However, the following exceptions were found:\n");
            for (String className : uncovered.keySet()) {
                builder.append("Class: ").append(className).append(", uncovered fields: ").append(uncovered.get(className)).append("\n");
            }
            builder.append("\nCorrect these fields and try compiling again");
            fail(builder.toString());
        }
    }
    
    public void testRepresentationEncoderImpl() throws Exception {
        FormRepresentationEncoderImpl encoder = new FormRepresentationEncoderImpl();
        FormRepresentationDecoderImpl decoder = new FormRepresentationDecoderImpl();
        FormEncodingFactory.register(encoder, decoder);
        
        assertNull("result should be null", encoder.fromMap(null));
        assertNull("result should be null", encoder.fromMap(new HashMap<String, Object>()));
        
        assertNotNull("formattedDate shouldn't be null", encoder.formatDate(new Date()));
        
        FormRepresentation form = RESTAbstractTest.createMockForm("myForm", "myParam1", "myParam2");
        FormItemRepresentation item = form.getFormItems().iterator().next();
        Map<String, Object> data = item.getDataMap();
        Object obj = encoder.fromMap(data);
        assertNotNull("obj shouldn't be null", obj);
        assertTrue("obj should be of type FormItemRepresentation", obj instanceof FormItemRepresentation);
        FormItemRepresentation retval = (FormItemRepresentation) obj;
        assertEquals("retval and item should be equal", retval, item);
    }
}
