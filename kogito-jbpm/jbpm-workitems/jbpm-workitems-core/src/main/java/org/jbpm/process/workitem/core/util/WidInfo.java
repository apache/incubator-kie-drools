/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

public class WidInfo {

    private String widfile;
    private String name;
    private String displayName;
    private String category;
    private String icon;
    private String description;
    private String defaultHandler;
    private Map<String, InternalWidParamsAndResults> parameters;
    private Map<String, InternalWidParameterValues> parameterValues;
    private Map<String, InternalWidParamsAndResults> results;
    private Map<String, InternalWidMavenDependencies> mavenDepends;

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

            if (wid.parameters().length > 0) {
                for (WidParameter widParam : wid.parameters()) {
                    this.parameters.put(widParam.name(),
                                        new InternalWidParamsAndResults(widParam.name(),
                                                                        widParam.type()));
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
                                                                     widResult.type()));
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

    private class InternalWidParamsAndResults {

        private String name;
        private String type;

        public InternalWidParamsAndResults(String name,
                                           String type) {
            this.name = name;
            this.type = type;
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
}