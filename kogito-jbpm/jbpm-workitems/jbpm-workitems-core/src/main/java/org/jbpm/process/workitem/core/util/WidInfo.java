/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.workitem.core.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import org.jbpm.process.workitem.core.util.service.WidService;

public class WidInfo {

    private String widfile;
    private String name;
    private String displayName;
    private String category;
    private String icon;
    private String description;
    private String defaultHandler;
    private String defaultHandlerNoType;
    private String documentation;
    private Map<String, InternalWidParamsAndResults> parameters;
    private Map<String, InternalWidParameterValues> parameterValues;
    private Map<String, InternalWidParamsAndResults> results;
    private Map<String, InternalWidMavenDependencies> mavenDepends;
    private InternalServiceInfo serviceInfo;

    public WidInfo(List<Wid> wids) {
        this.parameters = new HashMap<>();
        this.parameterValues = new HashMap<>();
        this.results = new HashMap<>();
        this.mavenDepends = new HashMap<>();

        // go over all @Wid and add/overwrite parameters
        for (Wid wid : wids) {
            this.widfile = setParamValue(this.widfile,
                                         wid.widfile());
            this.name = setParamValue(this.name,
                                      wid.name());
            this.displayName = setParamValue(this.displayName,
                                             wid.displayName());
            this.category = setParamValue(this.category,
                                          wid.category());
            this.icon = setParamValue(this.icon,
                                      wid.icon());
            this.description = setParamValue(this.description,
                                             wid.description());
            this.defaultHandler = setParamValue(this.defaultHandler,
                                                wid.defaultHandler());

            this.defaultHandlerNoType = removeType(setParamValue(this.defaultHandler,
                                                                 wid.defaultHandler()));

            this.documentation = setParamValue(this.documentation,
                                               wid.documentation());

            if (wid.parameters().length > 0) {
                for (WidParameter widParam : wid.parameters()) {
                    this.parameters.put(widParam.name(),
                                        new InternalWidParamsAndResults(widParam.name(),
                                                                        widParam.type(),
                                                                        widParam.required()));
                }
            }

            if (wid.parameterValues().length > 0) {
                for (WidParameterValues widParamValues : wid.parameterValues()) {
                    this.parameterValues.put(widParamValues.parameterName(),
                                             new InternalWidParameterValues(widParamValues.parameterName(),
                                                                            widParamValues.values()));
                }
            }

            if (wid.results().length > 0) {
                for (WidResult widResult : wid.results()) {
                    this.results.put(widResult.name(),
                                     new InternalWidParamsAndResults(widResult.name(),
                                                                     widResult.type(),
                                                                     false));
                }
            }

            if (wid.mavenDepends().length > 0) {
                for (WidMavenDepends widMavenDepends : wid.mavenDepends()) {
                    this.mavenDepends.put(widMavenDepends.group() + "." + widMavenDepends.artifact(),
                                          new InternalWidMavenDependencies(widMavenDepends.group(),
                                                                           widMavenDepends.artifact(),
                                                                           widMavenDepends.version()));
                }
            }

            this.serviceInfo = new InternalServiceInfo(wid.serviceInfo());
        }
    }

    private String setParamValue(String origVal,
                                 String newVal) {
        if (newVal != null && !newVal.isEmpty()) {
            return newVal;
        } else {
            return (origVal == null || origVal.isEmpty()) ? "" : origVal;
        }
    }

    private String removeType(String value) {
        if (value != null) {
            if (value.startsWith("mvel: ")) {
                return value.substring(6);
            } else if (value.startsWith("reflection: ")) {
                return value.substring(12);
            } else {
                return value;
            }
        } else {
            return value;
        }
    }

    private class InternalWidParamsAndResults {

        private String name;
        private String type;
        private boolean required;

        public InternalWidParamsAndResults(String name,
                                           String type,
                                           boolean required) {
            this.name = name;
            this.type = type;
            this.required = required;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }
    }

    private class InternalWidParameterValues {

        private String parameterName;
        private String values;

        public InternalWidParameterValues(String parameterName,
                                          String values) {
            this.parameterName = parameterName;
            this.values = values;
        }

        public String getParameterName() {
            return parameterName;
        }

