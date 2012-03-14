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

import java.util.ArrayList;
import java.util.List;

import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.api.items.CheckBoxRepresentation;
import org.jbpm.formapi.shared.api.items.ComboBoxRepresentation;
import org.jbpm.formapi.shared.api.items.OptionRepresentation;

import junit.framework.TestCase;

/**
 * 
 */
public class RenderTranslatorAbstractTest extends TestCase {

    protected FormRepresentation createBasicForm() {
        FormRepresentation form = new FormRepresentation();
        form.setTaskId("taskNameXXX");
        ComboBoxRepresentation combo = new ComboBoxRepresentation();
        combo.setName("comboName");
        List<OptionRepresentation> options = new ArrayList<OptionRepresentation>();
        OptionRepresentation option1 = new OptionRepresentation();
        option1.setLabel("Label 1");
        option1.setValue("l1");
        OptionRepresentation option2 = new OptionRepresentation();
        option2.setLabel("Label 2");
        option2.setValue("l2");
        options.add(option1);
        options.add(option2);
        combo.setElements(options);
        CheckBoxRepresentation checkbox = new CheckBoxRepresentation();
        checkbox.setFormValue("S");
        checkbox.setName("checkboxName");
        form.addFormItem(combo);
        form.addFormItem(checkbox);
        return form;
    }
}
