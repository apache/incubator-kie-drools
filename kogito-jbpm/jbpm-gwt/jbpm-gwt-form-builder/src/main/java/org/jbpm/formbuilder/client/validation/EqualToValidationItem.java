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
package org.jbpm.formbuilder.client.validation;

import java.util.Map;

import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.validation.FBValidationItem;
import org.jbpm.formapi.shared.api.FBValidation;
import org.jbpm.formapi.shared.api.validation.EqualToValidation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class EqualToValidationItem extends FBValidationItem {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    
    @Override
    public String getName() {
        return i18n.EqualToValidationName();
    }

    @Override
    public FBValidation createValidation() {
        return getRepresentation(new EqualToValidation());
    }

    @Override
    public Widget createDisplay() {
        return null;
    }

    @Override
    public FBValidationItem cloneItem() {
        EqualToValidationItem item = new EqualToValidationItem();
        item.populatePropertiesMap(getPropertiesMap());
        return item;
    }

    @Override
    public void populate(FBValidation validation) throws FormBuilderException {
        if (!(validation instanceof EqualToValidation)) {
            throw new FormBuilderException(i18n.RepNotOfType(validation.getClass().getName(), "EqualToValidation"));
        }
        TextBox valueTextBox = new TextBox();
        if (validation.getDataMap().get("value") != null) {
            valueTextBox.setValue(validation.getDataMap().get("value").toString());
        }
        super.getPropertiesMap().put("value", valueTextBox);
    }

    @Override
    public Map<String, HasValue<String>> getPropertiesMap() {
        Map<String, HasValue<String>> map = super.getPropertiesMap();
        if (!map.containsKey("errorMessage")) {
            map.put("errorMessage", new TextBox());
        }
        if (!map.containsKey("value")) {
            map.put("value", new TextBox());
        }
        return map;
    }
}
