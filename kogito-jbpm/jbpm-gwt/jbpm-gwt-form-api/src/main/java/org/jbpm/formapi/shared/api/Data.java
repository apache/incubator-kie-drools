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
package org.jbpm.formapi.shared.api;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationDecoder;

public abstract class Data implements Mappable {

    private String mimeType;
    private String name;
    private String value;
    private Formatter formatter;

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Formatter getFormatter() {
        return formatter;
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("@className", getClass().getName());
        data.put("mimeType", this.mimeType);
        data.put("name", this.name);
        data.put("value", this.value);
        data.put("formatter",
                this.formatter == null ? null : this.formatter.getDataMap());
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setDataMap(Map<String, Object> dataMap)
            throws FormEncodingException {
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        this.mimeType = (String) dataMap.get("mimeType");
        this.name = (String) dataMap.get("name");
        this.value = (String) dataMap.get("value");
        this.formatter = (Formatter) decoder
                .decode((Map<String, Object>) dataMap.get("formatter"));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Data))
            return false;
        Data other = (Data) obj;

        boolean equals = (this.mimeType == null && other.mimeType == null)
                || (this.mimeType != null && this.mimeType
                        .equals(other.mimeType));
        if (!equals)
            return equals;
        equals = (this.name == null && other.name == null)
                || (this.name != null && this.name.equals(other.name));
        if (!equals)
            return equals;
        equals = (this.value == null && other.value == null)
                || (this.value != null && this.value.equals(other.value));
        if (!equals)
            return equals;
        equals = (this.formatter == null && other.formatter == null)
                || (this.formatter != null && this.formatter
                        .equals(other.formatter));
        return equals;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.mimeType == null ? 0 : this.mimeType.hashCode();
        result = 37 * result + aux;
        aux = this.name == null ? 0 : this.name.hashCode();
        result = 37 * result + aux;
        aux = this.value == null ? 0 : this.value.hashCode();
        result = 37 * result + aux;
        aux = this.formatter == null ? 0 : this.formatter.hashCode();
        result = 37 * result + aux;
        return result;
    }
}
