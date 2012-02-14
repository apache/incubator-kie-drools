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

import java.util.List;

import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.client.menu.FBMenuItem;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.form.items.PasswordFieldFormItem;
import org.jbpm.formbuilder.client.resources.FormBuilderResources;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class PasswordFieldMenuItem extends FBMenuItem {

    public PasswordFieldMenuItem() {
        super();
    }
    
    public PasswordFieldMenuItem(List<FBFormEffect> formEffects) {
        super(formEffects);
    }

    @Override
    public Label getDescription() {
        return new HTML(FormBuilderGlobals.getInstance().getI18n().MenuItemPasswordField());
    }
    
    @Override
    protected ImageResource getIconUrl() {
        return FormBuilderResources.INSTANCE.passwordField();
    }

    @Override
    public FBMenuItem cloneWidget() {
        return clone(new PasswordFieldMenuItem());
    }
    
    @Override
    public FBFormItem buildWidget() {
        return build(new PasswordFieldFormItem());
    }
}
