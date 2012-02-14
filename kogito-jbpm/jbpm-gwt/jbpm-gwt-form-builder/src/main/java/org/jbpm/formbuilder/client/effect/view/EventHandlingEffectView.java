/*
k * Copyright 2011 JBoss Inc 
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.bus.ui.NotificationEvent;
import org.jbpm.formapi.client.bus.ui.NotificationEvent.Level;
import org.jbpm.formapi.common.reflect.ReflectionHelper;
import org.jbpm.formapi.shared.api.FBScript;
import org.jbpm.formapi.shared.api.FBScriptHelper;
import org.jbpm.formapi.shared.api.RepresentationFactory;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.effect.EventHandlingFormEffect;
import org.jbpm.formbuilder.client.effect.scripthandlers.PlainTextScriptHelper;
import org.jbpm.formbuilder.client.effect.view.ScriptHelperListPanel.ScriptOrderHandler;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventHandlingEffectView extends PopupPanel {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    
    private final EventHandlingFormEffect effect;
    private Map<String, FBScript> eventActions = new HashMap<String, FBScript>();
    
    private final VerticalPanel mainPanel = new VerticalPanel();
    private final ListBox eventSelectionCombo = new ListBox();
    private final ListBox helperSelectionCombo = new ListBox();
    
    private final Button addHelperButton = new Button("Add");
    
    public EventHandlingEffectView(EventHandlingFormEffect formEffect) {
        this.effect = formEffect;
        populateEventSelectionCombo();
        populateScriptHelpers();
        mainPanel.add(createEventPanel());
        mainPanel.add(new Label(i18n.LoadingLabel()));
        mainPanel.add(createButtonsPanel());
        startScriptPanel();
        add(mainPanel);
    }

    private void startScriptPanel() {
        String initialEventName = eventSelectionCombo.getValue(0);
        FBScript initialScript = eventActions.get(initialEventName);
        populateScriptHelperView(initialScript);
    }

    private HorizontalPanel createButtonsPanel() {
        HorizontalPanel buttonsPanel = new HorizontalPanel();
        buttonsPanel.add(createSaveContinueButton());
        buttonsPanel.add(createConfirmButton());
        buttonsPanel.add(createCancelButton());
        return buttonsPanel;
    }

    private Button createCancelButton() {
        Button cancelButton = new Button(i18n.CancelButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        return cancelButton;
    }

    private Button createConfirmButton() {
        Button confirmButton = new Button(i18n.ConfirmButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                int selectedIndex = eventSelectionCombo.getSelectedIndex();
                String evtName = eventSelectionCombo.getValue(selectedIndex);
                FBScript fbScript = eventActions.get(evtName);
                if (fbScript == null) {
                    fbScript = new FBScript();
                    eventActions.put(evtName, fbScript);
                }
                List<FBScriptHelper> helpers = fbScript.getHelpers();
                effect.confirmEventAction(evtName, toScript(helpers));
                hide();
            }
        });
        return confirmButton;
    }

    private Button createSaveContinueButton() {
        Button saveContinueButton = new Button(i18n.SaveChangesButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                int selectedIndex = eventSelectionCombo.getSelectedIndex();
                String evtName = eventSelectionCombo.getValue(selectedIndex);
                FBScript fbScript = eventActions.get(evtName);
                if (fbScript == null) {
                    fbScript = new FBScript();
                    eventActions.put(evtName, fbScript);
                }
                List<FBScriptHelper> helpers = fbScript.getHelpers();
                effect.storeEventAction(evtName, toScript(helpers));
            }
        });
        return saveContinueButton;
    }
    
    private FBScript toScript(List<FBScriptHelper> helpers) {
        FBScript script = new FBScript();
        if (helpers != null && !helpers.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (FBScriptHelper helper : helpers) {
                sb.append(helper.asScriptContent());
            }
            script.setContent(sb.toString());
            script.setHelpers(helpers);
        }
        script.setType("text/javascript");
        return script;
    }

    private Grid createEventPanel() {
        Grid eventPanel = new Grid(3, 3);
        eventPanel.setWidget(0, 0, new Label("Event:"));
        eventPanel.setWidget(0, 1, eventSelectionCombo);
        eventPanel.setWidget(1, 0, new Label("Editor:"));
        eventPanel.setWidget(1, 1, helperSelectionCombo);
        eventPanel.setWidget(1, 2, addHelperButton);
        eventPanel.setWidget(2, 0, new Label("Type:"));
        eventPanel.setWidget(2, 1, new Label("text/javascript"));
        return eventPanel;
    }
    
    private void populateScriptHelpers() { 
        String classesString = RepresentationFactory.getItemClassName("form.builder.scriptHelpers");
        final Map<String, String> helpersAvailable = new HashMap<String, String>();
        if (classesString != null) {
            String[] classesNames = classesString.split(",");
            for (String className : classesNames) {
                try {
                    Object obj = ReflectionHelper.newInstance(className);
                    if (obj instanceof FBScriptHelper) {
                        FBScriptHelper helper = (FBScriptHelper) obj;
                        helpersAvailable.put(helper.getName(), className);
                    }
                } catch (Exception e) {
                    bus.fireEvent(new NotificationEvent(Level.ERROR, "Problem loading script helper " + className, e));
                }
            }
        }
        for (Map.Entry<String, String> entry : helpersAvailable.entrySet()) {
            helperSelectionCombo.addItem(entry.getKey());
        }
        addHelperButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                String helperName = helperSelectionCombo.getValue(helperSelectionCombo.getSelectedIndex());
                String eventName = eventSelectionCombo.getValue(eventSelectionCombo.getSelectedIndex());
                String helperClassName = helpersAvailable.get(helperName);
                try {
                    FBScriptHelper helper = (FBScriptHelper) ReflectionHelper.newInstance(helperClassName);
                    FBScript fbScript = eventActions.get(eventName);
                    if (fbScript == null) {
                        fbScript = new FBScript();
                        eventActions.put(eventName, fbScript);
                    }
                    List<FBScriptHelper> helpers = getHelpersForEvent(fbScript);
                    helpers.add(helper);
                    ScriptHelperListPanel editors = new ScriptHelperListPanel();
                    for (FBScriptHelper helper2 : helpers) {
                        editors.addScriptHelper(helper2, newScriptOrderHandler(fbScript));
                    }
                    mainPanel.remove(1);
                    mainPanel.insert(editors, 1);
                } catch (Exception e) {
                    bus.fireEvent(new NotificationEvent(Level.ERROR, "Problem starting script helper " + helperClassName, e));
                }
            }
        });
        for (Map.Entry<String, FBScript> entry : this.eventActions.entrySet()) {
            FBScript script = entry.getValue();
            List<FBScriptHelper> helpers = script == null ? new ArrayList<FBScriptHelper>() : script.getHelpers();
            String key = entry.getKey();
            FBScript fbScript = this.eventActions.get(key);
            if (fbScript == null) {
                fbScript = new FBScript();
                eventActions.put(key, fbScript);
            }
            fbScript.setHelpers(helpers);
        }
    }
    
    private void populateEventSelectionCombo() {
        List<String> possibleEvents = this.effect.getPossibleEvents();
        if (possibleEvents != null) {
            for (String eventName : possibleEvents) {
                eventSelectionCombo.addItem(eventName);
            }
        }
        Map<String, FBScript> actions = this.effect.getItemActions();
        if (actions != null) {
            this.eventActions.putAll(actions);
        }
        eventSelectionCombo.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                int selectedIndex = eventSelectionCombo.getSelectedIndex();
                String eventName = eventSelectionCombo.getValue(selectedIndex);
                FBScript script = eventActions.get(eventName);
                if (script == null) {
                    script = new FBScript();
                    eventActions.put(eventName, script);
                }
                populateScriptHelperView(script);
            }
        });
    }

    private ScriptOrderHandler newScriptOrderHandler(final FBScript script) {
        return new ScriptOrderHandler() {
            @Override
            public void onRemove(int index) {
                if (script != null && script.getHelpers() != null) {
                    if (script.getHelpers().size() > index) {
                        script.getHelpers().remove(index);
                    }
                }
            }
            @Override
            public void onMoveUp(int index) {
                if (script != null && script.getHelpers() != null) {
                    if (script.getHelpers().size() > index + 1) {
                        List<FBScriptHelper> helpers = script.getHelpers();
                        FBScriptHelper helper = helpers.remove(index);
                        helpers.add(index + 1, helper);
                        script.setHelpers(helpers);
                    }
                }
            }
            @Override
            public void onMoveDown(int index) {
                if (script != null && script.getHelpers() != null) {
                    if (index > 0) {
                        List<FBScriptHelper> helpers = script.getHelpers();
                        FBScriptHelper helper = helpers.remove(index);
                        helpers.add(index - 1, helper);
                        script.setHelpers(helpers);
                    }
                }
            }
        };
    }

    private void populateScriptHelperView(FBScript script) {
        List<FBScriptHelper> helpers = getHelpersForEvent(script);
        ScriptHelperListPanel editorPanel = new ScriptHelperListPanel();
        for (FBScriptHelper helper : helpers) { 
            editorPanel.addScriptHelper(helper, newScriptOrderHandler(script));
        }
        mainPanel.remove(1);
        mainPanel.insert(editorPanel, 1);
    }
    
    private List<FBScriptHelper> getHelpersForEvent(FBScript script) {
        List<FBScriptHelper> helpers = null;
        if (script != null) {
            helpers = script.getHelpers();
        }
        if (helpers == null) {
            helpers = new ArrayList<FBScriptHelper>();
            FBScriptHelper helper = new PlainTextScriptHelper();
            helpers.add(helper);
            helper.setScript(script);
            script.setHelpers(helpers);
        }
        return helpers;
    }
}
