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
package org.jbpm.formbuilder.client.menu.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.client.menu.FBMenuItem;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.menu.MenuItemDescription;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.resources.FormBuilderResources;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * This class is used to represent an error when transforming {@link MenuItemDescription}s
 * from the server.
 */
@Reflectable
public class ErrorMenuItem extends FBMenuItem {

    private final String errMsg;
    
    public ErrorMenuItem(String errMsg) {
        super(new ArrayList<FBFormEffect>());
        this.errMsg = errMsg;
    }
    
    @Override
    public FBMenuItem cloneWidget() {
        return new ErrorMenuItem(this.errMsg);
    }

    @Override
    protected ImageResource getIconUrl() {
        return FormBuilderResources.INSTANCE.errorIcon();
    }

    @Override
    public Label getDescription() {
        return new Label(FormBuilderGlobals.getInstance().getI18n().Error(errMsg));
    }

    @Override
    public FBFormItem buildWidget() {
        return new FBFormItem(new ArrayList<FBFormEffect>()) {
            @Override
            public void saveValues(Map<String, Object> asPropertiesMap) {
            }
            
            @Override
            public Map<String, Object> getFormItemPropertiesMap() {
                return new HashMap<String, Object>();
            }
            
            @Override
            public FormItemRepresentation getRepresentation() {
                return null;
            }
            
            @Override
            public void populate(FormItemRepresentation rep) {
            }
            
            @Override
            public FBFormItem cloneItem() {
                return null;
            }
            
            @Override
            public Widget cloneDisplay(Map<String, Object> data) {
                return null;
            }
        };
    }
}
