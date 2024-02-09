/**
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
package org.drools.verifier.core.configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.api.reporting.Severity;

public class CheckConfiguration {

    private final Set<CheckType> configurations = new HashSet<>();

    private final Map<CheckType, Severity> severityOverwrites = new HashMap<>();

    private CheckConfiguration() {
    }

    public static CheckConfiguration newDefault() {
        final CheckConfiguration checkConfiguration = new CheckConfiguration();

        checkConfiguration.getCheckConfiguration()
                .addAll(Arrays.asList(CheckType.values()));

        return checkConfiguration;
    }

    public static CheckConfiguration newEmpty() {
        return new CheckConfiguration();
    }

    public Set<CheckType> getCheckConfiguration() {
        return configurations;
    }

    public void setSeverityOverwrites(CheckType checkType, Severity severity) {
        severityOverwrites.put(checkType, severity);
    }

    public Optional<Severity> getSeverityOverwrite(final CheckType checkType) {
        if (severityOverwrites.containsKey(checkType)) {
            return Optional.of(severityOverwrites.get(checkType));
        } else {
            return Optional.empty();
        }
    }
}
