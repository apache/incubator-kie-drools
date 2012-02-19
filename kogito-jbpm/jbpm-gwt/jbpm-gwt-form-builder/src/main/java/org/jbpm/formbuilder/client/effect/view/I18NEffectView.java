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

import java.util.HashMap;
import java.util.Map;

import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.effect.I18NFormEffect;
import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.client.resources.FormBuilderResources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class I18NEffectView extends PopupPanel {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final Grid grid = new Grid(2, 3);
    private final TextBox defaultText;
    private final I18NFormEffect effect;
    
    public I18NEffectView(I18NFormEffect formEffect) {
        this.effect = formEffect;
        defaultText = messageTextBox(effect.getItemI18nMap().get("default"));
        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(grid);
        populateGrid();
        HorizontalPanel buttonPanel = new HorizontalPanel();
        Button addLocaleButton = new Button(i18n.AddLocaleButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addLocaleToGrid("", "");
            }
        });
        Button doneButton = new Button(i18n.ConfirmButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Map<String, String> i18nMap = new HashMap<String, String>();
                i18nMap.put("default", defaultText.getValue());
                for (int row = 2; row < grid.getRowCount(); row++) {
                    TextBox keyBox = (TextBox) grid.getWidget(row, 0);
                    TextBox valueBox = (TextBox) grid.getWidget(row, 1);
                    i18nMap.put(keyBox.getValue(), valueBox.getValue());
                }
                effect.setItemI18NMap(i18nMap);
                effect.createStyles();
                hide();
            }
        });
        Button cancelButton = new Button(i18n.CancelButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        buttonPanel.add(addLocaleButton);
        buttonPanel.add(doneButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        mainPanel.add(buttonPanel);
        add(mainPanel);
    }
    
    private void populateGrid() {
        grid.setWidget(0, 0, new Label("default:"));
        grid.setWidget(0, 1, defaultText);
        grid.setWidget(1, 0, new Label(i18n.LocalesLabel()));
        grid.setWidget(1, 1, new Label(i18n.MessagesLabel()));
        for (Map.Entry<String, String> entry : effect.getItemI18nMap().entrySet()) {
            if (!"default".equals(entry.getKey())) {
                addLocaleToGrid(entry.getKey(), entry.getValue());
            }
        }
    }

    private void addLocaleToGrid(String localeName, String localeMessage) {
        grid.resizeRows(grid.getRowCount() + 1);
        int rowNumber = grid.getRowCount() - 1;
        grid.setWidget(rowNumber, 0, messageTextBox(localeName));
        grid.setWidget(rowNumber, 1, messageTextBox(localeMessage));
        grid.setWidget(rowNumber, 2, removeButton());
    }

    private Button removeButton() {
        Image img = new Image(FormBuilderResources.INSTANCE.removeSmallIcon());
        SafeHtmlBuilder builder = new SafeHtmlBuilder().appendHtmlConstant(img.toString());
        final Button removeButton = new Button(builder.toSafeHtml());
        ClickHandler handler = new ClickHandler() {
           @Override
           public void onClick(ClickEvent event) {
               int rowToRemove = -1;
               for (int rowNum = 2; rowNum < grid.getColumnCount(); rowNum++) {
                   Widget widget = grid.getWidget(rowNum, 2);
                   if (widget != null && widget == removeButton) {
                       rowToRemove = rowNum;
                       break;
                   }
               }
               if (rowToRemove > 0) {
                   grid.removeRow(rowToRemove);
               }
           }
        };
        removeButton.addClickHandler(handler);
        return removeButton;
    }
    
    private TextBox messageTextBox(String value) {
        TextBox textBox = new TextBox();
        if (value != null) {
            textBox.setValue(value);
        }
        return textBox;
    }
}
