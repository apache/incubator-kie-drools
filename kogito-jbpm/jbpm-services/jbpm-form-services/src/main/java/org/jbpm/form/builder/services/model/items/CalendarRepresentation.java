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

public class CalendarRepresentation extends FormItemRepresentation {

    private String iconUrl;
    private String calendarCss;
    private String defaultValue;
    
    public CalendarRepresentation() {
        super("calendar");
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getCalendarCss() {
        return calendarCss;
    }

    public void setCalendarCss(String calendarCss) {
        this.calendarCss = calendarCss;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = super.getDataMap();
        data.put("defaultValue", this.defaultValue);
        data.put("calendarCss", this.calendarCss);
        data.put("iconUrl", this.iconUrl);
        return data;
    }
    
    @Override
    public void setDataMap(Map<String, Object> data)
            throws FormEncodingException {
        super.setDataMap(data);    
        this.defaultValue = (String) data.get("defaultValue");
        this.calendarCss = (String) data.get("calendarCss");
        this.iconUrl = (String) data.get("iconUrl");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof CalendarRepresentation)) return false;
        CalendarRepresentation other = (CalendarRepresentation) obj;
        boolean equals = (this.defaultValue == null && other.defaultValue == null) || 
            (this.defaultValue != null && this.defaultValue.equals(other.defaultValue));
        if (!equals) return equals;
        equals = (this.calendarCss == null && other.calendarCss == null) || 
            (this.calendarCss != null && this.calendarCss.equals(other.calendarCss));
        if (!equals) return equals;
        equals = (this.iconUrl == null && other.iconUrl == null) || (this.iconUrl != null && this.iconUrl.equals(other.iconUrl));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.defaultValue == null ? 0 : this.defaultValue.hashCode();
        result = 37 * result + aux;
        aux = this.calendarCss == null ? 0 : this.calendarCss.hashCode();
        result = 37 * result + aux;
        aux = this.iconUrl == null ? 0 : this.iconUrl.hashCode();
        result = 37 * result + aux;
        return result;
    }
}
