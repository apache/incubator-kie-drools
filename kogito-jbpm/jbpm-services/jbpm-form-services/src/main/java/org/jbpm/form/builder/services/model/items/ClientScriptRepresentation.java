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

public class ClientScriptRepresentation extends FormItemRepresentation {

    private String type;
    private String src;
    
    public ClientScriptRepresentation() {
        super("clientScript");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    @Override
    public void setDataMap(Map<String, Object> data) throws FormEncodingException {
        this.type = (String) data.get("type");
        this.src = (String) data.get("src");
        super.setDataMap(data);
    }

    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = super.getDataMap();
        data.put("src", this.src);
        data.put("type", this.type);
        return data;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof ClientScriptRepresentation)) return false;
        ClientScriptRepresentation other = (ClientScriptRepresentation) obj;
        boolean equals = (this.src == null && other.src == null) || (this.src != null && this.src.equals(other.src));
        if (!equals) return equals;
        equals = (this.type == null && other.type == null) || (this.type != null && this.type.equals(other.type));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.src == null ? 0 : this.src.hashCode();
        result = 37 * result + aux;
        aux = this.type == null ? 0 : this.type.hashCode();
        result = 37 * result + aux;
        return result;
    }
}
