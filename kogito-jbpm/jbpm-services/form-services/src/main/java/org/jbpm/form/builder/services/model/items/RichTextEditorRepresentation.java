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

public class RichTextEditorRepresentation extends FormItemRepresentation {

    private String html;
    
    public RichTextEditorRepresentation() {
        super("richTextEditor");
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
    
    @Override
    public void setDataMap(Map<String, Object> data) throws FormEncodingException {
        this.html = (String) data.get("html");
        super.setDataMap(data);
    }
    
    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = super.getDataMap();
        data.put("html", this.html);
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof RichTextEditorRepresentation)) return false;
        RichTextEditorRepresentation other = (RichTextEditorRepresentation) obj;
        boolean equals = (this.html == null && other.html == null) || (this.html != null && this.html.equals(other.html));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.html == null ? 0 : this.html.hashCode();
        result = 37 * result + aux;
        return result;
    }
}
