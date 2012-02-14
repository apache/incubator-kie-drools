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
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.bus.UndoableEvent;
import org.jbpm.formbuilder.client.bus.UndoableHandler;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * Allows to set alignment
 */
@Reflectable
public class HorizontalAlignmentFormEffect extends FBFormEffect {

    private ListBox alignmentBox = new ListBox();
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    
    public HorizontalAlignmentFormEffect() {
        super(FormBuilderGlobals.getInstance().getI18n().HorizontalAlignment(), true);
        alignmentBox.addItem(i18n.AlignLeft(), i18n.AlignLeft());
        alignmentBox.addItem(i18n.AlignRight(), i18n.AlignRight());
        alignmentBox.addItem(i18n.AlignCenter(), i18n.AlignCenter());
        alignmentBox.addItem(i18n.AlignJustify(), i18n.AlignJustify());
    }
    
    @Override
    protected void createStyles() {
        int index = this.alignmentBox.getSelectedIndex();
        String value = this.alignmentBox.getValue(index);
        Widget widget = getWidget();
        if (widget instanceof HasHorizontalAlignment) {
            HasHorizontalAlignment hw = (HasHorizontalAlignment) widget;
            HorizontalAlignmentConstant align = null;
            if (i18n.AlignLeft().equals(value)) {
                align = HasHorizontalAlignment.ALIGN_LEFT;
            } else if (i18n.AlignRight().equals(value)) {
                align = HasHorizontalAlignment.ALIGN_RIGHT;
            } else if (i18n.AlignCenter().equals(value)) {
                align = HasHorizontalAlignment.ALIGN_CENTER;
            } else if (i18n.AlignJustify().equals(value)) {
                align = HasHorizontalAlignment.ALIGN_JUSTIFY;
            }
            Map<String, Object> dataSnapshot = new HashMap<String, Object>();
            dataSnapshot.put("oldAlignment", hw.getHorizontalAlignment());
            dataSnapshot.put("newAlignment", align);
            dataSnapshot.put("hwidget", hw);
            bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
                @Override
                public void onEvent(UndoableEvent event) {  }
                @Override
                public void undoAction(UndoableEvent event) {
                    HorizontalAlignmentConstant oldAlignment = (HorizontalAlignmentConstant) event.getData("oldAlignment");
                    HasHorizontalAlignment hwidget = (HasHorizontalAlignment) event.getData("hwidget");
                    hwidget.setHorizontalAlignment(oldAlignment);
                }
                @Override
                public void doAction(UndoableEvent event) {
                    HorizontalAlignmentConstant newAlignment = (HorizontalAlignmentConstant) event.getData("newAlignment");
                    HasHorizontalAlignment hwidget = (HasHorizontalAlignment) event.getData("hwidget");
                    hwidget.setHorizontalAlignment(newAlignment);
                }
            }));
        }
    }
    
    @Override
    public void remove(FBFormItem item) {
        super.remove(item);
        Widget widget = getWidget();
        if (widget instanceof HasHorizontalAlignment) {
            HasHorizontalAlignment hw = (HasHorizontalAlignment) widget;
            hw.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_DEFAULT);
        }
    }
    
    @Override
    public boolean isValidForItem(FBFormItem item) {
        return item.getWidget() != null && item.getWidget() instanceof HasHorizontalAlignment; 
    }
    
    @Override
    public PopupPanel createPanel() {
        final PopupPanel panel = new PopupPanel();
        panel.setSize("300px", "200px");
        VerticalPanel vPanel = new VerticalPanel();
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.add(new Label(i18n.Alignment()));
        alignmentBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                HorizontalAlignmentFormEffect.this.createStyles();
                panel.hide();
            };
        });
        hPanel.add(alignmentBox);
        Button fontSizeButton = new Button(i18n.ConfirmButton());
        fontSizeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                HorizontalAlignmentFormEffect.this.createStyles();
                panel.hide();
            }
        });
        vPanel.add(alignmentBox);
        vPanel.add(fontSizeButton);
        panel.add(vPanel);
        return panel;
    }
}
