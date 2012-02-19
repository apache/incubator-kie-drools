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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationDecoder;

import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class AbsolutePanelRepresentation extends FormItemRepresentation {

    public static class Position {
        private final int x;
        private final int y;
        
        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
    }
    
    private Map<Position, FormItemRepresentation> items = new HashMap<Position, FormItemRepresentation>();

    private String id;

    public AbsolutePanelRepresentation() {
        super("absolutePanel");
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addItem(FormItemRepresentation rep, int x, int y) {
        items.put(new Position(x, y), rep);
    }
    
    public Map<Position, FormItemRepresentation> getItems() {
        return items;
    }
    
    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = super.getDataMap();
        data.put("id", this.id);
        List<Map<String, Object>> mapItems = new ArrayList<Map<String, Object>>();
        if (this.items != null) {
            for (Map.Entry<Position, FormItemRepresentation> entry : this.items.entrySet()) {
                FormItemRepresentation item = entry.getValue();
                Position pos = entry.getKey();
                Map<String, Object> itemData = item == null ? null : item.getDataMap();
                itemData.put("x", pos.getX());
                itemData.put("y", pos.getY());
                mapItems.add(itemData);
            }
        }
        data.put("items", mapItems);
        return data;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void setDataMap(Map<String, Object> data) throws FormEncodingException {
        super.setDataMap(data);
        this.id = (String) data.get("id");
        this.items.clear();
        List<Map<String, Object>> mapItems = (List<Map<String, Object>>) data.get("items");
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        if (mapItems != null) {
            for (Map<String, Object> entry : mapItems) {
                int x = entry.get("x") == null ? 0 : ((Number) entry.get("x")).intValue();
                int y = entry.get("y") == null ? 0 : ((Number) entry.get("y")).intValue();
                Position pos = new Position(x, y);
                FormItemRepresentation item = (FormItemRepresentation) decoder.decode(entry);
                this.items.put(pos, item);
            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof AbsolutePanelRepresentation)) return false;
        AbsolutePanelRepresentation other = (AbsolutePanelRepresentation) obj;
        boolean equals = (this.items == null && other.items == null) || 
            (this.items != null && other.items != null && this.items.entrySet().equals(other.items.entrySet()));
        if (!equals) return equals;
        equals = (this.id == null && other.id == null) || (this.id != null && this.id.equals(other.id));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.items == null ? 0 : this.items.hashCode();
        result = 37 * result + aux;
        aux = this.id == null ? 0 : this.id.hashCode();
        result = 37 * result + aux;
        return result;
    }

}
