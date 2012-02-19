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
public class MenuPanelRepresentation extends FormItemRepresentation {

	private String type;
	private String cssClassName;
	private String id;
	private String dir;
	private List<FormItemRepresentation> items = new ArrayList<FormItemRepresentation>();
	
	public MenuPanelRepresentation() {
		super("menu");
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public List<FormItemRepresentation> getItems() {
		return items;
	}

	public void setItems(List<FormItemRepresentation> items) {
		this.items = items;
	}

	@Override
	public Map<String, Object> getDataMap() {
		
		Map<String, Object> data = super.getDataMap();
		data.put("cssClassName", this.cssClassName);
		data.put("type", this.type);
		data.put("id", this.id);
    	data.put("dir", this.dir);
		List<Object> mapItems = new ArrayList<Object>();
		for (FormItemRepresentation item : this.items) {
			mapItems.add(item.getDataMap());
		}
		data.put("items", mapItems);
		return data;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void setDataMap(Map<String, Object> data)
			throws FormEncodingException {
		
		super.setDataMap(data);
		this.cssClassName = (String) data.get("cssClassName");
		this.type = (String) data.get("type");
		this.id = (String) data.get("id");
		this.dir = (String) data.get("dir");
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
		result = prime * result + ((dir == null) ? 0 : dir.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		MenuPanelRepresentation other = (MenuPanelRepresentation) obj;
		if (cssClassName == null) {
			if (other.cssClassName != null) return false;
		} else if (!cssClassName.equals(other.cssClassName)) return false;
		if (dir == null) {
			if (other.dir != null) return false;
		} else if (!dir.equals(other.dir)) return false;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		if (items == null) {
			if (other.items != null) return false;
		} else if (!items.equals(other.items)) return false;
		if (type == null) {
			if (other.type != null) return false;
		} else if (!type.equals(other.type)) return false;
		return true;
	}
	
	
	  
	
}
