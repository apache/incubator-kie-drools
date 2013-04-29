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
import org.jbpm.form.builder.services.model.ScriptRepresentation;
import org.jbpm.form.builder.services.model.forms.FormEncodingException;
import org.jbpm.form.builder.services.model.forms.FormEncodingFactory;
import org.jbpm.form.builder.services.model.forms.FormRepresentationDecoder;

public class ComboBoxRepresentation extends FormItemRepresentation {

    private List<OptionRepresentation> elements;
    private ScriptRepresentation elementsPopulationScript;
    private String name;
    private String id;

    public ComboBoxRepresentation() {
        super("comboBox");
    }
    
    public List<OptionRepresentation> getElements() {
        return elements;
    }

    public void setElements(List<OptionRepresentation> elements) {
        this.elements = elements;
    }

    public ScriptRepresentation getElementsPopulationScript() {
        return elementsPopulationScript;
    }

    public void setElementsPopulationScript(ScriptRepresentation elementsPopulationScript) {
        this.elementsPopulationScript = elementsPopulationScript;
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
    
    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = super.getDataMap();
        data.put("name", this.name);
        data.put("id", this.id);
        List<Map<String, Object>> elementsAsMap = new ArrayList<Map<String, Object>>();
        if (this.elements != null) {
            for (OptionRepresentation option : this.elements) {
                elementsAsMap.add(option.getDataMap());
            }
        }
        data.put("elements", elementsAsMap);
        data.put("elementsPopulationScript", this.elementsPopulationScript == null ? null : this.elementsPopulationScript.getDataMap());
        return data;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void setDataMap(Map<String, Object> data) throws FormEncodingException {
        super.setDataMap(data);
        this.name = (String) data.get("name");
        this.id = (String) data.get("id");
        this.elements = new ArrayList<OptionRepresentation>();
        List<Map<String, Object>> elems = (List<Map<String, Object>>) data.get("elements");
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        if (elems != null) {
            for (Map<String, Object> map : elems) {
                this.elements.add((OptionRepresentation) decoder.decode(map));
            }
        }
        this.elementsPopulationScript = (ScriptRepresentation) decoder.decode((Map<String, Object>) data.get("elementsPopulationScript"));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof ComboBoxRepresentation)) return false;
        ComboBoxRepresentation other = (ComboBoxRepresentation) obj;
        boolean equals = (this.elements == null && other.elements == null) || 
            (this.elements != null && this.elements.equals(other.elements));
        if (!equals) return equals;
        equals = (this.elementsPopulationScript == null && other.elementsPopulationScript == null) || 
            (this.elementsPopulationScript != null && this.elementsPopulationScript.equals(other.elementsPopulationScript));
        if (!equals) return equals;
        equals = (this.name == null && other.name == null) || (this.name != null && this.name.equals(other.name));
        if (!equals) return equals;
        equals = (this.id == null && other.id == null) || (this.id != null && this.id.equals(other.id));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.elements == null ? 0 : this.elements.hashCode();
        result = 37 * result + aux;
        aux = this.elementsPopulationScript == null ? 0 : this.elementsPopulationScript.hashCode();
        result = 37 * result + aux;
        aux = this.name == null ? 0 : this.name.hashCode();
        result = 37 * result + aux;
        aux = this.id == null ? 0 : this.id.hashCode();
        result = 37 * result + aux;
        return result;
    }
}
