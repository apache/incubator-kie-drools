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

import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;

import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class ImageRepresentation extends FormItemRepresentation {

    private String altText;
    private String url;
    private String id;
    private Map<String, String> i18n = new HashMap<String, String>();

    public ImageRepresentation() {
        super("image");
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getI18n() {
        return i18n;
    }
    
    public void setI18n(Map<String, String> i18n) {
        this.i18n = i18n;
    }
    
    @Override
    public Map<String, Object> getDataMap() {
    	Map<String, Object> data = super.getDataMap();
    	data.put("altText", this.altText);
        data.put("url", this.url);
        data.put("id", this.id);
        data.put("i18n", this.i18n);
        return data;
    }
    
    @Override
    public void setDataMap(Map<String, Object> data) throws FormEncodingException {
        super.setDataMap(data);
        this.altText = (String) data.get("altText");
        this.url = (String) data.get("url");
        this.id = (String) data.get("id");
        @SuppressWarnings("unchecked")
        Map<String, String> i18nMap = (Map<String, String>) data.get("i18n");
        if (i18nMap != null) {
            this.i18n = new HashMap<String, String>();
            this.i18n.putAll(i18nMap);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof ImageRepresentation)) return false;
        ImageRepresentation other = (ImageRepresentation) obj;
        boolean equals = (this.altText == null && other.altText == null) || (this.altText != null && this.altText.equals(other.altText));
        if (!equals) return equals;
        equals = (this.url == null && other.url == null) || (this.url != null && this.url.equals(other.url));
        if (!equals) return equals;
        equals = (this.id == null && other.id == null) || (this.id != null && this.id.equals(other.id));
        if (!equals) return equals;
        equals = (this.i18n == null && other.i18n == null) || (this.i18n != null && this.i18n.entrySet().equals(other.i18n.entrySet()));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.altText == null ? 0 : this.altText.hashCode();
        result = 37 * result + aux;
        aux = this.url == null ? 0 : this.url.hashCode();
        result = 37 * result + aux;
        aux = this.id == null ? 0 : this.id.hashCode();
        result = 37 * result + aux;
        aux = this.i18n == null ? 0 : this.i18n.hashCode();
        result = 37 * result + aux;
        return result;
    }
}
