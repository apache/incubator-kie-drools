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
package org.jbpm.formapi.client.menu;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.common.panels.CommandPopupPanel;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Effects popup. Shows all valid effects for a given
 * {@link FBFormItem}, once the user click the right
 * button over it.
 */
public class EffectsPopupPanel extends CommandPopupPanel {

    private List<FBFormEffect> effects = new ArrayList<FBFormEffect>();
    
    public EffectsPopupPanel(final FBFormItem item, boolean autoHide) {
        super(autoHide);
        this.effects = item.getFormEffects();
        for (final FBFormEffect effect : effects) {
            if (effect.isValidForItem(item)) {
                addItem(effect.getName(), 
                new Command() {
                    @Override
                    public void execute() {
                        effect.apply(item, EffectsPopupPanel.this);
                        PopupPanel popup = effect.createPanel();
                        if (popup != null) {
                            popup.setPopupPosition(getPopupLeft(), getPopupTop() + 30);
                            popup.show();
                            hide();
                        } else {
                            hide();
                        }
                    }
                });
            }
        }
    }
}
