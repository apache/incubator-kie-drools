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

import org.jbpm.formbuilder.client.messages.I18NConstants;
import org.jbpm.formbuilder.client.resources.FormBuilderResources;

/**
 * Base singleton to obtain global variables, like service callers and event buses
 */
public class FormBuilderGlobals {

    public static final String FORM_PANEL_KEY = "org.jbpm.formbuilder.FormBuilder.FORM_PANEL";
    public static final String BASE_LOCALE = "org.jbpm.formbuilder.server.render.Renderer.BASE_LOCALE";
    
    private static final FormBuilderGlobals INSTANCE = new FormBuilderGlobals();
    
    private I18NConstants i18n;
    private FormBuilderService service;
    private FormBuilderResources resources = FormBuilderResources.INSTANCE;
    
    private FormBuilderGlobals() {
    }
    
    public static FormBuilderGlobals getInstance() {
        return INSTANCE;
    }

    public I18NConstants getI18n() {
        return i18n;
    }

    public void registerI18n(I18NConstants i18n) {
        this.i18n = i18n;
    }

    public void registerService(FormBuilderService service) {
        this.service = service;
    }
    
    public FormBuilderService getService() {
        return service;
    }

    public void registerResources(FormBuilderResources resources) {
    	this.resources = resources;
    }
    
	public FormBuilderResources getResources() {
		return resources;
	}
}
