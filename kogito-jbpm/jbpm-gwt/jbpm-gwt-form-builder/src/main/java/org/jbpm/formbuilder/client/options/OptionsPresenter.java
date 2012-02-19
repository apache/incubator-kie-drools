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
package org.jbpm.formbuilder.client.options;

import java.util.List;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formbuilder.client.bus.MenuOptionAddedEvent;
import org.jbpm.formbuilder.client.bus.MenuOptionAddedHandler;
import org.jbpm.formbuilder.client.bus.ui.EmbededIOReferenceEvent;
import org.jbpm.formbuilder.client.bus.ui.EmbededIOReferenceHandler;
import org.jbpm.formbuilder.client.command.BaseCommand;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Options presenter. Hears valid options from
 * the server and populates the view
 */
public class OptionsPresenter implements OptionsView.Presenter {

    private final OptionsView view;
    private final EventBus bus;
    
    public OptionsPresenter(OptionsView optionsView) {
        this.view = optionsView;
        this.bus = CommonGlobals.getInstance().getEventBus();
        
        bus.addHandler(MenuOptionAddedEvent.TYPE, new MenuOptionAddedHandler() {
            @Override
            public void onEvent(MenuOptionAddedEvent event) {
                view.addItem(event.getOption());
            }
        });
        bus.addHandler(EmbededIOReferenceEvent.TYPE, new EmbededIOReferenceHandler() {
            @Override
            public void onEvent(EmbededIOReferenceEvent event) {
                List<MenuItem> items = view.getItems();
                for (MenuItem item : items) {
                    Command cmd = item.getCommand();
                    if (cmd != null && cmd instanceof BaseCommand) {
                        BaseCommand baseCmd = (BaseCommand) cmd;
                        baseCmd.setEmbeded(event.getProfileName());
                    }
                }
            }
        });
    }
}
