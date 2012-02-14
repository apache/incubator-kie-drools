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
package org.jbpm.formbuilder.client;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;

/**
 * 
 */
public abstract class FBBaseEntryPoint implements EntryPoint {

    /**
     * Does the following steps to start the app:
     * 1 - Registers an event bus
     * 2-  Registers i18n modules
     * 3 - Starts a client service resolver and registers it
     * 4 - Delegates to subclass what to do next
     */
    @Override
    public final void onModuleLoad() {
        //register event bus
        CommonGlobals.getInstance().registerEventBus(new SimpleEventBus());
        //register i18n module
        I18NConstants constants = GWT.create(I18NConstants.class);
        FormBuilderGlobals.getInstance().registerI18n(constants);
        //start model
        RestyFormBuilderModel server = new RestyFormBuilderModel("rest");
        FormBuilderGlobals.getInstance().registerService(server);
        loadModule();
    }

    protected abstract void loadModule(); 
}
