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
import org.jbpm.formbuilder.client.effect.view.I18NEffectView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.PopupPanel;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class I18NFormEffect extends FBFormEffect {

    private EventBus bus = CommonGlobals.getInstance().getEventBus();
    private Map<String, String> savedMap = null;
    
    public I18NFormEffect() {
        super(FormBuilderGlobals.getInstance().getI18n().InternationalizeEffectLabel(), true);
    }
    
    @Override
    public void createStyles() {
        I18NFormItem item = (I18NFormItem) getItem();
        Map<String, Object> dataSnapshot = new HashMap<String, Object>();
        dataSnapshot.put("item", item);
        dataSnapshot.put("oldI18nMap", item.getI18nMap());
        dataSnapshot.put("newI18nMap", savedMap);
        bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
            @Override @SuppressWarnings("unchecked")
            public void undoAction(UndoableEvent event) {
                I18NFormItem item = (I18NFormItem) event.getData("item");
                Map<String, String> i18nMap = (Map<String, String>) event.getData("oldI18nMap");
                item.saveI18nMap(i18nMap);
            }
            @Override
            public void onEvent(UndoableEvent event) { }
            @Override @SuppressWarnings("unchecked")
            public void doAction(UndoableEvent event) {
                I18NFormItem item = (I18NFormItem) event.getData("item");
                Map<String, String> i18nMap = (Map<String, String>) event.getData("newI18nMap");
                item.saveI18nMap(i18nMap);
            }
        }));
    }

    @Override
    public PopupPanel createPanel() {
        return new I18NEffectView(this);
    }

    @Override
    public boolean isValidForItem(FBFormItem item) {
        return super.isValidForItem(item) && item instanceof I18NFormItem;
    }
    
    public Map<String, String> getItemI18nMap() {
        I18NFormItem item = (I18NFormItem) getItem();
        Map<String, String> map = new HashMap<String, String>();
        map.putAll(item.getI18nMap());
        return map;
    }
    
    public void setItemI18NMap(Map<String, String> i18nMap) {
        this.savedMap = i18nMap;
    }
}
