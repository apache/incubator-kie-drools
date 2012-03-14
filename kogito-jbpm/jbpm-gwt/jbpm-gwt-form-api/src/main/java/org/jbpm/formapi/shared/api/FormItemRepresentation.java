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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationDecoder;

public abstract class FormItemRepresentation implements Mappable {

    private Map<String, FBScript> eventActions = new HashMap<String, FBScript>();
    private List<FBValidation> itemValidations = new ArrayList<FBValidation>();
    private OutputData output;
    private InputData input;
    private ExternalData external;

    private String width;
    private String height;

    private final String typeId;
    private final String itemClassName;

    private List<String> effectClasses = new ArrayList<String>();

    public FormItemRepresentation(String typeId) {
        this.typeId = typeId;
        this.itemClassName = RepresentationFactory.getItemClassName(getClass()
                .getName());
    }

    public void setEventActions(Map<String, FBScript> eventActions) {
        this.eventActions = eventActions;
    }

    public Map<String, FBScript> getEventActions() {
        return eventActions;
    }

    public List<FBValidation> getItemValidations() {
        return itemValidations;
    }

    public void setItemValidations(List<FBValidation> itemValidations) {
        this.itemValidations = itemValidations;
    }

    public OutputData getOutput() {
        return output;
    }

    public void setOutput(OutputData output) {
        this.output = output;
    }

    public InputData getInput() {
        return input;
    }

    public void setInput(InputData input) {
        this.input = input;
    }

    public ExternalData getExternal() {
        return external;
    }

    public void setExternal(ExternalData external) {
        this.external = external;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getTypeId() {
        return typeId;
    }

    public String getItemClassName() {
        return itemClassName;
    }

    public List<String> getEffectClasses() {
        return effectClasses;
    }

    public void setEffectClasses(List<String> effectClasses) {
        this.effectClasses = effectClasses;
    }

    public boolean addEffectClass(Class<?> clazz) {
        return effectClasses.add(clazz.getName());
    }

    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = new HashMap<String, Object>();

        data.put("@className", getClass().getName());
        List<Object> validationsMap = new ArrayList<Object>();
        if (this.itemValidations != null) {
            for (FBValidation valid : this.itemValidations) {
                Map<String, Object> map = valid.getDataMap();
                validationsMap.add(map);
            }
        }
        data.put("itemValidations", validationsMap);
        data.put("effectClasses", new ArrayList<Object>(this.effectClasses));
        data.put("output",
                this.output == null ? null : this.output.getDataMap());
        data.put("input", this.input == null ? null : this.input.getDataMap());
        data.put("external",
                this.external == null ? null : this.external.getDataMap());
        data.put("width", this.width);
        data.put("height", this.height);
        data.put("typeId", this.typeId);
        Map<String, Object> eventActionsMap = new HashMap<String, Object>();
        if (this.eventActions != null) {
            for (Map.Entry<String, FBScript> entry : this.eventActions
                    .entrySet()) {
                FBScript script = entry.getValue();
                if (script != null) {
                    Map<String, Object> scriptMap = script.getDataMap();
                    eventActionsMap.put(entry.getKey(), scriptMap);
                }
            }
        }
        data.put("eventActions", eventActionsMap);
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setDataMap(Map<String, Object> data)
            throws FormEncodingException {
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        List<Object> validationsMap = (List<Object>) data
                .get("itemValidations");
        this.itemValidations.clear();
        if (validationsMap != null) {
            for (Object obj : validationsMap) {
                Map<String, Object> validMap = (Map<String, Object>) obj;
                FBValidation validation = (FBValidation) decoder
                        .decode(validMap);
                this.itemValidations.add(validation);
            }
        }
        List<Object> effectClassesObj = (List<Object>) data
                .get("effectClasses");
        effectClasses.clear();
        if (effectClassesObj != null) {
            for (Object obj : effectClassesObj) {
                effectClasses.add(obj.toString());
            }
        }
        this.eventActions.clear();
        Map<String, Object> eventActionsMap = (Map<String, Object>) data
                .get("eventActions");
        if (eventActionsMap != null) {
            for (Map.Entry<String, Object> entry : eventActionsMap.entrySet()) {
                Map<String, Object> scriptMap = (Map<String, Object>) entry
                        .getValue();
                if (scriptMap != null) {
                    FBScript script = new FBScript();
                    script.setDataMap(scriptMap);
                    this.eventActions.put(entry.getKey(), script);
                }
            }
        }
        this.output = (OutputData) decoder.decode((Map<String, Object>) data
                .get("output"));
        this.input = (InputData) decoder.decode((Map<String, Object>) data
                .get("input"));
        this.external = (ExternalData) decoder
                .decode((Map<String, Object>) data.get("external"));
        this.width = (String) data.get("width");
        this.height = (String) data.get("height");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((effectClasses == null) ? 0 : effectClasses.hashCode());
        result = prime * result
                + ((eventActions == null) ? 0 : eventActions.hashCode());
        result = prime * result
                + ((external == null) ? 0 : external.hashCode());
        result = prime * result + ((height == null) ? 0 : height.hashCode());
        result = prime * result + ((input == null) ? 0 : input.hashCode());
        result = prime * result
                + ((itemClassName == null) ? 0 : itemClassName.hashCode());
        result = prime * result
                + ((itemValidations == null) ? 0 : itemValidations.hashCode());
        result = prime * result + ((output == null) ? 0 : output.hashCode());
        result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
        result = prime * result + ((width == null) ? 0 : width.hashCode());
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
        FormItemRepresentation other = (FormItemRepresentation) obj;
        if (effectClasses == null) {
            if (other.effectClasses != null)
                return false;
        } else if (!effectClasses.equals(other.effectClasses))
            return false;
        if (eventActions == null) {
            if (other.eventActions != null)
                return false;
        } else if (!eventActions.equals(other.eventActions))
            return false;
        if (external == null) {
            if (other.external != null)
                return false;
        } else if (!external.equals(other.external))
            return false;
        if (height == null) {
            if (other.height != null)
                return false;
        } else if (!height.equals(other.height))
            return false;
        if (input == null) {
            if (other.input != null)
                return false;
        } else if (!input.equals(other.input))
            return false;
        if (itemClassName == null) {
            if (other.itemClassName != null)
                return false;
        } else if (!itemClassName.equals(other.itemClassName))
            return false;
        if (itemValidations == null) {
            if (other.itemValidations != null)
                return false;
        } else if (!itemValidations.equals(other.itemValidations))
            return false;
        if (output == null) {
            if (other.output != null)
                return false;
        } else if (!output.equals(other.output))
            return false;
        if (typeId == null) {
            if (other.typeId != null)
                return false;
        } else if (!typeId.equals(other.typeId))
            return false;
        if (width == null) {
            if (other.width != null)
                return false;
        } else if (!width.equals(other.width))
            return false;
        return true;
    }
}
