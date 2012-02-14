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
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.common.panels.ConfirmDialog;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.bus.UndoableEvent;
import org.jbpm.formbuilder.client.bus.UndoableHandler;
import org.jbpm.formbuilder.client.form.items.TableLayoutFormItem;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.PopupPanel;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class RemoveColumnFormEffect extends FBFormEffect {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    
    public RemoveColumnFormEffect() {
        super(FormBuilderGlobals.getInstance().getI18n().RemoveColumnEffectLabel(), true);
    }
    
    @Override
    protected void createStyles() {
        final Map<String, Object> dataSnapshot = new HashMap<String, Object>();
        dataSnapshot.put("selectedX", getParent().getAbsoluteLeft());
        dataSnapshot.put("item", getItem());
        bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
            @Override
            @SuppressWarnings("unchecked")
            public void undoAction(UndoableEvent event) {
                TableLayoutFormItem item = (TableLayoutFormItem) event.getData("item");
                Integer selectedX = (Integer) event.getData("selectedX");
                List<FBFormItem> deletedColumn = (List<FBFormItem>) event.getData("deletedColumn");
                int colNumber = item.getColumnForXCoordinate(selectedX);
                item.addColumn(colNumber);
                item.insertColumnElements(colNumber, deletedColumn);
            }
            @Override
            public void onEvent(UndoableEvent event) { }
            @Override
            public void doAction(UndoableEvent event) {
                TableLayoutFormItem item = (TableLayoutFormItem) event.getData("item");
                Integer selectedX = (Integer) event.getData("selectedX");
                int colNumber = item.getColumnForXCoordinate(selectedX);
                List<FBFormItem> deletedColumn = item.removeColumn(colNumber);
                event.setData("deletedColumn", deletedColumn);
            }
        }));
    }

    @Override
    public boolean isValidForItem(FBFormItem item) {
        return super.isValidForItem(item) && item instanceof TableLayoutFormItem;
    }

    @Override
    public PopupPanel createPanel() {
        ConfirmDialog dialog = new ConfirmDialog(i18n.RemoveColumnWarning());
        dialog.addOkButtonHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                createStyles();
            }
        });
        return dialog;
    }
}
