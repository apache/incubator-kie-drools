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
package org.jbpm.formbuilder.client.effect;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.client.form.I18NFormItem;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.bus.UndoableEvent;
import org.jbpm.formbuilder.client.bus.UndoableHandler;
import org.jbpm.formbuilder.client.effect.view.L10NEffectView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.PopupPanel;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class L10NFormEffect extends FBFormEffect {

    private EventBus bus = CommonGlobals.getInstance().getEventBus();
    private String savedFormat = null;
    
    public L10NFormEffect() {
        super(FormBuilderGlobals.getInstance().getI18n().ApplyLocaleFormattingLabel(), true);
    }
    
    @Override
    public void createStyles() {
        I18NFormItem item = (I18NFormItem) getItem();
        Map<String, Object> dataSnapshot = new HashMap<String, Object>();
        dataSnapshot.put("item", item);
        dataSnapshot.put("oldFormat", item.getFormat());
        dataSnapshot.put("newFormat", I18NFormItem.Format.valueOf(savedFormat));
        bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
            @Override
            public void undoAction(UndoableEvent event) {
                I18NFormItem item = (I18NFormItem) event.getData("item");
                I18NFormItem.Format format = (I18NFormItem.Format) event.getData("oldFormat");
                item.setFormat(format);
            }
            @Override
            public void onEvent(UndoableEvent event) { }
            @Override
            public void doAction(UndoableEvent event) {
                I18NFormItem item = (I18NFormItem) event.getData("item");
                I18NFormItem.Format format = (I18NFormItem.Format) event.getData("newFormat");
                item.setFormat(format);
            }
        }));
    }

    @Override
    public PopupPanel createPanel() {
        return new L10NEffectView(this);
    }
    
    @Override
    public boolean isValidForItem(FBFormItem item) {
        return super.isValidForItem(item) && item instanceof I18NFormItem;
    }
    
    public String getSelectedFormat() {
        I18NFormItem item = (I18NFormItem) getItem();
        I18NFormItem.Format format = item.getFormat();
        return format == null ? null : format.toString();
    }
    
    public void setSelectedFormat(String format) {
        this.savedFormat = format;
    }
}
