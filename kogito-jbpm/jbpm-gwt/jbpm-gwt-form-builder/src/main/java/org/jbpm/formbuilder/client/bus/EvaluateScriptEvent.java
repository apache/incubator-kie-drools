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
package org.jbpm.formbuilder.client.bus;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.formapi.shared.api.FBScript;

import com.google.gwt.event.shared.GwtEvent;

public class EvaluateScriptEvent extends GwtEvent<EvaluateScriptHandler> {

    public static final Type<EvaluateScriptHandler> TYPE = new Type<EvaluateScriptHandler>();
    
    private Map<String, Object> input = new HashMap<String, Object>();
    private FBScript script;
    
    public EvaluateScriptEvent(FBScript script) {
        this.script = script;
    }
    
    public EvaluateScriptEvent(FBScript script, Map<String, Object> input) {
        this.script = script;
        this.input = input;
    }
    
    public Map<String, Object> getInput() {
        return input;
    }
    
    public FBScript getScript() {
        return script;
    }
    
    public Object getInput(String key) {
        return input.get(key);
    }
    
    public Object putInput(String key, Object value) {
        return input.put(key, value);
    }
    
    @Override
    public Type<EvaluateScriptHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(EvaluateScriptHandler handler) {
        handler.onEvent(this);
    }

}
