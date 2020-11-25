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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.kie.kogito.grafana.model.functions.GrafanaFunction;
import org.kie.kogito.grafana.model.panel.common.YAxis;

public class SupportedDecisionTypes {

    private static final Set<AbstractDmnType> supportedDmnTypes = new HashSet<>();

    private static final Map<Class, String> dmnInternalClassToDmnStandardMap = new HashMap<>();

    static {
        supportedDmnTypes.add(new BooleanType());
        supportedDmnTypes.add(new NumberType());
        supportedDmnTypes.add(new StringType());
        supportedDmnTypes.add(new TimeAndDateType());
        supportedDmnTypes.add(new DaysAndTimeDurationType());
        supportedDmnTypes.add(new YearsAndMonthsDurationType());
        supportedDmnTypes.add(new LocalDateType());
        supportedDmnTypes.add(new LocalTimeType());
        supportedDmnTypes.stream().forEach(x -> dmnInternalClassToDmnStandardMap.put(x.getInternalClass(), x.getDmnType()));
    }

    private SupportedDecisionTypes() {
    }

    public static boolean isSupported(String type) {
        return dmnInternalClassToDmnStandardMap.containsValue(type);
    }

    public static boolean isSupported(Class c) {
        return dmnInternalClassToDmnStandardMap.containsKey(c);
    }

    public static String fromInternalToStandard(Class c) {
        return dmnInternalClassToDmnStandardMap.get(c);
    }

    public static SortedMap<Integer, GrafanaFunction> getGrafanaFunction(String dmnType) {
        if (isSupported(dmnType)) {
            Optional<AbstractDmnType> type = supportedDmnTypes.stream().filter(x -> x.getDmnType().equalsIgnoreCase(dmnType)).findFirst();
            if (type.isPresent()) {
                return type.get().getGrafanaFunctions();
            }
        }
        return new TreeMap<>();
    }

    public static List<YAxis> getYAxis(String dmnType) {
        if (isSupported(dmnType)) {
            Optional<AbstractDmnType> type = supportedDmnTypes.stream().filter(x -> x.getDmnType().equalsIgnoreCase(dmnType)).findFirst();
            if (type.isPresent()) {
                return type.get().getYaxes();
            }
        }
        return new ArrayList<>();
    }

    public static String getNameSuffix(String dmnType) {
        if (isSupported(dmnType)) {
            Optional<AbstractDmnType> type = supportedDmnTypes.stream().filter(x -> x.getDmnType().equalsIgnoreCase(dmnType)).findFirst();
            if (type.isPresent()) {
                return type.get().getNameSuffix();
            }
        }
        return "";
    }

    public static Collection<String> getSupportedDMNTypes() {
        return dmnInternalClassToDmnStandardMap.values();
    }
}