        public void setParameterName(String parameterName) {
            this.parameterName = parameterName;
        }

        public String getValues() {
            return values;
        }

        public void setValues(String values) {
            this.values = values;
        }
    }

    public class InternalWidMavenDependencies {

        private String group;
        private String artifact;
        private String version;

        public InternalWidMavenDependencies(String group,
                                            String artifact,
                                            String version) {
            this.group = group;
            this.artifact = artifact;
            this.version = version;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getArtifact() {
            return artifact;
        }

        public void setArtifact(String artifact) {
            this.artifact = artifact;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    public class InternalServiceInfo {

        private boolean hasTrigger;
        private boolean hasAction;
        private String category;
        private List<String> keywords;
        private String description;
        private InternalServiceTrigger trigger;
        private InternalServiceAction action;

        public InternalServiceInfo(WidService service) {
            if (service.category().length() > 0 && service.description().length() > 0) {
                this.category = service.category();
                this.description = service.description();
                this.keywords = Arrays.asList(service.keywords().split("\\s*,\\s*"));

                if (service.trigger().title().length() > 0) {
                    this.hasTrigger = true;
                    this.trigger = new InternalServiceTrigger(service.trigger().title(),
                                                              service.trigger().description());
                }

                if (service.action().title().length() > 0) {
                    this.hasAction = true;
                    this.action = new InternalServiceAction(service.action().title(),
                                                            service.action().description(),
                                                            service.action().requiredFieldsOnly());
                }
            } else {
                hasTrigger = false;
                hasAction = false;
            }
        }

        public boolean isHasTrigger() {
            return hasTrigger;
        }

        public void setHasTrigger(boolean hasTrigger) {
            this.hasTrigger = hasTrigger;
        }

        public boolean isHasAction() {
            return hasAction;
        }

        public void setHasAction(boolean hasAction) {
            this.hasAction = hasAction;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public List<String> getKeywords() {
            return keywords;
        }

        public void setKeywords(List<String> keywords) {
            this.keywords = keywords;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public InternalServiceTrigger getTrigger() {
            return trigger;
        }

        public void setTrigger(InternalServiceTrigger trigger) {
            this.trigger = trigger;
        }

        public InternalServiceAction getAction() {
            return action;
        }

        public void setAction(InternalServiceAction action) {
            this.action = action;
        }
    }

    public class InternalServiceTrigger {

        private String title;
        private String description;

        public InternalServiceTrigger(String title,
                                      String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public class InternalServiceAction {

        private String title;
        private String description;
        private boolean requiredFieldsOnly;

        public InternalServiceAction(String title,
                                     String description,
                                     boolean requiredFieldsOnly) {
            this.title = title;
            this.description = description;
            this.requiredFieldsOnly = requiredFieldsOnly;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isRequiredFieldsOnly() {
            return requiredFieldsOnly;
        }

        public void setRequiredFieldsOnly(boolean requiredFieldsOnly) {
            this.requiredFieldsOnly = requiredFieldsOnly;
        }
    }

    public String getWidfile() {
        return widfile;
    }

    public void setWidfile(String widfile) {
        this.widfile = widfile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultHandler() {
        return defaultHandler;
    }

    public void setDefaultHandler(String defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    public String getDefaultHandlerNoType() {
        return defaultHandlerNoType;
    }

    public void setDefaultHandlerNoType(String defaultHandlerNoType) {
        this.defaultHandlerNoType = defaultHandlerNoType;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public Map<String, InternalWidParamsAndResults> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, InternalWidParamsAndResults> parameters) {
        this.parameters = parameters;
    }

    public Map<String, InternalWidParameterValues> getParameterValues() {
        return parameterValues;
    }

    public void setParameterValues(Map<String, InternalWidParameterValues> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public Map<String, InternalWidParamsAndResults> getResults() {
        return results;
    }

    public void setResults(Map<String, InternalWidParamsAndResults> results) {
        this.results = results;
    }

    public Map<String, WidInfo.InternalWidMavenDependencies> getMavenDepends() {
        return mavenDepends;
    }

    public void setMavenDepends(Map<String, WidInfo.InternalWidMavenDependencies> mavenDepends) {
        this.mavenDepends = mavenDepends;
    }

    public InternalServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(InternalServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }
}