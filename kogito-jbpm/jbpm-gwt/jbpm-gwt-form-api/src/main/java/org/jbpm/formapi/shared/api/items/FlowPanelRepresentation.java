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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationDecoder;

import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class FlowPanelRepresentation extends FormItemRepresentation {

    private String cssClassName;
    private String id;
    private List<FormItemRepresentation> items = new ArrayList<FormItemRepresentation>();

    public FlowPanelRepresentation() {
        super("flowPanel");
    }

    public String getCssClassName() {
        return cssClassName;
    }

    public void setCssClassName(String cssClassName) {
        this.cssClassName = cssClassName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<FormItemRepresentation> getItems() {
        return items;
    }

    public void setItems(List<FormItemRepresentation> items) {
        this.items = items;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setDataMap(Map<String, Object> data)
            throws FormEncodingException {
        super.setDataMap(data);
        this.cssClassName = (String) data.get("cssClassName");
        this.id = (String) data.get("id");
        this.items.clear();
        List<Object> mapItems = (List<Object>) data.get("items");
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        if (mapItems != null) {
            for (Object obj : mapItems) {
                Map<String, Object> itemMap = (Map<String, Object>) obj;
                FormItemRepresentation item = (FormItemRepresentation) decoder
                        .decode(itemMap);
                this.items.add(item);
            }
        }
    }

    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = super.getDataMap();
        data.put("cssClassName", this.cssClassName);
        data.put("id", this.id);
        List<Object> mapItems = new ArrayList<Object>();
        for (FormItemRepresentation item : this.items) {
            mapItems.add(item.getDataMap());
        }
        data.put("items", mapItems);
        return data;
    }
}
