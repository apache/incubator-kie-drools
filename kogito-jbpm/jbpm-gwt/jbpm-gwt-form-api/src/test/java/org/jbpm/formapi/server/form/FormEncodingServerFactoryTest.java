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
package org.jbpm.formapi.server.form;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.jbpm.formapi.server.form.FormEncodingServerFactory;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationDecoder;
import org.jbpm.formapi.shared.form.FormRepresentationEncoder;

public class FormEncodingServerFactoryTest extends TestCase {

    public void testComplexFormDecoding() throws Exception {
        FormRepresentationEncoder encoder = FormEncodingServerFactory.getEncoder();
        FormRepresentationDecoder decoder = FormEncodingServerFactory.getDecoder();
        FormEncodingFactory.register(encoder, decoder);
        
        URL url = getClass().getResource("/org/jbpm/formapi/shared/form/testComplexFormDecoding.json");
        String json = FileUtils.readFileToString(new File(url.getFile()));
        
        assertNotNull("json shouldn't be null", json);
        assertNotSame("json shouldn't be empty", "", json);
        
        
        FormRepresentation form = decoder.decode(json);
        assertNotNull("form shouldn't be null", form);
        String json2 = encoder.encode(form);
        FormRepresentation form2 = decoder.decode(json2);
        assertNotNull("json2 shouldn't be null", json2);
        assertNotSame("json2 shouldn't be empty", "", json2);
        
        assertNotNull("form2 shouldn't be null", form2);
        assertEquals("both forms should be the same in contents", form, form2);
    }
}
