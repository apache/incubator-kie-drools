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
package org.jbpm.formbuilder.client.form.items;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.common.reflect.ReflectionHelper;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.RestyFormBuilderModel;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.Widget;

public class FormItemsGwtTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.jbpm.formbuilder.FormBuilder";
    }

    public void testItems() throws Exception {
        FormBuilderGlobals.getInstance().registerI18n((I18NConstants) GWT.create(I18NConstants.class));
        CommonGlobals.getInstance().registerEventBus(new SimpleEventBus());
        FormBuilderGlobals.getInstance().registerService(new RestyFormBuilderModel("rest"));
        
        testBasicItem(AbsoluteLayoutFormItem.class);
        testBasicItem(BorderLayoutFormItem.class);
        testBasicItem(CalendarFormItem.class);
        testBasicItem(CheckBoxFormItem.class);
        testBasicItem(ClientScriptFormItem.class);
        testBasicItem(ComboBoxFormItem.class);
        testBasicItem(CompleteButtonFormItem.class);
        testBasicItem(ConditionalBlockFormItem.class);
        testBasicItem(CSSLayoutFormItem.class);
        testBasicItem(FileInputFormItem.class);
        testBasicItem(FlowLayoutFormItem.class);
        testBasicItem(HeaderFormItem.class);
        testBasicItem(HiddenFormItem.class);
        testBasicItem(HorizontalLayoutFormItem.class);
        testBasicItem(HTMLFormItem.class);
        testBasicItem(ImageFormItem.class);
        testBasicItem(ImageRolodexFormItem.class);
        testBasicItem(LabelFormItem.class);
        //testBasicItem(LineGraphFormItem.class);
        testBasicItem(LoopBlockFormItem.class);
        testBasicItem(PasswordFieldFormItem.class);
        testBasicItem(RadioButtonFormItem.class);
        testBasicItem(RichTextEditorFormItem.class);
        //testBasicItem(ServerTransformationFormItem.class);
        testBasicItem(TabbedLayoutFormItem.class);
        testBasicItem(TableLayoutFormItem.class);
        testBasicItem(TextAreaFormItem.class);
        testBasicItem(TextFieldFormItem.class);
        testBasicItem(UploadWithProgressBarFormItem.class);
        
    }

    private <T extends FBFormItem> void testBasicItem(Class<T> clazz) throws Exception {
        @SuppressWarnings("unchecked")
        T actualItem = (T) ReflectionHelper.newInstance(clazz.getName());
        Map<String, Object> props = actualItem.getFormItemPropertiesMap();
        actualItem.saveValues(props);
        @SuppressWarnings("unchecked")
        T cloneItem = (T) actualItem.cloneItem();
        Widget cloneWidget = actualItem.cloneDisplay(new HashMap<String, Object>());
        Widget actualWidget = actualItem.getWidget();
        assertNotSame(cloneWidget, actualWidget);
        assertNotSame(cloneItem, actualItem);
    }
}
