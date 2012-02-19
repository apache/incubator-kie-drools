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
public class FieldSetPanelRepresentation extends FormItemRepresentation {

	private String cssClassName;
	private String id;
	private String legend;
	private List<FormItemRepresentation> items = new ArrayList<FormItemRepresentation>();
	private Map<String, String> i18n;
	 
	public FieldSetPanelRepresentation() {
		super("fieldSet");
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

	public String getLegend() {
		return legend;
	}

	public void setLegend(String legend) {
		this.legend = legend;
	}

	public List<FormItemRepresentation> getItems() {
		return items;
	}

	public void setItems(List<FormItemRepresentation> items) {
		this.items = items;
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
		data.put("cssClassName", this.cssClassName);
		data.put("legend", this.legend);
		data.put("id", this.id);
    	data.put("i18n", this.i18n);
		List<Object> mapItems = new ArrayList<Object>();
		for (FormItemRepresentation item : this.items) {
			mapItems.add(item.getDataMap());
		}
		data.put("items", mapItems);
		return data;

	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void setDataMap(Map<String, Object> data) throws FormEncodingException {
		super.setDataMap(data);
		this.cssClassName = (String) data.get("cssClassName");
		this.id = (String) data.get("id");
		this.legend = (String) data.get("legend");
        Map<String, String> i18nMap = (Map<String, String>) data.get("i18n");
        if (i18nMap != null) {
            this.i18n = new HashMap<String, String>();
            this.i18n.putAll(i18nMap);
        }
        this.items.clear();
		List<Object> mapItems = (List<Object>) data.get("items");
		FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
		if (mapItems != null) {
			for (Object obj : mapItems) {
				Map<String, Object> itemMap = (Map<String, Object>) obj;
				FormItemRepresentation item = (FormItemRepresentation) decoder.decode(itemMap);
				this.items.add(item);
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cssClassName == null) ? 0 : cssClassName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result + ((legend == null) ? 0 : legend.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		FieldSetPanelRepresentation other = (FieldSetPanelRepresentation) obj;
		if (cssClassName == null) {
			if (other.cssClassName != null) return false;
		} else if (!cssClassName.equals(other.cssClassName)) return false;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		if (items == null) {
			if (other.items != null) return false;
		} else if (!items.equals(other.items)) return false;
		if (legend == null) {
			if (other.legend != null) return false;
		} else if (!legend.equals(other.legend)) return false;
		return true;
	}
}
