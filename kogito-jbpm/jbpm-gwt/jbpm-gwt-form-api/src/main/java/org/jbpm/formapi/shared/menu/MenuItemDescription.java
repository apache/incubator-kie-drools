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
package org.jbpm.formapi.shared.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.Mappable;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationDecoder;

import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class MenuItemDescription implements Mappable {

    private String className;
    private String name;
    private FormItemRepresentation itemRepresentation;
    private List<FormEffectDescription> effects = new ArrayList<FormEffectDescription>();
    private List<String> allowedEvents = new ArrayList<String>();
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FormItemRepresentation getItemRepresentation() {
        return itemRepresentation;
    }

    public void setItemRepresentation(FormItemRepresentation itemRepresentation) {
        this.itemRepresentation = itemRepresentation;
    }

    public List<FormEffectDescription> getEffects() {
        return effects;
    }
    
    public void setEffects(List<FormEffectDescription> effects) {
        this.effects = effects;
    }
    
    public List<String> getAllowedEvents() {
        return allowedEvents;
    }
    
    public void setAllowedEvents(List<String> allowedEvents) {
        this.allowedEvents = allowedEvents;
    }
    
    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("className", this.className);
        data.put("name", this.name);
        data.put("itemRepresentation", itemRepresentation == null ? null : itemRepresentation.getDataMap());
        if (this.effects == null) {
            data.put("effects", null);
        } else {
            List<Object> effectsMap = new ArrayList<Object>();
            for (FormEffectDescription effect : this.effects) {
                effectsMap.add(effect.getDataMap());
            }
            data.put("effects", effectsMap);
        }
        data.put("allowedEvents", this.allowedEvents);
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setDataMap(Map<String, Object> data) throws FormEncodingException {
        this.className = (String) data.get("className");
        this.name = (String) data.get("name");
        List<Object> effectsMap = (List<Object>) data.get("effects");
        this.effects.clear();
        if (effectsMap != null) {
            for (Object objEffect : effectsMap) {
                Map<String, Object> effectDataMap = (Map<String, Object>) objEffect;
                FormEffectDescription effect = new FormEffectDescription();
                effect.setDataMap(effectDataMap);
                this.effects.add(effect);
            }
        }
        List<Object> allowedEventsList = (List<Object>) data.get("allowedEvents");
        if (allowedEventsList != null) {
            this.allowedEvents.clear();
            for (Object obj : allowedEventsList) {
                this.allowedEvents.add(obj.toString());
            }
        }
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        Map<String, Object> itemMap = (Map<String, Object>) data.get("itemRepresentation");
        this.itemRepresentation = (FormItemRepresentation) decoder.decode(itemMap);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((allowedEvents == null) ? 0 : allowedEvents.hashCode());
        result = prime * result
                + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((effects == null) ? 0 : effects.hashCode());
        result = prime
                * result
                + ((itemRepresentation == null) ? 0 : itemRepresentation
                        .hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MenuItemDescription other = (MenuItemDescription) obj;
        if (allowedEvents == null) {
            if (other.allowedEvents != null)
                return false;
        } else if (!allowedEvents.equals(other.allowedEvents))
            return false;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
        if (effects == null) {
            if (other.effects != null)
                return false;
        } else if (!effects.equals(other.effects))
            return false;
        if (itemRepresentation == null) {
            if (other.itemRepresentation != null)
                return false;
        } else if (!itemRepresentation.equals(other.itemRepresentation))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
