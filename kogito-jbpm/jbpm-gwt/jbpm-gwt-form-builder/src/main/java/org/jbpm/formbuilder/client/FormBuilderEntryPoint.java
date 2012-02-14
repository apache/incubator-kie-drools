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

import com.google.gwt.user.client.ui.RootPanel;

/**
 * Main entry point of the form builder application
 */
public class FormBuilderEntryPoint extends FBBaseEntryPoint {

    /**
     * Creates a FormBuilderView instance
     * Creates a FormBuilderController instance that adds all to the main view
     */
    @Override
    public void loadModule() {
        //start view and controller
        RootPanel rootPanel = RootPanel.get("formBuilder");
        FormBuilderView view = new FormBuilderView();
        new FormBuilderController(rootPanel, view);
    }
}
