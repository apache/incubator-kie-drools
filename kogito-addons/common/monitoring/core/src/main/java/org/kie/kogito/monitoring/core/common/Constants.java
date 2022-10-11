/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.monitoring.core.common;

public class Constants {

    // https://issues.redhat.com/browse/KOGITO-4618 remove and take the constant from org.kie.kogito.explainability.Constants once explainability addon will be part of product
    public static final String SKIP_MONITORING = "skipMonitoring";
    public static final String MONITORING_RULE_USE_DEFAULT = "kogito.monitoring.rule.useDefault";
    public static final String MONITORING_PROCESS_USE_DEFAULT = "kogito.monitoring.process.useDefault";
    public static final String HTTP_INTERCEPTOR_USE_DEFAULT = "kogito.monitoring.interceptor.useDefault";

    private Constants() {

    }
}
