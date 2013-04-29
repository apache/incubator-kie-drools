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
package org.jbpm.form.builder.services.encoders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.form.builder.services.model.FBValidation;
import org.jbpm.form.builder.services.model.FormItemRepresentation;
import org.jbpm.form.builder.services.model.FormRepresentation;
import org.jbpm.form.builder.services.model.InputData;
import org.jbpm.form.builder.services.model.Mappable;
import org.jbpm.form.builder.services.model.OutputData;
import org.jbpm.form.builder.services.model.ScriptRepresentation;
import org.jbpm.form.builder.services.model.forms.FormEncodingException;
import org.jbpm.form.builder.services.model.forms.FormRepresentationDecoder;
import org.jbpm.form.builder.services.model.menu.MenuItemDescription;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FormRepresentationDecoderImpl implements FormRepresentationDecoder {

    @Override
    public Object decode(Map<String, Object> data) throws FormEncodingException {
        if (data == null || data.isEmpty()) {
            return null;
        }
        String className = (String) data.get("@className");
        if (className == null) {
            throw new FormEncodingException(
                    "@className attribute cannot be null");
        }
        Object obj = null;
        try {
            obj = Class.forName(className).newInstance();
            if (obj instanceof Mappable) {
                Mappable item = (Mappable) obj;
                item.setDataMap(data);
            } else {
                throw new FormEncodingException("Type "
                        + obj.getClass().getName()
                        + " cannot be casted to FormItemRepresentation");
            }
        } catch (InstantiationException e) {
            throw new FormEncodingException("Couldn't instantiate class "
                    + className, e);
        } catch (IllegalAccessException e) {
            throw new FormEncodingException(
                    "Couldn't access constructor of class " + className, e);
        } catch (ClassNotFoundException e) {
            throw new FormEncodingException("Couldn't find class " + className,
                    e);
        }
        return obj;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, List<MenuItemDescription>> decodeMenuItemsMap(String json)
            throws FormEncodingException {
        JsonObject jsonObj = new JsonParser().parse(json).getAsJsonObject();
        Map<String, Object> dataMap = asMap(jsonObj);
        Map<String, List<MenuItemDescription>> retval = null;
        if (dataMap != null) {
            retval = new HashMap<String, List<MenuItemDescription>>();
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                List<MenuItemDescription> itemsList = new ArrayList<MenuItemDescription>();
                String key = entry.getKey();
                Object obj = entry.getValue();
                if (obj != null) {
                    List<Object> itemsMapList = (List<Object>) obj;
                    for (Object itemObj : itemsMapList) {
                        Map<String, Object> itemDescMap = (Map<String, Object>) itemObj;
                        MenuItemDescription desc = new MenuItemDescription();
                        desc.setDataMap(itemDescMap);
                        itemsList.add(desc);
                    }
                }
                retval.put(key, itemsList);
            }
        }
        return retval;
    }

    @Override
    public FormRepresentation decode(String code) throws FormEncodingException {
        FormRepresentation form = new FormRepresentation();
        JsonElement json = new JsonParser().parse(code);
        if (json.isJsonObject()) {
            JsonObject jsonObj = json.getAsJsonObject();
            if (jsonObj.entrySet().isEmpty()) {
                return null;
            }
            if (jsonObj.get("action") != null
                    && jsonObj.get("action").isJsonPrimitive()
                    && jsonObj.get("action").getAsJsonPrimitive().isString()) {
                form.setAction(jsonObj.get("action").getAsString());
            }
            if (jsonObj.get("documentation") != null
                    && jsonObj.get("documentation").isJsonPrimitive()
                    && jsonObj.get("documentation").getAsJsonPrimitive()
                            .isString()) {
                form.setDocumentation(jsonObj.get("documentation")
                        .getAsString());
            }
            if (jsonObj.get("enctype") != null
                    && jsonObj.get("enctype").isJsonPrimitive()
                    && jsonObj.get("enctype").getAsJsonPrimitive().isString()) {
                form.setEnctype(jsonObj.get("enctype").getAsString());
            }
            if (jsonObj.get("lastModified") != null
                    && jsonObj.get("lastModified").isJsonPrimitive()
                    && jsonObj.get("lastModified").getAsJsonPrimitive()
                            .isString()) {
                form.setLastModified(Double.valueOf(
                        jsonObj.get("lastModified").getAsString()).longValue());
            } else if (jsonObj.get("lastModified") != null
                    && jsonObj.get("lastModified").isJsonPrimitive()
                    && jsonObj.get("lastModified").getAsJsonPrimitive()
                            .isNumber()) {
                form.setLastModified(jsonObj.get("lastModified").getAsNumber()
                        .longValue());
            }
            if (jsonObj.get("method") != null
                    && jsonObj.get("method").isJsonPrimitive()
                    && jsonObj.get("method").getAsJsonPrimitive().isString()) {
                form.setMethod(jsonObj.get("method").getAsString());
            }
            if (jsonObj.get("name") != null
                    && jsonObj.get("name").isJsonPrimitive()
                    && jsonObj.get("name").getAsJsonPrimitive().isString()) {
                form.setName(jsonObj.get("name").getAsString());
            }
            if (jsonObj.get("taskId") != null
                    && jsonObj.get("taskId").isJsonPrimitive()
                    && jsonObj.get("taskId").getAsJsonPrimitive().isString()) {
                form.setTaskId(jsonObj.get("taskId").getAsString());
            }
            if (jsonObj.get("processName") != null
                    && jsonObj.get("processName").isJsonPrimitive()
                    && jsonObj.get("processName").getAsJsonPrimitive()
                            .isString()) {
                form.setProcessName(jsonObj.get("processName").getAsString());
            }
            form.setFormItems(decodeList(jsonObj.get("formItems"),
                    FormItemRepresentation.class));
            form.setFormValidations(decodeList(jsonObj.get("formValidations"),
                    FBValidation.class));
            form.setInputs(decodeStringIndexedMap(jsonObj.get("inputs"),
                    InputData.class));
            form.setOutputs(decodeStringIndexedMap(jsonObj.get("outputs"),
                    OutputData.class));
            form.setOnLoadScripts(decodeList(jsonObj.get("onLoadScripts"),
                    ScriptRepresentation.class));
            form.setOnSubmitScripts(decodeList(jsonObj.get("onSubmitScripts"),
            		ScriptRepresentation.class));
        }
        return form;
    }

    @Override
    public FormItemRepresentation decodeItem(String json)
            throws FormEncodingException {
        JsonElement jsonValue = new JsonParser().parse(json);
        if (jsonValue.isJsonObject()) {
            JsonObject jsonObj = jsonValue.getAsJsonObject();
            Map<String, Object> dataMap = asMap(jsonObj);
            return (FormItemRepresentation) decode(dataMap);
        } else {
            throw new FormEncodingException("Expected json object but found "
                    + jsonValue);
        }
    }

    @SuppressWarnings("unchecked")
    public <V> Map<String, V> decodeStringIndexedMap(JsonElement json,
            Class<V> valueType) throws FormEncodingException {
        Map<String, V> retval = new HashMap<String, V>();
        if (json != null && json.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject()
                    .entrySet()) {
                if (entry.getValue().isJsonObject()) {
                    JsonObject jsonObj = entry.getValue().getAsJsonObject();
                    retval.put(entry.getKey(), (V) decode(asMap(jsonObj)));
                }
            }
        }
        return retval;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> decodeList(JsonElement json, Class<T> elemType)
            throws FormEncodingException {
        List<T> retval = new ArrayList<T>();
        if (json != null && json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            for (int index = 0; index < array.size(); index++) {
                JsonElement elem = array.get(index);
                JsonObject jsonObj = elem.getAsJsonObject();
                retval.add((T) decode(asMap(jsonObj)));
            }
        }
        return retval;
    }

    private Map<String, Object> asMap(JsonObject jsonObj) {
        Map<String, Object> retval = new HashMap<String, Object>();
        for (Map.Entry<String, JsonElement> entry : jsonObj.entrySet()) {
            retval.put(entry.getKey(), fromJsonValue(entry.getValue()));
        }
        return retval;
    }

    private List<Object> asList(JsonArray array) {
        List<Object> retval = new ArrayList<Object>();
        if (array != null) {
            for (JsonElement elem : array) {
                retval.add(fromJsonValue(elem));
            }
        }
        return retval;
    }

    private Object fromJsonValue(JsonElement elem) {
        if (elem.isJsonPrimitive() && elem.getAsJsonPrimitive().isString()) {
            return elem.getAsString();
        } else if (elem.isJsonPrimitive()
                && elem.getAsJsonPrimitive().isNumber()) {
            return elem.getAsNumber();
        } else if (elem.isJsonArray()) {
            return asList(elem.getAsJsonArray());
        } else if (elem.isJsonNull()) {
            return null;
        } else if (elem.isJsonObject()) {
            return asMap(elem.getAsJsonObject());
        } else {
            return "";
        }
    }
}
