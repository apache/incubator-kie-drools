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
package org.jbpm.formbuilder.client.effect.view;

import org.jbpm.formapi.shared.api.FBScriptHelper;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScriptHelperListPanel extends VerticalPanel {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    
    public interface ScriptOrderHandler {
        void onRemove(int index);
        void onMoveUp(int index);
        void onMoveDown(int index);
    }
    
    public void addScriptHelper(FBScriptHelper helper, ScriptOrderHandler handler) {
        Widget editor = helper.draw();
        if (editor == null) {
            editor = new Label(i18n.ScriptHelperNullEditor());
        }
        final HorizontalPanel panel = new HorizontalPanel();
        String number = String.valueOf(getWidgetCount() + 1);
        panel.add(new Label(number));
        panel.add(editor);
        VerticalPanel buttons = new VerticalPanel();
        panel.add(buttons);
        buttons.add(createRemoveButton(panel, handler));
        buttons.add(createMoveUpButton(panel, handler));
        buttons.add(createMoveDownButton(panel, handler));
        add(panel);
    }

    private Button createMoveDownButton(final HorizontalPanel panel, final ScriptOrderHandler handler) {
        return new Button(i18n.MoveDownButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                int index = getWidgetIndex(panel);
                if (index + 1 < getWidgetCount()) {
                    remove(panel);
                    insert(panel, index + 1);
                    handler.onMoveDown(index);
                    renumber();
                }
            }
        });
    }

    private Button createMoveUpButton(final HorizontalPanel panel, final ScriptOrderHandler handler) {
        return new Button(i18n.MoveUpButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                int index = getWidgetIndex(panel);
                if (index -1 >= 0) {
                    remove(panel);
                    insert(panel, index - 1);
                    handler.onMoveUp(index);
                    renumber();
                }
            }
        });
    }

    private Button createRemoveButton(final HorizontalPanel panel, final ScriptOrderHandler handler) {
        return new Button(i18n.RemoveButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                int index = getWidgetIndex(panel);
                remove(panel);
                handler.onRemove(index);
                renumber();
            }
        });
    }
    
    private void renumber() {
        for (Widget widget : this) {
            HorizontalPanel panel = (HorizontalPanel) widget;
            int index = getWidgetIndex(panel) + 1;
            String number = String.valueOf(index);
            panel.remove(0);
            panel.insert(new Label(number), 0);
        }
         
    }
}
