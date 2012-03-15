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
package org.jbpm.formbuilder.client.effect.scripthandlers;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.formapi.shared.api.FBScript;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * 
 */
@Reflectable
public class PlainTextScriptHelper extends AbstractScriptHelper {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private String scriptPanel = "";
    
    private PlainTextScriptHelperView view;
    
    public PlainTextScriptHelper() {
        super();
    }
    
    @Override
    public void setScript(FBScript script) {
        if (script != null) {
            this.scriptPanel = script.getContent();
        }
    }
    
    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        String value = this.scriptPanel;
        value = value.replaceAll("\"", "\\\"").replaceAll("\n", "");
        dataMap.put("@className", PlainTextScriptHelper.class.getName());
        dataMap.put("scriptPanel", value);
        return dataMap;
    }

    @Override
    public void setDataMap(Map<String, Object> dataMap) {
        String value = (String) dataMap.get("scriptPanel");
        if (value == null) {
            this.scriptPanel = "";
        } else {
            this.scriptPanel = value;
        }
        if (view != null) {
            view.readDataFrom(this);
        }
    }

    @Override
    public String asScriptContent() {
        if (view != null) {
            view.writeDataTo(this);
        }
        return this.scriptPanel;
    }

    @Override
    public Widget draw() {
        if (view == null) {
            view = new PlainTextScriptHelperView(this);
        }
        return view;
    }

    @Override
    public String getName() {
        return i18n.PlainTextScriptHelperName();
    }
    
    public void setScriptPanel(String scriptPanel) {
        this.scriptPanel = scriptPanel;
    }
    
    public String getScriptPanel() {
        return scriptPanel;
    }
}
