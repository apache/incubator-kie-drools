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
package org.jbpm.formbuilder.server.trans.ftl;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formbuilder.server.RenderTranslatorAbstractTest;

public class TranslatorTest extends RenderTranslatorAbstractTest {

    public void testFormBasic() throws Exception {
        Translator lang = new Translator();
        FormRepresentation form = createBasicForm();
        URL url = lang.translateForm(form);
        String script = FileUtils.readFileToString(new File(url.getFile()));
        assertNotNull("script shouldn't be null", script);
        assertTrue("script should contain checkbox", script.contains("checkbox"));
        assertTrue("script should contain select", script.contains("select"));
        assertTrue("script should contain taskNameXXX", script.contains("taskNameXXX"));
    }
}
