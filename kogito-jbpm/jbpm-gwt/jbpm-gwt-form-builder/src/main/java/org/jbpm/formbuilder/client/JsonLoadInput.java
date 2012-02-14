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
package org.jbpm.formbuilder.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationDecoder;
import org.jbpm.formbuilder.shared.task.TaskPropertyRef;
import org.jbpm.formbuilder.shared.task.TaskRef;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * 
 */
public class JsonLoadInput {

    private FormRepresentation form;
    private TaskRef task;
    private String profile;
    private String _package;
    private String contextPath;
    private Map<String, Object> formData = new HashMap<String, Object>();
    
    private JsonLoadInput() {
    }

    public FormRepresentation getForm() {
        return form;
    }

    public void setForm(FormRepresentation form) {
        this.form = form;
    }

    public TaskRef getTask() {
        return task;
    }

    public void setTask(TaskRef task) {
        this.task = task;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPackage() {
        return _package;
    }

    public void setPackage(String _package) {
        this._package = _package;
    }
    

    public Map<String, Object> getFormData() {
        if (formData == null) {
            formData = new HashMap<String, Object>();
        }
        return formData;
    }

    public void setFormData(Map<String, Object> formData) {
        this.formData = formData;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
    
    public String getContextPath() {
        return contextPath;
    }
    
    public static JsonLoadInput parse(String innerHTML) throws FormEncodingException {
        JSONValue json = JSONParser.parseStrict(innerHTML);
        JsonLoadInput input = null;
        if (json.isObject() != null) {
            input = new JsonLoadInput();
            JSONObject jsonObj = json.isObject();
            if (jsonObj.get("embedded") != null && jsonObj.get("embedded").isString() != null) {
                input.setProfile(jsonObj.get("embedded").isString().stringValue());
            }
            JSONValue jsonPkg = jsonObj.get("packageName");
            if (jsonPkg != null && jsonPkg.isString() != null) {
                input.setPackage(jsonPkg.isString().stringValue());
            }
            JSONValue jsonCtx = jsonObj.get("contextPath");
            if (jsonCtx != null && jsonCtx.isString() != null) {
                input.setContextPath(jsonCtx.isString().stringValue());
            }
            if (jsonObj.get("task") != null && jsonObj.get("task").isObject() != null) {
                input.setTask(toTask(jsonObj.get("task").isObject()));
            }
            if (jsonObj.get("formData") != null && jsonObj.get("formData").isObject() != null) {
                input.setFormData(toFormData(jsonObj.get("formData").isObject()));
            }
            if (jsonObj.get("formjson") != null && jsonObj.get("formjson").isString() != null) {
                input.setForm(toForm(jsonObj.get("formjson").isString().stringValue()));
            }
        }
        return input;
    }
    
    private static Map<String, Object> toFormData(JSONObject json) {
        Map<String, Object> retval = new HashMap<String, Object>();
        for (String key : json.keySet()) {
            JSONValue value = json.get(key);
            retval.put(key, asActualValue(value));
        }
        return retval;
    }
    
    private static Object asActualValue(JSONValue value) {
        if (value.isArray() != null) {
            JSONArray arr = value.isArray();
            List<Object> retval = new ArrayList<Object>();
            for (int index = 0; index < arr.size(); index++) {
                JSONValue subValue = arr.get(index);
                retval.add(asActualValue(subValue));
            }
            return retval;
        } else if (value.isBoolean() != null) {
            return String.valueOf(value.isBoolean().booleanValue());
        } else if (value.isNull() != null) {
            return null;
        } else if (value.isNumber() != null) {
            return String.valueOf(value.isNumber().doubleValue());
        } else if (value.isString() != null) {
            return value.isString().stringValue();
        } else if (value.isObject() != null) {
            return toFormData(value.isObject());
        }
        return null;
    }
    
    private static FormRepresentation toForm(String json) throws FormEncodingException {
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        FormRepresentation form = decoder.decode(json);
        return form;
    }
    
    private static TaskRef toTask(JSONObject json) {
        TaskRef retval = null;
        if (json != null) {
            retval = new TaskRef();
            retval.setInputs(getIOData(json.get("inputs").isArray()));
            retval.setOutputs(getIOData(json.get("outputs").isArray()));
            Map<String, String> metaData = new HashMap<String, String>();
            JSONObject jsonMetaData = json.get("metaData") == null ? null : json.get("metaData").isObject();
            if (jsonMetaData != null) {
                for (String key : jsonMetaData.keySet()) {
                    metaData.put(key, jsonMetaData.get(key).isString().stringValue());
                }
            }
            retval.setMetaData(metaData);
            if (json.get("packageName") != null && json.get("packageName").isString() != null) {
                retval.setPackageName(json.get("packageName").isString().stringValue());
            }
            if (json.get("processId") != null && json.get("processId").isString() != null) {
                retval.setProcessId(json.get("processId").isString().stringValue());
            }
            if (json.get("taskId") != null && json.get("taskId").isString() != null) {
                retval.setTaskId(json.get("taskId").isString().stringValue());
            }
        }
        return retval;
    }

    private static List<TaskPropertyRef> getIOData(JSONArray jsonIO) {
        List<TaskPropertyRef> retval = new ArrayList<TaskPropertyRef>();
        if (jsonIO != null) {
            for (int index = 0; index < jsonIO.size(); index++) {
                JSONObject jsonIo = jsonIO.get(index).isObject();
                TaskPropertyRef io = new TaskPropertyRef();
                if (jsonIo.get("name") != null && jsonIo.get("name").isString() != null) {
                    io.setName(jsonIo.get("name").isString().stringValue());
                }
                if (jsonIo.get("sourceExpression") != null && jsonIo.get("sourceExpression").isString() != null) {
                    io.setSourceExpresion(jsonIo.get("sourceExpression").isString().stringValue());
                }
                retval.add(io);
            }
        }
        return retval;
    }
}
