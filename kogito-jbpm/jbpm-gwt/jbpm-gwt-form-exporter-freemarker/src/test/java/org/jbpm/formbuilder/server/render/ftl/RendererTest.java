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
package org.jbpm.formbuilder.server.render.ftl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formbuilder.server.RenderTranslatorAbstractTest;
import org.jbpm.formbuilder.server.trans.ftl.Translator;

public class RendererTest extends RenderTranslatorAbstractTest {

    public void testFormBasic() throws Exception {
        Translator lang = new Translator();
        FormRepresentation form = createBasicForm();
        URL url = lang.translateForm(form);
        assertNotNull("url shouldn't be null", url);
        
        Renderer renderer = new Renderer();
        Map<String, Object> inputData = new HashMap<String, Object>();
        inputData.put(Renderer.BASE_CONTEXT_PATH, "/");
        inputData.put(Renderer.BASE_LOCALE, "default");
        Object obj = renderer.render(url, inputData);
        assertNotNull("obj shouldn't be null", obj);
        assertTrue("obj should be a string", obj instanceof String);
        String html = obj.toString();
        assertTrue("html should contain comboName", html.contains("comboName"));
        assertFalse("html shouldn't contain taskNameXXX (it's produced by a freemarker comment) ", html.contains("taskNameXXX"));
    }
}
