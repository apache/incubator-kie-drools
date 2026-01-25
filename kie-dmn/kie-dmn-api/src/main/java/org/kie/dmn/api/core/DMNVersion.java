/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.api.core;

import java.util.Collection;

public enum DMNVersion {

    V1_1(11, "v1_1"),
    V1_2(12, "v1_2"),
    V1_3(13, "v1_3"),
    V1_4(14, "v1_4"),
    V1_5(15, "v1_5"),
    V1_6(16, "v1_6");

    private final int dmnVersion;
    private final String dmnVersionString;

    DMNVersion(int dmnVersion, String dmnVersionString) {
        this.dmnVersion = dmnVersion;
        this.dmnVersionString = dmnVersionString;
    }

    public int getDmnVersion() {
        return dmnVersion;
    }

    public static DMNVersion getLatest() {
        DMNVersion latest = null;
        for (DMNVersion version : DMNVersion.values()) {
            if (latest == null || version.dmnVersion > latest.dmnVersion) {
                latest = version;
            }
        }
        return latest;
    }

    public static String getLatestDmnVersionString() {
        return getLatest().dmnVersionString;

    }

    public static DMNVersion inferDMNVersion(Collection<String> nsContextValues) {
        DMNVersion toReturn = DMNVersion.getLatest();
        if (nsContextValues.stream().anyMatch(org.kie.dmn.model.v1_6.KieDMNModelInstrumentedBase.URI_DMN::equals)) {
            toReturn = DMNVersion.V1_6;
        }else if (nsContextValues.stream().anyMatch(org.kie.dmn.model.v1_5.KieDMNModelInstrumentedBase.URI_DMN::equals)) {
            toReturn = DMNVersion.V1_5;
        } else if (nsContextValues.stream().anyMatch(org.kie.dmn.model.v1_4.KieDMNModelInstrumentedBase.URI_DMN::equals)) {
            toReturn = DMNVersion.V1_4;
        } else if (nsContextValues.stream().anyMatch(org.kie.dmn.model.v1_3.KieDMNModelInstrumentedBase.URI_DMN::equals)) {
            toReturn = DMNVersion.V1_3;
        } else if (nsContextValues.stream().anyMatch(org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_DMN::equals)) {
            toReturn = DMNVersion.V1_2;
        } else if (nsContextValues.stream().anyMatch(org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase.URI_DMN::equals)) {
            toReturn = DMNVersion.V1_1;
        }
        return toReturn;
    }

}