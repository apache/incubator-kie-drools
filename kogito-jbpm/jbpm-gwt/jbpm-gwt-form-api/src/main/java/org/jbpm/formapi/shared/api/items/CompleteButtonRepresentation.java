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

import java.util.HashMap;
import java.util.Map;

import org.jbpm.formapi.shared.api.FBScript;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationDecoder;

import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class CompleteButtonRepresentation extends FormItemRepresentation {

    private String text;
    private String name;
    private String id;
    private FBScript onClickScript;
    private Map<String, String> i18n = new HashMap<String, String>();
    private String format;
    
    public CompleteButtonRepresentation() {
        super("completeButton");
        this.onClickScript = new FBScript();
        this.onClickScript.setType("text/javascript");
        this.onClickScript.setContent("document.forms[0].submit();");
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FBScript getOnClickScript() {
        return onClickScript;
    }

    public void setOnClickScript(FBScript onClickScript) {
        this.onClickScript = onClickScript;
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
        data.put("text", this.text);
        data.put("name", this.name);
        data.put("id", this.id);
        data.put("onClickScript", this.onClickScript == null ? null : this.onClickScript.getDataMap());
        data.put("i18n", this.i18n);
        data.put("format", this.format);
        return data;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void setDataMap(Map<String, Object> data) throws FormEncodingException {
        super.setDataMap(data);
        this.text = (String) data.get("text");
        this.name = (String) data.get("name");
        this.id = (String) data.get("id");
        Map<String, String> i18nMap = (Map<String, String>) data.get("i18n");
        if (i18nMap != null) {
            this.i18n = new HashMap<String, String>();
            this.i18n.putAll(i18nMap);
        }
        this.format = (String) data.get("format");
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        this.onClickScript = (FBScript) decoder.decode((Map<String, Object>) data.get("onClickScript"));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof CompleteButtonRepresentation)) return false;
        CompleteButtonRepresentation other = (CompleteButtonRepresentation) obj;
        boolean equals = (this.text == null && other.text == null) || (this.text != null && this.text.equals(other.text));
        if (!equals) return equals;
        equals = (this.name == null && other.name == null) || (this.name != null && this.name.equals(other.name));
        if (!equals) return equals;
        equals = (this.id == null && other.id == null) || (this.id != null && this.id.equals(other.id));
        if (!equals) return equals;
        equals = (this.onClickScript == null && other.onClickScript == null) || 
            (this.onClickScript != null && this.onClickScript.equals(other.onClickScript));
        if (!equals) return equals;
        equals = (this.i18n == null && other.i18n == null) || (this.i18n != null && this.i18n.entrySet().equals(other.i18n.entrySet()));
        if (!equals) return equals;
        equals = (this.format == null && other.format == null) || (this.format != null && this.format.equals(other.format));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.text == null ? 0 : this.text.hashCode();
        result = 37 * result + aux;
        aux = this.name == null ? 0 : this.name.hashCode();
        result = 37 * result + aux;
        aux = this.id == null ? 0 : this.id.hashCode();
        result = 37 * result + aux;
        aux = this.onClickScript == null ? 0 : this.onClickScript.hashCode();
        result = 37 * result + aux;
        aux = this.i18n == null ? 0 : this.i18n.hashCode();
        result = 37 * result + aux;
        aux = this.format == null ? 0 : this.format.hashCode();
        result = 37 * result + aux;
        return result;
    }
}
