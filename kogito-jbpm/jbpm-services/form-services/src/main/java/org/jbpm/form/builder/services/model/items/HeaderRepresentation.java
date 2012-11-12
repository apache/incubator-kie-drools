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

import java.util.HashMap;
import java.util.Map;

import org.jbpm.form.builder.services.model.FormItemRepresentation;
import org.jbpm.form.builder.services.model.forms.FormEncodingException;

public class HeaderRepresentation extends FormItemRepresentation {

    private String value;
    private String styleClass;
    private String cssId;
    private String cssName;
    private Map<String, String> i18n = new HashMap<String, String>();
    private String format;

    public HeaderRepresentation() {
        super("header");
    }
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getCssId() {
        return cssId;
    }

    public void setCssId(String cssId) {
        this.cssId = cssId;
    }

    public String getCssName() {
        return cssName;
    }

    public void setCssName(String cssName) {
        this.cssName = cssName;
    }
    
    public Map<String, String> getI18n() {
        return i18n;
    }
    
    public void setI18n(Map<String, String> i18n) {
        this.i18n = i18n;
    }
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = super.getDataMap();
        data.put("value", this.value);
        data.put("styleClass", this.styleClass);
        data.put("cssId", this.cssId);
        data.put("cssName", this.cssName);
        data.put("i18n", this.i18n);
        data.put("format", this.format);
        return data;
    }
    
    @Override
    public void setDataMap(Map<String, Object> data) throws FormEncodingException {
        super.setDataMap(data);
        this.value = (String) data.get("value");
        this.styleClass = (String) data.get("styleClass");
        this.cssId = (String) data.get("cssId");
        this.cssName = (String) data.get("cssName");
        @SuppressWarnings("unchecked")
        Map<String, String> i18nMap = (Map<String, String>) data.get("i18n");
        if (i18nMap != null) {
            this.i18n = new HashMap<String, String>();
            this.i18n.putAll(i18nMap);
        }
        this.format = (String) data.get("format");
    }

    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof HeaderRepresentation)) return false;
        HeaderRepresentation other = (HeaderRepresentation) obj;
        boolean equals = (this.value == null && other.value == null) || (this.value != null && this.value.equals(other.value));
        if (!equals) return equals;
        equals = (this.styleClass == null && other.styleClass == null) || (this.styleClass != null && this.styleClass.equals(other.styleClass));
        if (!equals) return equals;
        equals = (this.cssId == null && other.cssId == null) || (this.cssId != null && this.cssId.equals(other.cssId));
        if (!equals) return equals;
        equals = (this.cssName == null && other.cssName == null) || (this.cssName != null && this.cssName.equals(other.cssName));
        if (!equals) return equals;
        equals = (this.i18n == null && other.i18n == null) || (this.i18n != null && this.i18n.entrySet().equals(other.i18n.entrySet()));
        if (!equals) return equals;
        equals = (this.format == null && other.format == null) || (this.format != null && this.format.equals(other.format));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.value == null ? 0 : this.value.hashCode();
        result = 37 * result + aux;
        aux = this.styleClass == null ? 0 : this.styleClass.hashCode();
        result = 37 * result + aux;
        aux = this.cssId == null ? 0 : this.cssId.hashCode();
        result = 37 * result + aux;
        aux = this.cssName == null ? 0 : this.cssName.hashCode();
        result = 37 * result + aux;
        aux = this.i18n == null ? 0 : this.i18n.hashCode();
        result = 37 * result + aux;
        aux = this.format == null ? 0 : this.format.hashCode();
        result = 37 * result + aux;
        return result;
    }
}
