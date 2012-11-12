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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.form.builder.services.model.FormItemRepresentation;
import org.jbpm.form.builder.services.model.forms.FormEncodingException;
import org.jbpm.form.builder.services.model.forms.FormEncodingFactory;
import org.jbpm.form.builder.services.model.forms.FormRepresentationDecoder;

public class HorizontalPanelRepresentation extends FormItemRepresentation {

    private Integer borderWidth;
    private Integer spacing;
    private String cssClassName;
    private String horizontalAlignment;
    private String verticalAlignment;
    private String title;
    private String id;
    private List<FormItemRepresentation> items = new ArrayList<FormItemRepresentation>();
    
    public HorizontalPanelRepresentation() {
        super("horizontalPanel");
    }

    public Integer getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(Integer borderWidth) {
        this.borderWidth = borderWidth;
    }

    public Integer getSpacing() {
        return spacing;
    }

    public void setSpacing(Integer spacing) {
        this.spacing = spacing;
    }

    public String getCssClassName() {
        return cssClassName;
    }

    public void setCssClassName(String cssClassName) {
        this.cssClassName = cssClassName;
    }

    public String getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(String horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public String getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(String verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
    
    public void addItem(FormItemRepresentation item) {
        items.add(item);
    }
    
    public void setItems(List<FormItemRepresentation> items) {
        this.items = items;
    }
    
    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = super.getDataMap();
        data.put("borderWidth", this.borderWidth);
        data.put("spacing", this.spacing);
        data.put("cssClassName", this.cssClassName);
        data.put("horizontalAlignment", this.horizontalAlignment);
        data.put("verticalAlignment", this.verticalAlignment);
        data.put("title", this.title);
        data.put("id", this.id);
        List<Map<String, Object>> mapItems = new ArrayList<Map<String, Object>>();
        if (this.items != null) {
            for (FormItemRepresentation item : this.items) {
                mapItems.add(item == null ? null : item.getDataMap());
            }
        }
        data.put("items", mapItems);
        return data;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void setDataMap(Map<String, Object> data) throws FormEncodingException {
        super.setDataMap(data);
        this.borderWidth = data.get("borderWidth") == null ? null : ((Number) data.get("borderWidth")).intValue();
        this.spacing = data.get("spacing") == null ? null : ((Number) data.get("spacing")).intValue();
        this.cssClassName = (String) data.get("cssClassName");
        this.horizontalAlignment = (String) data.get("horizontalAlignment");
        this.verticalAlignment = (String) data.get("verticalAlignment");
        this.title = (String) data.get("title");
        this.id = (String) data.get("id");
        this.items.clear();
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        List<Map<String, Object>> mapItems = (List<Map<String, Object>>) data.get("items");
        if (mapItems != null) {
            for (Map<String, Object> mapItem : mapItems) {
                this.items.add((FormItemRepresentation) decoder.decode(mapItem));
            }
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof TableRepresentation)) return false;
        HorizontalPanelRepresentation other = (HorizontalPanelRepresentation) obj;
        boolean equals = (this.borderWidth == null && other.borderWidth == null) || 
            (this.borderWidth != null && this.borderWidth.equals(other.borderWidth));
        if (!equals) return equals;
        equals = (this.spacing == null && other.spacing == null) || (this.spacing != null && this.spacing.equals(other.spacing));
        if (!equals) return equals;
        equals = (this.cssClassName == null && other.cssClassName == null) || 
            (this.cssClassName != null && this.cssClassName.equals(other.cssClassName));
        if (!equals) return equals;
        equals = (this.horizontalAlignment == null && other.horizontalAlignment == null) || 
            (this.horizontalAlignment != null && this.horizontalAlignment.equals(other.horizontalAlignment));
        if (!equals) return equals;
        equals = (this.verticalAlignment == null && other.verticalAlignment == null) || 
            (this.verticalAlignment != null && this.verticalAlignment.equals(other.verticalAlignment));
        if (!equals) return equals;
        equals = (this.title == null && other.title == null) || (this.title != null && this.title.equals(other.title));
        if (!equals) return equals;
        equals = (this.id == null && other.id == null) || (this.id != null && this.id.equals(other.id));
        if (!equals) return equals;
        equals = (this.items == null && other.items == null) || (this.items != null && this.items.equals(other.items));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.borderWidth == null ? 0 : this.borderWidth.hashCode();
        result = 37 * result + aux;
        aux = this.spacing == null ? 0 : this.spacing.hashCode();
        result = 37 * result + aux;
        aux = this.cssClassName == null ? 0 : this.cssClassName.hashCode();
        result = 37 * result + aux;
        aux = this.horizontalAlignment == null ? 0 : this.horizontalAlignment.hashCode();
        result = 37 * result + aux;
        aux = this.verticalAlignment == null ? 0 : this.verticalAlignment.hashCode();
        result = 37 * result + aux;
        aux = this.title == null ? 0 : this.title.hashCode();
        result = 37 * result + aux;
        aux = this.id == null ? 0 : this.id.hashCode();
        result = 37 * result + aux;
        aux = this.items == null ? 0 : this.items.hashCode();
        result = 37 * result + aux;
        return result;
    }

}
