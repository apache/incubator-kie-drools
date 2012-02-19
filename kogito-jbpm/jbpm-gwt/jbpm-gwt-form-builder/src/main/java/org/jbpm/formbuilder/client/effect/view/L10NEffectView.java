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

import org.jbpm.formapi.client.form.I18NFormItem;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.effect.L10NFormEffect;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class L10NEffectView extends PopupPanel {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final L10NFormEffect effect;
    private final ListBox formatBox = new ListBox();
    
    public L10NEffectView(L10NFormEffect formEffect) {
        this.effect = formEffect;
        VerticalPanel mainPanel = new VerticalPanel();
        HorizontalPanel controlPanel = new HorizontalPanel();
        controlPanel.add(new Label(i18n.FormatAsLabel()));
        formatBox.addItem("...", "");
        formatBox.addItem(i18n.CurrencyFormatLabel(), I18NFormItem.Format.CURRENCY.toString());
        formatBox.addItem(i18n.NumberFormatLabel(), I18NFormItem.Format.NUMBER.toString());
        formatBox.addItem(i18n.DateFormatLabel(), I18NFormItem.Format.DATE.toString());
        formatBox.addItem(i18n.PercentFormatLabel(), I18NFormItem.Format.PERCENT.toString());
        formatBox.addItem(i18n.IntegerFormatLabel(), I18NFormItem.Format.INTEGER.toString());
        String selectedFormat = this.effect.getSelectedFormat();
        if (selectedFormat != null) {
            int selectedIndex = 0;
            for (int index = 0; index < formatBox.getItemCount(); index++) {
                if (formatBox.getValue(index).equals(selectedFormat)) {
                    selectedIndex = index;
                    break;
                }
            }
            formatBox.setSelectedIndex(selectedIndex);
        }
        controlPanel.add(formatBox);
        mainPanel.add(controlPanel);
        HorizontalPanel buttonsPanel = new HorizontalPanel();
        buttonsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        buttonsPanel.add(new Button(i18n.ConfirmButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                int selectedIndex = formatBox.getSelectedIndex();
                String selectedFormat = formatBox.getValue(selectedIndex);
                effect.setSelectedFormat(selectedFormat);
                effect.createStyles();
                hide();
            }
        }));
        buttonsPanel.add(new Button(i18n.CancelButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        }));
        mainPanel.add(buttonsPanel);
        setWidget(mainPanel);
    }
}
