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

import java.util.Date;

import org.drools.verifier.core.checks.base.CheckRunner;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.keys.UUIDKeyProvider;
import org.drools.verifier.core.maps.util.HasKeys;
import org.drools.verifier.core.util.PortablePreconditions;

public class AnalyzerConfiguration {

    private final UUIDKeyProvider uuidKeyProvider;
    private final String webWorkerUUID;
    private final DateTimeFormatProvider dateTimeFormatter;
    private final CheckConfiguration checkConfiguration;
    private final CheckRunner checkRunner;

    public AnalyzerConfiguration(final String webWorkerUUID,
                                 final DateTimeFormatProvider dateTimeFormatter,
                                 final UUIDKeyProvider uuidKeyProvider,
                                 final CheckConfiguration checkConfiguration,
                                 final CheckRunner checkRunner) {
        this.webWorkerUUID = PortablePreconditions.checkNotNull("webWorkerUUID",
                                                                webWorkerUUID);
        this.dateTimeFormatter = PortablePreconditions.checkNotNull("dateTimeFormatter",
                                                                    dateTimeFormatter);
        this.uuidKeyProvider = PortablePreconditions.checkNotNull("uuidKeyProvider",
                                                                  uuidKeyProvider);
        this.checkConfiguration = PortablePreconditions.checkNotNull("checkConfiguration",
                                                                     checkConfiguration);
        this.checkRunner = PortablePreconditions.checkNotNull("checkRunner",
                                                              checkRunner);
    }

    public String getWebWorkerUUID() {
        return webWorkerUUID;
    }

    public UUIDKey getUUID(final HasKeys hasKeys) {
        return uuidKeyProvider.get(hasKeys);
    }

    public String formatDate(final Date dateValue) {
        return dateTimeFormatter.format(dateValue);
    }

    public Date parse(final String dateValue) {
        return dateTimeFormatter.parse(dateValue);
    }

    public CheckConfiguration getCheckConfiguration() {
        return checkConfiguration;
    }

    public CheckRunner getCheckRunner() {
        return checkRunner;
    }
}

