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

import java.util.ArrayList;
import java.util.List;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.bus.ui.NotificationEvent;
import org.jbpm.formapi.client.bus.ui.NotificationEvent.Level;
import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.client.validation.FBValidationItem;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.FormBuilderService;
import org.jbpm.formbuilder.client.bus.ExistingValidationsResponseEvent;
import org.jbpm.formbuilder.client.bus.ExistingValidationsResponseHandler;
import org.jbpm.formbuilder.client.bus.ui.ValidationSavedEvent;
import org.jbpm.formbuilder.client.bus.ui.ValidationSavedHandler;
import org.jbpm.formbuilder.client.effect.view.ValidationsEffectView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.PopupPanel;
import com.gwtent.reflection.client.Reflectable;

/**
 * Allows to add validations to the related {@link FBFormItem}
 */
@Reflectable
public class ValidationsEffect extends FBFormEffect {

    private EventBus bus = CommonGlobals.getInstance().getEventBus();
    private FormBuilderService server = FormBuilderGlobals.getInstance().getService();
    
    private List<FBValidationItem> availableValidations = new ArrayList<FBValidationItem>();
    private List<FBValidationItem> currentValidations = new ArrayList<FBValidationItem>();
    
    private final ValidationsEffectView effectView = new ValidationsEffectView();
    
    public ValidationsEffect() {
        super(FormBuilderGlobals.getInstance().getI18n().ValidationsEffectLabel(), true);
        bus.addHandler(ValidationSavedEvent.TYPE, new ValidationSavedHandler() {
            @Override
            public void onEvent(ValidationSavedEvent event) {
                currentValidations.clear();
                currentValidations.addAll(event.getValidations());
                createStyles();
            }
        });
        bus.addHandler(ExistingValidationsResponseEvent.TYPE, new ExistingValidationsResponseHandler() {
            @Override
            public void onEvent(ExistingValidationsResponseEvent event) {
                availableValidations.clear();
                availableValidations.addAll(event.getExistingValidations());
                effectView.setAvailableValidations(availableValidations);
            }
        });
    }

    @Override
    protected void createStyles() {
        getItem().setValidations(currentValidations);
    }
    
    @Override
    public PopupPanel createPanel() {
        PopupPanel popup = new PopupPanel();
        popup.setWidget(this.effectView);
        this.effectView.setParentPopup(popup);
        try {
            server.getExistingValidations();
        } catch (FormBuilderException e) {
            bus.fireEvent(new NotificationEvent(Level.WARN, 
                FormBuilderGlobals.getInstance().getI18n().CouldntConnectServer(), e));
        }
        return popup;
    }
}
