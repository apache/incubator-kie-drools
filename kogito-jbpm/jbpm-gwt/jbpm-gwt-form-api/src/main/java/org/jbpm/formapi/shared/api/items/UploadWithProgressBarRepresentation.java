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
package org.jbpm.formapi.shared.api.items;

import java.util.Map;

import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;

import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class UploadWithProgressBarRepresentation extends FormItemRepresentation {

    private boolean autoSubmit;
    private boolean avoidRepeatFiles;
    private boolean enabled;
    private String cssClassName;
    
    public UploadWithProgressBarRepresentation() {
        super("uploadWithProgressBar");
    }

    public boolean isAutoSubmit() {
        return autoSubmit;
    }

    public void setAutoSubmit(boolean autoSubmit) {
        this.autoSubmit = autoSubmit;
    }

    public boolean isAvoidRepeatFiles() {
        return avoidRepeatFiles;
    }

    public void setAvoidRepeatFiles(boolean avoidRepeatFiles) {
        this.avoidRepeatFiles = avoidRepeatFiles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCssClassName() {
        return cssClassName;
    }

    public void setCssClassName(String cssClassName) {
        this.cssClassName = cssClassName;
    }
    
    @Override
    public void setDataMap(Map<String, Object> data)
            throws FormEncodingException {
        super.setDataMap(data);
        this.autoSubmit = extractBoolean(data.get("autoSubmit"));
        this.avoidRepeatFiles = extractBoolean(data.get("avoidRepeatFiles"));
        this.enabled = extractBoolean(data.get("enabled"));
        this.cssClassName = (String) data.get("cssClassName");
    }
    
    private boolean extractBoolean(Object aux) {
        if (aux == null) {
            return false;
        }
        if (aux instanceof String) {
            String saux = (String) aux;
            if ("".equals(saux)) {
                return false;
            }
            return Boolean.valueOf(saux);
        }
        if (aux instanceof Boolean) {
            Boolean baux = (Boolean) aux;
            return baux.booleanValue();
        }
        return false;
    }

    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = super.getDataMap();
        data.put("autoSubmit", Boolean.valueOf(this.autoSubmit));
        data.put("avoidRepeatFiles", Boolean.valueOf(this.avoidRepeatFiles));
        data.put("enabled", Boolean.valueOf(this.enabled));
        data.put("cssClassName", this.cssClassName);
        return data;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof UploadWithProgressBarRepresentation)) return false;
        UploadWithProgressBarRepresentation other = (UploadWithProgressBarRepresentation) obj;
        boolean equals = (this.autoSubmit == other.autoSubmit);
        if (!equals) return equals;
        equals = (this.avoidRepeatFiles == other.avoidRepeatFiles);
        if (!equals) return equals;
        equals = this.enabled == other.enabled;
        if (!equals) return equals;
        equals = (this.cssClassName == null && other.cssClassName == null) || (this.cssClassName != null && this.cssClassName.equals(other.cssClassName)); 
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.autoSubmit ? 0 : 1;
        result = 37 * result + aux;
        aux = this.avoidRepeatFiles ? 0 : 1;
        result = 37 * result + aux;
        aux = this.enabled ? 0 : 1;
        result = 37 * result + aux;
        aux = this.cssClassName == null ? 0 : this.cssClassName.hashCode();
        result = 37 * result + aux;
        return result;
    }
}
