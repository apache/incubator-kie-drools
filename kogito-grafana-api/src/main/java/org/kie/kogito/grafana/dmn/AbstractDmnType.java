/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.grafana.dmn;

import java.util.List;
import java.util.SortedMap;

import org.kie.kogito.grafana.model.functions.GrafanaFunction;
import org.kie.kogito.grafana.model.panel.common.YAxis;

public class AbstractDmnType {

    private final Class internalRepresentationClass;

    private String dmnType;

    private SortedMap<Integer, GrafanaFunction> grafanaFunctionsToApply;

    private List<YAxis> yaxes;

    private String nameSuffix;

    public AbstractDmnType(Class internalRepresentationClass, String dmnType) {
        this(internalRepresentationClass, dmnType, "");
    }

    public AbstractDmnType(Class internalRepresentationClass, String dmnType, String nameSuffix) {
        this.internalRepresentationClass = internalRepresentationClass;
        this.dmnType = dmnType;
        this.nameSuffix = nameSuffix;
    }

    protected void addFunctions(SortedMap<Integer, GrafanaFunction> grafanaFunctionsToApply) {
        this.grafanaFunctionsToApply = grafanaFunctionsToApply;
    }

    protected void setYAxes(List<YAxis> yaxes) {
        this.yaxes = yaxes;
    }

    public String getNameSuffix() {
        return nameSuffix;
    }

    public Class getInternalClass() {
        return internalRepresentationClass;
    }

    public SortedMap<Integer, GrafanaFunction> getGrafanaFunctions() {
        return grafanaFunctionsToApply;
    }

    public List<YAxis> getYaxes() {
        return yaxes;
    }

    public String getDmnType() {
        return dmnType;
    }
}
