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
package org.jbpm.form.builder.services.model.items;

import java.util.Map;

import org.jbpm.form.builder.services.model.FormItemRepresentation;
import org.jbpm.form.builder.services.model.forms.FormEncodingException;

public class ServerTransformationRepresentation extends FormItemRepresentation {

    private String language;
    private String script;
    
    public ServerTransformationRepresentation() {
        super("serverTransformation");
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
    
    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = super.getDataMap();
        data.put("script", this.script);
        data.put("language", this.language);
        return data;
    }
    
    @Override
    public void setDataMap(Map<String, Object> data) throws FormEncodingException {
        super.setDataMap(data);
        this.script = (String) data.get("script");
        this.language = (String) data.get("language");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof ServerTransformationRepresentation)) return false;
        ServerTransformationRepresentation other = (ServerTransformationRepresentation) obj;
        boolean equals = (this.script == null && other.script == null) || (this.script != null && this.script.equals(other.script));
        if (!equals) return equals;
        equals = (this.language == null && other.language == null) || (this.language != null && this.language.equals(other.language));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.script == null ? 0 : this.script.hashCode();
        result = 37 * result + aux;
        aux = this.language == null ? 0 : this.language.hashCode();
        result = 37 * result + aux;
        return result;
    }
}
