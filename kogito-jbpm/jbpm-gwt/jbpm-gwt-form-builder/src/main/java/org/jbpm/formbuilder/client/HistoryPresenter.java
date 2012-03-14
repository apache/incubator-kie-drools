/*
 * Copyright 2012 JBoss Inc
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
package org.jbpm.formbuilder.client;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formbuilder.client.bus.ui.HistoryStoreEvent;
import org.jbpm.formbuilder.client.bus.ui.HistoryStoreHandler;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;

/**
 * TODO Use the HistoryStoreEvent to actually create history management. Ideas:
 * on saving a form: #formId=something on loading a form: #formId=something on
 * selecting a userTask: #process=processName #task=taskName
 */
public class HistoryPresenter {

    private EventBus bus = CommonGlobals.getInstance().getEventBus();

    public HistoryPresenter() {
        bus.addHandler(HistoryStoreEvent.TYPE, new HistoryStoreHandler() {
            public void onEvent(HistoryStoreEvent event) {
                if (event.getTokens() != null && !event.getTokens().isEmpty()) {
                    for (String token : event.getTokens()) {
                        History.newItem(token);
                    }
                }
                if (event.getValueChangeHandler() != null) {
                    History.addValueChangeHandler(event.getValueChangeHandler());
                }
            }
        });
    }
}
