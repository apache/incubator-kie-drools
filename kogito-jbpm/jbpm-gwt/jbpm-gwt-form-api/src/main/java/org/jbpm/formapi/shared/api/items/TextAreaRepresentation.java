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
public class TextAreaRepresentation extends FormItemRepresentation {

    private String name;
    private int rows;
    private int cols;
    private String value;
    private String id;

    public TextAreaRepresentation() {
        super("textArea");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = super.getDataMap();
        data.put("name", this.name);
        data.put("rows", this.rows);
        data.put("cols", this.cols);
        data.put("value", this.value);
        data.put("id", this.id);
        return data;
    }

    @Override
    public void setDataMap(Map<String, Object> data)
            throws FormEncodingException {
        super.setDataMap(data);
        this.name = (String) data.get("name");
        this.rows = (data.get("rows") == null ? 0 : ((Number) data.get("rows"))
                .intValue());
        this.cols = (data.get("cols") == null ? 0 : ((Number) data.get("cols"))
                .intValue());
        this.value = (String) data.get("value");
        this.id = (String) data.get("id");
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;
        if (!(obj instanceof TextAreaRepresentation))
            return false;
        TextAreaRepresentation other = (TextAreaRepresentation) obj;
        boolean equals = (this.name == null && other.name == null)
                || (this.name != null && this.name.equals(other.name));
        if (!equals)
            return equals;
        equals = (this.value == null && other.value == null)
                || (this.value != null && this.value.equals(other.value));
        if (!equals)
            return equals;
        equals = (this.id == null && other.id == null)
                || (this.id != null && this.id.equals(other.id));
        if (!equals)
            return equals;
        equals = (this.rows != other.rows && this.cols != other.cols);
        return equals;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.name == null ? 0 : this.name.hashCode();
        result = 37 * result + aux;
        aux = this.value == null ? 0 : this.value.hashCode();
        result = 37 * result + aux;
        aux = this.id == null ? 0 : this.id.hashCode();
        result = 37 * result + aux;
        aux = this.rows;
        result = 37 * result + aux;
        aux = this.cols;
        result = 37 * result + aux;
        return result;
    }
}
