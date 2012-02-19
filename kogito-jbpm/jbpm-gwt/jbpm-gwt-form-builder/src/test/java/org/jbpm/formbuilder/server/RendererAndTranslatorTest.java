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
package org.jbpm.formbuilder.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.jbpm.formapi.server.form.FormEncodingServerFactory;
import org.jbpm.formapi.server.render.Renderer;
import org.jbpm.formapi.server.render.RendererException;
import org.jbpm.formapi.server.render.RendererFactory;
import org.jbpm.formapi.server.trans.Translator;
import org.jbpm.formapi.server.trans.TranslatorException;
import org.jbpm.formapi.server.trans.TranslatorFactory;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationDecoder;
import org.jbpm.formbuilder.server.menu.GuvnorMenuService;

import com.google.gwt.event.shared.UmbrellaException;

public class RendererAndTranslatorTest extends TestCase {

    private GuvnorMenuService service;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        FormEncodingFactory.register(FormEncodingServerFactory.getEncoder(), FormEncodingServerFactory.getDecoder());
        this.service = new GuvnorMenuService();
    }
    
    /**
     * Test, for every configured language, that a basic form can be populated
     * @throws Exception in case of an error on any of the languages.
     * TODO xulphp should be finished and xsl has problems
     */
    public void testAllAvailableLanguages() throws Exception {
        Map<String, String> props = service.getFormBuilderProperties();
        String langsProperty = props.get("form.builder.languages"); //"gwt,ftl"
        String[] langs = langsProperty.split(","); //{ "gwt", "ftl" }
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        String json = getFormTestJsonRepresentation();
        FormRepresentation form = decoder.decode(json);
        Set<Throwable> errors = new HashSet<Throwable>();
        for (String language : langs) {
            try {
                Translator translator = TranslatorFactory.getInstance().getTranslator(language);
                assertNotNull("translator shouldn't be null", translator);
                Renderer renderer = RendererFactory.getInstance().getRenderer(language);
                assertNotNull("renderer shouldn't be null", renderer);
                URL url = translator.translateForm(form);
                Map<String, Object> inputs = basicInputs();
                Object html = renderer.render(url, inputs);
                assertNotNull("html shouldn't be null", html);
            } catch (TranslatorException e) {
                errors.add(new Exception("translator for language " + language + " failed", e));
            } catch (RendererException e) {
                errors.add(new Exception("renderer for language " + language + " failed", e));
            }
        }
        if (!errors.isEmpty()) {
            throw new UmbrellaException(errors);
        }
    }

    private Map<String, Object> basicInputs() {
        Map<String, Object> inputs = new HashMap<String, Object>();
        inputs.put(Renderer.BASE_CONTEXT_PATH, "/");
        inputs.put(Renderer.BASE_LOCALE, "default");
        return inputs;
    }
    
    private String getFormTestJsonRepresentation() throws IOException {
        String url = "/org/jbpm/formbuilder/shared/form/testComplexFormDecoding.json";
        InputStream input = getClass().getResourceAsStream(url);
        return IOUtils.toString(input);
    }
}
