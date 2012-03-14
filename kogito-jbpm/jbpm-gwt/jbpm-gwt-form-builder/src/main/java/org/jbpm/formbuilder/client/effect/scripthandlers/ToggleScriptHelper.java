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

import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.effect.scriptviews.ToggleScriptHelperView;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class ToggleScriptHelper extends AbstractScriptHelper {

    public static final String TOGGLE = "toggle";
    public static final String SHOW = "show";
    public static final String HIDE = "hide";
    public static final String HIDING_STRATEGY_COLLAPSE = "collapse";
    public static final String HIDING_STRATEGY_HIDDEN = "hidden";
    
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();

    private String idField = "";
    private String actionOnEvent = TOGGLE;
    private String hidingStrategy = HIDING_STRATEGY_HIDDEN;
    
    private ToggleScriptHelperView view;
    
    public ToggleScriptHelper() {
        super();
    }
    
    @Override
    public Map<String, Object> getDataMap() {
        if (view != null) {
            view.writeDataTo(this);
        }
        String idFieldValue = this.idField;
        String actionOnEventValue = this.actionOnEvent;
        String hidingStrategyValue = this.hidingStrategy;
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("@className", ToggleScriptHelper.class.getName());
        map.put("idField", idFieldValue);
        map.put("actionOnEvent", actionOnEventValue);
        map.put("hidingStrategy", hidingStrategyValue);
        return map;
    }

    @Override
    public void setDataMap(Map<String, Object> dataMap) {
        String idFieldValue = (String) dataMap.get("idField");
        String actionOnEventValue = (String) dataMap.get("actionOnEvent");
        String hidingStrategyValue = (String) dataMap.get("hidingStrategy");
        
        this.idField = idFieldValue;
        this.actionOnEvent = actionOnEventValue;
        this.hidingStrategy = hidingStrategyValue;
        
        if (this.view != null) {
            this.view.readDataFrom(this);
        }
    }

    @Override
    public String asScriptContent() {
        if (view != null) {
            view.writeDataTo(this);
        }
        long id = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        String actionValue = actionOnEvent;
        String strategy = hidingStrategy;
        sb.append("var elementToggle" + id + " = document.getElementById('" + idField + "');");
        sb.append("if (elementToggle" + id + " != null) {");
        if (actionValue.equals(HIDE)) {
            //hide script
            sb.append("   elementToggle" + id + ".style.visibility = '" + strategy + "';");
        }
        if (actionValue.equals(SHOW)) {
            //show script
            sb.append("   elementToggle" + id + ".style.visibility = 'visible';");
        }
        if (actionValue.equals(TOGGLE)) {
            //show if not visible, hide if visible script
            sb.append("   if (elementToggle" + id + ".style.visibility == 'visible') {");
            sb.append("      elementToggle" + id + ".style.visibility = '" + strategy + "';");
            sb.append("   } else {");
            sb.append("      elementToggle" + id + ".style.visibility = 'visible';");
            sb.append("   }");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public Widget draw() {
        if (view == null) {
            view = new ToggleScriptHelperView(this);
            view.readDataFrom(this);
        }
        return view;
    }

    @Override
    public String getName() {
        return i18n.ToggleScriptHelperName();
    }

    public String getIdField() {
        return idField;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public String getActionOnEvent() {
        return actionOnEvent;
    }

    public void setActionOnEvent(String actionOnEvent) {
        this.actionOnEvent = actionOnEvent;
    }

    public String getHidingStrategy() {
        return hidingStrategy;
    }

    public void setHidingStrategy(String hidingStrategy) {
        this.hidingStrategy = hidingStrategy;
    }
}
