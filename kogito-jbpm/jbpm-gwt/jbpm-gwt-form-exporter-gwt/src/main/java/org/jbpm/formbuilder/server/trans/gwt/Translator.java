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
package org.jbpm.formbuilder.server.trans.gwt;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.jbpm.formapi.server.form.FormEncodingServerFactory;
import org.jbpm.formapi.server.trans.TranslatorException;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormRepresentationEncoder;

public class Translator implements org.jbpm.formapi.server.trans.Translator {

    private static final String LANG = "gwt";
    
    @Override
    public String getLanguage() {
        return LANG;
    }

    @Override
    public URL translateForm(FormRepresentation form) throws TranslatorException {
        FormRepresentationEncoder encoder = FormEncodingServerFactory.getEncoder();
        try {
            String json = encoder.encode(form);
            File file = File.createTempFile("form-gwt-", ".json");
            FileUtils.writeStringToFile(file, json);
            return FileUtils.toURLs(new File[] { file })[0];
        } catch (IOException e) {
            throw new TranslatorException("Problem writing temporal file", e);
        } catch (FormEncodingException e) {
            throw new TranslatorException("Problem encoding form", e);
        }
    }

    @Override
    public Object translateItem(FormItemRepresentation item) throws TranslatorException {
        /* not used */
        return null;
    }

}
